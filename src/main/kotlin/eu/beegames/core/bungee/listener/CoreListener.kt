package eu.beegames.core.bungee.listener

import com.maxmind.db.CHMCache
import com.maxmind.geoip2.DatabaseReader
import eu.beegames.core.bungee.CorePlugin
import eu.beegames.core.common.Constants
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import net.md_5.bungee.api.event.PreLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.protocol.ProtocolConstants
import java.io.File
import java.net.InetSocketAddress
import java.util.concurrent.CompletableFuture

class CoreListener(private val plugin: CorePlugin) : Listener {
    private val geoipDb = DatabaseReader.Builder(File(plugin.dataFolder, "GeoLite2-Country.mmdb"))
        .withCache(CHMCache()).build()

    private val lpApi = LuckPermsProvider.get()


    @EventHandler
    fun on(ev: PreLoginEvent) {
        ev.registerIntent(plugin)
        lpApi.userManager.lookupUniqueId(ev.connection.name)
            .thenComposeAsync {
                if (it == null) {
                    // return a future with null to avoid errors 
                    val f = CompletableFuture<User?>()
                    f.complete(null)
                    f
                }
                else lpApi.userManager.loadUser(it)
            }
            .thenAcceptAsync {
                if (it != null && it.cachedData.permissionData.checkPermission(Constants.Permissions.BypassGeoIP)
                        .asBoolean()
                ) {
                    return@thenAcceptAsync
                }

                val sa = ev.connection.socketAddress
                if (sa !is InetSocketAddress) {
                    plugin.logger.warning("${ev.connection.name} has connected to the server via non-internet sockets, ignoring lookup.")
                    return@thenAcceptAsync
                }

                val lr = geoipDb.tryCountry(sa.address)
                if (!lr.isPresent) {
                    plugin.logger.warning("${ev.connection.name} has connected to the server with an address from an unknown country, denying access.")
                    ev.isCancelled = true

                    ev.setCancelReason(
                        if (ev.connection.version >= ProtocolConstants.MINECRAFT_1_16) {
                            BungeeComponentSerializer.get()
                        } else {
                            BungeeComponentSerializer.legacy()
                        }.serialize(
                            plugin.disconnectGeoipUnknownPrecompiled
                        )[0]
                    )
                    return@thenAcceptAsync
                }

                val countryResp = lr.get()
                if (!plugin.whitelistedCountries.contains(countryResp.country.isoCode) && (it != null && !it.cachedData.permissionData
                        .checkPermission("${Constants.Permissions.BypassGeoIP}.${countryResp.country.isoCode}")
                        .asBoolean())
                ) {
                    plugin.logger.warning("${ev.connection.name} has connected to the server with an address from ${countryResp.country.name}, denying access.")
                    ev.isCancelled = true
                    ev.setCancelReason(
                        if (ev.connection.version >= ProtocolConstants.MINECRAFT_1_16) {
                            BungeeComponentSerializer.get()
                        } else {
                            BungeeComponentSerializer.legacy()
                        }.serialize(
                            plugin.disconnectGeoipBlacklistedPrecompiled
                                .replaceText(
                                    TextReplacementConfig.builder()
                                        .matchLiteral("{country}")
                                        .replacement(countryResp.country.name)
                                        .build()
                                )
                        )[0]
                    )
                    return@thenAcceptAsync
                }
            }
            .thenAcceptAsync { ev.completeIntent(plugin) }
            .exceptionally {
                ev.isCancelled = true
                ev.setCancelReason(
                    if (ev.connection.version >= ProtocolConstants.MINECRAFT_1_16) {
                        BungeeComponentSerializer.get()
                    } else {
                        BungeeComponentSerializer.legacy()
                    }.serialize(
                        Component.text(
                            "An internal error has occurred during checking access, please try again later.\n" +
                                    "If this persists, please let us know at info@beegames.eu"
                        )
                            .color(NamedTextColor.RED)
                    )[0]
                )
                ev.completeIntent(plugin)

                plugin.logger.warning("Checking eligibility to join for ${ev.connection.name} has failed")
                plugin.logger.warning(it.stackTraceToString())

                null // WTF: kotlin infers Void! for this function
            }
    }
}


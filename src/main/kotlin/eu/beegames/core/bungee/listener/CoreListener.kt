package eu.beegames.core.bungee.listener

import com.maxmind.db.CHMCache
import com.maxmind.geoip2.DatabaseReader
import eu.beegames.core.bungee.CorePlugin
import eu.beegames.core.common.Constants
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer
import net.luckperms.api.LuckPermsProvider
import net.md_5.bungee.api.event.PreLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.protocol.ProtocolConstants
import java.io.File
import java.net.InetSocketAddress

class CoreListener(private val plugin: CorePlugin) : Listener {
    private val geoipDb = DatabaseReader.Builder(File(plugin.dataFolder, "GeoLite2-Country.mmdb"))
        .withCache(CHMCache()).build()

    private val lpApi = LuckPermsProvider.get()


    @EventHandler
    fun on(ev: PreLoginEvent) {
        ev.registerIntent(plugin)
        lpApi.userManager.lookupUniqueId(ev.connection.name)
            .thenComposeAsync(lpApi.userManager::loadUser)
            .thenAcceptAsync {
                if (it.cachedData.permissionData.checkPermission(Constants.Permissions.BypassGeoIP)
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
                if (!plugin.whitelistedCountries.contains(countryResp.country.isoCode)) {
                    plugin.logger.warning("${ev.connection.name} has connected to the server with an address from ${countryResp.country.name}, denying access.")
                    ev.isCancelled = true
                    ev.setCancelReason(
                        if (ev.connection.version >= ProtocolConstants.MINECRAFT_1_16) {
                            BungeeComponentSerializer.get()
                        } else {
                            BungeeComponentSerializer.legacy()
                        }.serialize(
                            plugin.disconnectGeoipBlacklistedPrecompiled
                                .replaceText(TextReplacementConfig.builder()
                                    .matchLiteral("{country}")
                                    .replacement(countryResp.country.name)
                                    .build())
                        )[0]
                    )
                    return@thenAcceptAsync
                }
            }
            .thenAcceptAsync { ev.completeIntent(plugin) }
    }
}
package eu.beegames.core.bungee.listener

import com.maxmind.db.CHMCache
import com.maxmind.geoip2.DatabaseReader
import eu.beegames.core.bungee.CorePlugin
import eu.beegames.core.common.Constants
import net.luckperms.api.LuckPermsProvider
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.PreLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
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
                        TextComponent(
                            "Your location is unknown and therefore access to the server has been denied.\n" +
                                    "Please contact info@beegames.eu for more information.\n" +
                                    "-------------\n" +
                                    "Tvoje země nebyla rozpoznána, a tudíž ti byl odepřen přístup k serveru.\n" +
                                    "Prosím, kontaktuj info@beegames.eu pro více informací."
                        ).apply {
                            color = ChatColor.RED
                        }
                    )
                    return@thenAcceptAsync
                }

                val countryResp = lr.get()
                if (!plugin.whitelistedCountries.contains(countryResp.country.isoCode)) {
                    plugin.logger.warning("${ev.connection.name} has connected to the server with an address from ${countryResp.country.name}, denying access.")
                    ev.isCancelled = true
                    ev.setCancelReason(
                        TextComponent(
                            "Your location (${countryResp.country.name}) is not whitelisted and therefore access to the server has been denied.\n" +
                                    "Please contact info@beegames.eu for more information.\n" +
                                    "-------------\n" +
                                    "Tvoje země (${countryResp.country.name}) není na whitelistu, a tudíž ti byl odepřen přístup k serveru.\n" +
                                    "Prosím, kontaktuj info@beegames.eu pro více informací."
                        ).apply {
                            color = ChatColor.RED
                        }
                    )
                    return@thenAcceptAsync
                }
            }
            .thenAcceptAsync { ev.completeIntent(plugin) }
    }
}
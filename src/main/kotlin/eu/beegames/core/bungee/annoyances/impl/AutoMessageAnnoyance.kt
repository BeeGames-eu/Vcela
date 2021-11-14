package eu.beegames.core.bungee.annoyances.impl

import eu.beegames.core.bungee.CorePlugin
import eu.beegames.core.bungee.annoyances.Annoyance
import eu.beegames.core.common.Constants
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.luckperms.api.node.Node
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.scheduler.ScheduledTask
import net.md_5.bungee.config.Configuration
import java.util.concurrent.TimeUnit

/**
 * Annoys players with automatic messages.
 *
 * Can be turned off using /annoyances off automessage
 */
class AutoMessageAnnoyance(plugin: CorePlugin) : Annoyance(plugin) {
    private val name = "automessages"
    private var _interval: ScheduledTask? = null

    @Suppress("PrivatePropertyName")
    private val ReceiveNode
        get() = Node
            .builder("${Constants.Permissions.BaseAnnoyancePermission}.${name}.receive")
            .value(false)
            .build()

    override fun enable(conf: Configuration): Boolean {
        val interval = conf.getLong("interval.amount")
        if (interval == 0L) {
            plugin.proxy.logger.info("Interval for automessages not specified, not running annoyance")
            return false
        }

        val intervalUnit = try {
            conf.getString("interval.unit")?.uppercase()?.let { TimeUnit.valueOf(it) }
        } catch (ex: Exception) {
            plugin.proxy.logger.info("Invalid time unit for automessages specified, not running annoyance")
            return false
        }

        val annoyanceList = conf.getStringList("messages")

        if (annoyanceList == null || annoyanceList.size == 0) {
            plugin.proxy.logger.info("No messages have been set for automessages, not running annoyance")
            return false
        }

        var n = 0
        _interval = plugin.proxy.scheduler.schedule(plugin, {
            plugin.adventure.permission("${Constants.Permissions.BaseAnnoyancePermission}.${name}.receive")
                .sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                        // this is stupid, should we modulo instead?
                        if (n == annoyanceList.size) {
                            n = 1
                            annoyanceList[0]
                        } else {
                            annoyanceList[n++]
                        }
                    )
                )
        }, interval, interval, intervalUnit)

        return true
    }

    override fun disable() {
        _interval?.cancel()
    }

    override fun disableForPlayer(player: ProxiedPlayer) =
        plugin.lpApi.userManager.modifyUser(player.uniqueId) {
            it.data().add(ReceiveNode)
        }

    override fun enableForPlayer(player: ProxiedPlayer) =
        plugin.lpApi.userManager.modifyUser(player.uniqueId) {
            it.data().remove(ReceiveNode)
            /*it.data().clear { n -> (n.key == "${Constants.Permissions.BaseAnnoyancePermission}.${name}.receive").also { b ->
                plugin.logger.info("${n.key} $b")
            } }*/
        }
}

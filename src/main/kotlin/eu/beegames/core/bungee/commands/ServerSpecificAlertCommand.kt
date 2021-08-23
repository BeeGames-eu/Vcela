package eu.beegames.core.bungee.commands

import eu.beegames.core.bungee.CorePlugin
import eu.beegames.core.common.Constants
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.CommandSender

class ServerSpecificAlertCommand(plugin: CorePlugin) : BaseAlertCommand(plugin, "salert", "eu.beegames.core.send_alert") {
    override fun execute(sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) {
            plugin.adventure.sender(sender).sendMessage(
                Constants.ADVENTURE_PREFIX
                    .append(Component.text("Použití: /salert <server> <...text>", NamedTextColor.RED))
            )
            return
        }

        val serverName = args.first()
        val server = plugin.proxy.servers[serverName] ?: run {
            plugin.adventure.sender(sender).sendMessage(
                Constants.ADVENTURE_PREFIX
                    .append(Component.text("Server $serverName neexistuje!", NamedTextColor.RED))
            )
            return
        }

        val alertComponent = LegacyComponentSerializer.legacyAmpersand()
            .deserialize(args.drop(1).joinToString(" "))
            .colorIfAbsent(NamedTextColor.WHITE)
        val title = getAdventureTitle(alertComponent)

        if (!server.players.contains(sender)) {
            plugin.adventure.sender(sender).sendMessage(
                Constants.ADVENTURE_PREFIX
                    .append(Component.text("Posílám alert na server $serverName...", NamedTextColor.GREEN))
            )
        }

        for (player in server.players) {
            plugin.adventure.player(player).apply {
                sendMessage(ADVENTURE_ALERT_PREFIX.append(alertComponent))
                showTitle(title)
            }
        }
    }

}
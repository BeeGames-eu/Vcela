package eu.beegames.core.bungee.commands

import eu.beegames.core.bungee.CorePlugin
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.ChatColor

class GlobalAlertCommand(plugin: CorePlugin) : BaseAlertCommand(plugin, "alert", "eu.beegames.core.send_alert", "galert", "send_alert") {
    override fun execute(sender: CommandSender, args: Array<out String>) {
        val alert = ChatColor.translateAlternateColorCodes('&', args.joinToString(" "))

        val alertComponent = TextComponent.fromLegacyText(alert)

        val title = getTitle(alertComponent)

        plugin.proxy.broadcast(ALERT_PREFIX.duplicate(), *alertComponent)
        for (player in plugin.proxy.players) {
            player.sendTitle(title)
        }
    }
}
package eu.beegames.core.bungee.commands

import eu.beegames.core.bungee.CorePlugin
import eu.beegames.core.common.Constants
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer

class LocalAlertCommand(plugin: CorePlugin) : BaseAlertCommand(plugin, "lalert", "eu.beegames.core.send_alert") {
    override fun execute(sender: CommandSender, args: Array<out String>) {
        if (sender !is ProxiedPlayer) {
               sender.sendMessage(Constants.PREFIX.duplicate().apply {
                   addExtra(TextComponent("Tento prikaz nemuze byt spusten z konzole.").apply {
                       color = ChatColor.RED
                   })
               })
            return
        }

        val alert = ChatColor.translateAlternateColorCodes('&', args.joinToString(" "))

        val alertComponent = TextComponent.fromLegacyText(alert)

        val title = getTitle(alertComponent)

        for (player in sender.server.info.players) {
            player.sendMessage(ALERT_PREFIX.duplicate(), *alertComponent)
            player.sendTitle(title)
        }
    }
}
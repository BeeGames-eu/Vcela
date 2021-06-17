package eu.beegames.core.bungee.commands

import eu.beegames.core.bungee.CorePlugin
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent

class GlobalAlertCommand(plugin: CorePlugin) : BaseAlertCommand(plugin, "alert", "eu.beegames.core.send_alert", "galert", "send_alert") {
    override fun execute(sender: CommandSender, args: Array<out String>) {
        val alert = args.joinToString(" ")

        val alertComponent = TextComponent(alert)

        val title = getTitle(alertComponent)

        val alertMessage = getMessage(alertComponent)

        plugin.proxy.broadcast(alertMessage)
        for (player in plugin.proxy.players) {
            player.sendTitle(title)
        }
    }
}
package eu.beegames.core.bungee.commands

import eu.beegames.core.bungee.CorePlugin
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.plugin.Command

class AlertCommand(private val plugin: CorePlugin) : Command("send_alert", "eu.beegames.core.send_alert") {

    override fun execute(sender: CommandSender, args: Array<out String>) {
        val alert = args.joinToString(" ")

        val alertComponent = TextComponent(alert)

        val title = plugin.proxy.createTitle().apply {
            reset()
            title(TextComponent("Alert").apply {
                color = ChatColor.RED
            })
            subTitle(alertComponent)
        }

        val alertMessage = ALERT_PREFIX.duplicate().apply {
            addExtra(alertComponent.duplicate().apply {
                color = ChatColor.RESET
            })
        }

        plugin.proxy.broadcast(alertMessage)
        for (player in plugin.proxy.players) {
            player.sendTitle(title)
        }
    }

    companion object {
        val ALERT_PREFIX = TextComponent("[").apply {
            color = ChatColor.DARK_GRAY
            addExtra(TextComponent("Alert").apply {
                color = ChatColor.RED
            })
            addExtra(TextComponent("]").apply {
                color = ChatColor.DARK_GRAY
            })
            addExtra(TextComponent(" ").apply {
                color = ChatColor.RESET
            })
        }
    }

}
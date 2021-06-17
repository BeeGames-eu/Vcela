package eu.beegames.core.bungee.commands

import eu.beegames.core.bungee.CorePlugin
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.Title
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.plugin.Command

abstract class BaseAlertCommand(protected val plugin: CorePlugin, cmdName: String, permission: String, vararg aliases: String) : Command(cmdName, permission, *aliases) {

    fun getTitle(alertComponent: BaseComponent): Title {
        return plugin.proxy.createTitle().apply {
            reset()
            title(TextComponent("Alert").apply {
                color = ChatColor.RED
            })
            subTitle(alertComponent)
        }
    }

    fun getMessage(alertComponent: BaseComponent): BaseComponent {
        return ALERT_PREFIX.duplicate().apply {
            addExtra(alertComponent.duplicate().apply {
                color = ChatColor.RESET
            })
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
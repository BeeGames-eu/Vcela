package eu.beegames.core.bungee.commands

import eu.beegames.core.bungee.CorePlugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextFormat
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.Title
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.plugin.Command
import net.kyori.adventure.title.Title as AdventureTitle

abstract class BaseAlertCommand(protected val plugin: CorePlugin, cmdName: String, permission: String, vararg aliases: String) : Command(cmdName, permission, *aliases) {

    fun getAdventureTitle(subTitle: Component): AdventureTitle {
        return AdventureTitle.title(
            ALERT_TEXT,
            subTitle
        )
    }

    companion object {
        val ALERT_TEXT = Component.text("Alert", NamedTextColor.RED)

        val ADVENTURE_ALERT_PREFIX = Component.text("[", NamedTextColor.DARK_GRAY)
            .append(ALERT_TEXT)
            .append(Component.text("]"))
            .append(Component.text(" ", NamedTextColor.WHITE))
    }

}

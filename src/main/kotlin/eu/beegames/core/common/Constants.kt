package eu.beegames.core.common

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent

class Constants {
    companion object {
        val PREFIX = TextComponent("[").apply {
            color = ChatColor.DARK_GRAY
            addExtra(TextComponent("Vcela").apply {
                color = ChatColor.GOLD
            })
            addExtra(TextComponent("]").apply {
                color = ChatColor.DARK_GRAY
            })
            addExtra(TextComponent(" ").apply {
                color = ChatColor.RESET
            })
        }
        const val PluginMessagingChannel = "beegames_core:pmc"
    }
}
package eu.beegames.core.common

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent

class Constants {
    companion object {
        val ADVENTURE_PREFIX = Component.text("[", NamedTextColor.DARK_GRAY)
            .append(Component.text("Vcela", NamedTextColor.GOLD))
            .append(Component.text("]", NamedTextColor.DARK_GRAY))
            .append(Component.text(" ", NamedTextColor.WHITE))

        const val PluginMessagingChannel = "beegames_core:pmc"
    }

    object Permissions {
        const val BypassGeoIP = "eu.beegames.core.bypass_geoip"
        const val SendAlert = "eu.beegames.core.send_alert"
        const val MOTDReload = "eu.beegames.core.motd_reload"
        const val SendBugReport = "eu.beegames.core.send_bug_report"
        const val SendReport = "eu.beegames.core.send_report"
    }
}
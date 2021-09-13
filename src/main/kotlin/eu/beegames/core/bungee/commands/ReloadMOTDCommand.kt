package eu.beegames.core.bungee.commands

import eu.beegames.core.bungee.CorePlugin
import eu.beegames.core.common.Constants
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.plugin.Command

class ReloadMOTDCommand(private val plugin: CorePlugin) : Command("motd_reload", Constants.Permissions.MOTDReload) {
    override fun execute(sender: CommandSender, args: Array<out String>) {
        plugin.reloadMOTDFiles()
        plugin.adventure.sender(sender).sendMessage(
            Constants.ADVENTURE_PREFIX
                .append(Component.text("Konfigurace MOTD byla uspesne prenactena.", NamedTextColor.GREEN))
        )
    }
}
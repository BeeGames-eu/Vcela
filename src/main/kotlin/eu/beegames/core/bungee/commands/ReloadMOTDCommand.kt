package eu.beegames.core.bungee.commands

import eu.beegames.core.bungee.CorePlugin
import eu.beegames.core.common.Constants
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.plugin.Command

class ReloadMOTDCommand(private val plugin: CorePlugin) : Command("motd_reload", "eu.beegames.core.motd_reload") {
    override fun execute(sender: CommandSender, args: Array<out String>) {
        plugin.reloadMOTDFiles()
        sender.sendMessage(Constants.PREFIX.duplicate(),
            TextComponent("Konfigurace MOTD byla uspesne prenactena.").apply {
                color = ChatColor.GREEN
            }
        )
    }

}
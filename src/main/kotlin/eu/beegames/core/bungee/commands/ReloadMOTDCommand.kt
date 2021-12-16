/**
 * Copyright (C) 2021 TTtie (BeeGames.eu)
 * 
 * This file is part of Včela.
 * 
 * Včela is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Včela is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with Včela.  If not, see <http://www.gnu.org/licenses/>.
 */
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

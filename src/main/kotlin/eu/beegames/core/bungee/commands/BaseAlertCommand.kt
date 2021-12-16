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

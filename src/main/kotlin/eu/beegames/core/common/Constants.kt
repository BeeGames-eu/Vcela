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
package eu.beegames.core.common

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

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
        const val BaseAnnoyancePermission = "eu.beegames.core.annoyances"
    }
}

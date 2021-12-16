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
package eu.beegames.core.bungee.annoyances

import eu.beegames.core.bungee.CorePlugin
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.config.Configuration
import java.util.concurrent.CompletableFuture

abstract class Annoyance(protected val plugin: CorePlugin) {
    var isEnabled: Boolean = false

    abstract fun enable(conf: Configuration): Boolean
    abstract fun disable()

    abstract fun disableForPlayer(player: ProxiedPlayer): CompletableFuture<Void>
    abstract fun enableForPlayer(player: ProxiedPlayer): CompletableFuture<Void>
}

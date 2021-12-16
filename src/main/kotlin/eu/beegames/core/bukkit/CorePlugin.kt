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
package eu.beegames.core.bukkit

import eu.beegames.core.common.Constants
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.util.*

@Suppress("unused")
class CorePlugin : JavaPlugin(), PluginMessageListener, Listener {

    override fun onEnable() {
        // No messages need to be handled right now
        // server.messenger.registerIncomingPluginChannel(this, Constants.PluginMessagingChannel, this)
        // server.messenger.registerOutgoingPluginChannel(this, Constants.PluginMessagingChannel)

        // No listeners need to be handled right now
        // server.pluginManager.registerEvents(this, this)

        logger.info("Vcela was loaded")
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        // No messages need to be handled right now
        return

        /*if (channel != Constants.PluginMessagingChannel) return
        ByteArrayInputStream(message).use {
            DataInputStream(it).use {
                // handle any incoming messages as needed
            }
        }*/
    }
}

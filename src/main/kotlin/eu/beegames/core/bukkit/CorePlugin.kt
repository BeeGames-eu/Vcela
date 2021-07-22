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
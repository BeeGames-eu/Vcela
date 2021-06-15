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
    private val pendingTeleports = HashMap<String, String>()

    override fun onEnable() {
        server.messenger.registerIncomingPluginChannel(this, Constants.PluginMessagingChannel, this)
        server.messenger.registerOutgoingPluginChannel(this, Constants.PluginMessagingChannel)

        server.pluginManager.registerEvents(this, this)

        logger.info("Vcela was loaded")
    }

    @EventHandler
    fun on(ev: PlayerJoinEvent) {
        val id = synchronized(pendingTeleports) {
            pendingTeleports[ev.player.uniqueId.toString()]
        }
        if (id != null) {
            logger.info("${ev.player.name} should be teleported to player with ID ${id}")
            val toPlayer = server.getPlayer(UUID.fromString(id))
            if (toPlayer?.isOnline == true) {
                ev.player.teleport(toPlayer, PlayerTeleportEvent.TeleportCause.PLUGIN)
                ev.player.spigot().sendMessage(Constants.PREFIX.duplicate().apply {
                    addExtra(TextComponent("Byl jsi teleportovan k hraci ").apply {
                        color = ChatColor.GREEN
                        addExtra(TextComponent(toPlayer.name).apply {
                            color = ChatColor.YELLOW
                        })
                        addExtra(TextComponent(".").apply {
                            color = ChatColor.GREEN
                        })
                    })
                })
            } else {
                ev.player.spigot().sendMessage(Constants.PREFIX.duplicate().apply {
                    addExtra(TextComponent("Teleportace selhala, hrac stihl opustit server drive, nez jsi byl teleportovan.").apply {
                        color = ChatColor.RED
                    })
                })
            }

            synchronized(pendingTeleports) {
                pendingTeleports.remove(id)
            }
        }
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != Constants.PluginMessagingChannel) return

        val bis = DataInputStream(ByteArrayInputStream(message))

        when(bis.readUTF()) {
            "TeleportToPlayer" -> {
                val from = UUID.fromString(bis.readUTF())
                val to = UUID.fromString(bis.readUTF())

                val fromPlayer: Player? = server.getPlayer(from)
                val toPlayer: Player? = server.getPlayer(to)

                if (fromPlayer?.isOnline == true) {
                    if (toPlayer?.isOnline == true) {
                        fromPlayer.teleport(toPlayer.location, PlayerTeleportEvent.TeleportCause.PLUGIN)
                        fromPlayer.spigot().sendMessage(Constants.PREFIX.duplicate().apply {
                            addExtra(TextComponent("Byl jsi teleportovan k hraci ").apply {
                                color = ChatColor.GREEN
                                addExtra(TextComponent(toPlayer.name).apply {
                                    color = ChatColor.YELLOW
                                })
                                addExtra(TextComponent(".").apply {
                                    color = ChatColor.GREEN
                                })
                            })
                        })
                    }
                } else {
                    synchronized(pendingTeleports) {
                        pendingTeleports[from.toString()] = to.toString()
                    }
                }
            }
        }
    }
}
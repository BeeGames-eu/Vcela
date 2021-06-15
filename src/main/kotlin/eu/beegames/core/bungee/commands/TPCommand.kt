package eu.beegames.core.bungee.commands

import eu.beegames.core.bungee.CorePlugin
import eu.beegames.core.common.Constants
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.TabExecutor
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

class TPCommand(private val plugin: CorePlugin) : Command("xtp", "eu.beegames.core.tp"), TabExecutor {
    override fun execute(sender: CommandSender, args: Array<out String>) {
        if (sender !is ProxiedPlayer) return
        if (!sender.hasPermission("eu.beegames.core.tp")) {
            sender.sendMessage(Constants.PREFIX.duplicate().apply {
                addExtra(TextComponent("Na toto nemas opravneni!").apply {
                    color = ChatColor.RED
                })
            })
            return
        }

        plugin.logger.info("Permissions of ${sender.name}: ${sender.permissions.joinToString(", ")}")

        if (args.isEmpty()) {
            sender.sendMessage(Constants.PREFIX.duplicate().apply {
                addExtra(TextComponent("Specifikuj hrace, ke kteremu se chces teleportovat!").apply {
                    color = ChatColor.RED
                })
            })
            return
        }

        val playerName = args[0]
        val playerToTpTo = plugin.proxy.getPlayer(playerName)
        if (playerToTpTo == null) {
            sender.sendMessage(Constants.PREFIX.duplicate().apply {
                addExtra(TextComponent("Tento hrac nebyl nalezen!").apply {
                    color = ChatColor.RED
                })
            })
            return
        }

        /*if (args.size < 2) {
            sender.sendMessage(Constants.PREFIX.duplicate().apply {
                addExtra(TextComponent("Vyber, jak se teleportovat k hraci $playerName:").apply {
                    color = ChatColor.YELLOW
                })
            })
            sender.sendMessage(TextComponent("- Prima teleportace").apply {
                clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "xtp $playerName direct")
                color = ChatColor.YELLOW
            })
            sender.sendMessage(TextComponent("- Sledovat hrace").apply {
                clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "xtp $playerName spect")
                color = ChatColor.YELLOW
            })
        }*/

        val serverToTpTo = playerToTpTo.server

        val messageData = ByteArrayOutputStream().also {
            val bos = DataOutputStream(it)

            with(bos) {
                writeUTF("TeleportToPlayer")
                writeUTF(sender.uniqueId.toString())
                writeUTF(playerToTpTo.uniqueId.toString())
            }
        }.toByteArray()

        if (serverToTpTo.info == sender.server.info) {
            serverToTpTo.sendData(Constants.PluginMessagingChannel, messageData)
        } else {
            sender.connect(serverToTpTo.info, { success, _ ->
                if (!success) {
                    sender.sendMessage(Constants.PREFIX.duplicate().apply {
                        addExtra(TextComponent("Pokus o teleportaci se nezdaril. Zkus to znovu a pozdeji").apply {
                            color = ChatColor.RED
                        })
                    })
                } else {

                    serverToTpTo.sendData(Constants.PluginMessagingChannel, messageData)
                }
            }, ServerConnectEvent.Reason.COMMAND)
        }
    }

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): MutableIterable<String> {
        if (args.isEmpty() || args.size > 1) {
            return mutableSetOf()
        }

        val search = args[0].toLowerCase()
        return plugin.proxy.players.filter {
            it.name.toLowerCase().startsWith(search)
        }.map {
            it.name
        }.toMutableList()
    }

}
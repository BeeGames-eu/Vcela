package eu.beegames.core.bungee.commands

import eu.beegames.core.bungee.CorePlugin
import eu.beegames.core.common.Constants
import eu.beegames.core.common.db.models.DiscordLinkData
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import org.ktorm.dsl.*
import java.time.LocalDateTime
import java.util.*

// TODO(TTtie): maybe link this to an OAuth2-based API instead? We've got a web server available at the mc.beegames.eu subdomain
class DiscordLinkCommand(private val plugin: CorePlugin) : Command("dlink", "eu.beegames.core.discord-link") {
    private val tc = TextComponent("Prosím, dokonči propojení svého Discord účtu pomocí příkazu ").apply {
        color = ChatColor.RED
    }

    private val errorTc = TextComponent("Neco se porouchalo a nedá se nám vygenerovat kód pro propojení po 5 pokusech. Zkus to jindy.").apply {
        color = ChatColor.RED
    }
    
    private val alreadyConnectedTc = TextComponent("Již máš propojený účet!").apply {
        color = ChatColor.RED
    }

    private val r = Random()

    private val commandTc = TextComponent("!!verify ").apply {
        color = ChatColor.GOLD
    }

    override fun execute(sender: CommandSender, args: Array<out String>) {
        if (sender !is ProxiedPlayer) return
        if (!sender.hasPermission("eu.beegames.core.discord-link")) {
            sender.sendMessage(Constants.PREFIX.duplicate().apply {
                addExtra(TextComponent("Na toto nemas opravneni!").apply {
                    color = ChatColor.RED
                })
            })
            return
        }

        if (args.isEmpty()) {
            val ts = LocalDateTime.now()
            val uuid = sender.uniqueId.toString()

            val ld = plugin.db.from(DiscordLinkData)
                .select(DiscordLinkData.sessionID)
                .where { (DiscordLinkData.uuid eq uuid) and
                        (DiscordLinkData.createdAt greater ts.minusMinutes(15)) and
                        (DiscordLinkData.isLinked notEq true)}
            if (ld.totalRecords == 0) {
                var sessID = generateSessionID()

                for (i in 0..6) {
                    if (plugin.db.from(DiscordLinkData)
                            .select(DiscordLinkData.isLinked) // transfer as less data as possible
                            .where { DiscordLinkData.sessionID eq sessID }.totalRecords != 0) {
                        if (i == 5) {
                            sender.sendMessage(errorTc.duplicate())
                            return
                        } else {
                            sessID = generateSessionID()
                        }
                    } else {
                        break
                    }
                }

                plugin.db.insert(DiscordLinkData) {
                    set(it.createdAt, LocalDateTime.now())
                    set(it.discordID, null)
                    set(it.isLinked, false)
                    set(it.playerName, sender.name)
                    set(it.uuid, uuid)
                    set(it.sessionID, sessID)
                }

                sender.sendMessage(Constants.PREFIX.duplicate(), tc.duplicate().apply {
                    color = ChatColor.GREEN
                }, commandTc.duplicate().apply {
                    addExtra(sessID)
                    clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "!!verify $sessID")
                })
            } else {
                val rows = ld.rowSet
                rows.next()

                val sessID = rows[DiscordLinkData.sessionID]

                rows.close()
                
                if (sessID == null) {
                    sender.sendMessage(Constants.PREFIX.duplicate(), alreadyConnectedTc.duplicate())
                    return
                }

                sender.sendMessage(Constants.PREFIX.duplicate(), tc.duplicate(), commandTc.duplicate().apply {
                    addExtra(sessID)
                    clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "!!verify $sessID")
                })
            }
        }
    }

    private fun generateSessionID(): String {
        return r.ints(48, 123)
            .filter {
                i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)
            }
            .limit(32)
            .collect(::StringBuilder, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString()
    }
}

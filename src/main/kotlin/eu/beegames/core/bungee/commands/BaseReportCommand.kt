package eu.beegames.core.bungee.commands

import club.minnced.discord.webhook.receive.ReadonlyMessage
import club.minnced.discord.webhook.send.WebhookEmbed
import eu.beegames.core.bungee.CorePlugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.Plugin
import java.util.concurrent.CompletableFuture

abstract class BaseReportCommand(protected val plugin: CorePlugin, cmdName: String, permission: String, vararg aliases: String) : Command(cmdName, permission, *aliases)  {
    fun sendReportEmbeds(embeds: List<WebhookEmbed>, isBug: Boolean): CompletableFuture<ReadonlyMessage> {
        return if (isBug) {
            plugin.discordBugHookClient
        } else {
            plugin.discordReportHookClient
        }.send(embeds)
    }

    protected fun _getDiscordLinkUrl(it: ReadonlyMessage): String {
        return "https://discord.com/channels/824022859126931496/${it.channelId}/${it.id}"
    }


    companion object {
        val ADVENTURE_REPORTS_PREFIX = Component.text("[", NamedTextColor.DARK_GRAY)
            .append(Component.text("Reports", NamedTextColor.RED))
            .append(Component.text("]"))
            .append(Component.text(" ", NamedTextColor.WHITE))
    }
}
package eu.beegames.core.bungee.commands

import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import eu.beegames.core.bungee.CorePlugin
import eu.beegames.core.common.Constants
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.time.ZonedDateTime

class ReportBugCommand(plugin: CorePlugin): BaseReportCommand(plugin, "bug", Constants.Permissions.SendBugReport, "bugreport") {
    override fun execute(sender: CommandSender, args: Array<out String>) {
        if (sender !is ProxiedPlayer) {
            plugin.adventure.sender(sender).sendMessage(
                Constants.ADVENTURE_PREFIX
                    .append(Component.text("Tento příkaz nelze použít z konzole!", NamedTextColor.RED))
            )

            return
        }

        if (args.isEmpty()) {
            plugin.adventure.sender(sender).sendMessage(
                Constants.ADVENTURE_PREFIX
                    .append(Component.text("Použití: /bug <...text>", NamedTextColor.RED))
            )

            return
        }

        val description = args.joinToString(" ").apply {
            substring(0..kotlin.math.min(256, length - 1))
        }

        sendReportEmbeds(
            listOf(
                WebhookEmbedBuilder()
                    .setTitle(WebhookEmbed.EmbedTitle("\uD83D\uDC1B Nahlášení bugu", null))
                    .setAuthor(WebhookEmbed.EmbedAuthor(
                        sender.name,
                        "https://skin.beegames.eu/resources/server/skinRender.php?vr=0&hr=0&headOnly=true&ratio=20&user=${sender.name}",
                        null))
                    .addField(WebhookEmbed.EmbedField(false, "Server", "`${sender.server.info.name}`"))
                    .addField(WebhookEmbed.EmbedField(false, "Popis", description))
                    .setTimestamp(ZonedDateTime.now())
                    .setColor(0xFFFF00)
                    .build()
            ),
            true
        ).thenAcceptAsync {
            plugin.adventure.permission("eu.beegames.core.notify_bugs")
                .sendMessage(
                    ADVENTURE_REPORTS_PREFIX
                        .append(Component.text("Nové nahlášení bugu od hráče ", NamedTextColor.GOLD))
                        .append(Component.text(sender.name, NamedTextColor.YELLOW))
                        .append(Component.text(" - "))
                        .append(
                            Component.text("viz Discord", TextColor.color(0x7289da))
                                .clickEvent(ClickEvent.openUrl(getDiscordMessageLinkURL(it)))
                        )
                )

            plugin.adventure.sender(sender)
                .sendMessage(ADVENTURE_REPORTS_PREFIX
                    .append(Component.text("Report byl odeslán. Děkujeme za nahlášení!", NamedTextColor.GREEN)))
        }.exceptionally {
            plugin.adventure.sender(sender)
                .sendMessage(ADVENTURE_REPORTS_PREFIX
                    .append(Component.text("Nastala chyba při odesílání reportu, zkus to znovu, nebo nás kontaktuj ručně na ", NamedTextColor.RED)
                        .append(Component.text("Discordu", TextColor.color(0x7289da))
                            .clickEvent(ClickEvent.openUrl("https://beegames.eu/discord")))
                        .append(Component.text(".")))
                )

            it.printStackTrace()
            null // WTF: kotlin infers Void! for this
        }


    }

}
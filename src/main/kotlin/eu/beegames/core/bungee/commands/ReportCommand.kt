package eu.beegames.core.bungee.commands

import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import eu.beegames.core.bungee.CorePlugin
import eu.beegames.core.common.Constants
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.time.ZonedDateTime

class ReportCommand(plugin: CorePlugin): BaseReportCommand(plugin, "report", Constants.Permissions.SendReport) {
    override fun execute(sender: CommandSender, args: Array<out String>) {
        if (sender !is ProxiedPlayer) {
            plugin.adventure.sender(sender).sendMessage(
                Constants.ADVENTURE_PREFIX
                    .append(Component.text("Tento příkaz nelze použít z konzole!", NamedTextColor.RED))
            )

            return
        }

        if (args.size < 2) {
            plugin.adventure.sender(sender).sendMessage(
                Constants.ADVENTURE_PREFIX
                    .append(Component.text("Použití: /reportbug <...text>", NamedTextColor.RED))
            )

            return
        }

        val reportPlayer = plugin.proxy.getPlayer(args[0]) ?: return plugin.adventure.sender(sender)
            .sendMessage(
                ADVENTURE_REPORTS_PREFIX
                    .append(Component.text("Tento hráč se nenachází na serveru!", NamedTextColor.RED))
            )

        if (reportPlayer == sender) {
            plugin.adventure.sender(sender)
                .sendMessage(
                    ADVENTURE_REPORTS_PREFIX
                        .append(Component.text("Proč bys nahlašoval sám sebe? ;(", NamedTextColor.RED))
                )
            return
        }

        val reason = args.drop(1).joinToString(" ").apply {
            substring(0..kotlin.math.min(256, length - 1))
        }

        sendReportEmbeds(
            listOf(
                WebhookEmbedBuilder()
                    .setTitle(WebhookEmbed.EmbedTitle("\uD83D\uDC65 Nahlášení hráče", null))
                    .setAuthor(
                        WebhookEmbed.EmbedAuthor(
                            "${sender.name} (ze serveru ${sender.server.info.name})",
                        "https://skin.beegames.eu/resources/server/skinRender.php?vr=0&hr=0&headOnly=true&ratio=20&user=${sender.name}",
                        null))
                    .addField(WebhookEmbed.EmbedField(false, "Hráč", "`${reportPlayer.name}`"))
                    .addField(WebhookEmbed.EmbedField(false, "Server", "`${reportPlayer.server.info.name}`"))
                    .addField(WebhookEmbed.EmbedField(false, "Důvod", reason))
                    .setTimestamp(ZonedDateTime.now())
                    .setColor(0xFFFF00)
                    .build()
            ),
            false
        ).thenAcceptAsync {
            plugin.adventure.permission("eu.beegames.core.notify_reports")
                .sendMessage(
                    ADVENTURE_REPORTS_PREFIX
                        .append(Component.text(reportPlayer.name, NamedTextColor.RED))
                        .append(Component.text(" "))
                        .append(Component.text("(", NamedTextColor.DARK_GRAY)
                            .append(Component.text(reportPlayer.server.info.name, NamedTextColor.GOLD))
                                .clickEvent(ClickEvent.suggestCommand("/server ${reportPlayer.server.info.name}"))
                            .append(Component.text(")", NamedTextColor.DARK_GRAY)))
                        .append(Component.text(" - "))
                        .append(Component.text(reason, NamedTextColor.WHITE)
                            .decorate(TextDecoration.ITALIC))
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

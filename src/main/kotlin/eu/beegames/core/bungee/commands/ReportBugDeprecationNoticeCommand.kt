package eu.beegames.core.bungee.commands

import eu.beegames.core.bungee.CorePlugin
import eu.beegames.core.common.Constants
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer

class ReportBugDeprecationNoticeCommand(plugin: CorePlugin): BaseReportCommand(plugin, "reportbug", Constants.Permissions.SendBugReport) {
    override fun execute(sender: CommandSender, args: Array<out String>) {
        if (sender !is ProxiedPlayer) {
            plugin.adventure.sender(sender).sendMessage(
                Constants.ADVENTURE_PREFIX
                    .append(Component.text("Tento příkaz nelze použít z konzole!", NamedTextColor.RED))
            )

            return
        }

        plugin.adventure.sender(sender).sendMessage(
            Constants.ADVENTURE_PREFIX
                .append(
                    Component.text("Nemyslel jsi náhodou příkaz ", NamedTextColor.RED)
                    .append(Component.text("/bug", NamedTextColor.GOLD)
                        .clickEvent(ClickEvent.suggestCommand("/bug ${args.joinToString(" ")}")))
                    .append(Component.text("?"))
                )
        )

    }

}

package eu.beegames.core.bungee.commands

import eu.beegames.core.bungee.CorePlugin
import eu.beegames.core.common.Constants
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.CommandSender

class GlobalAlertCommand(plugin: CorePlugin) : BaseAlertCommand(plugin, "alert", "eu.beegames.core.send_alert", "galert", "send_alert") {
    override fun execute(sender: CommandSender, args: Array<out String>) {
        if (args.isEmpty()) {
            plugin.adventure.sender(sender).sendMessage(
                Constants.ADVENTURE_PREFIX
                    .append(Component.text("Použití: /alert <...text>", NamedTextColor.RED))
            )
            return
        }

        val alertComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(args.joinToString(" "))
            .colorIfAbsent(NamedTextColor.WHITE)
        val title = getAdventureTitle(alertComponent)

        plugin.adventure.players().apply {
            sendMessage(ADVENTURE_ALERT_PREFIX
                .append(alertComponent))
            showTitle(title)
        }
    }
}
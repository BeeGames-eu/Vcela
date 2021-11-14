package eu.beegames.core.bungee.commands

import eu.beegames.core.bungee.CorePlugin
import eu.beegames.core.bungee.annoyances.AnnoyanceManager
import eu.beegames.core.common.Constants
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

class AnnoyancesCommand(private val plugin: CorePlugin) : Command(
    "annoyances",
    Constants.Permissions.BaseAnnoyancePermission, "ann"
) {
    override fun execute(sender: CommandSender, args: Array<out String>) {
        if (args.isEmpty()) {
            plugin.adventure.sender(sender)
                .sendMessage(
                    Constants.ADVENTURE_PREFIX
                        .append(Component.text("Použití: /annoyances <on|off> <annoyanceName>", NamedTextColor.RED))
                )

            return
        }

        when (args[0]) {
            "enable" -> {
                if (!sender.hasPermission("${Constants.Permissions.BaseAnnoyancePermission}.manage_annoyances")) return
                if (args.size < 2) {
                    plugin.adventure.sender(sender)
                        .sendMessage(
                            Constants.ADVENTURE_PREFIX
                                .append(
                                    Component.text(
                                        "Použití: /annoyances enable <annoyanceName>",
                                        NamedTextColor.RED
                                    )
                                )
                        )

                    return
                }

                val annoyanceName = args[1]

                try {
                    plugin.annoyances.enableAnnoyance(annoyanceName)

                    plugin.adventure.sender(sender)
                        .sendMessage(
                            Constants.ADVENTURE_PREFIX
                                .append(
                                    Component.text(
                                        "Rušivý element $annoyanceName byl zapnut.",
                                        NamedTextColor.GREEN
                                    )
                                )
                        )
                } catch (ex: AnnoyanceManager.Exception) {
                    plugin.adventure.sender(sender)
                        .sendMessage(
                            Constants.ADVENTURE_PREFIX
                                .append(Component.text("Tento rušivý element neexistuje!", NamedTextColor.RED))
                        )
                } catch (ex: Exception) {
                    plugin.adventure.sender(sender)
                        .sendMessage(
                            Constants.ADVENTURE_PREFIX
                                .append(
                                    Component.text(
                                        "Nastala chyba při zapínaní rušivého elementu, mrkni do konzole.",
                                        NamedTextColor.RED
                                    )
                                )
                        )

                    ex.printStackTrace()
                }
            }
            "disable" -> {
                if (!sender.hasPermission("${Constants.Permissions.BaseAnnoyancePermission}.manage_annoyances")) return
                if (args.size < 2) {
                    plugin.adventure.sender(sender)
                        .sendMessage(
                            Constants.ADVENTURE_PREFIX
                                .append(
                                    Component.text(
                                        "Použití: /annoyances disable <annoyanceName>",
                                        NamedTextColor.RED
                                    )
                                )
                        )

                    return
                }

                val annoyanceName = args[1]

                try {
                    plugin.annoyances.disableAnnoyance(annoyanceName)

                    plugin.adventure.sender(sender)
                        .sendMessage(
                            Constants.ADVENTURE_PREFIX
                                .append(
                                    Component.text(
                                        "Rušivý element $annoyanceName byl vypnut.",
                                        NamedTextColor.GREEN
                                    )
                                )
                        )
                } catch (ex: AnnoyanceManager.Exception) {
                    plugin.adventure.sender(sender)
                        .sendMessage(
                            Constants.ADVENTURE_PREFIX
                                .append(Component.text("Tento rušivý element neexistuje!", NamedTextColor.RED))
                        )
                } catch (ex: Exception) {
                    plugin.adventure.sender(sender)
                        .sendMessage(
                            Constants.ADVENTURE_PREFIX
                                .append(
                                    Component.text(
                                        "Nastala chyba při vypínání rušivého elementu, mrkni do konzole.",
                                        NamedTextColor.RED
                                    )
                                )
                        )

                    ex.printStackTrace()
                }
            }
            "on" -> {
                if (sender !is ProxiedPlayer) {
                    plugin.adventure.sender(sender)
                        .sendMessage(
                            Constants.ADVENTURE_PREFIX
                                .append(
                                    Component.text(
                                        "Tento příkaz nemůže být spuštěn z konzole!",
                                        NamedTextColor.RED
                                    )
                                )
                        )

                    return
                }
                if (args.size < 2) {
                    plugin.adventure.sender(sender)
                        .sendMessage(
                            Constants.ADVENTURE_PREFIX
                                .append(Component.text("Použití: /annoyances on <annoyanceName>", NamedTextColor.RED))
                        )

                    return
                }

                val annoyanceName = args[1]

                plugin.annoyances.enableAnnoyanceForPlayer(annoyanceName, sender)
                    .thenAcceptAsync {
                        plugin.adventure.sender(sender)
                            .sendMessage(
                                Constants.ADVENTURE_PREFIX
                                    .append(
                                        Component.text(
                                            "Rušivý element $annoyanceName byl pro tebe zapnut.",
                                            NamedTextColor.GREEN
                                        )
                                    )
                            )
                    }
                    .exceptionally {
                        if (it.cause is AnnoyanceManager.Exception) {
                            plugin.adventure.sender(sender)
                                .sendMessage(
                                    Constants.ADVENTURE_PREFIX
                                        .append(Component.text("Tento rušivý element neexistuje!", NamedTextColor.RED))
                                )
                        } else {
                            plugin.adventure.sender(sender)
                                .sendMessage(
                                    Constants.ADVENTURE_PREFIX
                                        .append(
                                            Component.text(
                                                "Nastala chyba při zapínání rušivého elementu, kontaktuj nás na Discordu.",
                                                NamedTextColor.RED
                                            )
                                        )
                                )

                            it.printStackTrace()
                        }

                        null
                    }
            }
            "off" -> {
                if (sender !is ProxiedPlayer) {
                    plugin.adventure.sender(sender)
                        .sendMessage(
                            Constants.ADVENTURE_PREFIX
                                .append(
                                    Component.text(
                                        "Tento příkaz nemůže být spuštěn z konzole!",
                                        NamedTextColor.RED
                                    )
                                )
                        )

                    return
                }
                if (args.size < 2) {
                    plugin.adventure.sender(sender)
                        .sendMessage(
                            Constants.ADVENTURE_PREFIX
                                .append(Component.text("Použití: /annoyances off <annoyanceName>", NamedTextColor.RED))
                        )

                    return
                }

                val annoyanceName = args[1]

                plugin.annoyances.disableAnnoyanceForPlayer(annoyanceName, sender)
                    .thenAcceptAsync {
                        plugin.adventure.sender(sender)
                            .sendMessage(
                                Constants.ADVENTURE_PREFIX
                                    .append(
                                        Component.text(
                                            "Rušivý element $annoyanceName byl pro tebe vypnut.",
                                            NamedTextColor.GREEN
                                        )
                                    )
                            )
                    }
                    .exceptionally {
                        if (it.cause is AnnoyanceManager.Exception) {
                            plugin.adventure.sender(sender)
                                .sendMessage(
                                    Constants.ADVENTURE_PREFIX
                                        .append(Component.text("Tento rušivý element neexistuje!", NamedTextColor.RED))
                                )
                        } else {
                            plugin.adventure.sender(sender)
                                .sendMessage(
                                    Constants.ADVENTURE_PREFIX
                                        .append(
                                            Component.text(
                                                "Nastala chyba při vypínání rušivého elementu, kontaktuj nás na Discordu.",
                                                NamedTextColor.RED
                                            )
                                        )
                                )

                            it.printStackTrace()
                        }

                        null
                    }
            }
        }
    }

}
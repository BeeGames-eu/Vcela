package eu.beegames.core.bungee

import eu.beegames.core.bungee.commands.AlertCommand
import eu.beegames.core.bungee.commands.TPCommand
import net.md_5.bungee.api.plugin.Plugin

@Suppress("unused")
class CorePlugin : Plugin() {
    override fun onEnable() {
        proxy.pluginManager.registerCommand(this, TPCommand(this))
        proxy.pluginManager.registerCommand(this, AlertCommand(this))

        logger.info("Vcela was loaded.")
    }
}
package eu.beegames.core.bungee

import eu.beegames.core.bungee.commands.GlobalAlertCommand
import eu.beegames.core.bungee.commands.LocalAlertCommand
import eu.beegames.core.bungee.commands.TPCommand
import eu.beegames.core.common.Constants
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler

@Suppress("unused")
class CorePlugin : Plugin(), Listener {
    override fun onEnable() {
        proxy.pluginManager.registerCommand(this, TPCommand(this))
        proxy.pluginManager.registerCommand(this, GlobalAlertCommand(this))
        proxy.pluginManager.registerCommand(this, LocalAlertCommand(this))

        proxy.pluginManager.registerListener(this, this)

        logger.info("Vcela was loaded.")
    }

    @EventHandler
    fun on(ev: PluginMessageEvent) {
        if (ev.tag == Constants.PluginMessagingChannel) {
            if (ev.sender is ProxiedPlayer) {
                // Prevent users from sending their own commands (assumes the knowledge of the plugin channel)
                ev.isCancelled = true
                return
            }
            if (ev.receiver is ProxiedPlayer) {
                // Catch the plugin messages sent into this plugin
                handlePluginMessage(ev.data)
                // Prevent leaking the plugin channel messages to a player
                ev.isCancelled = true
                return
            }
        }
    }

    private fun handlePluginMessage(msg: ByteArray) {
        // No-op: might be handled soon.
    }
}
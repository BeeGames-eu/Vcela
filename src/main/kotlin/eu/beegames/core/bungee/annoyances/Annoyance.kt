package eu.beegames.core.bungee.annoyances

import eu.beegames.core.bungee.CorePlugin
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.config.Configuration
import java.util.concurrent.CompletableFuture

abstract class Annoyance(protected val plugin: CorePlugin) {
    var isEnabled: Boolean = false

    abstract fun enable(conf: Configuration): Boolean
    abstract fun disable()

    abstract fun disableForPlayer(player: ProxiedPlayer): CompletableFuture<Void>
    abstract fun enableForPlayer(player: ProxiedPlayer): CompletableFuture<Void>
}
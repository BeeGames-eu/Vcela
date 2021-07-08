package eu.beegames.core.bungee

import eu.beegames.core.bungee.commands.*
import eu.beegames.core.common.Constants
import eu.beegames.core.common.db.DatabaseGetter
import eu.beegames.core.common.db.models.DiscordLinkData
import eu.beegames.core.common.db.models.PlayerStatistic
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import net.md_5.bungee.event.EventHandler
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("unused")
class CorePlugin : Plugin(), Listener {
    private val quartersOfHour = arrayOf(0, 15, 30, 45)

    lateinit var db: Database
    private lateinit var config: Configuration


    override fun onEnable() {
        val plm = proxy.pluginManager

        plm.registerCommand(this, TPCommand(this))
        plm.registerCommand(this, GlobalAlertCommand(this))
        plm.registerCommand(this, LocalAlertCommand(this))
        plm.registerCommand(this, DiscordLinkCommand(this))

        plm.registerListener(this, this)

        if (!dataFolder.exists()) {
            dataFolder.mkdir()
        }

        val f = File(dataFolder, "config.yml")

        if (!f.exists()) {
            try {
                val ist = getResourceAsStream("bungee_config.yml")
                Files.copy(ist, f.toPath())
            } catch (ex: IOException) {
                logger.severe("Couldn't copy default configuration:")
                logger.severe(ex.stackTraceToString())
            }
        }

        config = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(File(dataFolder, "config.yml"))

        db = DatabaseGetter.getInstance(
            config.getInt("db.max_pool_size", 10),
            config.getString("db.host"),
            config.getString("db.port") ?: config.getInt("db.port", 3306).toString(),
            config.getString("db.name"),
            config.getString("db.username"),
            config.getString("db.password")
        )

        proxy.scheduler.schedule(this, {
            val cal = Calendar.getInstance()

            val min = cal.get(Calendar.MINUTE)
            val sec = cal.get(Calendar.SECOND)

            if (quartersOfHour.contains(min) && sec == 0) {
                val ts = LocalDateTime.now()
                db.insert(PlayerStatistic) {
                    set(it.timestamp, ts)
                    set(it.playerCount, proxy.players.size)
                }
                db.delete(PlayerStatistic) {
                    it.timestamp less ts.minusMonths(1L)
                }
                db.delete(DiscordLinkData) {
                    (it.createdAt less ts.minusMinutes(15)) and (it.isLinked eq false)
                }
            }
        }, 0, 1, TimeUnit.SECONDS)

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

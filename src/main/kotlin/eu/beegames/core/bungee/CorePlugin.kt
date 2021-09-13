package eu.beegames.core.bungee

import eu.beegames.core.bungee.commands.GlobalAlertCommand
import eu.beegames.core.bungee.commands.LocalAlertCommand
import eu.beegames.core.bungee.commands.ReloadMOTDCommand
import eu.beegames.core.bungee.commands.ServerSpecificAlertCommand
import eu.beegames.core.bungee.listener.CoreListener
import eu.beegames.core.common.Constants
import eu.beegames.core.common.db.DatabaseGetter
import eu.beegames.core.common.db.models.PlayerStatistic
import net.kyori.adventure.platform.bungeecord.BungeeAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ServerPing
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.event.ProxyPingEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.protocol.ProtocolConstants
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.insert
import org.ktorm.dsl.less
import java.io.File
import java.nio.file.Files
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("unused")
class CorePlugin : Plugin(), Listener {
    private val quartersOfHour = arrayOf(0, 15, 30, 45)

    private lateinit var db: Database
    private lateinit var config: Configuration

    private var _adv: BungeeAudiences? = null
    val adventure: BungeeAudiences
        get() {
            return _adv ?: throw IllegalStateException("Audience is not available")
        }

    private lateinit var precompiledPlayerList: Array<out ServerPing.PlayerInfo>
    private lateinit var precompiledMotdText: BaseComponent
    private lateinit var precompiledMotdTextPre116: BaseComponent

    internal lateinit var whitelistedCountries: List<String>

    override fun onEnable() {
        _adv = BungeeAudiences.create(this)

        arrayOf(
            ::GlobalAlertCommand,
            ::LocalAlertCommand,
            ::ServerSpecificAlertCommand,
            ::ReloadMOTDCommand,
        ).forEach {
            proxy.pluginManager.registerCommand(this, it(this))
        }


        proxy.pluginManager.registerListener(this, this)
        proxy.pluginManager.registerListener(this, CoreListener(this))

        if (!dataFolder.exists()) {
            dataFolder.mkdir()
        }

        val f = ensureFile(dataFolder, "config.yml", "bungee_config.yml")
        config = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(f)

        whitelistedCountries = config.getStringList("allowed_countries")

        reloadMOTDFiles()

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
            }
        }, 0, 1, TimeUnit.SECONDS)

        logger.info("Vcela was loaded.")
    }

    override fun onDisable() {
        this._adv?.close()
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

    @EventHandler
    fun on(ev: ProxyPingEvent) {
        ev.response.apply {
            players.sample = precompiledPlayerList
            descriptionComponent = if (ev.connection.version >= ProtocolConstants.MINECRAFT_1_16) precompiledMotdText
                else precompiledMotdTextPre116
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handlePluginMessage(msg: ByteArray) {
        // No-op: might be handled soon.
    }

    fun reloadMOTDFiles() {
        val motdFile = ensureFile(dataFolder, "motd.txt")
        val playerListFile = ensureFile(dataFolder, "player_list.txt")

        val motdText = Component.text()
            .append(LegacyComponentSerializer.legacyAmpersand().deserialize(motdFile.readText()))
            .build()

        precompiledMotdText = BungeeComponentSerializer.get().serialize(motdText)[0]
        precompiledMotdTextPre116 = BungeeComponentSerializer.legacy().serialize(motdText)[0]

        playerListFile.useLines {
            precompiledPlayerList = it.take(64).map { str ->
                ServerPing.PlayerInfo(
                    ChatColor.translateAlternateColorCodes('&', str),
                    UUID.randomUUID()
                )
            }.toList().toTypedArray()
        }
    }

    private fun ensureFile(path: File, child: String, sourceFileName: String = child): File {
        val f = File(path, child)

        if (!f.exists()) {
            try {
                getResourceAsStream(sourceFileName)
                    .use {
                        Files.copy(it, f.toPath())
                    }
            } catch (ex: Exception) {
                logger.severe("Couldn't copy default configuration:")
                logger.severe(ex.stackTraceToString())
            }
        }

        return f
    }
}

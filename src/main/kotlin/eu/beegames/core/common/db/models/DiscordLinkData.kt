package eu.beegames.core.common.db.models

import org.ktorm.schema.*


object DiscordLinkData : Table<Nothing>("vcela_discord_link_data") {
    val uuid = varchar("uuid").primaryKey()
    val playerName = varchar("name")
    val discordID = varchar("discord_id")
    val sessionID = varchar("link_session_id")
    val createdAt = datetime("created_at")
    val isLinked = boolean("linked")
    val hasVIPRole = boolean("has_vip_role")
}
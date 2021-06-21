package eu.beegames.core.common.db.models

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int

object PlayerStatistic : Table<Nothing>("vcela_player_stats") {
    val timestamp = datetime("timestamp").primaryKey()
    val playerCount = int("player_count")
}
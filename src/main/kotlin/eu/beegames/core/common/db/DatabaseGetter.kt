/**
 * Copyright (C) 2021 TTtie (BeeGames.eu)
 * 
 * This file is part of Včela.
 * 
 * Včela is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Včela is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with Včela.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.beegames.core.common.db

import com.zaxxer.hikari.HikariDataSource
import org.ktorm.database.Database

object DatabaseGetter {
    private var dbInst: Database? = null

    fun getInstance(
        maxPoolSize: Int,
        server: String,
        port: String,
        dbName: String,
        _username: String,
        _password: String
    ): Database {
        if (dbInst == null) {
            dbInst = Database.connect(
                HikariDataSource()
                    .apply {
                        maximumPoolSize = maxPoolSize

                        jdbcUrl = "jdbc:mysql://$server:$port/$dbName"
                        username = _username
                        password = _password
                    }
            )
            return dbInst!!
        }

        return dbInst!!
    }
}

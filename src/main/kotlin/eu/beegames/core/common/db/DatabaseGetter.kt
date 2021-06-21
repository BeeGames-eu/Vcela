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

                        // Using the real mariadb datasource would be better, but the less deps, the better
                        dataSourceClassName = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource"
                        addDataSourceProperty("serverName", server)
                        addDataSourceProperty("port", port)
                        addDataSourceProperty("databaseName", dbName)
                        username = _username
                        password = _password
                    }
            )
            return dbInst!!
        }

        return dbInst!!
    }
}
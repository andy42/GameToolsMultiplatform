package com.jaehl.gameTool.localSourceSqlDelight.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.jaehl.gameTool.localSourceSqlDelight.Database
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import java.io.File

actual fun bindSqlDriver(diBuilder : DI.Builder) = with(diBuilder) {
    bind<SqlDriver> { singleton {
        val databaseUrl = instance<String>("databaseUrl")
        val driver = JdbcSqliteDriver("jdbc:sqlite:$databaseUrl")
        if(!File(databaseUrl).exists()) {
            Database.Schema.create(driver)
        }
        return@singleton driver
    }}
}
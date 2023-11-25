package com.jaehl.gameTool.localSourceSqlDelight.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.jaehl.gameTool.localSourceSqlDelight.Database

import org.kodein.di.*

actual fun bindSqlDriver(diBuilder : DI.Builder) = with(diBuilder) {
    bind<SqlDriver> { provider {
        AndroidSqliteDriver(Database.Schema, instance<Context>(), "gameTools.db")
    }}
}
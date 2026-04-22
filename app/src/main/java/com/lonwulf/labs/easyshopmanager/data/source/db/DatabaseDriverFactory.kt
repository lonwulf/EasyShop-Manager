package com.lonwulf.labs.easyshopmanager.data.source.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.lonwulf.labs.easyshopmanager.db.Catalogue

class DatabaseDriverFactory(private val context: Context) {
    fun createDriver(): SqlDriver = AndroidSqliteDriver(
        schema = Catalogue.Schema,
        context = context,
        name = "catalogue.db"
    )
}
package jp.zero_x_d.workaholic.merltlreader.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

/**
 * Created by D on 17/08/07.
 * https://github.com/Kotlin/anko/wiki/Anko-SQLite
 */
class AppDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "AppDatabase", null, 1) {
    companion object {
        private var instance: AppDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): AppDatabaseOpenHelper {
            if (instance == null) {
                instance = AppDatabaseOpenHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable("AppRegistration", true,
                "instance_url" to TEXT + PRIMARY_KEY + UNIQUE,
                "json" to TEXT)
        // Here you create tables
        db.createTable("AccessToken", true,
                "instance_url" to TEXT + PRIMARY_KEY + UNIQUE,
                "user" to TEXT + PRIMARY_KEY + UNIQUE,
                "json" to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Nothing to do
        //db.dropTable("User", true)
    }
}

// Access property for Context
val Context.database: AppDatabaseOpenHelper
    get() = AppDatabaseOpenHelper.getInstance(applicationContext)
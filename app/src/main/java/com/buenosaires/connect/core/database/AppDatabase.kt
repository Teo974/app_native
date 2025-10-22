package com.buenosaires.connect.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.buenosaires.connect.core.data.CommentDao
import com.buenosaires.connect.core.data.MomentDao
import com.buenosaires.connect.core.data.UserDao
import com.buenosaires.connect.core.model.Comment
import com.buenosaires.connect.core.model.Moment
import com.buenosaires.connect.core.model.User

@Database(entities = [User::class, Moment::class, Comment::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun momentDao(): MomentDao
    abstract fun commentDao(): CommentDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE moments ADD COLUMN location TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS comments (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                momentId INTEGER NOT NULL,
                author TEXT NOT NULL,
                content TEXT NOT NULL,
                timestamp INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}

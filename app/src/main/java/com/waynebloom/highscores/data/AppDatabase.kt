package com.waynebloom.highscores.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec

@Database(
    version = 4,
    entities = [GameEntity::class, MatchEntity::class, ScoreEntity::class],
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 1, to = 2),
        AutoMigration (from = 2, to = 3, spec = AppDatabase.DeleteGameImage::class),
        AutoMigration (from = 3, to = 4)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    @DeleteColumn(tableName = "Game", columnName = "image")
    class DeleteGameImage : AutoMigrationSpec

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database")
                    .createFromAsset("database/scores_app.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
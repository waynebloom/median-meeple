package com.waynebloom.scorekeeper.data.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.waynebloom.scorekeeper.data.model.game.GameEntity
import com.waynebloom.scorekeeper.data.model.match.MatchEntity
import com.waynebloom.scorekeeper.data.model.player.PlayerEntity
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreEntity
import com.waynebloom.scorekeeper.data.model.subscoretitle.SubscoreTitleEntity

@Database(
    version = 10,
    entities = [
        GameEntity::class,
        MatchEntity::class,
        PlayerEntity::class,
        SubscoreTitleEntity::class,
        SubscoreEntity::class
    ],
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 1, to = 2),
        AutoMigration (from = 2, to = 3, spec = AppDatabase.DeleteGameImage::class),
        AutoMigration (from = 4, to = 5),
        AutoMigration (from = 5, to = 6),
        AutoMigration (from = 6, to = 7)
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
                    .addMigrations(MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
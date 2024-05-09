package com.waynebloom.scorekeeper.room.data.datasource

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.waynebloom.scorekeeper.room.MIGRATION_10_11
import com.waynebloom.scorekeeper.room.MIGRATION_7_8
import com.waynebloom.scorekeeper.room.MIGRATION_8_9
import com.waynebloom.scorekeeper.room.MIGRATION_9_10
import com.waynebloom.scorekeeper.room.data.repository.AppDao
import com.waynebloom.scorekeeper.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataModel
import com.waynebloom.scorekeeper.room.data.model.CategoryScoreDataModel
import com.waynebloom.scorekeeper.room.data.model.CategoryDataModel
import com.waynebloom.scorekeeper.room.domain.repository.CategoryRepository
import com.waynebloom.scorekeeper.room.domain.repository.CategoryScoreRepository
import com.waynebloom.scorekeeper.room.domain.repository.GameRepository
import com.waynebloom.scorekeeper.room.domain.repository.MatchRepository
import com.waynebloom.scorekeeper.room.domain.repository.PlayerRepository

@Database(
    version = 12,
    entities = [
        GameDataModel::class,
        MatchDataModel::class,
        PlayerDataModel::class,
        CategoryDataModel::class,
        CategoryScoreDataModel::class
    ],
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 1, to = 2),
        AutoMigration (from = 2, to = 3, spec = AppDatabase.DeleteGameImage::class),
        AutoMigration (from = 4, to = 5),
        AutoMigration (from = 5, to = 6),
        AutoMigration (from = 6, to = 7),
        AutoMigration (from = 11, to = 12)
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao
    abstract fun getCategoryRepository(): CategoryRepository
    abstract fun getCategoryScoreRepository(): CategoryScoreRepository
    abstract fun getGameRepository(): GameRepository
    abstract fun getMatchRepository(): MatchRepository
    abstract fun getPlayerRepository(): PlayerRepository

    @DeleteColumn(tableName = "Game", columnName = "image")
    class DeleteGameImage : AutoMigrationSpec

    companion object {

        val manualMigrations = arrayOf(
            MIGRATION_7_8,
            MIGRATION_8_9,
            MIGRATION_9_10,
            MIGRATION_10_11
        )

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database")
                    .createFromAsset("database/scores_app.db")
                    .addMigrations(*manualMigrations)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

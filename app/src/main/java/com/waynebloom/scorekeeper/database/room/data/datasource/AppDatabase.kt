package com.waynebloom.scorekeeper.database.room.data.datasource

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RenameColumn
import androidx.room.RenameTable
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.waynebloom.scorekeeper.database.repository.CategoryRepository
import com.waynebloom.scorekeeper.database.repository.GameRepository
import com.waynebloom.scorekeeper.database.repository.MatchRepository
import com.waynebloom.scorekeeper.database.repository.PlayerRepository
import com.waynebloom.scorekeeper.database.repository.ScoreRepository
import com.waynebloom.scorekeeper.database.room.data.model.CategoryDataModel
import com.waynebloom.scorekeeper.database.room.data.model.ScoreDataModel
import com.waynebloom.scorekeeper.database.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.database.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.database.room.data.model.PlayerDataModel

@Database(
    version = 15,
    entities = [
        GameDataModel::class,
        MatchDataModel::class,
        PlayerDataModel::class,
        CategoryDataModel::class,
        ScoreDataModel::class
    ],
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3, spec = AppDatabase.DeleteGameImage::class),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 11, to = 12),
        AutoMigration(from = 13, to = 14, spec = AppDatabase.AutoMigration13to14::class),
        AutoMigration(from = 14, to = 15),
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getCategoryRepository(): CategoryRepository
    abstract fun getCategoryScoreRepository(): ScoreRepository
    abstract fun getGameRepository(): GameRepository
    abstract fun getMatchRepository(): MatchRepository
    abstract fun getPlayerRepository(): PlayerRepository

    @DeleteColumn(tableName = "Game", columnName = "image")
    class DeleteGameImage : AutoMigrationSpec

    @RenameColumn(tableName = "SubscoreTitle", toColumnName = "name", fromColumnName = "title")
    @RenameTable(fromTableName = "Subscore", toTableName = "CategoryScore")
    @RenameTable(fromTableName = "SubscoreTitle", toTableName = "Category")
    @RenameColumn(tableName = "Match", toColumnName = "date_millis", fromColumnName = "time_modified")
    @DeleteColumn(tableName = "Player", columnName = "show_detailed_score")
    @DeleteColumn(tableName = "Player", columnName = "score")
    class AutoMigration13to14 : AutoMigrationSpec

    companion object {
        val manualMigrations = arrayOf(
            MIGRATION_7_8,
            MIGRATION_8_9,
            MIGRATION_9_10,
            MIGRATION_10_11,
            MIGRATION_12_13
        )
    }
}

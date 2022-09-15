package com.waynebloom.highscores.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    version = 5,
    entities = [GameEntity::class, MatchEntity::class, ScoreEntity::class],
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 1, to = 2),
        AutoMigration (from = 2, to = 3, spec = AppDatabase.DeleteGameImage::class),
        AutoMigration (from = 4, to = 5)
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
//                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create the new tables
        database.execSQL(
            "CREATE TABLE game_new (id INTEGER NOT NULL DEFAULT 0, name TEXT NOT NULL, color TEXT NOT NULL DEFAULT 'RED', PRIMARY KEY(id))"
        )
        database.execSQL(
            "CREATE TABLE match_new (id INTEGER NOT NULL DEFAULT 0, game_owner_id INTEGER NOT NULL, time_modified INTEGER NOT NULL, match_notes TEXT, PRIMARY KEY(id)," +
                    "FOREIGN KEY(game_owner_id) REFERENCES game(id) ON UPDATE CASCADE ON DELETE CASCADE)"
        )
        database.execSQL("CREATE INDEX IF NOT EXISTS index_game_owner_id ON match_new(id)")
        database.execSQL(
            "CREATE TABLE score_new (id INTEGER NOT NULL DEFAULT 0, match_id INTEGER NOT NULL, name TEXT NOT NULL, score INTEGER NOT NULL, PRIMARY KEY(id)," +
                    "FOREIGN KEY(match_id) REFERENCES `match`(id) ON UPDATE CASCADE ON DELETE CASCADE)"
        )
        database.execSQL("CREATE INDEX IF NOT EXISTS index_match_id ON score_new(id)")

        // Copy the data
        database.execSQL(
            "INSERT INTO game_new (name, color) SELECT name, color FROM game"
        )
        database.execSQL(
            "INSERT INTO match_new (game_owner_id, time_modified, match_notes) SELECT game_owner_id, time_modified, match_notes FROM `match`"
        )
        database.execSQL(
            "INSERT INTO score_new (match_id, name, score) SELECT match_id, name, score FROM score"
        )
        // Remove the old table
        database.execSQL("DROP TABLE game")
        database.execSQL("DROP TABLE `match`")
        database.execSQL("DROP TABLE score")
        // Change the table name to the correct one
        database.execSQL("ALTER TABLE game_new RENAME TO game")
        database.execSQL("ALTER TABLE match_new RENAME TO `match`")
        database.execSQL("ALTER TABLE score_new RENAME TO score")
    }
}
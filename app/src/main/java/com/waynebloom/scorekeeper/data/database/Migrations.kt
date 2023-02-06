package com.waynebloom.scorekeeper.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Score RENAME TO Player;")
        database.execSQL("""
                CREATE TABLE IF NOT EXISTS `SubscoreTitle` (
                  `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                  `game_id` INTEGER NOT NULL,
                  `position` INTEGER NOT NULL, 
                  `title` TEXT NOT NULL,
                  FOREIGN KEY(`game_id`) REFERENCES `Game`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
                );
            """.trimIndent())
        database.execSQL("""
                CREATE TABLE IF NOT EXISTS `Subscore` (
                  `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                  `subscore_title_id` INTEGER NOT NULL,
                  `player_id` INTEGER NOT NULL,
                  `value` INTEGER,
                  FOREIGN KEY(`subscore_title_id`) REFERENCES `SubscoreTitle`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
                  FOREIGN KEY(`player_id`) REFERENCES `Player`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
                );
            """.trimIndent())
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_SubscoreTitle_game_id` ON SubscoreTitle (`game_id`);")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_Subscore_player_id` ON Subscore (`player_id`);")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_Subscore_subscore_title_id` ON Subscore (`subscore_title_id`);")
    }
}

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TEMPORARY TABLE IF NOT EXISTS `Player_BKP` (
                id INTEGER,
                match_id INTEGER,
                name TEXT,
                score INTEGER,
                position INTEGER,
                show_detailed_score INTEGER
            );
        """.trimIndent())
        database.execSQL("INSERT INTO Player_BKP (id, match_id, name, score, position, show_detailed_score) SELECT id, match_id, name, score, 0, 0 FROM Player;")
        database.execSQL("DROP TABLE Player;")
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS Player (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT 0,
                `match_id` INTEGER NOT NULL,
                `name` TEXT NOT NULL,
                `score` INTEGER,
                `position` INTEGER NOT NULL,
                `show_detailed_score` INTEGER NOT NULL,
                FOREIGN KEY(`match_id`) REFERENCES `Match`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
            );
        """.trimIndent())
        database.execSQL("INSERT INTO Player SELECT id, match_id, name, score, position, show_detailed_score FROM Player_BKP;")
        database.execSQL("DROP TABLE Player_BKP;")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_Player_match_id` ON `Player` (`match_id`)")
    }
}

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // region Player

        database.execSQL("""
            CREATE TEMPORARY TABLE IF NOT EXISTS `Player_BKP` (
                id INTEGER,
                match_id INTEGER,
                name TEXT,
                score INTEGER,
                position INTEGER,
                show_detailed_score INTEGER
            );
        """.trimIndent())
        database.execSQL("INSERT INTO Player_BKP (id, match_id, name, score, position, show_detailed_score) SELECT id, match_id, name, score, position, show_detailed_score FROM Player WHERE score IS NOT NULL;")
        database.execSQL("INSERT INTO Player_BKP (id, match_id, name, score, position, show_detailed_score) SELECT id, match_id, name, 0, position, show_detailed_score FROM Player WHERE score IS NULL;")
        database.execSQL("DROP TABLE Player;")
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS Player (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT 0,
                `match_id` INTEGER NOT NULL,
                `name` TEXT NOT NULL,
                `score` INTEGER NOT NULL,
                `position` INTEGER NOT NULL,
                `show_detailed_score` INTEGER NOT NULL,
                FOREIGN KEY(`match_id`) REFERENCES `Match`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
            );
        """.trimIndent())
        database.execSQL("INSERT INTO Player SELECT id, match_id, name, score, position, show_detailed_score FROM Player_BKP;")
        database.execSQL("DROP TABLE Player_BKP;")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_Player_match_id` ON `Player` (`match_id`)")

        // endregion

        // region Subscore

        database.execSQL("""
            CREATE TEMPORARY TABLE IF NOT EXISTS `Subscore_BKP` (
                id INTEGER,
                subscore_title_id INTEGER,
                player_id INTEGER,
                value INTEGER
            );
        """.trimIndent())
        database.execSQL("INSERT INTO Subscore_BKP (id, subscore_title_id, player_id, value) SELECT id, subscore_title_id, player_id, value FROM Subscore WHERE value IS NOT NULL;")
        database.execSQL("INSERT INTO Subscore_BKP (id, subscore_title_id, player_id, value) SELECT id, subscore_title_id, player_id, 0 FROM Subscore WHERE value IS NULL;")
        database.execSQL("DROP TABLE Subscore;")
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS Subscore (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT 0,
                `subscore_title_id` INTEGER NOT NULL,
                `player_id` INTEGER NOT NULL,
                `value` INTEGER NOT NULL,
                FOREIGN KEY(`subscore_title_id`) REFERENCES `SubscoreTitle`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
                FOREIGN KEY(`player_id`) REFERENCES `Player`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
            );
        """.trimIndent())
        database.execSQL("INSERT INTO Subscore SELECT id, subscore_title_id, player_id, value FROM Subscore_BKP;")
        database.execSQL("DROP TABLE Subscore_BKP;")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_Subscore_player_id` ON `Subscore` (`player_id`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_Subscore_subscore_title_id` ON `Subscore` (`subscore_title_id`)")
    }
}
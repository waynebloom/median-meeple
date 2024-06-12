@file:SuppressWarnings("MaxLineLength")
package com.waynebloom.scorekeeper.room.data

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

        // endregion
    }
}

val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // region Player

        database.execSQL("""
            CREATE TEMPORARY TABLE IF NOT EXISTS `Player_BKP` (
                id INTEGER,
                match_id INTEGER,
                name TEXT,
                score TEXT,
                position INTEGER,
                show_detailed_score INTEGER
            );
        """.trimIndent())
        database.execSQL("INSERT INTO Player_BKP (id, match_id, name, score, position, show_detailed_score) SELECT id, match_id, name, score, position, show_detailed_score FROM Player;")
        database.execSQL("DROP TABLE Player;")
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS Player (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT 0,
                `match_id` INTEGER NOT NULL,
                `name` TEXT NOT NULL,
                `score` TEXT NOT NULL,
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
                value TEXT
            );
        """.trimIndent())
        database.execSQL("INSERT INTO Subscore_BKP (id, subscore_title_id, player_id, value) SELECT id, subscore_title_id, player_id, value FROM Subscore;")
        database.execSQL("DROP TABLE Subscore;")
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS Subscore (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT 0,
                `subscore_title_id` INTEGER NOT NULL,
                `player_id` INTEGER NOT NULL,
                `value` TEXT NOT NULL,
                FOREIGN KEY(`subscore_title_id`) REFERENCES `SubscoreTitle`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
                FOREIGN KEY(`player_id`) REFERENCES `Player`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
            );
        """.trimIndent())
        database.execSQL("INSERT INTO Subscore SELECT id, subscore_title_id, player_id, value FROM Subscore_BKP;")
        database.execSQL("DROP TABLE Subscore_BKP;")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_Subscore_player_id` ON `Subscore` (`player_id`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_Subscore_subscore_title_id` ON `Subscore` (`subscore_title_id`)")

        // endregion
    }
}

val MIGRATION_13_14 = object : Migration(13, 14) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `GAME_BKP` (
                ID INTEGER,
                COLOR INTEGER,
                NAME TEXT,
                SCORING_MODE INTEGER
            );
        """.trimIndent())
        database.execSQL("""
            INSERT INTO `GAME_BKP` (
                ID, COLOR, NAME, SCORING_MODE
            ) SELECT
                ID,
                CASE color
                    WHEN 'DEEP_ORANGE' THEN 3
                    WHEN 'ORANGE' THEN 6
                    WHEN 'AMBER' THEN 7
                    WHEN 'YELLOW' THEN 8
                    WHEN 'LIME' THEN 10
                    WHEN 'LIGHT_GREEN' THEN 11
                    WHEN 'GREEN' THEN 12
                    WHEN 'TEAL' THEN 13
                    WHEN 'CYAN' THEN 14
                    WHEN 'LIGHT_BLUE' THEN 15
                    WHEN 'BLUE' THEN 16
                    WHEN 'INDIGO' THEN 17
                    WHEN 'DEEP_PURPLE' THEN 18
                    WHEN 'PURPLE' THEN 20
                    WHEN 'PINK' THEN 0
                    ELSE 0
                END,
                NAME,
                SCORING_MODE
            FROM Game;
        """.trimIndent())
        database.execSQL("DROP TABLE Game;")
        database.execSQL("""
            CREATE TABLE Game (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT 0,
                color INTEGER NOT NULL,
                name TEXT NOT NULL,
                scoring_mode INTEGER NOT NULL DEFAULT 1
            );
        """.trimIndent())
        database.execSQL("INSERT INTO Game SELECT ID, COLOR, NAME, SCORING_MODE FROM GAME_BKP;")
        database.execSQL("DROP TABLE GAME_BKP;")
    }
}

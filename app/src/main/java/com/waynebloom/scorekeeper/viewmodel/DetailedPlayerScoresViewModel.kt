package com.waynebloom.scorekeeper.viewmodel

import android.content.res.Resources
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.data.model.player.PlayerObject
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreEntity
import com.waynebloom.scorekeeper.data.model.subscoretitle.SubscoreTitleEntity

class DetailedPlayerScoresViewModel(
    players: List<PlayerObject>,
    resources: Resources,
    private var subscoreTitles: List<SubscoreTitleEntity>
): ViewModel() {
    var activePage: Int by mutableStateOf(0)
    var includeUncategorizedScoreColumn = false
    val subscoreTitleStrings: List<String>

    init {
        subscoreTitles = subscoreTitles.sortedBy { it.position }
        includeUncategorizedScoreColumn = players.any { it.uncategorizedScore != 0L }
        subscoreTitleStrings = getSubscoreTitleStrings(resources)
    }

    fun getScoreString(scoreLong: Long?): String {
        return if (scoreLong != null && scoreLong != 0L) {
            scoreLong.toString()
        } else "-"
    }

    fun getSubscoresInOrder(player: PlayerObject): List<SubscoreEntity> {
        val categorizedSubscores = subscoreTitles.map { subscoreTitle ->
            player.score.find { it.subscoreTitleId == subscoreTitle.id } ?: SubscoreEntity()
        }
        return if (includeUncategorizedScoreColumn) {
            categorizedSubscores.plus(
                SubscoreEntity(value = player.uncategorizedScore)
            )
        } else categorizedSubscores
    }

    private fun getSubscoreTitleStrings(resources: Resources): List<String> {
        val definedTitles = subscoreTitles.map { it.title }
        return if (includeUncategorizedScoreColumn) {
            definedTitles.plus(resources.getString(R.string.field_uncategorized))
        } else definedTitles
    }
}

class DetailedPlayerScoresViewModelFactory(
    private val initialSubscoreTitles: List<SubscoreTitleEntity>,
    private val players: List<PlayerObject>,
    private val resources: Resources,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = DetailedPlayerScoresViewModel(
        players = players,
        resources = resources,
        subscoreTitles = initialSubscoreTitles
    ) as T
}
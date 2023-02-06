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
    private var subscoreTitles: List<SubscoreTitleEntity>,
    resources: Resources
): ViewModel() {
    var activePage: Int by mutableStateOf(0)
    val subscoreTitleStrings: List<String>

    init {
        subscoreTitles = subscoreTitles.sortedBy { it.position }
        subscoreTitleStrings = subscoreTitles
            .map { it.title }
            .plus(resources.getString(R.string.field_uncategorized))
    }

    fun getScoreString(scoreLong: Long?): String {
        return if (scoreLong != null && scoreLong != 0L) {
            scoreLong.toString()
        } else "-"
    }

    fun getSubscoreColumnsInOrder(player: PlayerObject): List<SubscoreEntity> {
        return subscoreTitles.map { subscoreTitle ->
            player.score.find { it.subscoreTitleId == subscoreTitle.id } ?: SubscoreEntity()
        }.plus(
            SubscoreEntity(
                value = player.getUncategorizedScoreRemainder()
            )
        )
    }
}

class DetailedPlayerScoresViewModelFactory(
    private val initialSubscoreTitles: List<SubscoreTitleEntity>,
    private val resources: Resources
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = DetailedPlayerScoresViewModel(
        subscoreTitles = initialSubscoreTitles,
        resources = resources
    ) as T
}
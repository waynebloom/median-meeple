package com.waynebloom.scorekeeper.dagger.module

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.waynebloom.scorekeeper.database.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.database.room.data.model.MatchDataModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object JsonModule {

	@Provides
	fun providesGameJsonAdapter(moshi: Moshi): JsonAdapter<GameDataModel> {
		return moshi.adapter(GameDataModel::class.java)
	}

	@Provides
	fun providesMatchJsonAdapter(moshi: Moshi): JsonAdapter<MatchDataModel> {
		return moshi.adapter(MatchDataModel::class.java)
	}
}

package com.waynebloom.scorekeeper.dagger.module

import com.waynebloom.scorekeeper.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

	@Provides
	@Singleton
	fun providesSupabaseClient(): SupabaseClient {
		return createSupabaseClient(
			supabaseUrl = BuildConfig.SUPABASE_URL,
			supabaseKey = BuildConfig.SUPABASE_KEY,
		) {
			install(Postgrest)
		}
	}

	@Provides
	@Singleton
	fun providesPostgrest(client: SupabaseClient): Postgrest {
		return client.postgrest
	}
}

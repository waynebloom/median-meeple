package com.waynebloom.scorekeeper.database.domain

import com.waynebloom.scorekeeper.database.room.data.datasource.CategoryDao
import javax.inject.Inject

class CategoryRepository @Inject constructor(
	private val categoryDao: CategoryDao,
) {

}
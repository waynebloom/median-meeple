package com.waynebloom.scorekeeper.database.repository

import com.waynebloom.scorekeeper.database.room.data.datasource.CategoryDao
import javax.inject.Inject

class CategoryRepository @Inject constructor(
	private val categoryDao: CategoryDao,
) {

}
package com.waynebloom.scorekeeper.room.domain.model

interface PlayerDomainModel {
    val name: String
    val position: Int
    val totalScore: String
    val showDetailedScore: Boolean
}
package com.waynebloom.scorekeeper.room.domain.model

interface GameDomainModel {
    val id: Long
    val color: String
    val name: String
    val scoringMode: Int
}

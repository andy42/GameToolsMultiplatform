package com.jaehl.gameTool.apiClientKtor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateItemRequest(
    val game: Int,
    val name: String,
    val categories: List<Int>,
    val image: Int
)

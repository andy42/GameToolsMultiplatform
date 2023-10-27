package com.jaehl.gameTool.apiClientKtor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateGameRequest(
    val name : String,
    val itemCategories : List<Int>,
    val icon : Int,
    val banner : Int
)

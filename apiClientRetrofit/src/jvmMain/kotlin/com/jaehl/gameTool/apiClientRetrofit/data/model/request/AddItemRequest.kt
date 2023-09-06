package com.jaehl.gameTool.apiClientRetrofit.data.model.request

data class AddItemRequest(
    val game: Int,
    val name: String,
    val categories: List<Int>,
    val image: Int
)

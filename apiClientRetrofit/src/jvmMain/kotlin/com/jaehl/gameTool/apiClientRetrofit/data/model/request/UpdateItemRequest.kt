package com.jaehl.gameTool.apiClientRetrofit.data.model.request

data class UpdateItemRequest(
    val game: Int,
    val name: String,
    val categories: List<Int>,
    val image: Int
)

package com.jaehl.gameTool.apiClientRetrofit.data.model.request

data class CreateGameRequest(
    val name : String,
    val icon : Int,
    val banner : Int
)

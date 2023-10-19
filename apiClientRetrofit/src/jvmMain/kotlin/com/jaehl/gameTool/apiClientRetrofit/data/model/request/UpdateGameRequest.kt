package com.jaehl.gameTool.apiClientRetrofit.data.model.request

data class UpdateGameRequest(
    val name : String,
    val itemCategories : List<Int>,
    val icon : Int,
    val banner : Int
)

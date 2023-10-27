package com.jaehl.gameTool.apiClientRetrofit.data.model

import retrofit2.Call

data class Response<T>(
    val data : T
)

data class ResponsePaged<T>(
    val data : List<T>,
    val meta : ResponsePageMeta
)

data class ResponsePageMeta(
    val total : Int = 0,
    val page : Int = 0,
    val limit : Int = 10
)
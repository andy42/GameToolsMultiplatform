package com.jaehl.gameTool.apiClientRetrofit.data.model.request

data class RegisterRequest(
    val userName : String,
    val email : String,
    val password : String
)

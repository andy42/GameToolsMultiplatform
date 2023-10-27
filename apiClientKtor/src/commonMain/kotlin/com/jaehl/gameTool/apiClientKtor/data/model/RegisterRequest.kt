package com.jaehl.gameTool.apiClientKtor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val userName : String,
    val email : String,
    val password : String
)

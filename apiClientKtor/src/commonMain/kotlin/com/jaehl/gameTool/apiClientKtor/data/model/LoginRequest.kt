package com.jaehl.gameTool.apiClientKtor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val userName : String,
    val password : String
)

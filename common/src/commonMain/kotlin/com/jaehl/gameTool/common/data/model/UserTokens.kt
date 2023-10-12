package com.jaehl.gameTool.common.data.model
import kotlinx.serialization.Serializable

@Serializable
data class UserTokens(
    val refreshToken : String,
    val accessToken : String
)

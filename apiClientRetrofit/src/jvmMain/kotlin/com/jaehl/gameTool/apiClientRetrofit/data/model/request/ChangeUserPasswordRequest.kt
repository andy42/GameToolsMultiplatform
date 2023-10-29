package com.jaehl.gameTool.apiClientRetrofit.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ChangeUserPasswordRequest(
    val password : String
)

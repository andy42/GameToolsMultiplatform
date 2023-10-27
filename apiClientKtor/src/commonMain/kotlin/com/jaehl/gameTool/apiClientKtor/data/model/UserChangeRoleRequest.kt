package com.jaehl.gameTool.apiClientKtor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserChangeRoleRequest(
    val userId : Int,
    val role : String
)

package com.jaehl.gameTool.apiClientRetrofit.data.model.request

data class UserChangeRoleRequest(
    val userId : Int,
    val role : String
)

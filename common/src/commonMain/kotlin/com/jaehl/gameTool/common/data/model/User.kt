package com.jaehl.gameTool.common.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id : String,
    val userName : String,
    val role : Role
) {
    enum class Role(){
        @SerializedName("User")
        User,
        @SerializedName("Admin")
        Admin
    }
}

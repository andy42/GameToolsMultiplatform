package com.jaehl.gameTool.common.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id : Int,
    val userName : String,
    val email : String,
    val role : Role
) {
    enum class Role{
        @SerializedName("User")
        User,
        @SerializedName("Admin")
        Admin,
        @SerializedName("Contributor")
        Contributor,
        @SerializedName("Unverified")
        Unverified
    }
}

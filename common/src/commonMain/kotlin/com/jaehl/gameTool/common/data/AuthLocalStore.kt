package com.jaehl.gameTool.common.data

import com.jaehl.gameTool.common.data.model.UserTokens

interface AuthLocalStore {
    fun getBearerToken() : String
    fun getUserTokens() : UserTokens
    fun saveToken(userTokens : UserTokens)
}

class AuthLocalStoreImp : AuthLocalStore {

    private var userTokens : UserTokens = UserTokens(refreshToken = "", accessToken = "")

    override fun getBearerToken(): String {
        return "$BEARER_TOKEN ${userTokens.accessToken}"
    }

    override fun saveToken(userTokens: UserTokens) {
        this.userTokens = userTokens
    }

    override fun getUserTokens(): UserTokens {
        return userTokens
    }

    companion object {
        private const val BEARER_TOKEN = "Bearer"
    }
}
package com.jaehl.gameTool.common.data

import com.jaehl.gameTool.common.data.model.AccessToken

interface AuthProvider {
    fun getBearerToken() : String
    fun saveToken(accessToken : AccessToken)
}

class AuthProviderImp : AuthProvider {

    private var accessToken : AccessToken = AccessToken("")

    override fun getBearerToken(): String {
        return "$BEARER_TOKEN ${accessToken.token}"
    }

    override fun saveToken(accessToken: AccessToken) {
        this.accessToken = accessToken
    }

    companion object {
        private const val BEARER_TOKEN = "Bearer"
    }
}
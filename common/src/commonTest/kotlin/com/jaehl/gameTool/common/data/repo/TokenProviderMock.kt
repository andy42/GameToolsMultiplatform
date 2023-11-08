package com.jaehl.gameTool.common.data.repo

class TokenProviderMock : TokenProvider {

    var refreshToken = ""
    var accessToken = ""
    var isRefreshTokenValid = true

    override suspend fun getBearerRefreshToken(): String {
        return refreshToken
    }

    override suspend fun getBearerAccessToken(): String {
        return accessToken
    }

    override suspend fun isRefreshTokenValid(): Boolean {
        return isRefreshTokenValid
    }

    override suspend fun clearTokens() {
        refreshToken = ""
        accessToken = ""
        isRefreshTokenValid = false
    }
}
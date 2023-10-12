package com.jaehl.gameTool.common.data

import com.jaehl.gameTool.common.data.model.UserTokens

interface AuthLocalStore {
    //suspend fun getBearerToken() : String
    suspend fun getUserTokens() : UserTokens
    suspend fun saveToken(userTokens : UserTokens)
}

class AuthLocalStoreInMemoryImp : AuthLocalStore {

    private var userTokens : UserTokens = UserTokens(refreshToken = "", accessToken = "")

    override suspend fun saveToken(userTokens: UserTokens) {
        this.userTokens = userTokens
    }

    override suspend fun getUserTokens(): UserTokens {
        return userTokens
    }
}
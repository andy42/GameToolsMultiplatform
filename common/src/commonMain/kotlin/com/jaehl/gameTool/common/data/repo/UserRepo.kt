package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.data.AuthLocalStore
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.common.data.service.UserService
import com.auth0.jwt.JWT
import com.jaehl.gameTool.common.data.AuthException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

interface UserRepo {
    suspend fun login(userName : String, password : String)
    suspend fun register(userName : String, email : String, password : String)
    suspend fun getUserSelf() : User
    suspend fun getUsers() : List<User>
}

interface TokenProvider {
    suspend fun getBearerRefreshToken() : String
    suspend fun getBearerAccessToken() : String
}

class UserRepoImp(
    val userService : UserService,
    val authLocalStore : AuthLocalStore
) : UserRepo, TokenProvider{

    override suspend fun login(userName: String, password: String) {
        authLocalStore.saveToken(
            userService.login(userName = userName, password = password)
        )
    }

    override suspend fun register(userName : String, email: String, password: String) {
        authLocalStore.saveToken(
            userService.register(userName = userName, email = email, password = password)
        )
    }

    override suspend fun getUserSelf(): User {
        return userService.getSelf(bearerToken = authLocalStore.getBearerToken())
    }

    override suspend fun getUsers(): List<User> {
        return userService.getUsers(bearerToken = authLocalStore.getBearerToken())
    }

    override suspend fun getBearerRefreshToken(): String {
        val userTokens = authLocalStore.getUserTokens()
        val refreshToken = JWT.decode(userTokens.refreshToken)
        if(refreshToken.expiresAt.before(Date())) throw AuthException()
        return "$BEARER_TOKEN ${userTokens.refreshToken}"
    }

    private val accessTokenMutex = Mutex()

    override suspend fun getBearerAccessToken(): String {
        accessTokenMutex.withLock {
            val userTokens = authLocalStore.getUserTokens()
            val accessToken = JWT.decode(userTokens.accessToken)
            if (accessToken.expiresAt.before(Date())) {
                val response = userService.refreshTokens(getBearerRefreshToken())
                authLocalStore.saveToken(response)
                return "$BEARER_TOKEN ${response.accessToken}"
            } else {
                return "$BEARER_TOKEN ${userTokens.accessToken}"
            }
        }
    }

    companion object {
        private const val BEARER_TOKEN = "Bearer"
    }
}
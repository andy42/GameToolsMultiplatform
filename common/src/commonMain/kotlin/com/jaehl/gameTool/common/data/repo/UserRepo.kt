package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.data.AuthLocalStore
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.common.data.service.UserService
import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTDecodeException
import com.jaehl.gameTool.common.data.AuthException
import com.jaehl.gameTool.common.data.model.UserTokens
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

interface UserRepo {
    suspend fun login(userName : String, password : String)
    suspend fun register(userName : String, email : String, password : String)
    suspend fun getUserSelf() : User
    suspend fun getUser(userId : Int) : User
    suspend fun getUsers() : List<User>
    suspend fun changeUserRole(userId : Int, role: User.Role) : User
    suspend fun changePassword(userId: Int, password: String)
}

interface TokenProvider {
    suspend fun getBearerRefreshToken() : String
    suspend fun getBearerAccessToken() : String
    suspend fun isRefreshTokenValid() : Boolean
    suspend fun clearTokens()
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
        return userService.getSelf(bearerToken = getBearerAccessToken())
    }

    override suspend fun getUser(userId: Int): User {
        return userService.getUser(bearerToken = getBearerAccessToken(), userId)
    }

    override suspend fun getUsers(): List<User> {
        return userService.getUsers(bearerToken = getBearerAccessToken())
    }

    override suspend fun changeUserRole(userId: Int, role: User.Role): User {
        return userService.changeUserRole(
            bearerToken = getBearerAccessToken(),
            userId = userId,
            role = role
        )
    }

    override suspend fun changePassword(userId: Int, password: String) {
        return userService.changeUserPassword(
            bearerToken = getBearerAccessToken(),
            userId = userId,
            password = password
        )
    }

    override suspend fun getBearerRefreshToken(): String {
        val userTokens = authLocalStore.getUserTokens()
        if(!isTokenValid(userTokens.refreshToken)) throw AuthException()
        return "$BEARER_TOKEN ${userTokens.refreshToken}"
    }

    private val accessTokenMutex = Mutex()

    private fun isTokenValid(token : String) : Boolean {
        try {
            return JWT.decode(token).expiresAt.after(Date())
        }
        catch (t: JWTDecodeException){
            return false
        }
    }

    override suspend fun getBearerAccessToken(): String {
        accessTokenMutex.withLock {
            val userTokens = authLocalStore.getUserTokens()
            if (isTokenValid(userTokens.accessToken)) {
                return "$BEARER_TOKEN ${userTokens.accessToken}"
            } else {
                val response = userService.refreshTokens(getBearerRefreshToken())
                authLocalStore.saveToken(response)
                return "$BEARER_TOKEN ${response.accessToken}"
            }
        }
    }

    override suspend fun isRefreshTokenValid() : Boolean{
        val userTokens = authLocalStore.getUserTokens()
        return isTokenValid(userTokens.refreshToken)
    }

    override suspend fun clearTokens() {
        authLocalStore.saveToken(UserTokens("", ""))
    }

    companion object {
        private const val BEARER_TOKEN = "Bearer"
    }
}
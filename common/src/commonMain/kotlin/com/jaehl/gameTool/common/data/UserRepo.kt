package com.jaehl.gameTool.common.data

import com.jaehl.gameTool.common.data.model.AccessToken
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.common.data.service.UserService

interface UserRepo {
    fun getBearerToken() : String
    fun isAccessTokenValid() : Boolean
    fun login(email : String, password : String)
    fun register(email : String, password : String)
    fun getUserSelf() : User
    fun getUsers() : List<User>
}

class UserRepoImp(
    val userService : UserService
) : UserRepo{

    private var accessToken : AccessToken = AccessToken("")

    override fun getBearerToken(): String {
        return "$bearerToken ${accessToken.token}"
    }

    override fun isAccessTokenValid(): Boolean {
        return true
    }

    override fun login(email: String, password: String) {
        accessToken = userService.login(email = email, password = password)
    }

    override fun register(email: String, password: String) {
        accessToken = userService.register(email = email, password = password)
    }

    override fun getUserSelf(): User {
        return userService.getSelf(bearerToken = getBearerToken())
    }

    override fun getUsers(): List<User> {
        return userService.getUsers(bearerToken = getBearerToken())
    }

    companion object {
        private const val bearerToken = "Bearer"
    }
}
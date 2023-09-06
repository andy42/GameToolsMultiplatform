package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.data.AuthProvider
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.common.data.service.UserService

interface UserRepo {
    fun isAccessTokenValid() : Boolean
    fun login(email : String, password : String)
    fun register(email : String, password : String)
    fun getUserSelf() : User
    fun getUsers() : List<User>
}

class UserRepoImp(
    val userService : UserService,
    val authProvider : AuthProvider
) : UserRepo {

    override fun isAccessTokenValid(): Boolean {
        return true
    }

    override fun login(email: String, password: String) {
        authProvider.saveToken(
            userService.login(email = email, password = password)
        )
    }

    override fun register(email: String, password: String) {
        authProvider.saveToken(
            userService.register(email = email, password = password)
        )
    }

    override fun getUserSelf(): User {
        return userService.getSelf(bearerToken = authProvider.getBearerToken())
    }

    override fun getUsers(): List<User> {
        return userService.getUsers(bearerToken = authProvider.getBearerToken())
    }


}
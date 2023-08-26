package com.jaehl.gameTool.apiClientRetrofit.data.service

import com.jaehl.gameTool.apiClientRetrofit.data.api.ServerApi
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.apiClientRetrofit.data.model.baseBody
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.LoginRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.RegisterRequest
import com.jaehl.gameTool.common.data.model.AccessToken
import com.jaehl.gameTool.common.data.service.UserService


class UserServiceRetrofit(
    val serverApi : ServerApi
) : UserService {

    override fun login(email: String, password: String): AccessToken {
        return serverApi.login(
            data = LoginRequest(
                username = email,
                password = password
            )
        ).baseBody()
    }

    override fun register(email: String, password: String): AccessToken {
        return serverApi.register(
            data = RegisterRequest(
                email = email,
                password = password
            )
        ).baseBody()
    }

    override fun getSelf(bearerToken: String): User {
        return serverApi.getUserSelf(
            bearerToken = bearerToken
        ).baseBody()
    }

    override fun getUsers(bearerToken: String): List<User> {
        return serverApi.getUsers(
            bearerToken = bearerToken
        ).baseBody()
    }
}
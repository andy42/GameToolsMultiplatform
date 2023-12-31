package com.jaehl.gameTool.apiClientRetrofit.data.service

import com.jaehl.gameTool.apiClientRetrofit.data.api.ServerApi
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.ChangeUserPasswordRequest
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.LoginRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.RegisterRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.UserChangeRoleRequest
import com.jaehl.gameTool.apiClientRetrofit.data.util.ExceptionHandler
import com.jaehl.gameTool.common.data.model.UserTokens
import com.jaehl.gameTool.common.data.service.UserService


class UserServiceRetrofit(
    private val serverApi : ServerApi,
    private val exceptionHandler : ExceptionHandler
) : UserService {

    override suspend fun login(userName: String, password: String): UserTokens = exceptionHandler.tryBlock {
        return@tryBlock serverApi.login(
            data = LoginRequest(
                userName = userName,
                password = password
            )
        ).data
    }

    override suspend fun refreshTokens(refreshToken : String): UserTokens = exceptionHandler.tryBlock {
        return@tryBlock serverApi.refreshTokens(
            bearerToken = refreshToken
        ).data
    }

    override suspend fun register(userName : String, email: String, password: String): UserTokens = exceptionHandler.tryBlock {
        return@tryBlock serverApi.register(
            data = RegisterRequest(
                userName = userName,
                email = email,
                password = password
            )
        ).data
    }

    override suspend fun getSelf(bearerToken: String): User = exceptionHandler.tryBlock {
        return@tryBlock serverApi.getUserSelf(
            bearerToken = bearerToken
        ).data
    }

    override suspend fun getUsers(bearerToken: String): List<User> = exceptionHandler.tryBlock {
        return@tryBlock serverApi.getUsers(
            bearerToken = bearerToken
        ).data
    }

    override suspend fun changeUserRole(bearerToken: String, userId: Int, role: User.Role) : User = exceptionHandler.tryBlock {
        return@tryBlock serverApi.changeUserRole(
            bearerToken = bearerToken,
            userId = userId,
            data = UserChangeRoleRequest(
                role = role.name,
            )
        ).data
    }

    override suspend fun getUser(bearerToken: String, userId: Int): User = exceptionHandler.tryBlock {
        return@tryBlock serverApi.getUser(
            bearerToken = bearerToken,
            id = userId
        ).data
    }

    override suspend fun changeUserPassword(bearerToken: String, userId: Int, password: String) = exceptionHandler.tryBlock {
        serverApi.changeUserPassword(
            bearerToken = bearerToken,
            userId = userId,
            data = ChangeUserPasswordRequest(
                password = password
            )
        )
    }
}
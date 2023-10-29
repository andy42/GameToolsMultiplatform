package com.jaehl.gameTool.apiClientKtor.data.service

import com.jaehl.gameTool.apiClientKtor.data.model.*
import com.jaehl.gameTool.apiClientKtor.data.util.ExceptionHandler
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.common.data.model.UserTokens
import com.jaehl.gameTool.common.data.service.UserService
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class UserServiceKtor(
    private val client: HttpClient,
    private val appConfig : AppConfig,
    private val exceptionHandler : ExceptionHandler
) : UserService {
    override suspend fun login(userName: String, password: String): UserTokens = exceptionHandler.tryBlock {
        val response = client.post(
            urlString = "${appConfig.baseUrl}/user/login"
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                LoginRequest(
                    userName = userName,
                    password = password
                )
            )
        }

        val responseBody : Response<UserTokens> = response.body()
        return@tryBlock responseBody.data
    }

    override suspend fun refreshTokens(refreshToken: String): UserTokens = exceptionHandler.tryBlock {
        val response = client.post(
            urlString = "${appConfig.baseUrl}/user/refresh"
        ) {
            headers {
                append("Authorization", refreshToken)
            }
        }

        val responseBody : Response<UserTokens> = response.body()
        return@tryBlock responseBody.data
    }

    override suspend fun register(userName: String, email: String, password: String): UserTokens = exceptionHandler.tryBlock {
        val response = client.post(
            urlString = "${appConfig.baseUrl}/user/register"
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    userName = userName,
                    email = email,
                    password = password
                )
            )
        }

        val responseBody : Response<UserTokens> = response.body()
        return@tryBlock responseBody.data
    }

    override suspend fun getSelf(bearerToken: String): User = exceptionHandler.tryBlock {
        val response = client.get(
            urlString = "${appConfig.baseUrl}/user/me"
        ) {
            headers {
                append("Authorization", bearerToken)
            }
        }
        val responseBody : Response<User> = response.body()
        return@tryBlock responseBody.data
    }

    override suspend fun getUsers(bearerToken: String): List<User> = exceptionHandler.tryBlock {
        val response = client.get(
            urlString = "${appConfig.baseUrl}/user"
        ) {
            headers {
                append("Authorization", bearerToken)
            }
        }
        val responseBody : Response<List<User>> = response.body()
        return@tryBlock responseBody.data
    }

    override suspend fun changeUserRole(bearerToken: String, userId: Int, role: User.Role): User = exceptionHandler.tryBlock {
        val response = client.post(
            urlString = "${appConfig.baseUrl}/user/$userId/changeRole"
        ) {
            headers {
                append("Authorization", bearerToken)
            }
            contentType(ContentType.Application.Json)
            setBody(
                UserChangeRoleRequest(
                    role = role.name
                )
            )
        }

        val responseBody : Response<User> = response.body()
        return@tryBlock responseBody.data
    }

    override suspend fun getUser(bearerToken: String, userId: Int): User = exceptionHandler.tryBlock {
        val response = client.get(
            urlString = "${appConfig.baseUrl}/user/$userId"
        ) {
            headers {
                append("Authorization", bearerToken)
            }
        }
        val responseBody : Response<User> = response.body()
        return@tryBlock responseBody.data
    }

    override suspend fun changeUserPassword(bearerToken: String, userId: Int, password: String): Unit = exceptionHandler.tryBlock {
        client.post(
            urlString = "${appConfig.baseUrl}/user/$userId/changePassword"
        ) {
            headers {
                append("Authorization", bearerToken)
            }
            contentType(ContentType.Application.Json)
            setBody(
                UserChangePasswordRequest(
                    password = password
                )
            )
        }
    }
}
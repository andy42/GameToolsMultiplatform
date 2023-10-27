package com.jaehl.gameTool.apiClientKtor.data.service

import com.jaehl.gameTool.apiClientKtor.data.model.Response
import com.jaehl.gameTool.apiClientKtor.data.util.ExceptionHandler
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.repo.TokenProvider
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

open class RequestUtil(
    val client: HttpClient,
    val appConfig : AppConfig,
    val exceptionHandler : ExceptionHandler,
    val tokenProvider : TokenProvider
) {
    suspend inline fun <reified T> createRequest(
        url : String,
        httpMethod : HttpMethod = HttpMethod.Get,
        requestBody : Any? = null
    ) : T = exceptionHandler.tryBlock {
        val bearerAccessToken = tokenProvider.getBearerAccessToken()
        val response = client.request("${appConfig.baseUrl}/$url") {
            method = httpMethod

            headers {
                append("Authorization",bearerAccessToken)
            }

            if(requestBody != null){
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
        }

        val responseBody : Response<T> = response.body()
        return@tryBlock responseBody.data
    }

    suspend fun createRequestNoResponse(
        url : String,
        httpMethod : HttpMethod = HttpMethod.Get,
        requestBody : Any? = null
    ) = exceptionHandler.tryBlock {
        val bearerAccessToken = tokenProvider.getBearerAccessToken()
        client.request("${appConfig.baseUrl}/$url") {
            method = httpMethod

            headers {
                append("Authorization",bearerAccessToken)
            }

            if(requestBody != null){
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
        }
    }
}
package com.jaehl.gameTool.apiClientRetrofit.data.api

import com.jaehl.gameTool.apiClientRetrofit.data.model.Response
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.CreateGameRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.LoginRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.RegisterRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.UpdateGameRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.response.AccessTokenResponse
import com.jaehl.gameTool.common.data.model.AccessToken
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ServerApi {

    @POST("user/login")
    fun login(
        @Body data : LoginRequest
    ) : Call<Response<AccessToken>>

    @POST("user/register")
    fun register(
        @Body data : RegisterRequest
    ) : Call<Response<AccessToken>>

    @GET("user/me")
    fun getUserSelf(
        @Header("Authorization") bearerToken : String,
    ) : Call<Response<User>>

    @GET("user")
    fun getUsers(
        @Header("Authorization") bearerToken : String,
    ) : Call<Response<List<User>>>

    @POST("games/new")
    fun createGame(
        @Header("Authorization") bearerToken : String,
        @Body data : CreateGameRequest
    ) : Call<Response<AccessTokenResponse>>

    @POST("games/{id}")
    fun updateGame(
        @Header("Authorization") bearerToken : String,
        @Path("id") id : String,
        @Body data : UpdateGameRequest
    ) : Call<Response<AccessTokenResponse>>

    @DELETE("games/{id}")
    fun deleteGame(
        @Header("Authorization") bearerToken : String,
        @Path("id") id : String,
    ) : Call<Response<AccessTokenResponse>>

    @GET("games/{id}")
    fun getGame(
        @Header("Authorization") bearerToken : String,
        @Path("id") id : String,
    ) : Call<Response<AccessTokenResponse>>

    @GET("games")
    fun getGame(
        @Header("Authorization") bearerToken : String,
    ) : Call<Response<AccessTokenResponse>>
}
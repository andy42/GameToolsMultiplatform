package com.jaehl.gameTool.apiClientRetrofit.data.api

import com.jaehl.gameTool.apiClientRetrofit.data.model.Response
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.*
import com.jaehl.gameTool.apiClientRetrofit.data.model.response.AccessTokenResponse
import com.jaehl.gameTool.common.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

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
    ) : Call<Response<Game>>

    @POST("games/{id}")
    fun updateGame(
        @Header("Authorization") bearerToken : String,
        @Path("id") id : Int,
        @Body data : UpdateGameRequest
    ) : Call<Response<Game>>

    @DELETE("games/{id}")
    fun deleteGame(
        @Header("Authorization") bearerToken : String,
        @Path("id") id : Int,
    ) : Call<Unit>

    @GET("games/{id}")
    fun getGame(
        @Header("Authorization") bearerToken : String,
        @Path("id") id : Int,
    ) : Call<Response<Game>>

    @GET("games")
    fun getGames(
        @Header("Authorization") bearerToken : String,
    ) : Call<Response<List<Game>>>


    @GET("items")
    fun getItems(
        @Header("Authorization") bearerToken : String,
        @Query("gameId") gameId : Int
    ) : Call<Response<List<Item>>>

    @GET("items/{id}")
    fun getItem(
        @Header("Authorization") bearerToken : String,
        @Path("id") id : Int
    ) : Call<Response<Item>>

    @POST("items/new")
    fun addItem(
        @Header("Authorization") bearerToken : String,
        @Body data : AddItemRequest
    ) : Call<Response<Item>>

    @GET("items/Categories")
    fun getItemCategories(
        @Header("Authorization") bearerToken : String
    ) : Call<Response<List<ItemCategory>>>

    @POST("items/Categories/new")
    fun addItemCategories(
        @Header("Authorization") bearerToken : String,
        @Body data : AddItemCategoriesRequest
    ) : Call<Response<ItemCategory>>

    @Multipart
    @POST("images/new")
    fun addImage(
        @Header("Authorization") bearerToken : String,
        @PartMap() partMap: MutableMap<String,RequestBody>,
        @Part image: MultipartBody.Part
    ) : Call<Response<Image>>
}
package com.jaehl.gameTool.apiClientRetrofit.data.api

import com.jaehl.gameTool.apiClientRetrofit.data.model.Response
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.*
import com.jaehl.gameTool.common.data.model.*
import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.common.data.model.request.NewAdminCollectionRequest
import com.jaehl.gameTool.common.data.model.request.NewCollectionRequest
import com.jaehl.gameTool.common.data.model.request.UpdateCollectionRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
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
    suspend fun login(
        @Body data : LoginRequest
    ) : Response<UserTokens>

    @POST("user/refresh")
    suspend fun refreshTokens(
        @Header("Authorization") bearerToken : String
    ) : Response<UserTokens>

    @POST("user/register")
    suspend fun register(
        @Body data : RegisterRequest
    ) : Response<UserTokens>

    @GET("user/me")
    suspend fun getUserSelf(
        @Header("Authorization") bearerToken : String,
    ) : Response<User>

    @GET("user")
    suspend fun getUsers(
        @Header("Authorization") bearerToken : String,
    ) : Response<List<User>>

    @POST("user/changeRole")
    suspend fun changeUserRole(
        @Header("Authorization") bearerToken : String,
        @Body data : UserChangeRoleRequest
    ) : Response<User>

    @POST("games/new")
    suspend fun createGame(
        @Header("Authorization") bearerToken : String,
        @Body data : CreateGameRequest
    ) : Response<Game>

    @POST("games/{id}")
    suspend fun updateGame(
        @Header("Authorization") bearerToken : String,
        @Path("id") id : Int,
        @Body data : UpdateGameRequest
    ) : Response<Game>

    @DELETE("games/{id}")
    suspend fun deleteGame(
        @Header("Authorization") bearerToken : String,
        @Path("id") id : Int,
    )

    @GET("games/{id}")
    suspend fun getGame(
        @Header("Authorization") bearerToken : String,
        @Path("id") id : Int,
    ) : Response<Game>

    @GET("games")
    suspend fun getGames(
        @Header("Authorization") bearerToken : String,
    ) : Response<List<Game>>


    @GET("items")
    suspend fun getItems(
        @Header("Authorization") bearerToken : String,
        @Query("gameId") gameId : Int
    ) : Response<List<Item>>

    @GET("items")
    suspend fun getItems(
        @Header("Authorization") bearerToken : String,
    ) : Response<List<Item>>

    @GET("items/{id}")
    suspend fun getItem(
        @Header("Authorization") bearerToken : String,
        @Path("id") id : Int
    ) : Response<Item>

    @POST("items/new")
    suspend fun addItem(
        @Header("Authorization") bearerToken : String,
        @Body data : AddItemRequest
    ) : Response<Item>

    @POST("items/{id}")
    suspend fun updateItem(
        @Header("Authorization") bearerToken : String,
        @Path("id") id : Int,
        @Body data : UpdateItemRequest
    ) : Response<Item>

    @GET("items/Categories")
    suspend fun getItemCategories(
        @Header("Authorization") bearerToken : String
    ) : Response<List<ItemCategory>>

    @POST("items/Categories/new")
    suspend fun addItemCategories(
        @Header("Authorization") bearerToken : String,
        @Body data : AddItemCategoriesRequest
    ) : Response<ItemCategory>

    @Multipart
    @POST("images/new")
    suspend fun addImage(
        @Header("Authorization") bearerToken : String,
        @PartMap() partMap: MutableMap<String,RequestBody>,
        @Part image: MultipartBody.Part
    ) : Response<ImageMetaData>

    @GET("images/{id}")
    suspend fun getImage(
        @Header("Authorization") bearerToken : String,
        @Path("id") id : Int,
    ) : ResponseBody

    @GET("images")
    suspend fun getImages(
        @Header("Authorization") bearerToken : String
    ) : Response<List<ImageMetaData>>

    @POST("recipes/new")
    suspend fun addRecipe(
        @Header("Authorization") bearerToken : String,
        @Body data : AddRecipeRequest
    ) : Response<Recipe>

    @POST("recipes/{id}")
    suspend fun updateRecipe(
        @Header("Authorization") bearerToken : String,
        @Path("id") id : Int,
        @Body data : UpdateRecipeRequest
    ) : Response<Recipe>

    @DELETE("recipes/{id}")
    suspend fun deleteRecipe(
        @Header("Authorization") bearerToken : String,
        @Path("id") id : Int
    )

    @GET("recipes")
    suspend fun getRecipes(
        @Header("Authorization") bearerToken : String,
        @Query("gameId") gameId : Int
    ) : Response<List<Recipe>>

    @GET("recipes")
    suspend fun getRecipes(
        @Header("Authorization") bearerToken : String,
    ) : Response<List<Recipe>>

    @GET("recipes/{id}")
    suspend fun getRecipe(
        @Header("Authorization") bearerToken : String,
        @Path("id") id : Int
    ) : Response<Recipe>

    @GET("collections")
    suspend fun getCollections(
        @Header("Authorization") bearerToken : String
    ) : Response<List<Collection>>

    @GET("collections")
    suspend fun getCollections(
        @Header("Authorization") bearerToken : String,
        @Query("gameId") gameId : Int
    ) : Response<List<Collection>>

    @GET("collections/{id}")
    suspend fun getCollection(
        @Header("Authorization") bearerToken : String,
        @Path("id") id : Int
    ) : Response<Collection>

    @POST("collections/new")
    suspend fun addCollection(
        @Header("Authorization") bearerToken : String,
        @Body data : NewCollectionRequest
    ) : Response<Collection>

    @POST("admin/collections/New")
    suspend fun addAdminCollection(
        @Header("Authorization") bearerToken : String,
        @Body data : NewAdminCollectionRequest
    ) : Response<Collection>


    @POST("collections/{collectionId}")
    suspend fun updateCollection(
        @Header("Authorization") bearerToken : String,
        @Path("collectionId") collectionId : Int,
        @Body data : UpdateCollectionRequest
    ) : Response<Collection>

    @DELETE("collections/{id}")
    suspend fun deleteCollection(
        @Header("Authorization") bearerToken : String,
        @Path("id") id : Int
    )

    @POST("collections/{collectionId}/new")
    suspend fun addCollectionGroup(
        @Header("Authorization") bearerToken : String,
        @Path("collectionId") collectionId : Int,
        @Body data : AddCollectionGroupRequest
    ) : Response<Collection.Group>

    @DELETE("collections/{collectionId}/{groupId}")
    suspend fun deleteCollectionGroup(
        @Header("Authorization") bearerToken : String,
        @Path("collectionId") collectionId : Int,
        @Path("groupId") groupId : Int
    )


    @POST("collections/{collectionId}/{groupId}/{itemId}")
    suspend fun addUpdateItemAmount(
        @Header("Authorization") bearerToken : String,
        @Path("collectionId") collectionId : Int,
        @Path("groupId") groupId : Int,
        @Path("itemId") itemId : Int,
        @Body data : AddCollectionItemAmountRequest
    ) : Response<Collection.ItemAmount>

    @DELETE("collections/{collectionId}/{groupId}/{itemId}")
    suspend fun deleteItemAmount(
        @Header("Authorization") bearerToken : String,
        @Path("collectionId") collectionId : Int,
        @Path("groupId") groupId : Int,
        @Path("itemId") itemId : Int,
    )

    @GET("admin/backups")
    suspend fun getBackups(
        @Header("Authorization") bearerToken : String,
    ) : Response<List<Backup>>

    @POST("admin/backups/create")
    suspend fun createBackup(
        @Header("Authorization") bearerToken : String,
    ) : Response<Backup>

    @POST("admin/backups/apply/{backupId}")
    suspend fun applyBackup(
        @Header("Authorization") bearerToken : String,
        @Path("backupId") backupId : String,
    )
}
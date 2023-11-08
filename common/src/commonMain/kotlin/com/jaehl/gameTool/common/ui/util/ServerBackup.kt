package com.jaehl.gameTool.common.ui.util

import com.google.gson.reflect.TypeToken
import com.jaehl.gameTool.common.data.local.ObjectListJsonLoader
import com.jaehl.gameTool.common.data.model.*
import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.common.data.model.request.NewAdminCollectionRequest
import com.jaehl.gameTool.common.data.model.request.NewCollectionRequest
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.service.*
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

class ServerBackup(
    private val tokenProvider: TokenProvider,
    private val userService: UserService,
    private val gameService: GameService,
    private val itemService : ItemService,
    private val imageService: ImageService,
    private val recipeService: RecipeService,
    private val collectionService: CollectionService
) {

    private val usersLoader = ObjectListJsonLoader<User>(
        type = object : TypeToken<Array<User>>() {}.type,
        projectUserDir = projectUserDir,
        listFilePath = "users.json"
    )

    private val gamesLoader = ObjectListJsonLoader<Game>(
        type = object : TypeToken<Array<Game>>() {}.type,
        projectUserDir = projectUserDir,
        listFilePath = "games.json"
    )

    private val itemsLoader = ObjectListJsonLoader<Item>(
        type = object : TypeToken<Array<Item>>() {}.type,
        projectUserDir = projectUserDir,
        listFilePath = "items.json"
    )

    private val itemCategoryLoader = ObjectListJsonLoader<ItemCategory>(
        type = object : TypeToken<Array<Item>>() {}.type,
        projectUserDir = projectUserDir,
        listFilePath = "itemCategory.json"
    )

    private val recipeLoader = ObjectListJsonLoader<Recipe>(
        type = object : TypeToken<Array<Recipe>>() {}.type,
        projectUserDir = projectUserDir,
        listFilePath = "recipes.json"
    )

    private val imagesLoader = ObjectListJsonLoader<ImageMetaData>(
        type = object : TypeToken<ImageMetaData>() {}.type,
        projectUserDir = projectUserDir,
        listFilePath = "images.json"
    )

    private val collectionLoader = ObjectListJsonLoader<Collection>(
        type = object : TypeToken<Array<Collection>>() {}.type,
        projectUserDir = projectUserDir,
        listFilePath = "collections.json"
    )

    private fun getFile(projectUserDir : String, filePath : String) : File {
        val directory = Paths.get(System.getProperty("user.home"), projectUserDir)
        if( !directory.exists()){
            directory.createDirectory()
        }
        val path = Paths.get(System.getProperty("user.home"),
            projectUserDir,
            filePath
        )
        return path.toFile()
    }

    private suspend fun backupCollections(){
        val collections = collectionService.getCollections()
        collectionLoader.save(collections)
    }

    suspend fun backup() {
        val users = userService.getUsers(tokenProvider.getBearerAccessToken())
        usersLoader.save(users)

        val games = gameService.getGames()
        gamesLoader.save(games)

        val items = itemService.getItems()
        itemsLoader.save(items)

        val itemCategories = itemService.getItemCategories()
        itemCategoryLoader.save(itemCategories)

        val recipes = recipeService.getRecipes()
        recipeLoader.save(recipes)

        val collections = collectionService.getCollections()
        collectionLoader.save(collections)

        val images = imageService.getImages()
        images.forEach { imageMetaData ->
            val imageData = imageService.getImage(imageMetaData.id)
            val imageType = ImageType.from(imageMetaData.imageType)
            getFile("$projectUserDir/images","${imageMetaData.id}.${imageType.fileExtension}").writeBytes(imageData)
            //File("$basePath/images/${imageMetaData.id}.${imageType.fileExtension}").writeBytes(imageData)
        }
        imagesLoader.save(images)
    }

    suspend fun uploadBackup(){
        val userMap = hashMapOf<Int, User>()
        val imageMap = hashMapOf<Int, ImageMetaData>()
        val gameMap = hashMapOf<Int, Game>()
        val itemCategoryMap = hashMapOf<Int, ItemCategory>()
        val itemMap = hashMapOf<Int, Item>()

        usersLoader.load().forEach { user ->
            val tokenResponse = userService.register(
                userName = user.userName,
                email = user.email,
                password = "Password"
            )
            val userResponse = userService.getSelf(tokenResponse.accessToken)
            userMap[user.id] = userResponse
        }

        imagesLoader.load().forEach { imageMetaData ->
            val imageType = ImageType.from(imageMetaData.imageType)
            val response = imageService.addImage(
                imageFile = getFile("$projectUserDir/images","${imageMetaData.id}.${imageType.fileExtension}"),
                imageType = imageType,
                description = imageMetaData.description
            )
            imageMap[imageMetaData.id] = response
        }

        itemCategoryLoader.load().forEach { itemCategory ->
            itemCategoryMap[itemCategory.id] = itemService.addItemCategories(itemCategory.name)
        }

        itemsLoader.load().forEach { item ->
            val response = itemService.addItem(
                game = gameMap[item.game]?.id ?: throw Exception("game id not found"),
                name = item.name,
                categories = item.categories.map {
                    itemCategoryMap[it.id]?.id ?: throw Exception("itemCategory not found")
                },
                image = imageMap[item.id]?.id ?: throw Exception("image not found")
            )
            itemMap[item.id] = response
        }

        recipeLoader.load().forEach { recipe ->
            recipeService.createRecipes(
                gameId = gameMap[recipe.gameId]?.id ?: throw Exception("game not found"),
                craftedAt = recipe.craftedAt.map {
                    itemMap[it]?.id ?: throw Exception("item not found : $it")
                 },
                input = recipe.input.map {
                    ItemAmount(
                        itemId = itemMap[it.itemId]?.id ?: throw Exception("item not found : $it"),
                        amount = it.amount
                    )
                 },
                output = recipe.output.map {
                    ItemAmount(
                        itemId = itemMap[it.itemId]?.id ?: throw Exception("item not found : $it"),
                        amount = it.amount
                    )
                }
            )
        }
        collectionLoader.load().forEach { collection ->
            collectionService.addAdminCollection(
                NewAdminCollectionRequest(
                    userId = userMap[collection.userId]?.id ?: throw Exception("user not found"),
                    gameId = gameMap[collection.gameId]?.id ?: throw Exception("game id not found"),
                    name = collection.name,
                    groups = collection.groups.map { group ->
                        NewCollectionRequest.NewGroup(
                            name = group.name,
                            itemAmounts = group.itemAmounts.map { itemAmount ->
                                Collection.ItemAmount(
                                    itemId = itemMap[itemAmount.itemId]?.id ?: throw Exception("item not found : ${itemAmount.itemId}"),
                                    amount = itemAmount.amount
                                )
                            }
                        )
                    }
                )
            )
        }
    }

    companion object {
        private val projectUserDir = "gameToolsApiBackup"
    }
}
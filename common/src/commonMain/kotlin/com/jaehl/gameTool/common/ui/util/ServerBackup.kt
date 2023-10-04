package com.jaehl.gameTool.common.ui.util

import com.jaehl.gameTool.common.data.model.ImageType
import com.jaehl.gameTool.common.data.service.*
import java.io.File
import java.security.AuthProvider

class ServerBackup(
    val userService: UserService,
    val gameService: GameService,
    val itemService : ItemService,
    val imageService: ImageService,
    val recipeService: RecipeService,
    val collectionService: CollectionService,
    val authProvider: AuthProvider
) {
    fun backup() {
//        val basePath = ""
//        val games = gameService.getGames()
//        val items = itemService.getItems()
//        val itemCategories = itemService.getItemCategories()
//        val images = imageService.getImages()
//        images.forEach { imageMetaData ->
//            val imageData = imageService.getImage(imageMetaData.id)
//            val imageType = ImageType.from(imageMetaData.imageType)
//            File("$basePath/images/${imageMetaData.id}.${imageType.fileExtension}").writeBytes(imageData)
//        }
//        val recipes = recipeService.getRecipes()
    }
}
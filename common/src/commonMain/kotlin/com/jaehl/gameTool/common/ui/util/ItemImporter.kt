package com.jaehl.gameTool.common.ui.util

import com.google.gson.reflect.TypeToken
import com.jaehl.gameTool.common.data.local.ObjectListJsonLoader
import com.jaehl.gameTool.common.data.model.ImageType
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemAmount
import com.jaehl.gameTool.common.data.service.ImageService
import com.jaehl.gameTool.common.data.service.ItemService
import com.jaehl.gameTool.common.data.service.RecipeService
import java.io.File
import java.nio.file.Paths

class ItemImporter(
    val itemService : ItemService,
    val imageService: ImageService,
    val recipeService: RecipeService
) {

    private fun getImageFile(filePath : String) : File{
        return Paths.get(System.getProperty("user.home"),
            "gameTools",
            filePath
        ).toFile()
    }

    fun import(gameId : Int){

        val categoriesResponse = itemService.getItemCategories()
        val categoriesMap = mutableMapOf<String, Int>()
        categoriesResponse.forEach{
            categoriesMap[it.name] = it.id
        }

        val itemLoader = ObjectListJsonLoader<ItemData>(
            type = object : TypeToken<Array<ItemData>>() {}.type,
            projectUserDir = "gameTools",
            listFilePath = "starfield_artemis/items.json"
        )

        val itemLocalMap = mutableMapOf<String, ItemData>()
        itemLoader.load().forEach {
            itemLocalMap[it.id] = it
        }

        val itemServerMap = mutableMapOf<String, Item>()
        itemService.getItems(gameId).forEach {
            itemServerMap[it.name] = it
        }

        //add missing categories
        itemLocalMap.values.forEach{ itemData ->
            itemData.categories.forEach{
                if(!categoriesMap.containsKey(it)){
                    val response = itemService.addItemCategories(it)
                    categoriesMap[response.name] = response.id
                }
            }
        }

        itemLocalMap.values.forEach { itemData ->
            if(itemServerMap.containsKey(itemData.name)){
                itemData.serverId = itemServerMap[itemData.name]?.id ?: -1
                return@forEach
            }

            val imageFile = getImageFile(itemData.iconPath)
            val imageType = ImageType.fromFileExtension(itemData.iconPath.split(".").lastOrNull() ?: "")
            if(!imageFile.exists()){
                System.out.println("image missing ${itemData.iconPath}")
                return@forEach
            }

            val imageResponse = imageService.addImage(imageFile, imageType, itemData.name)

            val item = itemService.addItem(
                game = gameId,
                name = itemData.name,
                categories = itemData.categories.map { categoriesMap[it] ?: -1 },
                image = imageResponse.imageId
            )
            itemData.serverId = item.id
        }

        ObjectListJsonLoader<RecipeData>(
            type = object : TypeToken<Array<RecipeData>>() {}.type,
            projectUserDir = "gameTools",
            listFilePath = "starfield_artemis/recipes.json"
        ).load().forEach { recipeData ->
            val input = recipeData.input.mapNotNull {
                ItemAmount(
                    itemId = itemLocalMap[it.itemId]?.serverId ?: return@mapNotNull null,
                    amount = it.amount
                )
            }
            val output = recipeData.output.mapNotNull {
                ItemAmount(
                    itemId = itemLocalMap[it.itemId]?.serverId ?: return@mapNotNull null,
                    amount = it.amount
                )
            }
            if(output.firstOrNull{it.itemId == 0} != null) return@forEach

            try {
                recipeService.createRecipes(
                    gameId = gameId,
                    craftedAt = recipeData.craftedAt.map {
                        itemLocalMap[it]?.serverId ?: return@forEach
                    },
                    input = input,
                    output = output
                )
            }
            catch (t : Throwable){
                System.out.println("Throwable : ${t.message}")
            }
        }
    }
}

data class ItemData(
    var serverId : Int = -1,
    val id : String,
    val name : String,
    val categories : List<String>,
    val iconPath : String
)

data class RecipeData(
    val craftedAt : List<String>,
    val input : List<RecipeAmountData>,
    val output : List<RecipeAmountData>
)

data class RecipeAmountData(
    val itemId : String,
    val amount : Int
)
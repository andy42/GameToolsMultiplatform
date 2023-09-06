package com.jaehl.gameTool.common.ui.screens.gameDetails

import com.google.gson.reflect.TypeToken
import com.jaehl.gameTool.common.data.local.ObjectListJsonLoader
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.service.ImageService
import com.jaehl.gameTool.common.data.service.ItemService
import java.io.File
import java.nio.file.Paths

object ItemImporter {

    private fun getImageFile(filePath : String) : File{
        return Paths.get(System.getProperty("user.home"),
            "gameTools",
            filePath
        ).toFile()
    }

    fun import(itemService : ItemService, imageService: ImageService, gameId : Int){

        val categoriesResponse = itemService.getItemCategories()
        val categoriesMap = mutableMapOf<String, Int>()
        categoriesResponse.forEach{
            categoriesMap[it.name] = it.id
        }

        val loader = ObjectListJsonLoader<ItemData>(
            type = object : TypeToken<Array<ItemData>>() {}.type,
            projectUserDir = "gameTools",
            listFilePath = "icarus/items.json"
        )
        val test = loader.load()


        val itemMap = mutableMapOf<String, Item>()
        itemService.getItems(gameId).forEach {
            itemMap[it.name] = it
        }

        //add missing categories
        test.forEach{ itemData ->
            itemData.categories.forEach{
                if(!categoriesMap.containsKey(it)){
                    val response = itemService.addItemCategories(it)
                    categoriesMap[response.name] = response.id
                }
            }
        }

        test.forEach { itemData ->
            if(itemMap.containsKey(itemData.name)) return

            val imageFile = getImageFile(itemData.iconPath)
            if(!imageFile.exists()){
                System.out.println("image missing ${itemData.iconPath}")
            }

            val imageResponse = imageService.addImage(imageFile, itemData.name)

            itemService.addItem(
                game = gameId,
                name = itemData.name,
                categories = itemData.categories.map { categoriesMap[it] ?: -1 },
                image = imageResponse.imageId
            )
        }
    }
}
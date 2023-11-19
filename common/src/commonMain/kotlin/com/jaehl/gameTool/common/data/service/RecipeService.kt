package com.jaehl.gameTool.common.data.service

import com.jaehl.gameTool.common.data.model.ItemAmount
import com.jaehl.gameTool.common.data.model.Recipe

interface RecipeService {
    suspend fun getRecipes(gameId : Int?) : List<Recipe>
    suspend fun getRecipes() : List<Recipe>
    suspend fun getRecipe(recipeId : Int) : Recipe
    suspend fun createRecipes(
        gameId : Int,
        craftedAt : List<Int>,
        input : List<ItemAmount>,
        output : List<ItemAmount>
    ) : Recipe

    suspend fun updateRecipes(
        recipeId : Int,
        gameId : Int,
        craftedAt : List<Int>,
        input : List<ItemAmount>,
        output : List<ItemAmount>
    ) : Recipe

    suspend fun deleteRecipe(recipeId : Int)
}
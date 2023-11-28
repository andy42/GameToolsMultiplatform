package com.jaehl.gameTool.common.data.local

import com.jaehl.gameTool.common.data.model.Recipe
import com.jaehl.gameTool.common.ui.util.UiException

interface RecipeLocalSource {
    suspend fun getRecipes(gameId : Int?) : List<Recipe>
    suspend fun getRecipe(id : Int) : Recipe

    suspend fun deleteRecipe(id : Int)

    suspend fun updateRecipe(recipe : Recipe)
    suspend fun updateRecipes(gameId : Int?, recipes : List<Recipe>)

    suspend fun getRecipesForOutput(itemId : Int) : List<Recipe>
    suspend fun getRecipesForInput(itemId : Int) : List<Recipe>
}

class RecipeLocalSourceInMemory() : RecipeLocalSource {

    private val recipesMap = hashMapOf<Int, Recipe>()

    override suspend fun getRecipes(gameId: Int?): List<Recipe> {
        return if(gameId == null) {
            recipesMap.values.toList()
        } else {
            recipesMap.values.filter { it.gameId == gameId }
        }
    }

    override suspend fun getRecipe(id: Int): Recipe {
        return recipesMap[id] ?: throw UiException.NotFound("Recipe not found : $id")
    }

    override suspend fun deleteRecipe(id: Int) {
        recipesMap.remove(id)
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        recipesMap[recipe.id] = recipe
    }

    override suspend fun updateRecipes(gameId : Int?, recipes: List<Recipe>) {
        if(gameId != null) {
            recipesMap.entries.removeIf {
                it.value.gameId == gameId
            }
        }
        recipes.forEach {
            recipesMap[it.id] = it
        }
    }

    override suspend fun getRecipesForOutput(itemId: Int): List<Recipe> {
        return recipesMap.values.filter {  recipe ->
            recipe.output.firstOrNull { it.itemId == itemId } != null
        }
    }

    override suspend fun getRecipesForInput(itemId: Int): List<Recipe> {
        return recipesMap.values.filter {  recipe ->
            recipe.input.firstOrNull { it.itemId == itemId } != null
        }
    }
}
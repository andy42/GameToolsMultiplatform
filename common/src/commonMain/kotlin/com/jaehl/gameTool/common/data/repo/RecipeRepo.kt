package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.model.ItemAmount
import com.jaehl.gameTool.common.data.model.Recipe
import com.jaehl.gameTool.common.data.service.RecipeService
import java.lang.Exception

interface RecipeRepo {
    suspend fun preloadRecipes(gameId : Int)
    suspend fun getRecipes(gameId : Int) : List<Recipe>
    suspend fun getRecipe(id : Int) : Recipe
    suspend fun getRecipesForOutput(inputItemId : Int) : List<Recipe>

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

class RecipeRepoImp(
    private val jobDispatcher: JobDispatcher,
    private val recipeService: RecipeService
) : RecipeRepo {

    private val recipeMap = LinkedHashMap<Int, Recipe>()
    private val recipeOutputMap = LinkedHashMap<Int, ArrayList<Recipe>>()
    private var loadedGameId : Int = -1

    private fun isGameLoaded(gameId : Int) : Boolean {
        return (loadedGameId == gameId)
    }

    private fun updateRecipeOutputArray(array : ArrayList<Recipe>?, recipe : Recipe) : ArrayList<Recipe>{
        val array = array ?: arrayListOf()
        array.add(recipe)
        return array
    }

    private fun updateOutputMap(){
        recipeOutputMap.clear()
        recipeMap.values.forEach { recipe ->
            recipe.output.forEach {itemAmount ->
                recipeOutputMap[itemAmount.itemId] = updateRecipeOutputArray(recipeOutputMap[itemAmount.itemId], recipe)
            }
        }
    }

    override suspend fun preloadRecipes(gameId : Int){
        if(!isGameLoaded(gameId)){
            updateFromServer(gameId)
        }
    }

    private suspend fun updateFromServer(gameId : Int){

        val recipes = recipeService.getRecipes(gameId = gameId)
        loadedGameId = gameId
        recipeMap.clear()
        recipeOutputMap.clear()

        try {
            recipeMap.clear()
            recipes.forEach {
                recipeMap[it.id] = it
            }
            updateOutputMap()
        } catch (t : Throwable){
            System.out.println(t.message)
        }
    }

    override suspend fun getRecipes(gameId: Int): List<Recipe> {
        if(!isGameLoaded(gameId)) updateFromServer(gameId)
        return recipeService.getRecipes(gameId)
    }

    override suspend fun getRecipe(id: Int): Recipe {
        return recipeMap[id] ?: throw Exception("Recipe not found")
    }

    override suspend fun getRecipesForOutput(inputItemId: Int): List<Recipe> {
        return recipeOutputMap[inputItemId] ?: listOf()
    }

    override suspend fun createRecipes(
        gameId: Int,
        craftedAt: List<Int>,
        input: List<ItemAmount>,
        output: List<ItemAmount>
    ): Recipe {
        val recipe = recipeService.createRecipes(
            gameId = gameId,
            craftedAt = craftedAt,
            input = input,
            output = output
        )
        recipeMap[recipe.id] = recipe
        updateOutputMap()
        return recipe
    }

    override suspend fun updateRecipes(
        recipeId: Int,
        gameId : Int,
        craftedAt: List<Int>,
        input: List<ItemAmount>,
        output: List<ItemAmount>
    ): Recipe {
        val recipe = recipeService.updateRecipes(
            recipeId = recipeId,
            gameId = gameId,
            craftedAt = craftedAt,
            input = input,
            output = output
        )
        recipeMap[recipe.id] = recipe
        updateOutputMap()
        return recipe
    }

    override suspend fun deleteRecipe(recipeId: Int) {
        recipeService.deleteRecipe(recipeId)
        recipeMap.remove(recipeId)
        updateOutputMap()
    }
}
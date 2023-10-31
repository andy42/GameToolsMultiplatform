package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.local.RecipeLocalSource
import com.jaehl.gameTool.common.data.model.ItemAmount
import com.jaehl.gameTool.common.data.model.Recipe
import com.jaehl.gameTool.common.data.service.RecipeService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface RecipeRepo {
    suspend fun preloadRecipes(gameId : Int)
    suspend fun getRecipes(gameId : Int) : List<Recipe>
    suspend fun getRecipesFlow(gameId : Int) : Flow<Resource<List<Recipe>>>
    suspend fun getRecipe(id : Int) : Recipe
    suspend fun getRecipeFlow(id : Int) : Flow<Resource<Recipe>>
    suspend fun getRecipesForOutput(inputItemId : Int) : List<Recipe>
    suspend fun getRecipesForOutputFlow(gameId : Int, inputItemId : Int) : Flow<Resource<List<Recipe>>>

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
    private val recipeService: RecipeService,
    private val recipeLocalSource : RecipeLocalSource
) : RecipeRepo {

    private fun updateRecipeOutputArray(array : ArrayList<Recipe>?, recipe : Recipe) : ArrayList<Recipe>{
        val array = array ?: arrayListOf()
        array.add(recipe)
        return array
    }

    override suspend fun preloadRecipes(gameId : Int){
        recipeLocalSource.updateRecipes(gameId, recipeService.getRecipes(gameId))
    }

    override suspend fun getRecipes(gameId: Int): List<Recipe> {
        return recipeService.getRecipes(gameId)
    }

    override suspend fun getRecipeFlow(id: Int): Flow<Resource<Recipe>> =  flow {
        try {
            emit(Resource.Loading(recipeLocalSource.getRecipe(id)))
            val recipe = recipeService.getRecipe(id)
            recipeLocalSource.updateRecipe(recipe)
            emit(Resource.Success(recipe))
        }
        catch (t: Throwable){
            emit(Resource.Error(t))
        }
    }

    override suspend fun getRecipesFlow(gameId: Int): Flow<Resource<List<Recipe>>> = flow {
        try {
            emit(Resource.Loading(recipeLocalSource.getRecipes(gameId)))
            val recipes = recipeService.getRecipes(gameId)
            recipeLocalSource.updateRecipes(gameId, recipes)
            emit(Resource.Success(recipes))
        }
        catch (t : Throwable){
            emit(Resource.Error(t))
        }
    }

    override suspend fun getRecipe(id: Int): Recipe {
        return recipeLocalSource.getRecipe(id)
    }

    override suspend fun getRecipesForOutput(inputItemId: Int): List<Recipe> {
        return recipeLocalSource.getRecipesForOutput(inputItemId)
    }

    override suspend fun getRecipesForOutputFlow(gameId : Int, inputItemId: Int): Flow<Resource<List<Recipe>>> = flow {
        try {
            emit(Resource.Loading(recipeLocalSource.getRecipesForOutput(inputItemId)))
            val recipes = recipeService.getRecipes(gameId)
            recipeLocalSource.updateRecipes(gameId, recipes)
            emit(Resource.Success(recipeLocalSource.getRecipesForOutput(inputItemId)))
        }
        catch (t : Throwable){
            emit(Resource.Error(t))
        }
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
        recipeLocalSource.updateRecipe(recipe)
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
        recipeLocalSource.updateRecipe(recipe)
        return recipe
    }

    override suspend fun deleteRecipe(recipeId: Int) {
        recipeService.deleteRecipe(recipeId)
        recipeLocalSource.deleteRecipe(recipeId)
    }
}
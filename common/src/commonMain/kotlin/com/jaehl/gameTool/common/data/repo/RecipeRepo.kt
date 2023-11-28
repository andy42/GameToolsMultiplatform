package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.FlowResource
import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.local.RecipeLocalSource
import com.jaehl.gameTool.common.data.model.ItemAmount
import com.jaehl.gameTool.common.data.model.Recipe
import com.jaehl.gameTool.common.data.service.RecipeService
import kotlinx.coroutines.flow.flow

interface RecipeRepo {
    suspend fun getRecipesFlow(gameId : Int? = null) : FlowResource<List<Recipe>>
    suspend fun getRecipeFlow(id : Int) : FlowResource<Recipe>

    suspend fun getRecipesForOutputFlow(gameId : Int, outputItemId : Int) : FlowResource<List<Recipe>>
    suspend fun getRecipesForOutputCached(gameId : Int, inputItemId : Int) : List<Recipe>

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

    override suspend fun getRecipeFlow(id: Int) = flow {
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

    override suspend fun getRecipesFlow(gameId: Int?) = flow {
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

    override suspend fun getRecipesForOutputFlow(gameId : Int, outputItemId: Int) = flow {
        try {
            emit(Resource.Loading(recipeLocalSource.getRecipesForOutput(outputItemId)))
            val recipes = recipeService.getRecipes(gameId)
            recipeLocalSource.updateRecipes(gameId, recipes)
            emit(Resource.Success(recipeLocalSource.getRecipesForOutput(outputItemId)))
        }
        catch (t : Throwable){
            emit(Resource.Error(t))
        }
    }

    override suspend fun getRecipesForOutputCached(gameId: Int, outputItemId: Int): List<Recipe> {
        return recipeLocalSource.getRecipesForOutput(outputItemId)
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
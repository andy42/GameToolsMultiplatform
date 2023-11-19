package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.data.FlowResource
import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.model.ItemAmount
import com.jaehl.gameTool.common.data.model.Recipe
import kotlinx.coroutines.flow.flow

class RecipeRepoMock : RecipeRepo {

    val recipeList = arrayListOf<Recipe>()

    var getRecipesError : Resource.Error? = null
    var getRecipeError : Resource.Error? = null

    override suspend fun getRecipesFlow(gameId: Int?): FlowResource<List<Recipe>> = flow {
        getRecipesError?.let {
            emit(it)
            return@flow
        }
        emit(Resource.Success(
            recipeList
                .filter { it.gameId == gameId }
        ))
    }

    override suspend fun getRecipeFlow(id: Int): FlowResource<Recipe> = flow {
        getRecipeError?.let {
            emit(it)
            return@flow
        }
        emit(Resource.Success(
            recipeList.first { it.id == id }
        ))
    }

    override suspend fun getRecipesForOutputFlow(gameId: Int, inputItemId: Int): FlowResource<List<Recipe>> = flow {
        emit(Resource.Success(
            getRecipesForOutputCached(gameId, inputItemId)
        ))
    }

    override suspend fun getRecipesForOutputCached(gameId: Int, inputItemId: Int): List<Recipe> {
        val recipesMap = HashMap<Int, Recipe>()
        recipeList.forEach {
            recipesMap[it.id] = it
        }
        return recipesMap.values.filter {  recipe ->
            recipe.output.firstOrNull { it.itemId == inputItemId } != null
        }
    }

    private fun createUniqueRecipeId() : Int = (recipeList.lastOrNull()?.id  ?: 0) + 1

    override suspend fun createRecipes(
        gameId: Int,
        craftedAt: List<Int>,
        input: List<ItemAmount>,
        output: List<ItemAmount>
    ): Recipe {
        val recipe = Recipe(
            id = createUniqueRecipeId(),
            gameId = gameId,
            craftedAt = craftedAt,
            input = input,
            output = output
        )
        recipeList.add(recipe)
        return recipe
    }

    override suspend fun updateRecipes(
        recipeId: Int,
        gameId: Int,
        craftedAt: List<Int>,
        input: List<ItemAmount>,
        output: List<ItemAmount>
    ): Recipe {
        val recipeIndex = recipeList.indexOfFirst { it.id == recipeId }
        val recipe = recipeList[recipeIndex].copy(
            gameId = gameId,
            craftedAt = craftedAt,
            input = input,
            output = output
        )
        recipeList[recipeIndex] = recipe
        return recipe
    }

    override suspend fun deleteRecipe(recipeId: Int) {
        recipeList.removeIf{it.id == recipeId }
    }

    fun clear() {
        recipeList.clear()

        getRecipesError = null
        getRecipeError = null
    }
}
package com.jaehl.gameTool.apiClientRetrofit.data.service

import com.jaehl.gameTool.apiClientRetrofit.data.api.ServerApi
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.AddRecipeRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.RecipeAmountRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.UpdateRecipeRequest
import com.jaehl.gameTool.common.data.model.ItemAmount
import com.jaehl.gameTool.common.data.model.Recipe
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.service.RecipeService

class RecipeServiceRetroFit(
    private val serverApi : ServerApi,
    private val tokenProvider : TokenProvider
) : RecipeService {

    override suspend fun getRecipes(gameId: Int?): List<Recipe> {
        if(gameId == null){
            return getRecipes()
        }
        return serverApi.getRecipes(
            bearerToken = tokenProvider.getBearerAccessToken(),
            gameId = gameId
        ).data
    }

    override suspend fun getRecipes(): List<Recipe> {
        return serverApi.getRecipes(
            bearerToken = tokenProvider.getBearerAccessToken(),
        ).data
    }

    override suspend fun getRecipe(recipeId: Int): Recipe {
        return serverApi.getRecipe(
            bearerToken = tokenProvider.getBearerAccessToken(),
            id = recipeId
        ).data
    }

    override suspend fun createRecipes(gameId : Int, craftedAt: List<Int>, input: List<ItemAmount>, output: List<ItemAmount>): Recipe {
        return serverApi.addRecipe(
            bearerToken = tokenProvider.getBearerAccessToken(),
            data = AddRecipeRequest(
                gameId = gameId,
                craftedAt = craftedAt,
                input = input.map { RecipeAmountRequest(
                    itemId = it.itemId,
                    amount = it.amount)
                },
                output = output.map { RecipeAmountRequest(
                    itemId = it.itemId,
                    amount = it.amount)
                },
            )
        ).data
    }

    override suspend fun updateRecipes(
        recipeId: Int,
        gameId: Int,
        craftedAt: List<Int>,
        input: List<ItemAmount>,
        output: List<ItemAmount>
    ): Recipe {
        return serverApi.updateRecipe(
            bearerToken = tokenProvider.getBearerAccessToken(),
            id = recipeId,
            data = UpdateRecipeRequest(
                gameId = gameId,
                craftedAt = craftedAt,
                input = input.map { RecipeAmountRequest(
                    itemId = it.itemId,
                    amount = it.amount)
                },
                output = output.map { RecipeAmountRequest(
                    itemId = it.itemId,
                    amount = it.amount)
                },
            )
        ).data
    }

    override suspend fun deleteRecipe(recipeId: Int) {
        serverApi.deleteRecipe(
            bearerToken = tokenProvider.getBearerAccessToken(),
            id = recipeId
        )
    }
}
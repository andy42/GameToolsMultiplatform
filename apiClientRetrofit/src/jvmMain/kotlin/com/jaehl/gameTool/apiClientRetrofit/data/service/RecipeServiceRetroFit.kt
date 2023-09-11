package com.jaehl.gameTool.apiClientRetrofit.data.service

import com.jaehl.gameTool.apiClientRetrofit.data.api.ServerApi
import com.jaehl.gameTool.apiClientRetrofit.data.model.baseBody
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.AddItemRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.AddRecipeRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.RecipeAmountRequest
import com.jaehl.gameTool.common.data.AuthProvider
import com.jaehl.gameTool.common.data.model.ItemAmount
import com.jaehl.gameTool.common.data.model.Recipe
import com.jaehl.gameTool.common.data.service.RecipeService

class RecipeServiceRetroFit(
    val serverApi : ServerApi,
    val authProvider: AuthProvider
) : RecipeService {

    override fun getRecipes(gameId: Int): List<Recipe> {
        return serverApi.getRecipes(
            bearerToken = authProvider.getBearerToken(),
            gameId = gameId
        ).baseBody()
    }

    override fun getRecipe(recipeId: Int): Recipe {
        return serverApi.getRecipe(
            bearerToken = authProvider.getBearerToken(),
            id = recipeId
        ).baseBody()
    }

    override fun createRecipes(gameId : Int, craftedAt: List<Int>, input: List<ItemAmount>, output: List<ItemAmount>): Recipe {
        return serverApi.addRecipe(
            bearerToken = authProvider.getBearerToken(),
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
        ).baseBody()
    }
}
package com.jaehl.gameTool.common.ui.util

import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemAmount
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.data.model.Recipe
import com.jaehl.gameTool.common.data.repo.ItemRepoMock
import com.jaehl.gameTool.common.data.repo.TokenProviderMock
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ItemRecipeNodeUtilTest {

    private val dispatcher = StandardTestDispatcher ()
    private val itemCategoriesMap = HashMap<Int, ItemCategory>()
    private val itemRepo = ItemRepoMock(itemCategoriesMap)
    private val appConfig = AppConfig()
    private val tokenProvider = TokenProviderMock()

    fun buildItemRecipeNodeUtil() : ItemRecipeNodeUtil{
        return ItemRecipeNodeUtil(
            itemRepo,
            appConfig,
            tokenProvider
        )
    }

    @Before
    fun before() {
        itemRepo.clear()
        itemCategoriesMap.clear()
    }

    private suspend fun createItem(name : String) : Item{
        return itemRepo.addItem(
            game = 1,
            name = name,
            categories = listOf(),
            image = 1
        )
    }
    private fun createItemAmount(item : Item, amount : Int) : ItemAmount{
        return ItemAmount(
            itemId = item.id,
            amount = amount
        )
    }

    @Test
    fun `circular recipe dependence`()= runTest(dispatcher) {
        val itemRecipeNodeUtil = buildItemRecipeNodeUtil()

        val item1 = createItem("item1")
        val item2 = createItem( "item2")

        val recipeItemOutputMap = mutableMapOf<Int, List<Recipe> >()
        recipeItemOutputMap[item1.id] = listOf(
            Recipe(
                input = listOf(
                    createItemAmount(item2, 100)
                ),
                output = listOf(
                    createItemAmount(item1, 1),
                    createItemAmount(item2, 99)
                )
            )
        )

        val node = itemRecipeNodeUtil.buildTree(
            itemAmount = ItemAmount(itemId = 1, amount = 1),
            parentNode = null,
            recipeId = null,
            itemRecipePreferenceMap = hashMapOf(),
            getRecipesForOutput = { itemId ->
                recipeItemOutputMap[itemId] ?: listOf()
            }
        )

        assertNotNull(node)
        assertEquals(item1.id, node.itemAmount.itemModel.id)
        assertEquals(1, node.itemAmount.amount)

        assertEquals(item2.id, node.inputs.first().itemAmount.itemModel.id)
        assertEquals(100, node.inputs.first().itemAmount.amount)

        assertEquals(item2.id, node.byProducts.first().itemModel.id)
        assertEquals(99, node.byProducts.first().amount)
    }
}
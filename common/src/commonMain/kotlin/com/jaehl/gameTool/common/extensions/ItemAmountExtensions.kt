package com.jaehl.gameTool.common.extensions

import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemAmount
import com.jaehl.gameTool.common.data.repo.ItemRepo
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel

suspend fun ItemAmount.toItemAmountViewModel(itemRepo : ItemRepo, appConfig : AppConfig, tokenProvider: TokenProvider) : ItemAmountViewModel {
    return ItemAmountViewModel(
        itemModel = itemRepo.getItemCached(this.itemId)?.toItemModel(appConfig, tokenProvider) ?: throw Exception("item not found ${this.itemId}"),
        amount = this.amount
    )
}

suspend fun ItemAmount.toItemAmountViewModel(item : Item, appConfig : AppConfig, tokenProvider: TokenProvider) : ItemAmountViewModel {
    return ItemAmountViewModel(
        itemModel = item.toItemModel(appConfig, tokenProvider),
        amount = this.amount
    )
}
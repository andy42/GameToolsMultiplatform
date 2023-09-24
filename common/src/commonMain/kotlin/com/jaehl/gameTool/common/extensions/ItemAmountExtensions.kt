package com.jaehl.gameTool.common.extensions

import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.AuthProvider
import com.jaehl.gameTool.common.data.model.ItemAmount
import com.jaehl.gameTool.common.data.repo.ItemRepo
import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel

fun ItemAmount.toItemAmountViewModel(itemRepo : ItemRepo ,appConfig : AppConfig, authProvider: AuthProvider) : ItemAmountViewModel {
    return ItemAmountViewModel(
        itemModel = itemRepo.getItem(this.itemId)?.toItemModel(appConfig, authProvider) ?: throw Exception("item not found ${this.itemId}"),
        amount = this.amount
    )
}
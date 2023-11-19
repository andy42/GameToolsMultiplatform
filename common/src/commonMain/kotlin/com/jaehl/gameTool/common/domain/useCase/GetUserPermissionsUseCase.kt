package com.jaehl.gameTool.common.domain.useCase

import com.jaehl.gameTool.common.data.FlowResource
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.common.data.repo.UserRepo
import com.jaehl.gameTool.common.domain.model.UserPermissions
import com.jaehl.gameTool.common.ui.util.resourceMap
import kotlinx.coroutines.flow.map

interface GetUserPermissionsUseCase {
    suspend operator fun invoke() : FlowResource<UserPermissions>
}

class GetUserPermissionsUseCaseImp(
    private val userRepo: UserRepo
) : GetUserPermissionsUseCase {

    private fun toUserState(user : User) : UserPermissions {
        return UserPermissions(
            isAdmin =  user.role == User.Role.Admin,
            isVerified = user.role != User.Role.Unverified,
            gameEditPermission = listOf(
                User.Role.Admin,
                User.Role.Contributor
            ).contains(user.role)
        )
    }

    override suspend fun invoke() = userRepo.getUserSelFlow().map { userResource ->
        return@map resourceMap(userResource, ::toUserState)
    }
}


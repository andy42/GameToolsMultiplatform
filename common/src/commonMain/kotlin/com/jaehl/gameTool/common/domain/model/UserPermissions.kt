package com.jaehl.gameTool.common.domain.model

data class UserPermissions (
    val isAdmin : Boolean,
    val isVerified : Boolean,
    val gameEditPermission : Boolean
)

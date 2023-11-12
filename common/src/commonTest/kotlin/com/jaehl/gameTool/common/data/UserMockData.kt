package com.jaehl.gameTool.common.data

import com.jaehl.gameTool.common.data.model.User

object UserMockData {
    val adminUser = User(
        id = 1,
        userName = "Admin",
        email = "admin@test.com",
        role = User.Role.Admin
    )

    val standardUser = User(
        id = 1,
        userName = "User",
        email = "user@test.com",
        role = User.Role.User
    )

    val contributorUser = User(
        id = 1,
        userName = "Contributor",
        email = "contributor@test.com",
        role = User.Role.Contributor
    )

    val unverifiedUser = User(
        id = 1,
        userName = "Unverified",
        email = "unverified@test.com",
        role = User.Role.Unverified
    )
}
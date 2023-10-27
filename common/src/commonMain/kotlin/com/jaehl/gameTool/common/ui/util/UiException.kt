package com.jaehl.gameTool.common.ui.util

sealed class UiException() : Throwable() {
    class ServerError(override val cause: Throwable? = null) : UiException()
    class ServerConnectionError(override val cause: Throwable? = null) : UiException()
    class ForbiddenError(override val cause: Throwable? = null) : UiException()
    class UnauthorizedError(override val cause: Throwable? = null) : UiException()
    class GeneralError(override val cause: Throwable? = null) : UiException()
}
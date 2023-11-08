package com.jaehl.gameTool.apiClientKtor.data.util

import com.jaehl.gameTool.common.ui.util.ServerErrorResponse
import com.jaehl.gameTool.common.ui.util.UiException
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import java.net.ConnectException

class ExceptionHandler {

    private suspend fun parseErrorResponse(response : HttpResponse) : ServerErrorResponse {
        return try {
            response.body()
        } catch (t : Throwable) {
            System.err.println(t.message)
            ServerErrorResponse(
                code = 500,
                message = "server error, invalid error response"
            )
        }
    }

    private fun toUiException(e : ClientRequestException, serverErrorResponse : ServerErrorResponse) : UiException {
        return when (serverErrorResponse.code) {
            401 -> UiException.UnauthorizedError(cause = e)
            403 -> UiException.ForbiddenError(cause = e)
            else -> UiException.ServerError(cause = e)
        }
    }

    private suspend fun parse(e: Throwable) : UiException {

        return when (e) {
            is ClientRequestException -> {
                val exceptionResponse = e.response
                toUiException(
                    e,
                    parseErrorResponse(exceptionResponse)
                )
            }

            is ConnectException -> {
                UiException.ServerConnectionError(cause = e)
            }

            else -> {
                UiException.GeneralError(cause = e)
            }
        }
    }

    suspend fun <T>tryBlock(block : suspend () -> T) : T{
        try {
            return block()
        }
        catch (t : Throwable) {
            throw parse(t)
        }
    }
}
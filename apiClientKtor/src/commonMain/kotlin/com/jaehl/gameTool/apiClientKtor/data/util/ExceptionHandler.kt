package com.jaehl.gameTool.apiClientKtor.data.util

import com.jaehl.gameTool.common.ui.util.ServerErrorResponse
import com.jaehl.gameTool.common.ui.util.UiException
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import java.net.ConnectException

class ExceptionHandler {

    private suspend fun parseErrorResponse(response : HttpResponse) : ServerErrorResponse {
        try {
            return response.body()
        }
        catch (t : Throwable) {
            System.err.println(t.message)
            return ServerErrorResponse(
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

        if(e is ClientRequestException) {
            val exceptionResponse = e.response
            return toUiException(
                e,
                parseErrorResponse(exceptionResponse)
            )
        }
        else if (e is ConnectException){
            return UiException.ServerConnectionError(cause = e)
        }
        else {
            return UiException.GeneralError(cause = e)
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
package com.jaehl.gameTool.apiClientKtor.data.service

import com.jaehl.gameTool.apiClientKtor.data.model.Response
import com.jaehl.gameTool.apiClientKtor.data.util.ExceptionHandler
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.model.ImageMetaData
import com.jaehl.gameTool.common.data.model.ImageType
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.service.ImageService
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.io.File

class ImageServiceKtor(
    private val client: HttpClient,
    private val appConfig : AppConfig,
    private val exceptionHandler : ExceptionHandler,
    private val tokenProvider : TokenProvider,
) : ImageService {

    override suspend fun addImage(imageFile: File, imageType: ImageType, description: String): ImageMetaData = exceptionHandler.tryBlock {
        val bearerAccessToken = tokenProvider.getBearerAccessToken()
        val response = client.submitFormWithBinaryData(
            url = "${appConfig.baseUrl}/images/new",
            formData = formData{
                append("description", description)
                append("imageType", imageType.value.toString() )
                append(
                    "image",
                    imageFile.readBytes(),
                    headers {
                        append(HttpHeaders.ContentType, "image/${imageType.fileExtension}")
                        append(HttpHeaders.ContentDisposition, "filename=${imageFile.getName()}")
                    }
                )
            }
        ) {
            headers { append("Authorization",bearerAccessToken) }
        }
        return@tryBlock response.body<Response<ImageMetaData>>().data
    }

    override suspend fun getImages(): List<ImageMetaData> = exceptionHandler.tryBlock {
        val bearerAccessToken = tokenProvider.getBearerAccessToken()
        val response = client.request("${appConfig.baseUrl}/images") {
            method = HttpMethod.Get
            headers { append("Authorization",bearerAccessToken) }
        }
        return@tryBlock response.body<Response<List<ImageMetaData>>>().data
    }

    override suspend fun getImage(id: Int): ByteArray = exceptionHandler.tryBlock {
        val bearerAccessToken = tokenProvider.getBearerAccessToken()
        val response = client.request("${appConfig.baseUrl}/images/$id") {
            method = HttpMethod.Get
            headers { append("Authorization",bearerAccessToken) }
        }
        return@tryBlock response.readBytes()
    }
}
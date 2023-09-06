package com.jaehl.gameTool.common.data.service

import com.jaehl.gameTool.common.data.model.Image
import java.io.File

interface ImageService {
    fun addImage(imageFile : File, description : String) : Image
}
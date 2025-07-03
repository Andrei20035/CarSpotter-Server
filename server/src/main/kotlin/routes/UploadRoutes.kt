package com.carspotter.routes

import com.carspotter.data.dto.request.UploadImageRequest
import com.carspotter.data.dto.response.UploadUrlResponse
import com.carspotter.data.service.aws_S3.IStorageService
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject

fun Route.uploadRoutes() {
    val storageService: IStorageService by application.inject()

    authenticate("jwt") {
        post("/upload-url") {

            val request = call.receive<UploadImageRequest>()
            val imageName = request.imageName

            val presignedUrl = storageService.getPresignedUploadUrl(imageName)
            val publicUrl = storageService.getPublicImageUrl(imageName)

            call.respond(
                UploadUrlResponse(
                    uploadUrl = presignedUrl.toString(),
                    publicUrl = publicUrl
                )
            )
        }
    }
}

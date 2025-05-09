package com.carspotter.routes

import com.carspotter.data.dto.toResponse
import com.carspotter.data.service.car_model.ICarModelService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.carModelRoutes() {
    val carModelService: ICarModelService by inject()

    route("/car-models") {
        get("/all") {
            val models = carModelService.getAllCarModels()
            if (models.isNotEmpty())
                call.respond(models.map { it.toResponse() })
            else
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "No car models found")
                )
        }
    }
}
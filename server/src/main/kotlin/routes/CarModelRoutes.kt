package com.carspotter.routes

import com.carspotter.data.service.car_model.ICarModelService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.carModelRoutes() {
    val carModelService: ICarModelService by application.inject()

    route("/car-models") {
        get {
            val models = carModelService.getAllCarModels()
            if (models.isNotEmpty())
                call.respond(models)
            else
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "No car models found")
                )
        }
        get("/{modelId}") {
            val modelId = call.parameters["modelId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid or missing model ID"))

            val model = carModelService.getCarModelById(modelId)
            if (model != null)
                call.respond(model)
            else
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "Car model with ID $modelId not found")
                )
        }
    }
}
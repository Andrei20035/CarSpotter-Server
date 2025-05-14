package com.carspotter.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.carspotter.data.dto.AuthCredentialDTO
import com.carspotter.data.dto.request.GoogleLoginRequest
import com.carspotter.data.dto.request.RegisterRequest
import com.carspotter.data.dto.request.RegularLoginRequest
import com.carspotter.data.dto.request.UpdatePasswordRequest
import com.carspotter.data.model.AuthCredential
import com.carspotter.data.service.auth_credential.IAuthCredentialService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import java.util.Date

fun Route.authRoutes() {
    val authCredentialService: IAuthCredentialService by inject()

    route("/auth") {
        post("/regular-login") {
            val request = call.receive<RegularLoginRequest>()

            val result = authCredentialService.regularLogin(request.email, request.password)
            if (result != null) {
                call.respond(generateJwtToken(result))
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
            }
        }

        post("/google-login") {
            val request = call.receive<GoogleLoginRequest>()

            val result = authCredentialService.googleLogin(request.email, request.googleId)
            if (result != null) {
                call.respond(generateJwtToken(result))
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
            }
        }

        post("/register") {
            val request = call.receive<RegisterRequest>()

            val authCredential = AuthCredential(
                email = request.email,
                password = request.password,
                googleId = request.googleId,
                provider = request.provider,
            )

            val credentialId = authCredentialService.createCredentials(authCredential)

            call.respond(HttpStatusCode.Created, mapOf("credentialId" to credentialId))
        }

        authenticate("jwt") {
            delete("/auth/delete-account") {
                val credentialId = call.principal<JWTPrincipal>()?.payload?.getClaim("credentialId")?.asInt()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid token"))

                val deletedRows = authCredentialService.deleteCredentials(credentialId)

                if (deletedRows > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Account deleted successfully"))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to delete account"))
                }
            }

            put("/update-password") {
                val principal = call.principal<JWTPrincipal>()
                val credentialId = principal?.payload?.getClaim("credentialId")?.asInt()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, "Invalid token")

                val request = call.receive<UpdatePasswordRequest>()
                val updatedRows = authCredentialService.updatePassword(credentialId, request.newPassword)

                if (updatedRows > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Password updated successfully"))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to update password"))
                }
            }
        }
    }
}

private fun generateJwtToken(credential: AuthCredentialDTO): Map<String, String> {
    val secret = System.getenv("JWT_SECRET") ?: throw IllegalStateException("JWT_SECRET environment variable is not set")

    val token = JWT.create()
        .withClaim("userId", credential.id)
        .withClaim("email", credential.email)
        .withExpiresAt(Date(System.currentTimeMillis() + 86400000)) // 24 hours
        .sign(Algorithm.HMAC256(secret))

    return mapOf("token" to token)
}
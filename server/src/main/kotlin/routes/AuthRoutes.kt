package com.carspotter.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.carspotter.data.dto.AuthCredentialDTO
import com.carspotter.data.dto.request.GoogleLoginRequest
import com.carspotter.data.dto.request.LoginRequest
import com.carspotter.data.dto.request.RegisterRequest
import com.carspotter.data.dto.request.RegularLoginRequest
import com.carspotter.data.dto.request.UpdatePasswordRequest
import com.carspotter.data.model.AuthCredential
import com.carspotter.data.model.AuthProvider
import com.carspotter.data.service.auth_credential.IAuthCredentialService
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.authRoutes() {
    val authCredentialService: IAuthCredentialService by application.inject()

    route("/auth") {
        post("/login") {
            val request = call.receive<LoginRequest>()

            if (request.email.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Email must not be empty"))
                return@post
            }

            if (!isValidEmail(request.email)) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid email format"))
                return@post
            }

            val result = when (request.provider) {
                AuthProvider.REGULAR -> {
                    if (request.password.isNullOrBlank()) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Password must not be empty"))
                        return@post
                    }
                    authCredentialService.regularLogin(request.email, request.password)
                }

                AuthProvider.GOOGLE -> {
                    if (request.googleId.isNullOrBlank()) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Google ID must not be empty"))
                        return@post
                    }
                    authCredentialService.googleLogin(request.email, request.googleId)
                }
            }

            if (result != null) {
                call.respond(generateJwtToken(result))
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
            }
        }

        post("/register") {
            val request = call.receive<RegisterRequest>()

            if (request.email.isBlank() || !isValidEmail(request.email)) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid email"))
                return@post
            }

            if (request.provider == AuthProvider.REGULAR && request.password.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Password is required for regular registration"))
                return@post
            }

            val authCredential = AuthCredential(
                email = request.email,
                password = request.password,
                googleId = null,
                provider = request.provider,
            )

            try {
                val credentialId = authCredentialService.createCredentials(authCredential)
                call.respond(HttpStatusCode.Created, mapOf("credentialId" to credentialId))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }

        }

        authenticate("jwt") {
            delete("/account") {
                val credentialId = call.principal<JWTPrincipal>()?.payload?.getClaim("credentialId")?.asInt()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid or missing credentialId"))

                val deletedRows = authCredentialService.deleteCredentials(credentialId)

                if (deletedRows > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Account deleted successfully"))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to delete account"))
                }
            }

            put("/password") {
                val credentialId = call.principal<JWTPrincipal>()?.payload?.getClaim("credentialId")?.asInt()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid or missing credentialId"))

                val request = call.receive<UpdatePasswordRequest>()

                if(request.newPassword.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid new password"))
                    return@put
                }
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
    val dotenv = dotenv()
    val secret = System.getenv("JWT_SECRET") ?: dotenv["JWT_SECRET"] ?: throw IllegalStateException("JWT_SECRET environment variable is not set")

    val token = JWT.create()
        .withClaim("credentialId", credential.id)
        .withClaim("email", credential.email)
        .withExpiresAt(Date(System.currentTimeMillis() + 86400000)) // 24 hours
        .sign(Algorithm.HMAC256(secret))

    return mapOf("token" to token)
}

private fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    return emailRegex.matches(email)
}
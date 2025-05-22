package routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.carspotter.configureSerialization
import com.carspotter.data.dto.AuthCredentialDTO
import com.carspotter.data.model.AuthProvider
import com.carspotter.data.service.auth_credential.IAuthCredentialService
import com.carspotter.routes.authRoutes
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthRoutesTest : KoinTest {

    private lateinit var authCredentialService: IAuthCredentialService

    @BeforeAll
    fun setup() {
//        Database.connect(
//            url = TestDatabase.postgresContainer.jdbcUrl,
//            driver = "org.postgresql.Driver",
//            user = TestDatabase.postgresContainer.username,
//            password = TestDatabase.postgresContainer.password
//        )

        authCredentialService = mockk()
    }

    @BeforeEach
    fun setupKoin() {
        // Start Koin with mocked service
        startKoin {
            modules(
                module {
                    single { authCredentialService }
                }
            )
        }
    }

    // Helper function to configure the application for testing
    private fun Application.configureTestApplication() {
        System.setProperty("JWT_SECRET", "test-secret-key")

        configureSerialization()

        install(Authentication) {
            jwt("jwt") {
                realm = "Test Server"
                verifier(
                    JWT
                        .require(Algorithm.HMAC256("test-secret-key"))
                        .build()
                )
                validate { credential ->
                    if (credential.payload.getClaim("credentialId").asInt() != null) {
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                }
            }
        }

        routing {
            authRoutes()
        }
    }

    @AfterEach
    fun tearDownKoin() {
        stopKoin()
        clearAllMocks()
    }

    @Test
    fun `regular login with valid credentials returns token`() = testApplication {
        val email = "test@example.com"
        val password = "password123"
        val credentialId = 1

        val mockCredential = AuthCredentialDTO(
            id = credentialId,
            email = email,
            provider = AuthProvider.REGULAR,
            providerId = null
        )

        coEvery { authCredentialService.regularLogin(email, password) } returns mockCredential

        // Configure the application
        application {
            configureTestApplication()
        }

        // Act
        val response = client.post("/auth/regular-login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"$email","password":"$password"}""")
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        val responseBody = response.bodyAsText()
        assertTrue(responseBody.contains("token"))

        coVerify(exactly = 1) { authCredentialService.regularLogin(email, password) }
    }

    @Test
    fun `regular login with invalid credentials returns unauthorized`() = testApplication {
        // Arrange
        val email = "test@example.com"
        val password = "wrongpassword"

        coEvery { authCredentialService.regularLogin(email, password) } returns null

        // Configure the application
        application {
            configureTestApplication()
        }

        // Act
        val response = client.post("/auth/regular-login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"$email","password":"$password"}""")
        }

        // Assert
        assertEquals(HttpStatusCode.Unauthorized, response.status)

        coVerify(exactly = 1) { authCredentialService.regularLogin(email, password) }
    }

    @Test
    fun `regular login with invalid email format returns bad request`() = testApplication {
        // Arrange
        val email = "invalid-email"
        val password = "password123"

        // Configure the application
        application {
            configureTestApplication()
        }

        // Act
        val response = client.post("/auth/regular-login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"$email","password":"$password"}""")
        }

        // Assert
        assertEquals(HttpStatusCode.BadRequest, response.status)

        // Verify service was not called
        coVerify(exactly = 0) { authCredentialService.regularLogin(any(), any()) }
    }

    @Test
    fun `google login with valid credentials returns token`() = testApplication {
        // Arrange
        val email = "test@example.com"
        val googleId = "google123"
        val credentialId = 1

        val mockCredential = AuthCredentialDTO(
            id = credentialId,
            email = email,
            provider = AuthProvider.GOOGLE,
            providerId = googleId
        )

        coEvery { authCredentialService.googleLogin(email, googleId) } returns mockCredential

        // Configure the application
        application {
            configureTestApplication()
        }

        // Act
        val response = client.post("/auth/google-login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"$email","googleId":"$googleId"}""")
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        val responseBody = response.bodyAsText()
        assertTrue(responseBody.contains("token"))

        coVerify(exactly = 1) { authCredentialService.googleLogin(email, googleId) }
    }

    @Test
    fun `register with valid data returns created status`() = testApplication {
        // Arrange
        val email = "newuser@example.com"
        val password = "newpassword123"
        val credentialId = 2

        coEvery {
            authCredentialService.createCredentials(match {
                it.email == email && it.password == password && it.provider == AuthProvider.REGULAR
            })
        } returns credentialId

        // Configure the application
        application {
            configureTestApplication()
        }

        // Act
        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"$email","password":"$password","provider":"REGULAR"}""")
        }

        // Assert
        assertEquals(HttpStatusCode.Created, response.status)

        coVerify(exactly = 1) {
            authCredentialService.createCredentials(match {
                it.email == email && it.password == password && it.provider == AuthProvider.REGULAR
            })
        }
    }

//    @Test
//    fun `update password with valid token returns success`() = testApplication {
//        // Arrange
//        val credentialId = 1
//        val newPassword = "newpassword456"
//
//        // Mock JWT environment
//        System.setProperty("JWT_SECRET", "test-secret-key")
//
//        coEvery { authCredentialService.updatePassword(credentialId, newPassword) } returns 1
//
//        // Configure the application
//        application {
//            configureTestApplication()
//        }
//
//        // Create a JWT token for testing
//        val token = JWT.create()
//            .withClaim("credentialId", credentialId)  // Using credentialId as that's what the route expects
//            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
//            .sign(Algorithm.HMAC256(System.getenv("JWT_SECRET") ?: "test-secret-key"))
//
//        // Act
//        val response = client.put("/auth/update-password") {
//            contentType(ContentType.Application.Json)
//            header(HttpHeaders.Authorization, "Bearer $token")
//            setBody("""{"newPassword":"$newPassword"}""")
//        }
//
//        // Assert
//        assertEquals(HttpStatusCode.OK, response.status)
//
//        coVerify(exactly = 1) { authCredentialService.updatePassword(credentialId, newPassword) }
//    }
}

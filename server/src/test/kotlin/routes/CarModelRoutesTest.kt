package routes

import com.carspotter.configureSerialization
import com.carspotter.data.model.CarModel
import com.carspotter.data.service.car_model.ICarModelService
import com.carspotter.routes.carModelRoutes
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.junit.jupiter.api.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CarModelRoutesTest : KoinTest {

    private lateinit var carModelService: ICarModelService

    @BeforeAll
    fun setup() {
        carModelService = mockk()
    }

    @BeforeEach
    fun setupKoin() {
        startKoin {
            modules(
                module {
                    single { carModelService }
                }
            )
        }
    }

    private fun Application.configureTestApplication() {
        System.setProperty("JWT_SECRET", "test-secret-key")

        configureSerialization()

        routing {
            carModelRoutes()
        }
    }

    @AfterEach
    fun tearDownKoin() {
        stopKoin()
        clearAllMocks()
    }

    @Test
    fun `GET all car models returns models when available`() = testApplication {
        application {
            configureTestApplication()
        }

        val carModels = listOf(
            CarModel(1, "BMW M3", "Sedan", 2021),
            CarModel(2, "Audi R8", "Coupe", 2020)
        )
        coEvery { carModelService.getAllCarModels() } returns carModels

        val response = client.get("/car-models")

        assertEquals(HttpStatusCode.OK, response.status)

        val expectedJson = Json.parseToJsonElement("""[{"id":1,"brand":"BMW M3","model":"Sedan","year":2021}, {"id":2,"brand":"Audi R8","model":"Coupe","year":2020}]""").jsonArray
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonArray

        assertEquals(expectedJson, actualJson)

        coVerify(exactly = 1) { carModelService.getAllCarModels() }
    }

    @Test
    fun `GET all car models returns 404 when empty`() = testApplication {
        application {
            configureTestApplication()
        }

        coEvery { carModelService.getAllCarModels() } returns emptyList()

        val response = client.get("/car-models")

        assertEquals(HttpStatusCode.NotFound, response.status)

        val expectedJson = Json.parseToJsonElement("""{"error":"No car models found"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals(expectedJson, actualJson)

        coVerify(exactly = 1) { carModelService.getAllCarModels() }
    }

    @Test
    fun `GET car model by ID returns model when found`() = testApplication {
        application {
            configureTestApplication()
        }

        val model = CarModel(1, "Ferrari 488", "Coupe", 2022)
        coEvery { carModelService.getCarModelById(1) } returns model

        val response = client.get("/car-models/1")

        assertEquals(HttpStatusCode.OK, response.status)

        val expectedJson = Json.encodeToJsonElement(model).jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)

        coVerify(exactly = 1) { carModelService.getCarModelById(1) }
    }

    @Test
    fun `GET car model by ID returns 404 when not found`() = testApplication {
        application {
            configureTestApplication()
        }

        coEvery { carModelService.getCarModelById(1) } returns null

        val response = client.get("/car-models/1")

        assertEquals(HttpStatusCode.NotFound, response.status)

        val expectedJson = Json.parseToJsonElement("""{"error":"Car model with ID 1 not found"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)

        coVerify(exactly = 1) { carModelService.getCarModelById(1) }
    }

    @Test
    fun `GET car model by ID returns 400 for invalid ID`() = testApplication {
        application {
            configureTestApplication()
        }

        val response = client.get("/car-models/abc")

        assertEquals(HttpStatusCode.BadRequest, response.status)

        val expectedJson = Json.parseToJsonElement("""{"error":"Invalid or missing modelId"}""").jsonObject
        val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        assertEquals(expectedJson, actualJson)
    }

}
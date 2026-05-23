package clean.architecture.omdb.exception

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebExchange
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    @Test
    fun `when handling validation error, then should return 400 status with errors`() {
        // Given
        val bindingResult = mockk<BindingResult>()
        val fieldError = FieldError("searchQuery", "query", "Query must not be blank")
        val exception = mockk<WebExchangeBindException>()
        val exchange = mockk<ServerWebExchange>()
        val responseMock = mockk<ServerHttpResponse>()

        every { bindingResult.fieldErrors } returns listOf(fieldError)
        every { exception.bindingResult } returns bindingResult
        every { exchange.request.id } returns "test-id"
        every { exchange.response } returns responseMock
        every { responseMock.headers } returns HttpHeaders()
        every { responseMock.isCommitted } returns false

        // When
        val response = handler.handleWebExchangeBindException(
            exception,
            HttpHeaders.EMPTY,
            HttpStatus.BAD_REQUEST,
            exchange
        ).block()

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        val body = response.body as ProblemDetail
        assertEquals("Invalid Request Content", body.title)
        assertEquals("Validation failed", body.detail)
        assertEquals("/api/problems/validation-error", body.type.toString())
        assertNotNull(body.properties?.get("timestamp"))
        assertEquals("test-id", body.properties?.get("requestId"))

        @Suppress("UNCHECKED_CAST")
        val errors = body.properties?.get("errors") as List<ErrorMessage>
        assertEquals(1, errors.size)
        assertEquals("query", errors.first().field)
        assertEquals("Query must not be blank", errors.first().message)
    }
}

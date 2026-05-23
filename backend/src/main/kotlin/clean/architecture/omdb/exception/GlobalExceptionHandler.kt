package clean.architecture.omdb.exception

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Instant

/**
 * Global exception handler to provide consistent ProblemDetail responses.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    override fun createResponseEntity(
        body: Any?,
        headers: HttpHeaders?,
        status: HttpStatusCode,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Any>> {
        if (body is ProblemDetail) {
            body.setProperty("timestamp", Instant.now())
            body.setProperty("requestId", exchange.request.id)
        }
        return super.createResponseEntity(body, headers, status, exchange)
    }

    /**
     * Handle validation exceptions specifically to include field-level errors.
     */
    public override fun handleWebExchangeBindException(
        ex: WebExchangeBindException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Any>> {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Validation failed"
        )
        problemDetail.title = "Invalid Request Content"
        problemDetail.type = URI.create("/api/problems/validation-error")
        val errors = ex.bindingResult.fieldErrors.map { ErrorMessage(it.field, it.defaultMessage) }
        problemDetail.setProperty("errors", errors)
        return handleExceptionInternal(ex, problemDetail, headers, status, exchange)
    }
}

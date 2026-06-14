package clean.architecture.omdb.controller

import clean.architecture.omdb.model.ValidationErrorType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for [Problem Details](https://datatracker.ietf.org/doc/html/rfc7807).
 */
@RestController
@RequestMapping("/api/problems")
class ProblemTypesController {

    /**
     * Problem details for validation errors.
     */
    @Operation(
        summary = "Get validation error type",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved validation error type",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ValidationErrorType::class)
                    )
                ]
            )
        ]
    )
    @GetMapping("/validation-error-type")
    fun getValidationErrorType(): ValidationErrorType = ValidationErrorType(
        type = "/api/problems/validation-error-type",
        title = "Invalid Request Content",
        status = HttpStatus.BAD_REQUEST.value(),
        description = "The request content failed validation."
    )
}

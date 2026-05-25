package clean.architecture.omdb.controller

import clean.architecture.omdb.model.ValidationErrorType
import org.springframework.http.HttpStatus
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
    @GetMapping("/validation-error")
    fun validationError(): ValidationErrorType = ValidationErrorType(
        type = "/api/problems/validation-error",
        title = "Invalid Request Content",
        status = HttpStatus.BAD_REQUEST.value(),
        description = "The request content failed validation."
    )
}

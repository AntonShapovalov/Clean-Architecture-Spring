package clean.architecture.omdb.model

import clean.architecture.omdb.controller.ProblemTypesController

/**
 * Validation error model for [ProblemTypesController].
 *
 * @param type the error type.
 * @param title the error title.
 * @param status the HTTP status code.
 * @param description the error description.
 */
data class ValidationErrorType(
    val type: String,
    val title: String,
    val status: Int,
    val description: String
)

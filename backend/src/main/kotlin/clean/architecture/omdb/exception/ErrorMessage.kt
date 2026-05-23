package clean.architecture.omdb.exception

import org.springframework.http.ProblemDetail

/**
 * Stores an error message for [ProblemDetail].
 *
 * @param field the field name that caused the error.
 * @param message the error message.
 */
data class ErrorMessage(
    val field: String,
    val message: String?
)

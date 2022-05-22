package sm.tech.exception

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class ResponseError(
    val timestamp: String,
    val path: String,
    val status: Int? = null,
    val error: String? = null,
    val message: String? = null
)

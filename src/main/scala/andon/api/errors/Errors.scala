package andon.api.errors

sealed abstract class AndonError(val code: String, val message: String) extends Exception(message)
case class NotImplemented(msg: String = "Requested API is not implemented.") extends AndonError(
  code = "not_implemented",
  message = msg
)
case class ApiNotFound(msg: String = "Requested API is not found.") extends AndonError(
  code = "api_not_found",
  message = msg
)
case class ResourceNotFound(msg: String = "Requested resource is not found.") extends AndonError(
  code = "resource_not_found",
  message = msg
)
case class Unauthorized(msg: String = "You can not access this API.") extends AndonError(
  code = "unauthorized",
  message = msg
)
case class Unexpected(msg: String) extends AndonError(
  code = "unexpected_error",
  message = msg
)
case class BadRequest(msg: String) extends AndonError(
  code = "bad_request",
  message = msg
)

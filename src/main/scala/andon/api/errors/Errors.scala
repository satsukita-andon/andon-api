package andon.api.errors

// do not duplicate name with T: io.finch.Failure
sealed abstract class AndonError(val code: String, val message: String) extends Exception(message)
case class ApiNotImplemented(msg: String = "Requested API is not implemented.") extends AndonError(
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
case class ResourceAlreadyExists(msg: String = "Resource already exists.") extends AndonError(
  code = "resource_already_exists",
  message = msg
)
case class AuthRequired(msg: String = "Authentication is required.") extends AndonError(
  code = "authentication_required",
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

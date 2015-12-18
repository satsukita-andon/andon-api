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
case class TokenRequired(msg: String = "A valid access token is required.") extends AndonError(
  code = "token_required",
  message = msg
)
case class NoAuthorization(msg: String = "You cannot access the resource.") extends AndonError(
  code = "no_authorization",
  message = msg
)
case class Unexpected(msg: String) extends AndonError(
  code = "unexpected_error",
  message = msg
)
case class Incorrect(msg: String = "Your request is something incorrect.") extends AndonError(
  code = "incorrect",
  message = msg
)
case class JsonError(msg: String = "Cannot parse as json.") extends AndonError(
  code = "json_error",
  message = msg
)

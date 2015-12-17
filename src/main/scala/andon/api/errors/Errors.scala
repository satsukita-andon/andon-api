package andon.api.errors

sealed abstract class AndonError(val code: String, val message: String) extends Exception
case object NotImplemented extends AndonError(
  code = "not_implemented",
  message = "Requested API is not implemented."
)
case object ApiNotFound extends AndonError(
  code = "api_not_found",
  message = "Requested API is not found."
)
case object ResourceNotFound extends AndonError(
  code = "resource_not_found",
  message = "Requested resource is not found."
)
case object Unauthorized extends AndonError(
  code = "unauthorized",
  message = "You do not have the right to access this API."
)

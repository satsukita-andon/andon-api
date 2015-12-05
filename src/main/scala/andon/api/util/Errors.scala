package andon.api.util

import akka.http.scaladsl.model.{ StatusCode, StatusCodes }

object Errors {

  case class RawError(code: String, message: String)
  type Error = (StatusCode, RawError)

  val ApiNotImplemented = (StatusCodes.NotImplemented, RawError(code = "api_not_implemented", message = "requested API not implemented"))
  val ApiNotFound = (StatusCodes.NotFound, RawError(code = "api_not_found", message = "requested API not found"))
  val ResourceNotFound = (StatusCodes.NotFound, RawError(code = "resource_not_found", message = "resource not found"))
  val JsonError = (StatusCodes.BadRequest, RawError(code = "json_error", message = "cannot extract entity to certain value"))
  val Unauthorized = (StatusCodes.Unauthorized, RawError(code = "unauthorized", message = "unauthorized"))
  def Unexpected(e: Throwable) = (StatusCodes.InternalServerError, RawError(code = "unexpected", message = "Unexpected error: " + e.getMessage))
}

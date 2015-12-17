package andon.api.jsons

import andon.api.errors._

final case class ErrorResponse(code: String, message: String)

object ErrorResponse {
  def apply(e: Throwable): ErrorResponse = e match {
    case e: AndonError => ErrorResponse(e.code, e.message)
    case e: io.finch.Error => ErrorResponse(BadRequest(e.getMessage))
    case _ => ErrorResponse(Unexpected(e.getMessage))
  }
}

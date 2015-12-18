package andon.api.jsons

import io.finch.{ Error => FinchError }

import andon.api.errors._

final case class ErrorResponse(code: String, message: String)

object ErrorResponse {
  def apply(e: Throwable): ErrorResponse = {
    val andonError = e match {
      case e: AndonError => e
      case e: FinchError.NotParsed => JsonError(e.getMessage)
      case e: FinchError => Incorrect(e.getMessage)
      case _ => Unexpected(e.toString)
    }
    ErrorResponse(andonError.code, andonError.message)
  }
}

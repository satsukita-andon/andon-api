package andon.api.errors

import io.circe._, generic.auto._, syntax._
import io.finch.{ Error => FinchError }

// do not duplicate name with T: io.finch.Failure
sealed abstract class AndonError(val code: String, val message: String) extends Exception(message) {
  def toJson: Json = {
    val fields = Seq(
      "code" -> Json.string(code),
      "message" -> Json.string(message)
    ) ++ extraFields
    Json.obj(fields: _*)
  }
  def extraFields: Seq[(String, Json)] = Seq()
}
object AndonError {
  implicit val encodeAndonError: Encoder[AndonError] = Encoder.instance(_.toJson)
  def apply(e: Throwable): AndonError = e match {
    case e: AndonError => e
    case e: FinchError.NotParsed => JsonError(e.getMessage)
    case e: FinchError => Incorrect(e.getMessage)
    case _ => Unexpected(e.toString)
  }
}
final case class ApiNotImplemented(msg: String = "Requested API is not implemented.") extends AndonError(
  code = "not_implemented",
  message = msg
)
final case class ApiNotFound(msg: String = "Requested API is not found.") extends AndonError(
  code = "api_not_found",
  message = msg
)
final case class ResourceNotFound(msg: String = "Requested resource is not found.") extends AndonError(
  code = "resource_not_found",
  message = msg
)
final case class ResourceAlreadyExists(msg: String = "Resource already exists.") extends AndonError(
  code = "resource_already_exists",
  message = msg
)
final case class TokenRequired(msg: String = "A valid access token is required.") extends AndonError(
  code = "token_required",
  message = msg
)
final case class NoPermission(msg: String = "You do not have permission to access the resource.") extends AndonError(
  code = "no_permission",
  message = msg
)
final case class Unexpected(msg: String) extends AndonError(
  code = "unexpected_error",
  message = msg
)
final case class Incorrect(msg: String = "Your request is something incorrect.") extends AndonError(
  code = "incorrect",
  message = msg
)
final case class InvalidFileFormat(msg: String = "Requested file format is invalid.") extends AndonError(
  code = "invalid_file_format",
  message = msg
)
final case class JsonError(msg: String = "Cannot parse as json.") extends AndonError(
  code = "json_error",
  message = msg
)
final case class ValidationError(items: cats.data.NonEmptyList[InvalidItem], msg: String = "Validation failed.") extends AndonError(
  code = "validation_error",
  message = msg
) {
  override def extraFields = Seq("errors" -> items.asJson)
}

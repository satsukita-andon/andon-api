package andon.api.errors

import cats._
import cats.data._
import cats.std.all._
import cats.syntax.cartesian._
import org.apache.commons.validator.routines.{EmailValidator, UrlValidator}

final case class InvalidItem(field: String, reason: String)
object InvalidItem {
  implicit val nonEmptyListOfInvalidItemIsSemigroup: Semigroup[NonEmptyList[InvalidItem]] =
    SemigroupK[NonEmptyList].algebra[InvalidItem]
}

object Validation {
  import InvalidItem._
  def run[A](zero: A, checks: Seq[(Boolean, InvalidItem)]): ValidatedNel[InvalidItem, A] = {
    val ok = Validated.valid[NonEmptyList[InvalidItem], A](zero)
    checks.foldRight(ok) { (a, b) =>
      val (pred, report) = a
      if (pred) {
        Validated.invalidNel(report) *> b
      } else {
        b
      }
    }
  }

  val urlValidator = {
    val schemes = Array("http", "https")
    new UrlValidator(schemes)
  }

  val emailValidator = {
    val allowLocal = false
    EmailValidator.getInstance(allowLocal)
  }

  def url(s: String): Boolean = urlValidator.isValid(s)

  def email(s: String): Boolean = emailValidator.isValid(s)
}

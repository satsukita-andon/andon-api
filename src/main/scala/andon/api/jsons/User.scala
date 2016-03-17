package andon.api.jsons

import andon.api.errors.{Validation, InvalidItem}
import cats.data.ValidatedNel
import org.joda.time.DateTime

import andon.api.models.generated.{
  User => UserRow,
  Class => ClassRow,
  Prize => PrizeRow
}
import andon.api.util.{Token, OrdInt}

final case class UserCreation(
  login: String,
  password: String,
  name: String,
  times: Short
) {
  def validate(logins: Seq[String], timesUpper: OrdInt): ValidatedNel[InvalidItem, UserCreation] = {
    Validation.run(this, Seq(
      logins.contains(login) -> InvalidItem(
        field = "login",
        reason = "`login` must be unique"
      ),
      (login.length > 30) -> InvalidItem(
        field = "login",
        reason = "`login` length must be less than or equal to 30 characters"
      ),
      (name.length > 30) -> InvalidItem(
        field = "name",
        reason = "`name` length must be less than or equal to 30 characters"
      ),
      (times <= 0 || timesUpper.raw < times) -> InvalidItem(
        field = "times",
        reason = s"`times` must be within 1 <= times <= ${timesUpper.raw}"
      )
    ))
  }
}

final case class UserAuthorityModification(
  admin: Boolean,
  suspended: Boolean
)

final case class UserModification(
  login: String,
  name: String,
  biography: Option[String],
  class_first: Option[Short],
  class_second: Option[Short],
  class_third: Option[Short],
  chief_first: Option[Boolean],
  chief_second: Option[Boolean],
  chief_third: Option[Boolean],
  email: Option[String]
) {
  def validate(logins: Seq[String]): ValidatedNel[InvalidItem, UserModification] = {
    Validation.run(this, Seq(
      logins.contains(login) -> InvalidItem(
        field = "login",
        reason = "`login` must be unique"
      ),
      (login.length > 30) -> InvalidItem(
        field = "login",
        reason = "`login` length must be less than or equal to 30 characters"
      ),
      (name.length > 30) -> InvalidItem(
        field = "name",
        reason = "`name` length must be less than or equal to 30 characters"
      ),
      email.map(Validation.email).getOrElse(true) -> InvalidItem(
        field = "email",
        reason = "`email` must be valid email"
      )
    ))
  }
}

final case class User(
  id: Int,
  login: String,
  name: String,
  icon_url: Option[String],
  admin: Boolean,
  suspended: Boolean
)

object User {
  def apply(user: UserRow): User = User(
    id = user.id,
    login = user.login,
    name = user.name,
    icon_url = user.iconUrl,
    admin = user.admin,
    suspended = user.suspended
  )
}

final case class DetailedUser(
  id: Int,
  login: String,
  name: String,
  biography: Option[String],
  times: Short,
  times_ord: String,
  class_first: Option[Class],
  class_second: Option[Class],
  class_third: Option[Class],
  chief_first: Option[Boolean],
  chief_second: Option[Boolean],
  chief_third: Option[Boolean],
  icon_url: Option[String],
  email: Option[String],
  admin: Boolean,
  suspended: Boolean, // if true, other fields are dummy data
  created_at: DateTime,
  updated_at: DateTime
)

object DetailedUser {
  def apply(
    user: UserRow,
    first: Option[(ClassRow, Seq[PrizeRow], Seq[String])],
    second: Option[(ClassRow, Seq[PrizeRow], Seq[String])],
    third: Option[(ClassRow, Seq[PrizeRow], Seq[String])]
  ): DetailedUser = DetailedUser(
    id = user.id,
    login = user.login,
    name = user.name,
    biography = user.biography,
    times = user.times,
    times_ord = OrdInt(user.times).toString,
    class_first = first.map { case (c, ps, ts) => Class(c, ps, ts) },
    class_second = second.map { case (c, ps, ts) => Class(c, ps, ts) },
    class_third = third.map { case (c, ps, ts) => Class(c, ps, ts) },
    chief_first = user.chiefFirst,
    chief_second = user.chiefSecond,
    chief_third = user.chiefThird,
    icon_url = user.iconUrl,
    email = user.email,
    admin = user.admin,
    suspended = user.suspended,
    created_at = user.createdAt,
    updated_at = user.updatedAt
  )
}

final case class DetailedUserWithToken(
  token: String,
  user: DetailedUser
)

object DetailedUserWithToken {
  def apply(user: DetailedUser): DetailedUserWithToken = {
    val token = Token(userId = user.id).encode
    DetailedUserWithToken(token = token, user = user)
  }
}

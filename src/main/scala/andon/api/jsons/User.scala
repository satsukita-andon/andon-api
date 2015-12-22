package andon.api.jsons

import org.joda.time.DateTime

import andon.api.models.generated.{
  User => UserRow,
  Class => ClassRow,
  Prize => PrizeRow
}
import andon.api.util.OrdInt

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

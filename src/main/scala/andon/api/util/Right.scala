package andon.api.util

import andon.api.models.generated.User

sealed abstract class Right
object Right {
  case object Admin extends Right
  case object Suspended extends Right
  case class CohortOf(times: Short) extends Right
  // if classmate then cohort
  case class ClassmateOf(classId: ClassId) extends Right
  // if chief then classmate
  case class ChiefOf(classId: ClassId) extends Right
  case class In(userIds: Set[Int]) extends Right
  case class Is(userId: Int) extends Right

  def has(user: User, right: Right): Boolean = right match {
    case Right.Admin => user.admin
    case Right.Suspended => user.suspended
    case Right.CohortOf(t) => user.times == t
    case Right.ClassmateOf(ClassId(t, 1, c)) =>
      user.times == t.raw && user.classFirst == Some(c)
    case Right.ClassmateOf(ClassId(t, 2, c)) =>
      user.times == t.raw && user.classSecond == Some(c)
    case Right.ClassmateOf(ClassId(t, 3, c)) =>
      user.times == t.raw && user.classThird == Some(c)
    case Right.ClassmateOf(_) => false
    case Right.ChiefOf(ClassId(t, 1, c)) =>
      user.times == t.raw && user.classFirst == Some(c) && user.chiefFirst == Some(true)
    case Right.ChiefOf(ClassId(t, 2, c)) =>
      user.times == t.raw && user.classSecond == Some(c) && user.chiefSecond == Some(true)
    case Right.ChiefOf(ClassId(t, 3, c)) =>
      user.times == t.raw && user.classThird == Some(c) && user.chiefThird == Some(true)
    case Right.ChiefOf(_) => false
    case Right.In(userIds) => userIds.contains(user.id)
    case Right.Is(userId) => user.id == userId
  }
}

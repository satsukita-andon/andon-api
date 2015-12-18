package andon.api.util

import andon.api.models.generated.User

sealed abstract class Right
object Right {
  case object Admin extends Right
  case object Suspended extends Right
  case class CohortOf(times: Short) extends Right
  // if classmate then cohort
  case class ClassmateOf(times: Short, grade: Short, `class`: Short) extends Right
  // if chief then classmate
  case class ChiefOf(times: Short, grade: Short, `class`: Short) extends Right
  case class In(userIds: Set[Int]) extends Right

  def has(user: User, right: Right): Boolean = right match {
    case Right.Admin => user.admin
    case Right.Suspended => user.suspended
    case Right.CohortOf(t) => user.times == t
    case Right.ClassmateOf(t, 1, c) =>
      user.times == t && user.classFirst == Some(c)
    case Right.ClassmateOf(t, 2, c) =>
      user.times == t && user.classSecond == Some(c)
    case Right.ClassmateOf(t, 3, c) =>
      user.times == t && user.classThird == Some(c)
    case Right.ClassmateOf(_, _, _) => false
    case Right.ChiefOf(t, 1, c) =>
      user.times == t && user.classFirst == Some(c) && user.chiefFirst == Some(true)
    case Right.ChiefOf(t, 2, c) =>
      user.times == t && user.classSecond == Some(c) && user.chiefSecond == Some(true)
    case Right.ChiefOf(t, 3, c) =>
      user.times == t && user.classThird == Some(c) && user.chiefThird == Some(true)
    case Right.ChiefOf(_, _, _) => false
    case Right.In(userIds) => userIds.contains(user.id)
  }
}

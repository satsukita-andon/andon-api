package andon.api.models

import scalikejdbc._

class PostgresSyntax(self: SQLSyntax) {
  def lower = sqls"lower(${self})"
}
object PostgresSyntax {
  import scala.language.implicitConversions
  implicit def toPostgresSyntax(s: SQLSyntax) = new PostgresSyntax(s)
}

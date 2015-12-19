package andon.api

import scalikejdbc._

package object models {
  implicit class PostgresSyntax(self: SQLSyntax) {
    def lower = sqls"lower(${self})"
  }
}

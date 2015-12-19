package andon.api.endpoints

import scalikejdbc.DB

import andon.api.models.UserModel
import andon.api.util._

trait AndonUtil {
  def tokenOf(login: String): String = {
    DB.localTx { implicit s =>
      UserModel.findByLogin(login).map(u => Token.encode(Token(u.id))).get
    }
  }
}

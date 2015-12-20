package andon.api.endpoints

import scalikejdbc.DB

import andon.api.models._, generated._
import andon.api.jsons.FestivalCreation
import andon.api.util._

trait AndonUtil {
  def tokenOf(login: String): String = {
    DB.localTx { implicit s =>
      UserModel.findByLogin(login).map(u => Token.encode(Token(u.id))).get
    }
  }

  def withFestivals(festivals: Seq[FestivalCreation])(f: Seq[Festival] => Unit) = {
    val fs = DB.localTx { implicit s =>
      festivals.map { f =>
        FestivalModel.create(OrdInt(f.times), f.theme, f.theme_roman, f.theme_kana, f.thumbnail_url).toOption.get
      }
    }
    try {
      f(fs)
    } finally {
      DB.localTx { implicit s =>
        fs.foreach(_.destroy)
      }
    }
  }
}

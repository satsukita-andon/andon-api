package andon.api.endpoints

import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
import scalikejdbc.DB

// import andon.api.errors._
import andon.api.jsons.{ Festival, FestivalCreation }
import andon.api.models.FestivalModel
import andon.api.util.{ OrdInt, Token }

object FestivalEndpoint extends EndpointBase {

  val name = "festivals"

  val create: Endpoint[Festival] = post(ver / name ? body.as[FestivalCreation] ? auth) { (fes: FestivalCreation, _: Token) =>
    DB.localTx { implicit s =>
      FestivalModel.create(
        times = fes.times,
        theme = fes.theme,
        themeKana = fes.theme_kana,
        themeRoman = fes.theme_roman,
        thumbnailUrl = fes.thumbnail_url
      ).fold(BadRequest(_), f => Ok(Festival(f)))
    }
  }

  val all = create
}

package andon.api.endpoints

import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
import scalikejdbc.DB

// import andon.api.errors._
import andon.api.jsons.{ Festival, FestivalCreation }
import andon.api.models.FestivalModel
import andon.api.util.OrdInt

object FestivalEndpoint extends EndpointBase {

  val name = "festivals"

  val create: Endpoint[Festival] = post(ver / name ? body.as[FestivalCreation]) { fes: FestivalCreation =>
    DB.localTx { implicit s =>
      val created = FestivalModel.create(
        times = fes.times,
        theme = fes.theme,
        themeKana = fes.theme_kana,
        themeRoman = fes.theme_roman,
        thumbnailUrl = fes.thumbnail_url
      )
      Ok(Festival(created))
    }
  }

  val all = create
}

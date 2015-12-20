package andon.api.endpoints

import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
import scalikejdbc.DB

// import andon.api.errors._
import andon.api.jsons.{ Festival, FestivalCreation }
import andon.api.models.FestivalModel
import andon.api.util._

object FestivalEndpoint extends FestivalEndpoint {
  protected val FestivalModel = andon.api.models.FestivalModel
}
trait FestivalEndpoint extends EndpointBase {

  protected val FestivalModel: FestivalModel

  val name = "festivals"
  def all = findAll :+: create

  val findAll: Endpoint[Seq[Festival]] = get(ver / name ? order) { (order: Option[SortOrder]) =>
    DB.readOnly { implicit s =>
      val o = order.getOrElse(DESC)
      val fs = FestivalModel.findAll(o).map(Festival.apply) // Descending
      Ok(fs)
    }
  }

  val create: Endpoint[Festival] = post(ver / name ? token ? body.as[FestivalCreation]) { (token: Token, fes: FestivalCreation) =>
    DB.localTx { implicit s =>
      token.allowedOnly(Right.Admin) { _ =>
        FestivalModel.create(
          times = fes.times,
          theme = fes.theme,
          themeKana = fes.theme_kana,
          themeRoman = fes.theme_roman,
          thumbnailUrl = fes.thumbnail_url
        ).fold(BadRequest(_), f => Ok(Festival(f)))
      }
    }
  }
}

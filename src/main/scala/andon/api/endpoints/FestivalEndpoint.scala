package andon.api.endpoints

import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
import scalikejdbc.DB

// import andon.api.errors._
import andon.api.jsons.{Items, Festival, FestivalCreation}
import andon.api.models.FestivalModel
import andon.api.util._

object FestivalEndpoint extends FestivalEndpoint {
  protected val FestivalModel = andon.api.models.FestivalModel
}
trait FestivalEndpoint extends EndpointBase {

  protected val FestivalModel: FestivalModel

  val name = "festivals"
  def all = findAll :+: create

  def findAll: Endpoint[Items[Festival]] = get(ver / name ? paging()) { (p: Paging) =>
    DB.readOnly { implicit s =>
      val paging = p.defaultOrder(FestivalModel.f.times -> DESC)
      val fs = FestivalModel.findAll(paging).map(Festival.apply) // Descending
      val all = FestivalModel.countAll
      Ok(Items(
        count = fs.length.toLong,
        all_count = all,
        items = fs
      ))
    }
  }

  def create: Endpoint[Festival] = post(ver / name ? token ? body.as[FestivalCreation]) { (token: Token, fes: FestivalCreation) =>
    DB.localTx { implicit s =>
      token.allowedOnly(Right.Admin) { _ =>
        FestivalModel.create(
          times = OrdInt(fes.times),
          theme = fes.theme,
          themeKana = fes.theme_kana,
          themeRoman = fes.theme_roman,
          thumbnailUrl = fes.thumbnail_url
        ).fold(BadRequest(_), f => Ok(Festival(f)))
      }
    }
  }
}

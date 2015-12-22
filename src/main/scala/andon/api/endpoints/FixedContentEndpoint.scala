package andon.api.endpoints

import io.finch._
import scalikejdbc.DB

import andon.api.errors._
import andon.api.jsons._
import andon.api.models.FixedContentModel
import andon.api.util._

object FixedContentEndpoint extends FixedContentEndpoint {
  protected val FixedContentModel = andon.api.models.FixedContentModel
}
trait FixedContentEndpoint extends EndpointBase {

  protected val FixedContentModel: FixedContentModel

  val name = "contents" // contents? fixed_contents?
  def all = findByType

  def findByType: Endpoint[FixedContent] = get(ver / name / fixedContentType) { typ: FixedContentType =>
    DB.readOnly { implicit s =>
      FixedContentModel.findByType(typ).map { case (c, r) =>
        Ok(FixedContent(c, r))
      }.getOrElse(NotFound(ResourceNotFound()))
    }
  }
}

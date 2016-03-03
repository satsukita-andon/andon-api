package andon.api.endpoints

import io.finch._
import io.finch.circe._
import scalikejdbc.DB

import andon.api.jsons._
import andon.api.models._

object OtherEndpoint extends OtherEndpoint {
  protected val ClassImageModel = andon.api.models.ClassImageModel
}
trait OtherEndpoint extends EndpointBase {

  protected val ClassImageModel: ClassImageModel

  val name = "others"
  def all = random

  def random: Endpoint[Seq[ClassImage]] = get(
    ver / name / "random-images" ? paramOption("n").as[Int]
  ) { (n: Option[Int]) =>
    DB.readOnly { implicit s =>
      val num = n.getOrElse(20)
      Ok(ClassImageModel.random(num).map { case (i, u) =>
        ClassImage.apply(i, u)
      })
    }
  }
}

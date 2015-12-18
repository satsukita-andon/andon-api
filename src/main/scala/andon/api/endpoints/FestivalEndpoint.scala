package andon.api.endpoints

import io.finch._
// import io.finch.circe._
import scalikejdbc.DB

// import andon.api.errors._
import andon.api.jsons.Festival
import andon.api.models.FestivalModel
import andon.api.util.OrdInt

object FestivalEndpoint extends EndpointBase {

  val name = "festivals"

  // post(ver / name ? body.as[FestivalCreation])
  val create: Endpoint[Festival] = get("error") {
    val t = (1 / 0).toShort
    Ok(Festival(t, OrdInt(t).toString, "aaa"))
  }

  val all = create
}

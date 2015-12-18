package andon.api.jsons

import cats.data.Xor
import io.circe._, Encoder._
import org.joda.time.DateTime

object Implicits {
  implicit val decodeDateTime: Decoder[DateTime] = Decoder.instance { cursor =>
    cursor.as[String].flatMap { s =>
      Xor.catchOnly[IllegalArgumentException] {
        DateTime.parse(s)
      }.leftMap(e => DecodingFailure(e.getMessage, cursor.history))
    }
  }
  implicit val encodeDateTime: Encoder[DateTime] = Encoder.instance { dt =>
    encodeString(dt.toString)
  }
}

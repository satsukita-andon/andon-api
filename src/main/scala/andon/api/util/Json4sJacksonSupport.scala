package andon.api.util

import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.ext.JodaTimeSerializers

trait JsonFormats {
  implicit val formats = Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all
}

trait JsonSerialization {
  implicit val serialization = Serialization
}

trait JsonSupport extends Json4sSupport with JsonFormats with JsonSerialization

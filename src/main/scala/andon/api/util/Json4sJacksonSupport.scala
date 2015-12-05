package andon.api.util

import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.ext.JodaTimeSerializers

trait Json4sJacksonSupport extends Json4sSupport {
  implicit val serialization = Serialization
  implicit val formats = Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all
}

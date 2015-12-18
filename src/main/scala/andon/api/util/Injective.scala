package andon.api.util

import cats.data.Xor
import io.circe._

// injective function from S to T
trait Injective[S, T] {
  // required: all.map(mapping) == all.map(mapping).distinct
  def to(s: S): T
  val all: Set[S] // all elements of finite set S

  def from(t: T): Option[S] = revdict.get(t)
  private val revdict = {
    val images = all.map(to)
    if (images.size != all.size) {
      throw new Exception("This is not an injective function")
    }
    images.zip(all).toMap
  }

  implicit def injectiveDomainEncoder(implicit encode: Encoder[T]): Encoder[S] = {
    encode.contramap(to)
  }
  implicit def injectiveDomainDecoder(implicit decode: Decoder[T]): Decoder[S] = {
    Decoder.withReattempt { cursor =>
      decode.tryDecode(cursor).flatMap { t =>
        from(t) match {
          case None => Xor.left(DecodingFailure("Injective", cursor.history))
          case Some(s) => Xor.right(s)
        }
      }
    }
  }
  implicit def injectiveRangeEncoder(implicit encode: Encoder[S]): Encoder[T] = {
    Encoder.instance { t =>
      Encoder.encodeOption(encode)(from(t))
    }
  }
  implicit def injectiveRangeDecoder(implicit decode: Decoder[S]): Decoder[T] = {
    decode.map(to)
  }
}

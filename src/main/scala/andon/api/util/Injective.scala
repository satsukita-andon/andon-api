package andon.api.util

import cats.data.Xor
import io.circe._

// injective function from S to T
trait Injective[S, T] {
  // required: domain.map(to) == domain.map(to).distinct
  def to(s: S): T
  val domain: Set[S] // all elements of finite set S

  lazy val images = domain.map(to)
  def from(t: T): Option[S] = revdict.get(t)

  private lazy val revdict = { // lazy is important!
    if (images.size != domain.size) {
      throw new Exception("This is not an injective function")
    }
    images.zip(domain).toMap
  }

  implicit def injectiveDomainEncoder(implicit encode: Encoder[T]): Encoder[S] = {
    encode.contramap(to)
  }
  implicit def injectiveDomainDecoder(implicit decode: Decoder[T]): Decoder[S] = {
    Decoder.instance { cursor =>
      decode(cursor).flatMap { t =>
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

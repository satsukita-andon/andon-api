package andon.api.util

import scalikejdbc.TypeBinder

// injective function from S to T
trait Injective[S, T] {
  // required: all.map(mapping) == all.map(mapping).distinct
  def mapping(s: S): T
  val all: Seq[S] // all (finite) sequence of S

  def unapply(t: T): Option[S] = revdict.get(t)
  private val revdict = all.map(mapping).zip(all).toMap
}

// with TypeBinder
trait InjectiveTypeBinder[S, T] extends Injective[S, T] {
  implicit def typeBinder(implicit tb: TypeBinder[T], nl: <:<[Null, S]): TypeBinder[S] =
    tb.map(t => unapply(t).orNull)
}

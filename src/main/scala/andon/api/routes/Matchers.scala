package andon.api.routes

import akka.http.scaladsl.server._
import akka.http.scaladsl.server.PathMatcher.{ Matching, Matched, Unmatched }
import akka.http.scaladsl.server.util.Tuple._
import akka.http.scaladsl.model.Uri.Path, Path.Segment

import andon.api.util.OrdInt

object OrdIntMatcher extends PathMatcher[Tuple1[OrdInt]] {
  def apply(path: Path): Matching[Tuple1[OrdInt]] = path match {
    case Segment(hd, tl) => {
      OrdInt.fromString(hd).map { ordint =>
        Matched(tl, Tuple1(ordint))
      }.getOrElse(Unmatched)
    }
    case _ => Unmatched
  }
}

object SignedIntNumber extends PathMatcher[Tuple1[Int]] {
  def apply(path: Path): Matching[Tuple1[Int]] = path match {
    case Segment(hd, tl) => {
      try {
        Matched(tl, Tuple1(hd.toInt))
      } catch {
        case _: NumberFormatException => Unmatched
      }
    }
    case _ => Unmatched
  }
}

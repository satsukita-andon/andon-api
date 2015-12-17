package andon.api.util

import org.scalatest.FlatSpec

class OrdIntTest extends FlatSpec {

  val numStrPairs = Seq(
    0 -> "0th",
    1 -> "1st",
    2 -> "2nd",
    3 -> "3rd",
    4 -> "4th",
    10 -> "10th",
    11 -> "11th",
    12 -> "12th",
    13 -> "13th",
    21 -> "21st",
    22 -> "22nd",
    23 -> "23rd"
  )

  "OrdInt#toString" should "be OK" in {
    intercept[IllegalArgumentException](OrdInt(-1))
    numStrPairs.foreach { case (q, a) =>
      assert(OrdInt(q).toString == a)
    }
  }

  "OrdInt.parse" should "parse correctly" in {
    val qa = numStrPairs.map { case (a, q) => (q, Some(OrdInt(a))) } ++ Seq(
      "1th", "2th", "3th", "11st", "12nd", "13rd"
    ).map(s => (s, None))
    qa.foreach { case (q, a) =>
      assert(OrdInt.parse(q) == a)
    }
  }
}

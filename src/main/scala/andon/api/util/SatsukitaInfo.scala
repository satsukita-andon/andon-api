package andon.api.util

import org.joda.time.DateTime

object SatsukitaInfo {
  // list of times of "現役生"
  def activeTimes: Seq[OrdInt] = {
    Seq(firstGradeTimes, secondGradeTimes, thirdGradeTimes)
  }
  def firstGradeTimes = OrdInt((times.raw + 2).toShort)
  def secondGradeTimes = OrdInt((times.raw + 1).toShort)
  def thirdGradeTimes = times
  def times = timesFromDateTime(DateTime.now)
  def timesFromDateTime(dt: DateTime): OrdInt = {
    val year = dt.year().get()
    val month = dt.monthOfYear().get()
    val basic = year - 1949 // e.g., 60th = 2009 - 1949, 66th = 2015 - 1949
    val raw = if (month < 4) { // before new semester
      basic - 1
    } else basic
    OrdInt(raw.toShort)
  }
}

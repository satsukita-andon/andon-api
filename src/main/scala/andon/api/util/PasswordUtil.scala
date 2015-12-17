package andon.api.util

import java.security.MessageDigest
import org.mindrot.BCrypt

object PasswordUtil {

  def encrypt(raw: String): String = BCrypt.hashpw(raw, BCrypt.gensalt)
  def check(raw: String, encrypted: String): Boolean = BCrypt.checkpw(raw, encrypted)

  // for backward compatibility
  def checkSha1(raw: String, encrypted: String): Boolean = {
    val md = MessageDigest.getInstance("SHA-1")
    md.digest(raw.getBytes).map("%02X" format _).mkString == encrypted
  }
}

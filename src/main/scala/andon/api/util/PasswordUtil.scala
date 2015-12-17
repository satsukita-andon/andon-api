package andon.api.util

import java.security.MessageDigest
import org.mindrot.BCrypt

object PasswordUtil {

  def encrypt(raw: String): String = BCrypt.hashpw(raw, BCrypt.gensalt)
  def check(raw: String, encrypted: String): Boolean = try {
    BCrypt.checkpw(raw, encrypted)
  } catch {
    case e: IllegalArgumentException => false // occured if not bcrypt hash
  }

  // for backward compatibility
  def checkSha1(raw: String, encrypted: String): Boolean = {
    val md = MessageDigest.getInstance("SHA-1")
    val hashed = md.digest(raw.getBytes).map("%02x" format _).mkString
    hashed == encrypted
  }
}

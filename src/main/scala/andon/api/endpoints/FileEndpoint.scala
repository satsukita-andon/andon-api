package andon.api.endpoints

import java.io.{ File, FileOutputStream }
import java.util.UUID
import org.apache.commons.io.FilenameUtils
import com.twitter.util.Future
import com.twitter.finagle.http.exp.Multipart.{ FileUpload, InMemoryFileUpload, OnDiskFileUpload }
import com.twitter.io._
import io.finch._
import scalikejdbc.DB

import andon.api.jsons._
import andon.api.errors._
import andon.api.util._

object FileEndpoint extends FileEndpoint {
}
trait FileEndpoint extends EndpointBase {

  val storage = "../static.satsukita-andon.com/" // must be end with slash ("/")
  val baseUrl = "https://static.satsukita-andon.com/"

  val name = "file"
  def all = resources :+: classImages

  // dir: "" or "hoge/" or "hoge/fuga/"
  // filename: "123456789" (not "123456789.jpg")
  def output(uploaded: FileUpload, dir: => String, filename: => String): Future[Output[Url]] = {
    val ext = FilenameUtils.getExtension(uploaded.fileName)
    val path = dir + filename + "." + ext
    val dest = new FileOutputStream(new File(storage + path))
    val w = Writer.fromOutputStream(dest)
    def result(r: Reader) = {
      new File(storage + dir).mkdirs()
      Reader.copy(r, w).map { _ =>
        Ok(Url(baseUrl + path))
      }
    }
    uploaded match {
      case u: InMemoryFileUpload => {
        result(Reader.fromBuf(u.content))
      }
      case u: OnDiskFileUpload => {
        result(Reader.fromFile(u.content))
      }
      case _ => {
        Future.value(InternalServerError(Unexpected("unexpected subtype of FileUpload. please report.")))
      }
    }
  }

  def resources: Endpoint[Url] = post(ver / name / "resources" :: token :: fileUpload("file")) { (token: Token, uploaded: FileUpload) =>
    DB.readOnly { implicit s =>
      token.rejectedOnlyAsync(Right.Suspended) { _ =>
        val uuid = UUID.randomUUID().toString()
        output(uploaded, "files/data/", uuid)
      }
    }
  }

  def classImages: Endpoint[Url] = post(ver / name / "class-images" / classId :: token :: fileUpload("file")) { (c: ClassId, token: Token, uploaded: FileUpload) =>
    DB.readOnly { implicit s =>
      token.rejectedOnlyAsync(Right.Suspended) { _ =>
        val uuid = UUID.randomUUID().toString()
        output(uploaded, dir, uuid)
      }
    }
  }
}

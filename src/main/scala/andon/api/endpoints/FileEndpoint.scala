package andon.api.endpoints

import scala.sys.process._
import java.io.{ File, FileOutputStream }
import java.util.UUID
import org.apache.commons.io.{ FilenameUtils, FileUtils }
import com.twitter.util.Future
import com.twitter.finagle.http.exp.Multipart.{ FileUpload, InMemoryFileUpload, OnDiskFileUpload }
import com.twitter.io._
import io.finch._
import scalikejdbc.DB
import com.typesafe.config.ConfigFactory

import andon.api.jsons._
import andon.api.errors._
import andon.api.util._
import andon.api.models.UserModel

object FileEndpoint extends FileEndpoint {
  val UserModel = andon.api.models.UserModel
}
trait FileEndpoint extends EndpointBase {

  val UserModel: UserModel

  private val conf = ConfigFactory.load()
  private val storage = conf.getString("static.path")
  private val baseUrl = conf.getString("static.base")

  val name = "file"
  def all = resources :+: images :+: icon

  sealed abstract class ImageFormat
  object ImageFormat {
    // http://www.yosbits.com/wordpress/?p=1683
    case object Jpeg extends ImageFormat
    case object Png extends ImageFormat
    case object Gif extends ImageFormat
    case object Tiff extends ImageFormat
    case object Bmp extends ImageFormat
    case object Svg extends ImageFormat
    case object Pdf extends ImageFormat
    def fromExtension(ext: String): Option[ImageFormat] = ext.toLowerCase match {
      case "jpg" | "jpeg" => Some(Jpeg)
      case "png" => Some(Png)
      case "gif" => Some(Gif)
      case "tiff" | "tif" => Some(Tiff)
      case "bmp" => Some(Bmp)
      case "svg" | "svgz" => Some(Svg)
      case "pdf" => Some(Pdf)
      case _ => None
    }
  }

  private def saveThumbnail(format: ImageFormat, src: String, dest: String, width: Int = 500): Unit = {
    format match {
      case ImageFormat.Pdf | ImageFormat.Svg => FileUtils.copyFile(new File(src), new File(dest))
      case _ => s"convert -resize ${width}x -quality 75 $src $dest".!
    }
  }

  private def saveFullsize(format: ImageFormat, src: String, dest: String): Unit = {
    format match {
      case ImageFormat.Pdf | ImageFormat.Svg => FileUtils.copyFile(new File(src), new File(dest))
      case _ => s"convert -quality 50 $src $dest".!
    }
  }

  private def fileReader[A](uploaded: FileUpload)(cont: Reader => Future[Output[A]]): Future[Output[A]] = uploaded match {
    case u: InMemoryFileUpload => cont(Reader.fromBuf(u.content))
    case u: OnDiskFileUpload => cont(Reader.fromFile(u.content))
    case _ => Future.value(InternalServerError(Unexpected("unexpected subtype of FileUpload. please report.")))
  }

  def resources: Endpoint[Url] = post(
    ver / name / "resources" :: token :: fileUpload("file")
  ) { (token: Token, uploaded: FileUpload) =>
    DB.readOnly { implicit s =>
      token.rejectedOnlyAsync(Right.Suspended) { user =>
        fileReader(uploaded) { reader =>
          val ext = FilenameUtils.getExtension(uploaded.fileName)
          val uuid = UUID.randomUUID().toString()
          val dir = s"${user.id}/resources/"
          val path = dir + uuid + "." + ext
          val dest = new File(storage + path)
          new File(storage + dir).mkdirs()
          val writer = Writer.fromOutputStream(new FileOutputStream(dest))
          Reader.copy(reader, writer).map { _ =>
            Ok(Url(baseUrl + path))
          }
        }
      }
    }
  }

  def images: Endpoint[ImageUrl] = post(
    ver / name / "images" / token :: fileUpload("file")
  ) { (token: Token, uploaded: FileUpload) =>
    DB.readOnly { implicit s =>
      token.rejectedOnlyAsync(Right.Suspended) { user =>
        val ext = FilenameUtils.getExtension(uploaded.fileName)
        ImageFormat.fromExtension(ext).map { format =>
          fileReader(uploaded) { reader =>
            val uuid = UUID.randomUUID().toString()
            def dir(t: String) = s"${user.id}/images/${t}/"
            def path(t: String) = dir(t) + uuid + "." + ext
            val rawFile = new File(storage + path("raw"))
            new File(storage + dir("raw")).mkdirs()
            val writer = Writer.fromOutputStream(new FileOutputStream(rawFile))
            Reader.copy(reader, writer).map { _ =>
              format match {
                case ImageFormat.Svg | ImageFormat.Pdf => {
                  val rawUrl = baseUrl + path("raw")
                  Ok(ImageUrl(rawUrl, rawUrl, rawUrl))
                }
                case _ => {
                  new File(storage + dir("thumbnail")).mkdirs()
                  new File(storage + dir("fullsize")).mkdirs()
                  saveThumbnail(format, storage + path("raw"), storage + path("thumbnail"))
                  saveFullsize(format, storage + path("raw"), storage + path("fullsize"))
                  Ok(ImageUrl(
                    thumbnail_url = baseUrl + path("thumbnail"),
                    fullsize_url = baseUrl + path("fullsize"),
                    raw_url = baseUrl + path("raw")
                  ))
                }
              }
            }
          }
        }.getOrElse(Future.value(BadRequest(InvalidFileFormat())))
      }
    }
  }

  def icon: Endpoint[Url] = post(
    ver / name / "icon" :: token :: fileUpload("file")
  ) { (token: Token, uploaded: FileUpload) =>
    DB.localTx { implicit s =>
      token.rejectedOnlyAsync(Right.Suspended) { user =>
        val ext = FilenameUtils.getExtension(uploaded.fileName)
        ImageFormat.fromExtension(ext).map { format =>
          fileReader(uploaded) { reader =>
            val uuid = UUID.randomUUID().toString()
            val dir = s"${user.id}/icon/"
            val path = dir + uuid + "." + ext
            new File(storage + dir).mkdirs()
            val writer = Writer.fromOutputStream(new FileOutputStream(storage + path))
            UserModel.updateIcon(user.id, baseUrl + path) // do not put in Future (i.e., another execution context)
            Reader.copy(reader, writer).map { _ =>
              saveThumbnail(format, storage + path, storage + path, 256)
              Ok(Url(baseUrl + path))
            }
          }
        }.getOrElse(Future.value(BadRequest(InvalidFileFormat())))
      }
    }
  }
}

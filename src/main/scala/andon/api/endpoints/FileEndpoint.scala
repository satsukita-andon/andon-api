package andon.api.endpoints

import java.io.{ File, FileOutputStream }
import java.util.UUID
import org.apache.commons.io.{ FilenameUtils, FileUtils }
import com.twitter.util.Future
import com.twitter.finagle.http.exp.Multipart.{ FileUpload, InMemoryFileUpload, OnDiskFileUpload }
import com.twitter.io._
import io.finch._
import scalikejdbc.DB
import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.nio.{ Reader => _, _ }

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

  private def saveThumbnail(format: ImageFormat, src: File, dest: File): Unit = {
    val width = 500
    (format match {
      case ImageFormat.Jpeg | ImageFormat.Bmp => Some(JpegWriter(75, true))
      case ImageFormat.Png => Some(PngWriter(7))
      case ImageFormat.Gif => Some(GifWriter.Progressive)
      case ImageFormat.Tiff => Some(new TiffWriter)
      case _ => None // vector image
    }) match {
      case Some(writer) => Image.fromFile(src).scaleToWidth(width).output(dest)(writer)
      case None => FileUtils.copyFile(src, dest)
    }
  }

  private def saveFullsize(format: ImageFormat, src: File, dest: File): Unit = {
    (format match {
      case ImageFormat.Jpeg | ImageFormat.Bmp => Some(JpegWriter(50, true))
      case ImageFormat.Png => Some(PngWriter.MaxCompression)
      case ImageFormat.Gif => Some(GifWriter.Progressive)
      case ImageFormat.Tiff => Some(new TiffWriter)
      case _ => None // vector image
    }) match {
      case Some(writer) => Image.fromFile(src).output(dest)(writer)
      case None => FileUtils.copyFile(src, dest)
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
      token.rejectedOnlyAsync(Right.Suspended) { _ =>
        fileReader(uploaded) { reader =>
          val ext = FilenameUtils.getExtension(uploaded.fileName)
          val uuid = UUID.randomUUID().toString()
          val dir = "files/data/"
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

  def classImages: Endpoint[ImageUrl] = post(
    ver / name / "class-images" / classId :: token :: fileUpload("file")
  ) { (c: ClassId, token: Token, uploaded: FileUpload) =>
    DB.readOnly { implicit s =>
      token.rejectedOnlyAsync(Right.Suspended) { _ =>
        val ext = FilenameUtils.getExtension(uploaded.fileName)
        ImageFormat.fromExtension(ext).map { format =>
          fileReader(uploaded) { reader =>
            val uuid = UUID.randomUUID().toString()
            def dir(t: String) = {
              s"files/gallery/${t}/${c.times}/${c.grade}/${c.`class`}/"
            }
            def path(t: String) = dir(t) + uuid + "." + ext
            val rawFile = new File(storage + path("raw"))
            new File(storage + dir("raw")).mkdirs()
            val writer = Writer.fromOutputStream(new FileOutputStream(rawFile))
            Reader.copy(reader, writer).map { _ =>
              format match {
                case ImageFormat.Svg | ImageFormat.Pdf => {
                  Ok(ImageUrl(baseUrl + path("raw"), baseUrl + path("raw")))
                }
                case _ => {
                  new File(storage + dir("thumbnail")).mkdirs()
                  new File(storage + dir("fullsize")).mkdirs()
                  saveThumbnail(format, rawFile, new File(storage + path("thumbnail")))
                  saveFullsize(format, rawFile, new File(storage + path("fullsize")))
                  Ok(ImageUrl(
                    thumbnail_url = baseUrl + path("thumbnail"),
                    fullsize_url = baseUrl + path("fullsize")
                  ))
                }
              }
            }
          }
        }.getOrElse(Future.value(BadRequest(InvalidFileFormat())))
      }
    }
  }
}

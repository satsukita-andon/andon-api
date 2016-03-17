package andon.api.models

import cats.data.Xor
import org.joda.time.DateTime
import scalikejdbc._

import andon.api.errors._
import andon.api.models.generated.{ Class, ClassArticle, ClassArticleRevision, User }
import andon.api.util._

object ClassArticleModel extends ClassArticleModel {
  protected val ClassModel = andon.api.models.ClassModel
}
trait ClassArticleModel {

  protected val ClassModel: ClassModel

  private val c = Class.c
  val ca = ClassArticle.ca
  val car = ClassArticleRevision.car

  private def revisionOpt(r: SyntaxProvider[ClassArticleRevision])(rs: WrappedResultSet): Option[ClassArticleRevision] =
    rs.shortOpt(r.resultName.id).map(_ => ClassArticleRevision(r)(rs))

  def find(id: Int)(implicit s: DBSession): Option[(ClassArticle, ClassArticleRevision)] = {
    withSQL {
      select.from(ClassArticle as ca)
        .innerJoin(ClassArticleRevision as car)
        .on(SQLSyntax.eq(ca.id, car.articleId).and
          .eq(ca.latestRevisionNumber, car.revisionNumber))
        .where
        .eq(ca.id, id)
    }.one(ClassArticle(ca))
      .toOne(ClassArticleRevision(car))
      .map { (article, revision) => (article, revision) }
      .single
      .apply()
  }

  def findAll(classId: Short, paging: Paging)(implicit s: DBSession):
      Seq[(ClassArticle, ClassArticleRevision, Option[User], Option[User])] = {
    val u1 = User.syntax("u1")
    val u2 = User.syntax("u2")
    withSQL {
      paging.sql {
        select.from(ClassArticle as ca)
          .innerJoin(ClassArticleRevision as car)
          .on(SQLSyntax.eq(ca.id, car.articleId).and
            .eq(ca.latestRevisionNumber, car.revisionNumber))
          .leftJoin(User as u1).on(ca.createdBy, u1.id)
          .leftJoin(User as u2).on(car.userId, u2.id)
          .where
          .eq(ca.classId, classId)
      }
    }.one(ClassArticle(ca))
      .toManies(rs => Some(ClassArticleRevision(car)(rs)), UserModel.opt(u1), UserModel.opt(u2))
      .map { (article, revisions, cs, us) => (article, revisions.head, cs.headOption, us.headOption) } // safe
      .list
      .apply()
  }
  def findAll(classId: ClassId, paging: Paging)(implicit s: DBSession):
      Seq[(ClassArticle, ClassArticleRevision, Option[User], Option[User])] = {
    ClassModel.findId(classId).map(findAll(_, paging)).getOrElse(Seq())
  }

  def count(classId: Short)(implicit s: DBSession): Long = {
    ClassArticle.countBy(SQLSyntax.eq(ca.classId, classId))
  }
  def count(classId: ClassId)(implicit s: DBSession): Long = {
    ClassModel.findId(classId).map(count).getOrElse(0L)
  }

  def findRevisions(articleId: Int, paging: Paging)(implicit s: DBSession): Option[(ClassArticle, Option[User], Seq[(ClassArticleRevision, Option[User])])] = {
    val u1 = User.syntax("u1")
    val u2 = User.syntax("u2")
    withSQL {
      paging.sql {
        select.from(ClassArticle as ca)
          .leftJoin(ClassArticleRevision as car).on(SQLSyntax
            .eq(ca.id, car.articleId).and
            .eq(ca.latestRevisionNumber, car.revisionNumber))
          .leftJoin(User as u1).on(ca.createdBy, u1.id)
          .leftJoin(User as u2).on(car.userId, u2.id)
          .where.eq(ca.id, articleId)
      }
    }.one(ClassArticle(ca))
      .toManies(revisionOpt(car), UserModel.opt(u1), UserModel.opt(u2))
      .map { (a, rs, cs, us) =>
        val d = us.groupBy(_.id).mapValues(_.head) // safe
        (a, cs.headOption, rs.map(r => (r, r.userId.flatMap(d.get))))
      }
      .single.apply()
  }

  def countRevisions(articleId: Int)(implicit s: DBSession): Long = {
    ClassArticleRevision.countBy(SQLSyntax.eq(car.articleId, articleId))
  }

  def findClassId(articleId: Int)(implicit s: DBSession): Option[ClassId] = {
    withSQL {
      select.from(ClassArticle as ca)
        .innerJoin(Class as c).on(ca.classId, c.id)
        .where.eq(ca.id, articleId)
    }.map(rs => (rs.short(c.times), rs.short(c.times), rs.short(c.`class`)))
      .single.apply()
      .map { case (t, g, c) => ClassId(OrdInt(t), g, c) }
  }

  def create(
    userId: Int, // must be existing user id
    classId: Short, // must be existing class id
    status: PublishingStatus,
    title: String,
    body: String,
    comment: String
  )(implicit s: DBSession): Xor[AndonError, (ClassArticle, ClassArticleRevision)] = try {
    val now = DateTime.now
    val ca = ClassArticle.create(
      classId = classId,
      latestRevisionNumber = 1,
      status = status.code,
      createdBy = Some(userId),
      updatedBy = Some(userId),
      createdAt = now,
      updatedAt = now
    )
    val rev = ClassArticleRevision.create(
      articleId = ca.id,
      revisionNumber = 1,
      userId = Some(userId),
      title = title,
      body = body,
      comment = comment,
      createdAt = now
    )
    Xor.right((ca, rev))
  } catch {
    case e: java.sql.SQLException => Xor.left(Incorrect(e.getMessage)) // TODO: check exception type
  }

  def destroy(articleId: Int)(implicit s: DBSession): Boolean = {
    val n = withSQL {
      delete.from(ClassArticle as ca)
        .where.eq(ca.id, articleId)
    }.update.apply()
    if (n == 0) false else true
  }
}

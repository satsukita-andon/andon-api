package andon.api.models

import andon.api.jsons.ArticleCreation
import org.joda.time.DateTime
import scalikejdbc._

import andon.api.models.generated._
import andon.api.util._

object ArticleModel extends ArticleModel {
  val UserModel = andon.api.models.UserModel
}
trait ArticleModel {

  val UserModel: UserModel

  private val a = Article.a
  private val ar = ArticleRevision.ar
  private val u = User.u

  def revisionOpt(r: SyntaxProvider[ArticleRevision])(rs: WrappedResultSet): Option[ArticleRevision] =
    rs.shortOpt(r.resultName.id).map(_ => ArticleRevision(r)(rs))

  def find(articleId: Int)(implicit s: DBSession): Option[(Article, User, ArticleRevision, Option[User])] = {
    // TODO: optimize
    val ao = withSQL {
      select.from(Article as a)
        .innerJoin(User as u).on(u.id, a.ownerId)
        .where
        .eq(a.id, articleId)
    }.one(Article(a))
      .toOne(User(u))
      .map((a, o) => (a, o))
      .single.apply()

    ao.flatMap { case (a, o) =>
      val ru = withSQL {
        select.from(ArticleRevision as ar)
          .leftJoin(User as u).on(u.id, ar.userId)
          .where
          .eq(ar.articleId, articleId)
      }.one(ArticleRevision(ar))
        .toOptionalOne(UserModel.opt(u))
        .map((r, u) => (r, Option(u)))
        .single.apply()
      ru.map { case (r, u) => (a, o, r, u) }
    }
  }

  def findRevisions(
    articleId: Int, paging: Paging
  )(implicit s: DBSession): Option[(Article, User, Seq[(ArticleRevision, Option[User])])] = {
    // TODO: optimize
    val ao = withSQL {
      select.from(Article as a)
        .innerJoin(User as u).on(u.id, a.ownerId)
        .where
        .eq(a.id, articleId)
    }.one(Article(a))
      .toOne(User(u))
      .map((a, o) => (a, o))
      .single.apply()

    ao.map { case (a, o) =>
      val rus = withSQL {
        paging.sql {
          select.from(ArticleRevision as ar)
            .leftJoin(User as u).on(u.id, ar.userId)
            .where
            .eq(ar.articleId, articleId)
            .orderBy(ar.revisionNumber)
        }
      }.one(ArticleRevision(ar))
        .toOne(UserModel.opt(u))
        .map((r, u) => (r, u))
        .list.apply()
      (a, o, rus)
    }
  }

  def countRevisions(articleId: Int)(implicit s: DBSession): Long = {
    ArticleRevision.countBy(SQLSyntax.eq(ar.articleId, articleId))
  }

  def create(userId: Int, creation: ArticleCreation)(implicit s: DBSession): (Article, ArticleRevision) = {
    val now = DateTime.now
    val article = Article.create(
      ownerId = userId,
      latestRevisionNumber = 1,
      status = creation.status.toString,
      editorialRight = creation.editorial_right.toString,
      createdBy = Some(userId),
      updatedBy = Some(userId),
      createdAt = now,
      updatedAt = now
    )
    val revision = ArticleRevision.create(
      articleId = article.id,
      revisionNumber = 1,
      userId = Some(userId),
      title = creation.title,
      body = creation.body,
      comment = creation.comment,
      createdAt = now
    )
    (article, revision)
  }
}

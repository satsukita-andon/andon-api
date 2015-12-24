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
  private val aer = ArticleEditorRel.aer
  private val u = User.u

  def revisionOpt(r: SyntaxProvider[ArticleRevision])(rs: WrappedResultSet): Option[ArticleRevision] =
    rs.shortOpt(r.resultName.id).map(_ => ArticleRevision(r)(rs))

  def editorRelOpt(er: SyntaxProvider[ArticleEditorRel])(rs: WrappedResultSet): Option[ArticleEditorRel] =
    rs.intOpt(er.resultName.id).map(_ => ArticleEditorRel(er)(rs))

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
          .eq(ar.articleId, a.id).and
          .eq(ar.revisionNumber, a.latestRevisionNumber)
      }.one(ArticleRevision(ar))
        .toOne(UserModel.opt(u))
        .map((r, u) => (r, u))
        .single.apply()
      ru.map { case (r, u) => (a, o, r, u) }
    }
  }

  def findAll(paging: Paging)(implicit s: DBSession): Seq[(Article, User, ArticleRevision, Option[User])] = {
    // TODO: optimize
    val aos = withSQL {
      paging.sql {
        select.from(Article as a)
          .innerJoin(User as u).on(u.id, a.ownerId)
          .orderBy(a.createdAt)
      }
    }.one(Article(a))
      .toOne(User(u))
      .map((a, o) => (a, o))
      .list.apply()

    aos.map { case (a, o) =>
      val ru = withSQL {
        select.from(ArticleRevision as ar)
          .leftJoin(User as u).on(u.id, ar.userId)
          .where
          .eq(ar.articleId, a.id).and
          .eq(ar.revisionNumber, a.latestRevisionNumber)
      }.one(ArticleRevision(ar))
        .toOne(UserModel.opt(u))
        .map((r, u) => (r, u))
        .single.apply()
      ru.map { case (r, u) => (a, o, r, u) }.get // not cause error
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

  def findMeta(articleId: Int)(implicit s: DBSession): Option[(PublishingStatus, EditorialRight, Int, Seq[Int])] = {
    withSQL {
      select.from(Article as a)
        .leftJoin(ArticleEditorRel as aer).on(a.id, aer.articleId)
        .where
        .eq(a.id, articleId)
    }.one(Article(a))
      .toMany(editorRelOpt(aer))
      .map((a: Article, es: Seq[ArticleEditorRel]) => (
        PublishingStatus.unsafeFrom(a.status),
        EditorialRight.unsafeFrom(a.editorialRight),
        a.ownerId, es.map(_.userId))
      )
      .single.apply()
  }

  def countAll(implicit s: DBSession): Long = {
    Article.countAll()
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

  def updateContent(
    articleId: Int,
    userId: Int,
    title: String,
    body: String,
    comment: String
  )(implicit s: DBSession): Option[(Article, ArticleRevision)] = {
    val now = DateTime.now
    Article.find(articleId).map { article =>
      val updated = article.copy(
        latestRevisionNumber = (article.latestRevisionNumber + 1).toShort,
        updatedBy = Some(userId),
        updatedAt = now
      ).save()
      val revision = ArticleRevision.create(
        articleId = articleId,
        revisionNumber = updated.latestRevisionNumber,
        userId = Some(userId),
        title = title,
        body = body,
        comment = comment,
        createdAt = now
      )
      (updated, revision)
    }
  }

  def updateMeta(
    articleId: Int,
    userId: Int,
    status: PublishingStatus,
    editorialRight: EditorialRight,
    editorIdSet: Set[Int]
  )(implicit s: DBSession): Option[(Article, Set[Int])] = {
    val now = DateTime.now
    Article.find(articleId).map { article =>
      // update article
      val updated = article.copy(
        status = status.toString,
        editorialRight = editorialRight.toString,
        updatedBy = Some(userId),
        updatedAt = now
      ).save()
      // prepare editor ids
      val currentEditorIdSet = ArticleEditorRel.findAllBy(SQLSyntax.eq(aer.articleId, articleId)).map(_.userId).toSet
      val newEditorIdSet = editorIdSet -- currentEditorIdSet
      val removeEditorIdSet = currentEditorIdSet -- editorIdSet
      // insert new editors
      ArticleEditorRel.batchInsert(
        newEditorIdSet.toSeq.map { editorId =>
          ArticleEditorRel(0, articleId, editorId)
        }
      )
      // remove old editors
      withSQL {
        delete.from(ArticleEditorRel as aer)
          .where.in(aer.userId, removeEditorIdSet.toSeq)
      }.update().apply()
      (updated, editorIdSet)
    }
  }
}

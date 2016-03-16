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

  val a = Article.a
  val ar = ArticleRevision.ar
  val aer = ArticleEditorRel.aer
  val at = ArticleTag.at
  private val u = User.u

  def revisionOpt(r: SyntaxProvider[ArticleRevision])(rs: WrappedResultSet): Option[ArticleRevision] =
    rs.shortOpt(r.resultName.id).map(_ => ArticleRevision(r)(rs))

  def articleTagOpt(t: SyntaxProvider[ArticleTag])(rs: WrappedResultSet): Option[ArticleTag] =
    rs.intOpt(t.resultName.id).map(_ => ArticleTag(t)(rs))

  def editorRelOpt(er: SyntaxProvider[ArticleEditorRel])(rs: WrappedResultSet): Option[ArticleEditorRel] =
    rs.intOpt(er.resultName.id).map(_ => ArticleEditorRel(er)(rs))

  def find(articleId: Int)(implicit s: DBSession): Option[(Article, User, Seq[String], ArticleRevision, Option[User])] = {
    // TODO: optimize
    val aot = withSQL {
      select.from(Article as a)
        .innerJoin(User as u).on(u.id, a.ownerId)
        .leftJoin(ArticleTag as at).on(a.id, at.articleId)
        .where
        .eq(a.id, articleId)
    }.one(Article(a))
      .toManies(rs => Some(User(u)(rs)), articleTagOpt(at))
      .map((a, os, ts) => (a, os.head, ts.map(_.label)))
      .single.apply()

    aot.flatMap { case (a, o, ts) =>
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
      ru.map { case (r, u) => (a, o, ts, r, u) }
    }
  }

  def findAll(paging: Paging)(implicit s: DBSession): Seq[(Article, User, Seq[String], ArticleRevision, Option[User])] = {
    // TODO: optimize, N + 1 query
    val aots = withSQL {
      paging.sql {
        select.from(Article as a)
          .innerJoin(User as u).on(u.id, a.ownerId)
          .leftJoin(ArticleTag as at).on(at.articleId, a.id)
      }
    }.one(Article(a))
      .toManies(rs => Some(User(u)(rs)), articleTagOpt(at))
      .map((a, os, ats) => (a, os.head, ats.map(_.label))) // head is safe
      .list.apply()

    aots.map { case (a, o, ts) =>
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
      ru.map { case (r, u) => (a, o, ts, r, u) }.get // not cause error
    }
  }

  def findRevisions(
    articleId: Int, paging: Paging
  )(implicit s: DBSession): Option[(Article, User, Seq[String], Seq[(ArticleRevision, Option[User])])] = {
    // TODO: optimize
    val aot = withSQL {
      select.from(Article as a)
        .innerJoin(User as u).on(u.id, a.ownerId)
        .leftJoin(ArticleTag as at).on(a.id, at.articleId)
        .where
        .eq(a.id, articleId)
    }.one(Article(a))
      .toManies(rs => Some(User(u)(rs)), articleTagOpt(at))
      .map((a, os, ats) => (a, os.head, ats.map(_.label))) // head is safe
      .single.apply()

    aot.map { case (a, o, t) =>
      val rus = withSQL {
        paging.sql {
          select.from(ArticleRevision as ar)
            .leftJoin(User as u).on(u.id, ar.userId)
            .where
            .eq(ar.articleId, articleId)
        }
      }.one(ArticleRevision(ar))
        .toOne(UserModel.opt(u))
        .map((r, u) => (r, u))
        .list.apply()
      (a, o, t, rus)
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

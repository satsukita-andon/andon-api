package andon.api.models.generated

import scalikejdbc._
import org.joda.time.{DateTime}

case class User(
  id: Int,
  login: String,
  password: String,
  name: String,
  biography: Option[String] = None,
  times: Short,
  classFirst: Option[Short] = None,
  classSecond: Option[Short] = None,
  classThird: Option[Short] = None,
  chiefFirst: Option[Boolean] = None,
  chiefSecond: Option[Boolean] = None,
  chiefThird: Option[Boolean] = None,
  iconUrl: Option[String] = None,
  email: Option[String] = None,
  admin: Boolean,
  suspended: Boolean,
  createdAt: DateTime,
  updatedAt: DateTime) {

  def save()(implicit session: DBSession): User = User.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = User.destroy(this)(session)

}


object User extends SQLSyntaxSupport[User] {

  override val tableName = "users"

  override val columns = Seq("id", "login", "password", "name", "biography", "times", "class_first", "class_second", "class_third", "chief_first", "chief_second", "chief_third", "icon_url", "email", "admin", "suspended", "created_at", "updated_at")

  def apply(u: SyntaxProvider[User])(rs: WrappedResultSet): User = apply(u.resultName)(rs)
  def apply(u: ResultName[User])(rs: WrappedResultSet): User = new User(
    id = rs.get(u.id),
    login = rs.get(u.login),
    password = rs.get(u.password),
    name = rs.get(u.name),
    biography = rs.get(u.biography),
    times = rs.get(u.times),
    classFirst = rs.get(u.classFirst),
    classSecond = rs.get(u.classSecond),
    classThird = rs.get(u.classThird),
    chiefFirst = rs.get(u.chiefFirst),
    chiefSecond = rs.get(u.chiefSecond),
    chiefThird = rs.get(u.chiefThird),
    iconUrl = rs.get(u.iconUrl),
    email = rs.get(u.email),
    admin = rs.get(u.admin),
    suspended = rs.get(u.suspended),
    createdAt = rs.get(u.createdAt),
    updatedAt = rs.get(u.updatedAt)
  )

  val u = User.syntax("u")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[User] = {
    withSQL {
      select.from(User as u).where.eq(u.id, id)
    }.map(User(u.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[User] = {
    withSQL(select.from(User as u)).map(User(u.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(User as u)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[User] = {
    withSQL {
      select.from(User as u).where.append(where)
    }.map(User(u.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[User] = {
    withSQL {
      select.from(User as u).where.append(where)
    }.map(User(u.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(User as u).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    login: String,
    password: String,
    name: String,
    biography: Option[String] = None,
    times: Short,
    classFirst: Option[Short] = None,
    classSecond: Option[Short] = None,
    classThird: Option[Short] = None,
    chiefFirst: Option[Boolean] = None,
    chiefSecond: Option[Boolean] = None,
    chiefThird: Option[Boolean] = None,
    iconUrl: Option[String] = None,
    email: Option[String] = None,
    admin: Boolean,
    suspended: Boolean,
    createdAt: DateTime,
    updatedAt: DateTime)(implicit session: DBSession): User = {
    val generatedKey = withSQL {
      insert.into(User).columns(
        column.login,
        column.password,
        column.name,
        column.biography,
        column.times,
        column.classFirst,
        column.classSecond,
        column.classThird,
        column.chiefFirst,
        column.chiefSecond,
        column.chiefThird,
        column.iconUrl,
        column.email,
        column.admin,
        column.suspended,
        column.createdAt,
        column.updatedAt
      ).values(
        login,
        password,
        name,
        biography,
        times,
        classFirst,
        classSecond,
        classThird,
        chiefFirst,
        chiefSecond,
        chiefThird,
        iconUrl,
        email,
        admin,
        suspended,
        createdAt,
        updatedAt
      )
    }.updateAndReturnGeneratedKey.apply()

    User(
      id = generatedKey.toInt,
      login = login,
      password = password,
      name = name,
      biography = biography,
      times = times,
      classFirst = classFirst,
      classSecond = classSecond,
      classThird = classThird,
      chiefFirst = chiefFirst,
      chiefSecond = chiefSecond,
      chiefThird = chiefThird,
      iconUrl = iconUrl,
      email = email,
      admin = admin,
      suspended = suspended,
      createdAt = createdAt,
      updatedAt = updatedAt)
  }

  def batchInsert(entities: Seq[User])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'login -> entity.login,
        'password -> entity.password,
        'name -> entity.name,
        'biography -> entity.biography,
        'times -> entity.times,
        'classFirst -> entity.classFirst,
        'classSecond -> entity.classSecond,
        'classThird -> entity.classThird,
        'chiefFirst -> entity.chiefFirst,
        'chiefSecond -> entity.chiefSecond,
        'chiefThird -> entity.chiefThird,
        'iconUrl -> entity.iconUrl,
        'email -> entity.email,
        'admin -> entity.admin,
        'suspended -> entity.suspended,
        'createdAt -> entity.createdAt,
        'updatedAt -> entity.updatedAt))
        SQL("""insert into users(
        login,
        password,
        name,
        biography,
        times,
        class_first,
        class_second,
        class_third,
        chief_first,
        chief_second,
        chief_third,
        icon_url,
        email,
        admin,
        suspended,
        created_at,
        updated_at
      ) values (
        {login},
        {password},
        {name},
        {biography},
        {times},
        {classFirst},
        {classSecond},
        {classThird},
        {chiefFirst},
        {chiefSecond},
        {chiefThird},
        {iconUrl},
        {email},
        {admin},
        {suspended},
        {createdAt},
        {updatedAt}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: User)(implicit session: DBSession): User = {
    withSQL {
      update(User).set(
        column.id -> entity.id,
        column.login -> entity.login,
        column.password -> entity.password,
        column.name -> entity.name,
        column.biography -> entity.biography,
        column.times -> entity.times,
        column.classFirst -> entity.classFirst,
        column.classSecond -> entity.classSecond,
        column.classThird -> entity.classThird,
        column.chiefFirst -> entity.chiefFirst,
        column.chiefSecond -> entity.chiefSecond,
        column.chiefThird -> entity.chiefThird,
        column.iconUrl -> entity.iconUrl,
        column.email -> entity.email,
        column.admin -> entity.admin,
        column.suspended -> entity.suspended,
        column.createdAt -> entity.createdAt,
        column.updatedAt -> entity.updatedAt
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: User)(implicit session: DBSession): Unit = {
    withSQL { delete.from(User).where.eq(column.id, entity.id) }.update.apply()
  }

}

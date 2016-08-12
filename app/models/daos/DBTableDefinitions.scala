package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape.proveShapeOf

trait DBTableDefinitions {

  protected val driver: JdbcProfile
  import driver.api._

  case class DBUser(
    userID: UUID,
    firstName: Option[String],
    lastName: Option[String],
    fullName: Option[String],
    email: Option[String],
    avatarURL: Option[String]
  )

  class Users(tag: Tag) extends Table[DBUser](tag, "users") {
    def userID = column[UUID]("user_id", O.PrimaryKey)
    def firstName = column[Option[String]]("first_name")
    def lastName = column[Option[String]]("last_name")
    def fullName = column[Option[String]]("full_name")
    def email = column[Option[String]]("email")
    def avatarURL = column[Option[String]]("avatar_url")
    def * = (userID, firstName, lastName, fullName, email, avatarURL) <> (DBUser.tupled, DBUser.unapply)
  }

  case class DBLoginInfo(
    loginInfoID: UUID,
    providerID: String,
    providerKey: String
  )

  class LoginInfos(tag: Tag) extends Table[DBLoginInfo](tag, "logininfos") {
    def loginInfoID = column[UUID]("login_info_id", O.PrimaryKey)
    def providerID = column[String]("provider_id")
    def providerKey = column[String]("provider_key")
    def * = (loginInfoID, providerID, providerKey) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  case class DBUserLoginInfo(
    userLoginInfoID: UUID,
    userID: UUID,
    loginInfoID: UUID
  )

  class UserLoginInfos(tag: Tag) extends Table[DBUserLoginInfo](tag, "userlogininfos") {
    def userLoginInfoID = column[UUID]("user_login_info_id")
    def userID = column[UUID]("user_id")
    def loginInfoID = column[UUID]("login_info_id")
    def * = (userLoginInfoID, userID, loginInfoID) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  case class DBPasswordInfo(
    passwordInfoID: UUID,
    hasher: String,
    password: String,
    salt: Option[String],
    loginInfoID: UUID
  )

  class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, "passwordinfos") {
    def passwordInfoID = column[UUID]("password_info_id", O.PrimaryKey)
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")
    def loginInfoID = column[UUID]("login_info_id")
    def * = (passwordInfoID, hasher, password, salt, loginInfoID) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  case class DBOAuth1Info(
    oauth1InfoID: UUID,
    token: String,
    secret: String,
    loginInfoID: UUID
  )

  class OAuth1Infos(tag: Tag) extends Table[DBOAuth1Info](tag, "oauth1infos") {
    def oauth1InfoID = column[UUID]("oauth1_info_id", O.PrimaryKey)
    def token = column[String]("token")
    def secret = column[String]("secret")
    def loginInfoID = column[UUID]("login_info_id")
    def * = (oauth1InfoID, token, secret, loginInfoID) <> (DBOAuth1Info.tupled, DBOAuth1Info.unapply)
  }

  case class DBOAuth2Info(
    oauth2InfoID: UUID,
    accessToken: String,
    tokenType: Option[String],
    expiresIn: Option[Int],
    refreshToken: Option[String],
    loginInfoID: UUID
  )

  class OAuth2Infos(tag: Tag) extends Table[DBOAuth2Info](tag, "oauth2infos") {
    def oauth2InfoID = column[UUID]("oauth2_info_id", O.PrimaryKey)
    def accessToken = column[String]("accesstoken")
    def tokenType = column[Option[String]]("tokentype")
    def expiresIn = column[Option[Int]]("expiresin")
    def refreshToken = column[Option[String]]("refreshtoken")
    def loginInfoID = column[UUID]("login_info_id")
    def * = (oauth2InfoID, accessToken, tokenType, expiresIn, refreshToken, loginInfoID) <> (DBOAuth2Info.tupled, DBOAuth2Info.unapply)
  }

  case class DBOpenIDInfo(
    id: String,
    loginInfoID: UUID
  )

  class OpenIDInfos(tag: Tag) extends Table[DBOpenIDInfo](tag, "openidinfos") {
    def id = column[String]("id", O.PrimaryKey)
    def loginInfoID = column[UUID]("login_info_id")
    def * = (id, loginInfoID) <> (DBOpenIDInfo.tupled, DBOpenIDInfo.unapply)
  }

  case class DBOpenIDAttribute(
    id: String,
    key: String,
    value: String
  )

  class OpenIDAttributes(tag: Tag) extends Table[DBOpenIDAttribute](tag, "openidattributes") {
    def id = column[String]("id")
    def key = column[String]("key")
    def value = column[String]("value")
    def * = (id, key, value) <> (DBOpenIDAttribute.tupled, DBOpenIDAttribute.unapply)

  }

  // table query definitions
  val slickUsers = TableQuery[Users]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickUserLoginInfos = TableQuery[UserLoginInfos]
  val slickPasswordInfos = TableQuery[PasswordInfos]
  val slickOAuth1Infos = TableQuery[OAuth1Infos]
  val slickOAuth2Infos = TableQuery[OAuth2Infos]
  val slickOpenIDInfos = TableQuery[OpenIDInfos]
  val slickOpenIDAttributes = TableQuery[OpenIDAttributes]

  // queries used in multiple places
  def loginInfoQuery(loginInfo: LoginInfo) =
    slickLoginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)
}

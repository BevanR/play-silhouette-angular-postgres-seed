package models.daos

import java.util.UUID
import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.dbio.DBIOAction
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future

/**
 * Give access to the user object using Slick
 */
class UserDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends UserDAO with DAOSlick {

  import driver.api._

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo) = {
    val userQuery = for {
      dbLoginInfo <- loginInfoQuery(loginInfo)
      dbUserLoginInfo <- slickUserLoginInfos.filter(_.loginInfoID === dbLoginInfo.loginInfoID)
      dbUser <- slickUsers.filter(_.userID === dbUserLoginInfo.userID)
    } yield dbUser
    db.run(userQuery.result.headOption).map { dbUserOption =>
      dbUserOption.map { user =>
        User(user.userID, loginInfo, user.firstName, user.lastName, user.fullName, user.email, user.avatarURL)
      }
    }
  }

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: UUID) = {
    val query = for {
      dbUser <- slickUsers.filter(_.userID === userID)
      dbUserLoginInfo <- slickUserLoginInfos.filter(_.userID === dbUser.userID)
      dbLoginInfo <- slickLoginInfos.filter(_.loginInfoID === dbUserLoginInfo.loginInfoID)
    } yield (dbUser, dbLoginInfo)
    db.run(query.result.headOption).map { resultOption =>
      resultOption.map {
        case (user, loginInfo) =>
          User(
            user.userID,
            LoginInfo(loginInfo.providerID, loginInfo.providerKey),
            user.firstName,
            user.lastName,
            user.fullName,
            user.email,
            user.avatarURL)
      }
    }
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User) = {
    val dbUser = DBUser(user.userID, user.firstName, user.lastName, user.fullName, user.email, user.avatarURL)
    val dbLoginInfo = DBLoginInfo(UUID.randomUUID(), user.loginInfo.providerID, user.loginInfo.providerKey)
    // We don't have the LoginInfo id so we try to get it first.
    // If there is no LoginInfo yet for this user we retrieve the id on insertion.    
    val loginInfoAction = {
      val retrieveLoginInfo = slickLoginInfos.filter(
        info => info.providerID === user.loginInfo.providerID &&
          info.providerKey === user.loginInfo.providerKey).result.headOption
      val insertLoginInfo = slickLoginInfos.returning(slickLoginInfos.map(_.loginInfoID)).
        into((info, id) => info.copy(id)) += dbLoginInfo
      for {
        loginInfoOption <- retrieveLoginInfo
        loginInfo <- loginInfoOption.map(DBIO.successful(_)).getOrElse(insertLoginInfo)
      } yield loginInfo
    }
    // combine database actions to be run sequentially
    val actions = (for {
      _ <- slickUsers.insertOrUpdate(dbUser)
      loginInfo <- loginInfoAction
      _ <- slickUserLoginInfos += DBUserLoginInfo(UUID.randomUUID(), dbUser.userID, loginInfo.loginInfoID)
    } yield ()).transactionally
    // run actions and return user afterwards
    db.run(actions).map(_ => user)
  }
}

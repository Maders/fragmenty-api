package controllers

import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class UserController @Inject() (
    val controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext)
    extends BaseController {

  def authenticateUser: Action[AnyContent] = Action { implicit request =>
    Ok("User authenticated").withSession("user" -> "testuser")
  }
}

package controllers

import play.api.mvc._

import javax.inject.{Inject, Singleton}

@Singleton
class UserController @Inject() (
    val controllerComponents: ControllerComponents
) extends BaseController {

  def authenticateUser: Action[AnyContent] = Action { implicit request =>
    Ok("User authenticated").withSession("user" -> "test_user")
  }
}

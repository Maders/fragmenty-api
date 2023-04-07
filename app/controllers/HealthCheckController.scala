package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class HealthCheckController @Inject() (val controllerComponents: ControllerComponents) extends BaseController {

  def healthCheck(): Action[AnyContent] = Action {
    Ok("Application is healthy")
  }
}

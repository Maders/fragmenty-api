package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl._
import play.api.libs.json._
import play.api.mvc._
import repositories._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuctionController @Inject() (
                                 val controllerComponents: ControllerComponents,
                                 auctionRepository: AuctionRepository
                               )(implicit ec: ExecutionContext, system: ActorSystem, mat: Materializer)
  extends BaseController {

  def authenticateUser: Action[AnyContent] = Action { implicit request =>
    Ok("User authenticated").withSession("user" -> "testuser")
  }


  def getAuctions(page: Int, pageSize: Int): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      val skip = (page - 1) * pageSize
      auctionRepository.findAll(pageSize, skip).map { auctions =>
        Ok(Json.toJson(auctions))
      }
  }

  def auctionsWebSocket: WebSocket =
    WebSocket.acceptOrResult[JsValue, JsValue] { request =>
      Future.successful(request.session.get("user") match {
        case None =>
          Left(Forbidden)
        case Some(_) =>
          val source = Source
            .fromPublisher(auctionRepository.findAllPublisher(10, 0))
            .map(Json.toJson(_))
            .grouped(10)
            .map(
              _.foldLeft(JsArray())((acc, jsValue) => acc ++ Json.arr(jsValue))
            )

          val flow = Flow.fromSinkAndSource(Sink.ignore, source)

          Right(flow)
      })
    }
}

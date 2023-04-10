package controllers

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl._
import play.api.libs.json._
import play.api.mvc._
import repositories._
import akka.stream.scaladsl.Source

import scala.concurrent.duration._
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

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

  def auctionsWebSocket: WebSocket = {
    WebSocket.accept[JsValue, JsValue] { request =>
      val source = Source
        .tick(0.seconds, 5.minute, ())
        .flatMapConcat { _ =>
          Source
            .fromPublisher(auctionRepository.findAllPublisher())
            .map(Json.toJson(_))
            .fold(JsArray())((acc, jsValue) => acc ++ Json.arr(jsValue))
            .mapMaterializedValue(_ => NotUsed)
        }

      val flow = Flow.fromSinkAndSource(Sink.ignore, source)
      flow
    }
  }

  def futureAuctionsWebSocket: WebSocket = {
    WebSocket.accept[JsValue, JsValue] { request =>
      val source = Source
        .tick(0.seconds, 5.minute, ())
        .flatMapConcat { _ =>
          Source
            .fromPublisher(auctionRepository.findFutureAuctions())
            .map(Json.toJson(_))
            .fold(JsArray())((acc, jsValue) => acc ++ Json.arr(jsValue))
            .mapMaterializedValue(_ => NotUsed)
        }

      val flow = Flow.fromSinkAndSource(Sink.ignore, source)
      flow
    }
  }

  def pastAuctionsWebSocket: WebSocket = {
    WebSocket.accept[JsValue, JsValue] { request =>
      val source = Source
        .tick(0.seconds, 5.minute, ())
        .flatMapConcat { _ =>
          Source
            .fromPublisher(auctionRepository.findPastAuctions())
            .map(Json.toJson(_))
            .fold(JsArray())((acc, jsValue) => acc ++ Json.arr(jsValue))
            .mapMaterializedValue(_ => NotUsed)
        }

      val flow = Flow.fromSinkAndSource(Sink.ignore, source)
      flow
    }
  }
}

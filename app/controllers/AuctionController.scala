package controllers

import akka.stream.Materializer
import akka.stream.scaladsl.Source
import play.api.libs.json._
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

import repositories._
import utils.WebSocketUtil._

@Singleton
class AuctionController @Inject() (
    val controllerComponents: ControllerComponents,
    auctionRepository: AuctionRepository
)(implicit ec: ExecutionContext, mat: Materializer)
    extends BaseController {

  def authenticateUser: Action[AnyContent] = Action { implicit request =>
    Ok("User authenticated").withSession("user" -> "test_user")
  }

  def getAuctions(page: Int, pageSize: Int): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      val skip = (page - 1) * pageSize
      auctionRepository
        .findAll(pageSize, skip)
        .map(auctions => Ok(Json.toJson(auctions)))
  }

  def auctionsWebSocket: WebSocket = createWebSocketFlow(() =>
    Source.fromPublisher(auctionRepository.findAllPublisher())
  )

  def futureAuctionsWebSocket: WebSocket = createWebSocketFlow(() =>
    Source.fromPublisher(auctionRepository.findFutureAuctions())
  )

  def pastAuctionsWebSocket: WebSocket = createWebSocketFlow(() =>
    Source.fromPublisher(auctionRepository.findPastAuctions())
  )
}

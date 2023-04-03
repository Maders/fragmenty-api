package controllers

import javax.inject._
import akka.actor.ActorSystem
import akka.stream.Materializer
import models.Auction
import play.api.libs.json.Json
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import repositories.AuctionRepository
import actors.AuctionsWebSocketActor

import scala.concurrent.ExecutionContext

@Singleton
class UserController @Inject()(val controllerComponents: ControllerComponents, auctionRepository: AuctionRepository)(implicit ec: ExecutionContext, system: ActorSystem, mat: Materializer) extends BaseController {

  def getAuctions(page: Int, pageSize: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val skip = (page - 1) * pageSize
    auctionRepository.findAll(pageSize, skip).future.map { auctions =>
      Ok(Json.toJson(auctions))
    }
  }

  def auctionsWebSocket: WebSocket = WebSocket.accept[String, String] { _ =>
    ActorFlow.actorRef { out =>
      AuctionsWebSocketActor.props(out, auctionRepository)
    }
  }
}

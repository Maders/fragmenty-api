package actors

import akka.actor.{Actor, ActorRef, Props}
import repositories.AuctionRepository
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext

class AuctionsWebSocketActor(out: ActorRef, auctionRepository: AuctionRepository)(implicit ec: ExecutionContext) extends Actor {
  import AuctionsWebSocketActor._

  override def receive: Receive = {
    case FetchAuctions(page, pageSize) =>
      val skip = (page - 1) * pageSize
      auctionRepository.findAll(pageSize, skip).future.map { auctions =>
        out ! Json.toJson(auctions).toString
      }
  }
}

object AuctionsWebSocketActor {
  def props(out: ActorRef, auctionRepository: AuctionRepository)(implicit ec: ExecutionContext): Props = Props(new AuctionsWebSocketActor(out, auctionRepository))

  case class FetchAuctions(page: Int, pageSize: Int)
}

package actors

import akka.actor.{Actor, ActorRef, Props}
import play.api.libs.json.Json
import repositories.AuctionRepository

import scala.concurrent.ExecutionContext.Implicits.global

class AuctionsWebSocketActor(
    out: ActorRef,
    auctionRepository: AuctionRepository
) extends Actor {
  import AuctionsWebSocketActor._

  override def receive: Receive = { case FetchAuctions(page, pageSize) =>
    val skip = (page - 1) * pageSize
    auctionRepository.findAll(pageSize, skip).map { auctions =>
      out ! Json.toJson(auctions).toString
    }
  }
}

object AuctionsWebSocketActor {
  def props(out: ActorRef, auctionRepository: AuctionRepository): Props = Props(
    new AuctionsWebSocketActor(out, auctionRepository)
  )

  case class FetchAuctions(page: Int, pageSize: Int)
}

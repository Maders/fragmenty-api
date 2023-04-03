package models

import play.api.libs.json._

case class Auction(
    _id: String,
    id: String,
    auctionEndTimestamp: String,
    memorabilityScore: Double,
    minimumBid: Int,
    minimumBidInUSD: Int,
    number: String
)

object Auction {
  implicit val auctionFormat: Format[Auction] = Json.format[Auction]
}

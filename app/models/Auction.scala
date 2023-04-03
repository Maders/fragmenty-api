package models

import play.api.libs.json._
//import java.math.BigDecimal

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
//  implicit val decimal128Reads: Reads[Decimal128] = Reads { json =>
//    json.validate[String].map(s => new BigDecimal(s)).map(new Decimal128(_))
//  }
//
//  implicit val decimal128Writes: Writes[Decimal128] = Writes { decimal128 =>
//    JsString(decimal128.toString)
//  }

  implicit val auctionFormat: Format[Auction] = Json.format[Auction]
}

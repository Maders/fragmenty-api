package repositories

import models.Auction
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Sorts
import utils.MongoDBConnector
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Promise}

class AuctionRepository @Inject()(implicit ec: ExecutionContext) {
  private val collection: MongoCollection[Document] = MongoDBConnector.database.getCollection("numbers")

  def findAll(limit: Int, skip: Int): Promise[Seq[Auction]] = {
    val promise = Promise[Seq[Auction]]()
    val results = collection.find()
      .sort(Sorts.descending("_id"))
      .skip(skip)
      .limit(limit)
      .toFuture()
    promise.completeWith(results.map(_.map(documentToAuction)))
    promise
  }

  private def documentToAuction(document: Document): Auction = {
    Auction(
      _id = document.getObjectId("_id").toString,
      id = document.getString("id"),
      auctionEndTimestamp = document.getString("auctionEndTimestamp"),
      memorabilityScore = document.getDouble("memorabilityScore"),
      minimumBid = document.getInteger("minimumBid"),
      minimumBidInUSD = document.getInteger("minimumBidInUSD"),
      number = document.getString("number")
    )
  }
}

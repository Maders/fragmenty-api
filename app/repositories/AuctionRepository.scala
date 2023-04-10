package repositories

import akka.stream.Materializer
import akka.stream.scaladsl._
import models.Auction
import org.mongodb.scala._
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.model.Filters._
import org.reactivestreams.Publisher
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import com.google.inject.ImplementedBy
import java.time.Instant

@ImplementedBy(classOf[AuctionRepositoryImpl])
trait AuctionRepository {
  def findAll(limit: Int, skip: Int): Future[Seq[Auction]]

  def findAllPublisher(): Publisher[Auction]

  def findFutureAuctions(): Publisher[Auction]

  def findPastAuctions(): Publisher[Auction]

}

@Singleton
class AuctionRepositoryImpl @Inject() (
    mongoClient: MongoClient
)(implicit ec: ExecutionContext, mat: Materializer)
    extends AuctionRepository {
  private val database: MongoDatabase = mongoClient.getDatabase("fragmenty")
  private val collection: MongoCollection[Document] =
    database.getCollection("numbers")

  override def findAll(limit: Int, skip: Int): Future[Seq[Auction]] = {
    collection
      .find()
      .sort(descending("auctionEndTimestamp"))
      .skip(skip)
      .limit(limit)
      .toFuture()
      .map(_.map(documentToAuction))
  }

  override def findAllPublisher(): Publisher[Auction] = {
    val observable = collection
      .find()
      .sort(descending("auctionEndTimestamp"))
      .toObservable()

    val futureAuctions = observable.toFuture().map(_.map(documentToAuction))

    Source
      .future(futureAuctions)
      .flatMapConcat(auctions => Source(auctions))
      .runWith(Sink.asPublisher(false))
  }

  override def findFutureAuctions(): Publisher[Auction] = {
    val now = Instant.now()
    val futureAuctions = collection
      .find(gt("auctionEndTimestamp", now.toString))
      .sort(ascending("auctionEndTimestamp"))
      .toFuture()
      .map(_.map(documentToAuction))

    Source
      .future(futureAuctions)
      .flatMapConcat(auctions => Source(auctions))
      .runWith(Sink.asPublisher(false))
  }

  override def findPastAuctions(): Publisher[Auction] = {
    val now = Instant.now()
    val futureAuctions = collection
      .find(lt("auctionEndTimestamp", now.toString))
      .sort(ascending("auctionEndTimestamp"))
      .toFuture()
      .map(_.map(documentToAuction))

    Source
      .future(futureAuctions)
      .flatMapConcat(auctions => Source(auctions))
      .runWith(Sink.asPublisher(false))
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

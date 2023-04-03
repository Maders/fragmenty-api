package utils

import org.mongodb.scala.{MongoClient, MongoDatabase}

object MongoDBConnector {
  private val connectionString = sys.env.getOrElse("MONGODB_CONNECTION_STRING", "mongodb://localhost:27017")
  private val client: MongoClient = MongoClient(connectionString)
  val database: MongoDatabase = client.getDatabase("fragmenty")
}

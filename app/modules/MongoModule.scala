package modules

import com.google.inject.{AbstractModule, Provider}
import org.mongodb.scala.MongoClient
import play.api.{Configuration, Environment}

import javax.inject.{Inject, Singleton}

class MongoModule(environment: Environment, configuration: Configuration)
    extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[MongoClient])
      .toProvider(classOf[MongoClientProvider])
      .in(classOf[Singleton])
  }
}

@Singleton
class MongoClientProvider @Inject() (configuration: Configuration)
    extends Provider[MongoClient] {
  override def get(): MongoClient = {
    val mongoUri = configuration.get[String]("mongodb.uri")
    MongoClient(mongoUri)
  }
}

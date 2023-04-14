package utils

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import play.api.libs.json.{JsArray, JsValue, Json, Writes}
import play.api.mvc.WebSocket
import scala.concurrent.duration._

object WebSocketUtil {

  private def createWebSocketSource[A](
      fetchAuctions: () => Source[A, NotUsed]
  )(implicit writes: Writes[A], mat: Materializer): Source[JsValue, NotUsed] = {
    Source
      .tick(0.seconds, 5.minutes, ())
      .mapAsync(1) { _ =>
        fetchAuctions()
          .map(Json.toJson(_))
          .runFold(JsArray())((acc, jsValue) => acc :+ jsValue)
      }
      .mapMaterializedValue((_: Any) => NotUsed)
  }

  def createWebSocketFlow[A](
      fetchAuctions: () => Source[A, NotUsed]
  )(implicit writes: Writes[A], mat: Materializer): WebSocket = {
    WebSocket.accept[JsValue, JsValue](_ =>
      Flow.fromSinkAndSource(Sink.ignore, createWebSocketSource(fetchAuctions))
    )
  }
}

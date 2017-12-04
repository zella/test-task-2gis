package org.zella.titler.server

import com.typesafe.scalalogging.Logger
import io.vertx.core.http.HttpServer
import io.vertx.core.{AsyncResult, Handler, Vertx}
import io.vertx.ext.web.Router
import org.zella.titler.crawler.TitleCrawler
import org.zella.titler.server.Server.Started
import play.api.libs.json.Json

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

object Server {

  case class Started(port: Int)

  private val log = Logger[Server]

  def main(args: Array[String]): Unit = {

    val port = System.getProperty("http.port", "8080").toInt

    new Server(port, TitleCrawler.default).start()
      .onComplete {
        case Success(started) =>
          log.info("Http server started at port: {}", started.port)
        case Failure(e) =>
          log.error("Can't start server", e)
          System.exit(1)
      }

  }
}

class Server(port: Int, crawler: TitleCrawler) {

  private val log = Logger[Server]

  def start(): Future[Started] = {

    val router = Router.router(Vertx.vertx())

    router.get("/grab_titles").handler(ctx => {
      val urls = ctx.request().params().getAll("url").asScala

      Future.traverse(urls)(url =>
        crawler.crawl(url)
          .map(ut => Json.obj("url" -> ut._1, "title" -> ut._2))
          //here we can set failure reason by exception
          .recover { case e => Json.obj("url" -> url, "failed" -> true) })
        .onComplete {
          case Success(jsObjects) => ctx.response().end(
            Json.obj("result" -> jsObjects).toString()
          )
            log.debug(jsObjects.toString())
          case Failure(e) => ctx.fail(e)
        }
    })

    val startPromise = Promise[Started]()

    Vertx.vertx().createHttpServer()
      .requestHandler(router.accept(_))
      .listen(port, new Handler[AsyncResult[HttpServer]] {
        override def handle(event: AsyncResult[HttpServer]): Unit = {
          if (event.succeeded()) {
            startPromise.success(Started(port))
          } else if (event.failed()) {
            startPromise.failure(event.cause())
          }
        }
      })

    startPromise.future

  }

}

package org.zella.titler.server

import dispatch.{Http, as, url}
import org.junit.Test
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.mockito.Mockito._
import org.scalatest.time.{Millis, Seconds, Span}
import org.zella.titler.crawler.TitleCrawler

import scala.concurrent.Future

class ServerTest extends Matchers with ScalaFutures {

  implicit def executor = scala.concurrent.ExecutionContext.Implicits.global

  //scala test waiting futures conf
  implicit val defaultPatience =
    PatienceConfig(timeout = Span(10, Seconds), interval = Span(500, Millis))

  @Test
  def requestShouldGiveCorrectAnswer(): Unit = {

    val crawler = mock(classOf[TitleCrawler])
    when(crawler.crawl("someUrl1")).thenReturn(Future(("someUrl1", "title1")))
    when(crawler.crawl("someUrl2")).thenReturn(Future(("someUrl2", "title2")))
    when(crawler.crawl("wrongUrl")).thenReturn(Future.failed(new RuntimeException))

    whenReady(new Server(8080, crawler).start()) { result =>
      result shouldBe Server.Started(8080)
    }

    val req = url("http://localhost:8080/grab_titles")
      .addQueryParameter("url", "someUrl1")
      .addQueryParameter("url", "someUrl2")
      .addQueryParameter("url", "wrongUrl")

    whenReady(Http.default(req OK as.String)) { result =>
      result shouldBe "{\"result\":[{\"url\":\"someUrl1\",\"title\":\"title1\"}," +
        "{\"url\":\"someUrl2\",\"title\":\"title2\"}," +
        "{\"url\":\"wrongUrl\",\"failed\":true}]}"
    }

    verify(crawler, times(1)).crawl("someUrl1")
    verify(crawler, times(1)).crawl("someUrl2")
    verify(crawler, times(1)).crawl("wrongUrl")
    verifyNoMoreInteractions(crawler)
  }

}

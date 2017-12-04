package org.zella.titler.crawler.impl

import org.junit.Test
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

class DispatchTitleCrawlerTest extends Matchers with ScalaFutures {


  //scala test waiting futures conf
  implicit val defaultPatience =
    PatienceConfig(timeout = Span(10, Seconds), interval = Span(500, Millis))


  @Test
  def validUrlShouldReturnTitle(): Unit = {
    whenReady(AsyncTitleCrawler.crawl("http://www.google.ru")) { result =>
      result shouldBe("http://www.google.ru", "Google")
    }
  }

  @Test
  def validUrlWithRedirectShouldReturnTitle(): Unit = {
    whenReady(AsyncTitleCrawler.crawl("http://www.google.ru")) { result =>
      result shouldBe("http://www.google.ru", "Google")
    }
  }

  @Test
  def wrongHostShouldFail(): Unit = {
    val f = AsyncTitleCrawler.crawl("http://www.goo123gle.ru")
    whenReady(f.failed) { e =>
      e shouldBe an[Exception]
    }
  }

  @Test
  def wrongUrlShouldFail(): Unit = {
    val f = AsyncTitleCrawler.crawl("www.google.ru")
    whenReady(f.failed) { e =>
      e shouldBe an[Exception]
    }
  }

}

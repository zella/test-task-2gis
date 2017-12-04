package org.zella.titler.crawler

import org.zella.titler.crawler.impl.AsyncTitleCrawler

import scala.concurrent.Future

trait TitleCrawler {

  type Title = String
  type Url = String

  /**
    * Grab title from html page
    *
    * @param url
    * @return title of html page
    */
  def crawl(url: Url): Future[(Url, Title)]
}

object TitleCrawler {

  /**
    * @return default crawler
    */
  def default: TitleCrawler = AsyncTitleCrawler
}


package org.zella.titler.crawler.impl

import dispatch.Defaults._
import dispatch._
import org.jsoup.Jsoup
import org.zella.titler.crawler.TitleCrawler

import scala.concurrent.Future

object AsyncTitleCrawler extends TitleCrawler {

  private val http = Http.withConfiguration(
    _.setFollowRedirect(true)
  )

  override def crawl(link: Url): Future[(Url, Title)] = {
    //wrapped for failure handling
    Future {
      url(link)
      //можно, конечно, не загружать все тело, но не будем усложнять оптимизацией
    }.flatMap(u => http(u OK as.String))
      .map(res => (link, Jsoup.parse(res).title()))
  }
}

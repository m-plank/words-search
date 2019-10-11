package words.search.wiki.mongo

import cats.effect.Sync
import org.mongodb.scala.{Document, MongoClient}
import org.mongodb.scala.model.{Filters, TextSearchOptions}
import words.search.wiki.{WikiPage, WikiRepo}
import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by Bondarenko on Sep, 25, 2019
  * 4:24 PM.
  * Project: words-search
  */
object Mongo extends App {

  def wikiRepo[F[_]: Sync]: WikiRepo[F] = (words: String) => {
    val pendingResult =
      collection("articles")
        .find(
          Filters
            .text(s""""$words"""", new TextSearchOptions().caseSensitive(false))
        )
        .limit(5)
        .toFuture()
    Sync[F].delay {
      Await
        .result(pendingResult, 30 seconds)
        .map(docToPage)
        .toStream
    }
  }

  private def docToPage(doc: Document): WikiPage =
    WikiPage(doc("title").asString().getValue, doc("body").asString().getValue)

  private lazy val client = MongoClient("mongodb://localhost:27017")

  private def collection(collectionName: String) =
    client.getDatabase("wikipedia").getCollection(collectionName)

}

package words.search.wiki.mongo

import cats.effect.Sync
import cats.implicits._
import reactivemongo.api._
import reactivemongo.bson._
import words.search.wiki.{WikiPage, WikiRepo}
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Try

/**
 * Created by Bondarenko on Sep, 25, 2019
 * 4:24 PM.
 * Project: words-search
 */
object Mongo extends App {

  import MongoReactive._

  def wikiRepo[F[_]: Sync]: WikiRepo[F] = (words: String) => {
    queryPages("wikipedia")("usages") {
      buildQuery(words)
    }.map(_.toStream)
  }

}

object MongoReactive extends App {

  import cats.implicits._
  import reactivemongo.api.collections.bson._
  import reactivemongo.bson.{document, BSONDocument}
  import ExecutionContext.Implicits.global

  def queryPages[F[_]: Sync](dbName: String)(collectionName: String)(query: BSONDocument) =
    for {
      docs <- queryDocuments(dbName)(collectionName)(query)
      pages <- docs.foldRight(Sync[F].pure(List.empty[WikiPage])) { (a, acc) =>
        Sync[F].map2(toPage(a), acc)(_ :: _)
      }
    } yield pages

  def buildQuery(search: String) = document("$text" -> document("$search" -> s""""$search""""))

  private def driver = MongoDriver()
  private def connection = {
    println(s"CREATING NEW CONNECTION..")
    Try {
      driver.connection(
        List("127.0.0.1:27017"),
        MongoConnectionOptions.default.copy(connectTimeoutMS = 5000, failoverStrategy = FailoverStrategy(retries = 3))
      )
    }
//    val c = driver.connection("mongodb://127.0.0.1:27017")
//    println(s"CONNECTION CREATED: ${c}")
//    c
  }

  private def futureConnection = Future.fromTry { connection }
  private def db(dbName: String): Future[DefaultDB] = futureConnection.flatMap(_.database(dbName))
  private def collection(dbName: String)(collectionName: String) = db(dbName).map(_.collection(collectionName))

  private def fromFuture[F[_]: Sync, A](f: => Future[A]): F[A] =
    Sync[F].delay {
      timing("executing mongodb query")(Await.result(f, 20 seconds))
    }

  def timing[A](message: => String)(f: => A) = {
    val start = System.nanoTime()
    val res = f
    println(s"$message: ${(System.nanoTime() - start) / 1000000} ms")
    res
  }

  private def queryDocuments[F[_]: Sync](dbName: String)(collectionName: String)(query: BSONDocument) = fromFuture {

    for {
      coll <- collection(dbName)(collectionName)
      docs <- coll
        .find(query, None)
        .cursor[BSONDocument]()
        .collect[Vector](10, Cursor.FailOnError[Vector[BSONDocument]]())
    } yield docs

  }

  private def toPage[F[_]: Sync](document: BSONDocument) =
    Sync[F].fromOption(
      for {
        title <- document.get("title").collect { case BSONString(s) => s }
        body <- document.getAs[BSONArray]("body").map(_.elements)
      } yield WikiPage(
        title,
        body.map(_.value).collect { case BSONString(s) => s }.mkString("\n")
      ),
      new Exception("Document doesn't contain required fields")
    )

}

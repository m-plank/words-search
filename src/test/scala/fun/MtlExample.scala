package fun

import cats.data.Chain
import cats.implicits._

/**
 * Created by Bondarenko on Nov, 05, 2019
 14:16.
 Project: words-search
 */
object MtlExample extends App {
  import cats._
  import cats.mtl._
  import fun.MtlImplicits.{stdInputAsk, stdOutTell}

  type ASK[F[_]] = ApplicativeAsk[F, String]
  type ME[F[_]] = MonadError[F, String]
  type TELL[F[_]] = FunctorTell[F, Int]

  def parseNumber[F[_]: ME](in: String): F[Int] =
    if (in.matches("-?[0-9]+")) in.toInt.pure[F] else implicitly[ME[F]].raiseError(s"[$in] is invalid number")

  def divide[F[_]: ME](a: Int, b: Int): F[Int] =
    if (b != 0) (a / b).pure[F] else implicitly[ME[F]].raiseError("cannot divide by zero")

  def divide[F[_]: ME](a: String, b: String): F[Int] =
    for {
      x <- parseNumber[F](a)
      y <- parseNumber[F](b)
      result <- divide[F](x, y)
    } yield result

  def parseLine[F[_]: ME](s: String): F[(String, String)] = {
    val p = "([\\S]+)[ \t]+([\\S]+)".r
    s match {
      case p(a, b) => (a -> b).pure[F]
      case _       => implicitly[ME[F]].raiseError(s"Unable to read numbers from input [$s]")
    }
  }

  def divideNumbers[F[_]: ASK: ME](implicit Tell: FunctorTell[F, Chain[String]]) =
    for {
      _ <- Tell.tell(Chain.one("Input two integer numbers (a, b):"))
      l <- implicitly[ASK[F]].ask
      (a, b) <- parseLine[F](l)
      result <- divide[F](a, b)
      _ <- Tell.tell(Chain.one(s"Result: ${result}"))
    } yield result

  divideNumbers[Either[String, ?]]

}

package fun

import cats.data.Chain
import cats.implicits._
import cats.mtl.{ApplicativeAsk, DefaultFunctorTell}
import cats.{Applicative, Functor}
import java.util.Scanner

/**
 * Created by Bondarenko on Nov, 05, 2019
 * 14:15.
 * Project: words-search
 */
object MtlImplicits {
  type ERR[A] = Either[String, A]

  implicit val stdInputAsk = new EitherAsk {
    protected def read: String = {
      val scanner = new Scanner(System.in)
      scanner.nextLine()
    }
  }

  implicit val stdOutTell = new Tell {
    protected def write(l: String): Unit = println(l)
  }

  trait EitherAsk extends ApplicativeAsk[ERR, String] {

    protected def read: String

    val applicative: Applicative[ERR] = new Applicative[ERR] {
      def pure[A](x: A): ERR[A] = Right(x)

      def ap[A, B](ff: ERR[A => B])(fa: ERR[A]): ERR[B] = ff match {
        case Left(value) => Left(value)
        case Right(map)  => fa.map(map)
      }
    }

    def ask: ERR[String] =
      read match {
        case s if !s.isEmpty => Right(s)
        case _               => Left("Empty input is not allowed")
      }

    def reader[A](f: String => A): ERR[A] = ???
  }

  trait Tell extends DefaultFunctorTell[ERR, Chain[String]] {
    val functor: Functor[ERR] = Functor[ERR]

    def tell(l: Chain[String]): ERR[Unit] =
      Either
        .catchNonFatal {
          l.map(write(_))

        }
        .bimap(ex => ex.getMessage, _ => ())

    protected def write(l: String): Unit
  }

}

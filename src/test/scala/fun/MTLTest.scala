package fun

import cats.implicits._
import cats.scalatest.{EitherMatchers, ValidatedMatchers}
import org.scalatest.{FlatSpec, Matchers}

/**
 * Created by Bondarenko on Nov, 02, 2019
 * 20:51.
 * Project: words-search
 */
class MTLTest extends FlatSpec with Matchers with EitherMatchers with ValidatedMatchers {
  import MtlExample._

  "MtlFun" should "parse str to int" in {

    divide[Either[String, ?]]("12", "2") should beRight(6)
    divide[Either[String, ?]]("12", "a") should beLeft("[a] is invalid number")
    divide[Either[String, ?]]("12", "0") should beLeft("cannot divide by zero")

  }

  it should "read numbers from input and divide" in {
    import fun.MtlImplicits.{EitherAsk, Tell}

    implicit val tell = new Tell {
      protected def write(l: String): Unit = ()
    }

    def pureAsk(l: String) = new EitherAsk {
      protected def read: String = l
    }

    divideNumbers[Either[String, ?]](pureAsk(""), catsStdInstancesForEither, tell) should beLeft(
      "Empty input is not allowed"
    )
    divideNumbers[Either[String, ?]](pureAsk("a bbb"), catsStdInstancesForEither, tell) should beLeft(
      "[a] is invalid number"
    )

    divideNumbers[Either[String, ?]](pureAsk("12 2"), catsStdInstancesForEither, tell) should beRight(6)
  }

}

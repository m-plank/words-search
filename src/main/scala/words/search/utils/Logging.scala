package words.search.utils

import org.log4s._

/**
 * Created by Bondarenko on Oct, 16, 2019
 17:55.
 Project: words-search
 */
trait Logging {
  private val logger = getLogger

  def error(err: Throwable) = logger.error(err)(err.getMessage)

  def debug(s: => String) = logger.debug(s)

  def info(s: => String) = logger.info(s)
}

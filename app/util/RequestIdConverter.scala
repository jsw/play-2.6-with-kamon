package util

import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import kamon.Kamon
import kamon.context.Key

class RequestIDConverter extends ClassicConverter {
  private val key = Key.broadcast("request-id", "undefined")
  override def convert(event: ILoggingEvent): String = Kamon.currentContext().get(key)
}

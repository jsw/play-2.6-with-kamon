package kamon.logback

import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import kamon.Kamon
import kamon.context.Key

class RequestIDConverter extends ClassicConverter {
  val RequestIDKey = Key.broadcastString("request-id")

  override def convert(event: ILoggingEvent): String = {
    Kamon.currentContext().get(RequestIDKey).getOrElse("undefined")
  }
}

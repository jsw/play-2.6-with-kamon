package filters

import akka.stream.Materializer
import util.{ContextNames, RequestAttrKey, RequestIdUtil}
import javax.inject.{Inject, Singleton}
import com.typesafe.scalalogging.StrictLogging
import org.slf4j.MDC
import play.api.inject.Module
import play.api.mvc.{EssentialAction, EssentialFilter}
import play.api.{Configuration, Environment}

import scala.concurrent.ExecutionContext

object HttpContextFilter {
  def apply()(implicit ec: ExecutionContext) = new HttpContextFilter()
}


/**
  * Play Filter that generates a requestId via base64-encocded UUID, appending to an optional incoming HTTP header requestId.
  * The requestId is stored in the MDC.
  */
@Singleton
class HttpContextFilter @Inject()()(implicit ec: ExecutionContext) extends EssentialFilter with ContextNames with StrictLogging {

  val NRequestId = "Request-Id"

  override def apply(next: EssentialAction) = EssentialAction { requestHeader =>
    val incomingRequestId = requestHeader.headers.get(NRequestId).map(_.trim).filter(_.nonEmpty)
    val requestId = RequestIdUtil.newRequestId(incomingRequestId)
    MDC.put(RequestId, requestId)
    logger.info("MDC set")
    next(requestHeader.addAttr(RequestAttrKey.RequestId, requestId)).map { result =>
      MDC.clear()
      result
    }
  }
}

class HttpContextModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = Seq(
    bind[HttpContextFilter].toSelf
  )
}

trait HttpContextComponents {
  implicit def materializer: Materializer
  lazy val httpContextFilter: HttpContextFilter = HttpContextFilter()(materializer.executionContext)
}

package filters


import javax.inject.{Inject, Provider, Singleton}
import akka.stream.Materializer
import com.typesafe.scalalogging.StrictLogging
import org.slf4j.MDC
import play.api.http.HeaderNames
import play.api.inject.Module
import play.api.mvc._
import play.api.{Configuration, Environment}
import util.ContextNames

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext

// https://www.playframework.com/documentation/2.5.x/ScalaHttpFilters
// https://groups.google.com/forum/#!topic/play-framework/3hvCr4E_6Q0
// https://github.com/playframework/playframework/tree/master/framework/src/play-filters-helpers/src/main/scala/play/filters

object AccessLogFilter {
  def apply(config: AccessLogConfig = AccessLogConfig())(implicit ec: ExecutionContext): AccessLogFilter = new AccessLogFilter(config)
  def apply(config: Configuration)(implicit ec: ExecutionContext): AccessLogFilter = new AccessLogFilter(AccessLogConfig.fromConfiguration(config))
}

case class AccessLogConfig(excludedHeaders: Set[String] = Set.empty, excludedPaths: Set[String] = Set.empty)

object AccessLogConfig {
  def fromConfiguration(conf: Configuration): AccessLogConfig = {
    val config = conf.underlying.getConfig("filters.accesslog")
    AccessLogConfig(config.getStringList("excludedHeaders").asScala.toSet, config.getStringList("excludedPaths").asScala.toSet)
  }
}

/**
  * access log filter with configurable headers and paths to exclude
  * @param config
  * @param ec
  */
@Singleton
class AccessLogFilter @Inject() (config: AccessLogConfig)(implicit ec: ExecutionContext)
  extends EssentialFilter with HeaderNames with ContextNames with StrictLogging {

  private val excludedHeadersUpperCase = config.excludedHeaders.map(_.toUpperCase)

  override def apply(next: EssentialAction) = EssentialAction { requestHeader =>
    val startTime = System.currentTimeMillis
    next(requestHeader).map { result =>
      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime
      val headerLog = requestHeader.headers.headers
        .filter(kv => !excludedHeadersUpperCase.contains(kv._1.toUpperCase))
        .map(kv => s"${kv._1}=${kv._2}")
        .mkString(", ")
      val message = List(
        requestHeader.remoteAddress,
        requestHeader.method,
        requestHeader.uri,
        s"requestTime=${requestTime}",
        s"status=${result.header.status}").mkString(" ")
      if (!config.excludedPaths.contains(requestHeader.path)) logger.info(message)
      if (MDC.get(RequestId) == null) logger.error("No MDC")
      result.withHeaders("X-Request-Time" -> requestTime.toString)
    }
  }
}

@Singleton
class AccessLogConfigProvider @Inject() (configuration: Configuration) extends Provider[AccessLogConfig] {
  lazy val get = AccessLogConfig.fromConfiguration(configuration)
}

class AccessLogModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = Seq(
    bind[AccessLogConfig].toProvider[AccessLogConfigProvider],
    bind[AccessLogFilter].toSelf
  )
}

trait AccessLogComponents {
  def configuration: Configuration
  implicit def materializer: Materializer
  lazy val accessLogConfig: AccessLogConfig = AccessLogConfig.fromConfiguration(configuration)
  lazy val accessLogFilter: AccessLogFilter = AccessLogFilter(accessLogConfig)(materializer.executionContext)
}

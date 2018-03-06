package init

import controllers.HomeController
import filters.{AccessLogComponents, HttpContextComponents}
import play.api.{ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator}
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.AhcWSComponents
import play.filters.headers.SecurityHeadersComponents
import router.Routes
import util.Client

class MyApplicationLoader extends ApplicationLoader {
  override def load(context: Context) = {
    LoggerConfigurator(context.environment.classLoader).foreach { _.configure(context.environment)}
    new MyComponents(context).application
  }
}

class MyComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with AhcWSComponents
  with SecurityHeadersComponents
  with AccessLogComponents
  with HttpContextComponents {

  val client = new Client(wsClient)

  lazy val homeController = new HomeController(controllerComponents, client)

  override lazy val router: play.api.routing.Router = new Routes(httpErrorHandler, homeController)
  override lazy val httpFilters = Seq(httpContextFilter, accessLogFilter, securityHeadersFilter)
}

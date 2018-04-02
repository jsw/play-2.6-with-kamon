package init

import controllers.HomeController
import filters.AccessLogComponents
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.routing.Router
import play.api.{ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator}
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
  with AccessLogComponents {

  val client = new Client(wsClient)

  lazy val homeController = new HomeController(controllerComponents, client)

  override lazy val router: Router = new Routes(httpErrorHandler, homeController)
  override lazy val httpFilters = Seq(accessLogFilter)
}

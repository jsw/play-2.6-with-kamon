package controllers

import com.typesafe.scalalogging.StrictLogging
import play.api.mvc._
import util.Client

import scala.concurrent.ExecutionContext

class HomeController(cc: ControllerComponents, client: Client)(implicit ec: ExecutionContext) extends AbstractController(cc) with StrictLogging {

  def index() = Action.async {
    logger.info("HomeController.index")
    val str1 = client.get()
    val str2 = client.get()
    for {
      _ <- str1
      _ <- client.sleep()
      _ <- client.timer()
      r <- str2
    } yield Ok(s"$r")
  }
}

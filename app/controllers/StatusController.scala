package controllers


import connectors.K8055Connector
import model._
import play.api.libs.json.Json
import play.api.routing.JavaScriptReverseRouter
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object StatusController extends Controller with StatusController{
  lazy val k8055Connector = K8055Connector
}

trait StatusController  {
  this: Controller =>
  val k8055Connector:K8055Connector

  def present = Action.async {
    implicit request => {
      k8055Connector.k8055State.map(dc =>
        Ok(views.html.index(dc))
      )
    }
  }

  def sequencerStatus() = Action.async { implicit request =>
    k8055Connector.k8055State.map(dc => {
      val partitionedStatuses = dc.devices.partition(device => device.deviceType == Device.MONITOR)

      //running, currentStep, componentStatuses, monitorStatuses
      val ss = AppStatus(running = false, 1, partitionedStatuses._2, partitionedStatuses._1)
      Ok(Json.toJson(ss))
    })
  }

  def setDeviceState(deviceId: String, state:String) = Action.async { implicit request =>
    println(s"########## deviceId: $deviceId; setDeviceState:$state")
    Future.successful(Ok("Ok"))
  }

  def javascriptReverseRoutes = Action { implicit request =>
    Ok(JavaScriptReverseRouter("jsRoutes")(
      controllers.routes.javascript.StatusController.setDeviceState,
      controllers.routes.javascript.StatusController.sequencerStatus
    )).as("text/javascript")
  }
}

package controllers


import connectors.K8055Connector
import model._
import play.api.libs.json.Json
import play.api.routing.JavaScriptReverseRouter
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

class StatusController extends Controller {

  def present = Action.async {
    implicit request => {
      K8055Connector.k8055State.map(dc =>
        Ok(views.html.index(dc))
      )
    }
  }


  def sequencerStatus() = Action.async { implicit request =>

    //println("######### getSequencerStatus")
    //running, currentStep, componentStatuses, monitorStatuses
    K8055Connector.k8055State.map(dc => {
      val deviceStatuses = dc.devices.filter(device => device.deviceType != Device.MONITOR)
      val monitorStatuses = dc.devices.filter(device => device.deviceType == Device.MONITOR)
      val ss = AppStatus(running = false, 1, deviceStatuses, monitorStatuses)
      Ok(Json.toJson(ss).toString())
    })
  }

  def setDeviceState(deviceId: String, state:String) = Action { implicit request =>
    println(s"########## deviceId: $deviceId; setDeviceState:$state")
    Ok("Ok")
  }

  def javascriptReverseRoutes = Action { implicit request =>
    Ok(JavaScriptReverseRouter("jsRoutes")(
      controllers.routes.javascript.StatusController.setDeviceState,
      controllers.routes.javascript.StatusController.sequencerStatus
    )).as("text/javascript")
  }


}

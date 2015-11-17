package controllers


import connectors.K8055Connector
import model.Device._
import model._
import play.api.libs.json.Json
import play.api.routing.{JavaScriptReverseRouter, JavaScriptReverseRoute}
import play.api.mvc._

import scala.concurrent.Future

class StatusController extends Controller {

  def present = Action.async {
    implicit request => {
      Future.successful(Ok(views.html.index(K8055Connector.k8055State)))
    }
  }


  def sequencerStatus() = Action { implicit request =>

    println("######### getSequencerStatus")
    //running, currentStep, componentStatuses, monitorStatuses
    val deviceStatuses = K8055Connector.k8055State.devices.filter(device => device.deviceType != Device.MONITOR)
    val monitorStatuses = K8055Connector.k8055State.devices.filter(device => device.deviceType == Device.MONITOR)
    val ss = AppStatus(false, 1, deviceStatuses, monitorStatuses)
    Ok(Json.toJson(ss).toString())
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

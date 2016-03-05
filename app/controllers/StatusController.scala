package controllers


import connectors.{SequencerConnector, K8055Connector}
import model._
import play.api.libs.json.Json
import play.api.routing.JavaScriptReverseRouter
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger
import play.mvc.Http.MimeTypes

object StatusController extends Controller with StatusController{

  lazy val k8055Connector = K8055Connector
  lazy val sequencerConnector = SequencerConnector
}

trait StatusController  {
  this: Controller =>
  val k8055Connector:K8055Connector
  val sequencerConnector:SequencerConnector

  def present = Action.async {
    implicit request => {
      sequencerConnector.sequenceState.flatMap{rs =>
        k8055Connector.k8055State.map { dc =>
          Ok(views.html.index(rs, dc))
        }
      }
    }
  }

  def sequencerStatus() = Action.async { implicit request =>
    k8055Connector.k8055State.map(dc => {
      val componentsAndMonitors = dc.devices.partition(device => device.deviceType != Device.MONITOR)
      val firstStep = 1

    //  println(s"############# sequenceStatus: $componentsAndMonitors")
      //running, currentStep, componentStatuses, monitorStatuses
      val ss = AppStatus(running = false, firstStep, componentsAndMonitors._1, componentsAndMonitors._2)
      Ok(Json.toJson(ss)).as(MimeTypes.TEXT)
    })
  }

  def setDeviceState(deviceId: String, state:String) = Action.async { implicit request =>
    Logger.info(s"StatusController.setDeviceState: deviceId: $deviceId; setDeviceState:$state")
    K8055Connector.setK8055State(deviceId, state).map(result =>
      if(result) Ok("Ok")
      else{
        Logger.warn("StatusController.setDeviceState setK8055State failed")
        BadRequest("Not Ok")
      }
    )
  }

  def javascriptReverseRoutes = Action { implicit request =>
    Ok(JavaScriptReverseRouter("jsRoutes")(
      controllers.routes.javascript.StatusController.setDeviceState,
      controllers.routes.javascript.StatusController.sequencerStatus
    )).as(MimeTypes.JAVASCRIPT) //"text/javascript"
  }
}

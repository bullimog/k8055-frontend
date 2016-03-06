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
      sequencerConnector.getSequence.flatMap{rs =>
        k8055Connector.k8055State.map { dc =>
          Ok(views.html.index(rs, dc))
        }
      }
    }
  }

  def sequencerStatus() = Action.async { implicit request =>
    k8055Connector.k8055State.flatMap(dc => {
      val componentsAndMonitors = dc.devices.partition(device => device.deviceType != Device.MONITOR)

      sequencerConnector.getSequenceState.map { sequenceState =>
        //println(s"############# sequenceStatus: $componentsAndMonitors")
        //running, currentStep, componentStatuses, monitorStatuses
        val appStatus = AppStatus(sequenceState, componentsAndMonitors._1, componentsAndMonitors._2)
        Ok(Json.toJson(appStatus)).as(MimeTypes.TEXT)
      }
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

  def startSequencer() = Action { implicit request =>
    println("Started")
    sequencerConnector.startSequencer
    Ok("Started")
  }

  def stopSequencer() = Action { implicit request =>
    println("Stopped")
    sequencerConnector.stopSequencer
    Ok("Stopped")
  }

  def resetSequencer() = Action { implicit request =>
    println("Reset")
    sequencerConnector.resetSequencer
    Ok("Reset")
  }

  def nextStep() = Action { implicit request =>
    println("Next")
    sequencerConnector.nextSequencerStep
    Ok("Next")
  }

  def previousStep() = Action { implicit request =>
    println("Previous")
    sequencerConnector.previousSequencerStep
    Ok("Previous")
  }



  def javascriptReverseRoutes = Action { implicit request =>
    Ok(JavaScriptReverseRouter("jsRoutes")(
      controllers.routes.javascript.StatusController.setDeviceState,
      controllers.routes.javascript.StatusController.sequencerStatus,
      controllers.routes.javascript.StatusController.startSequencer,
      controllers.routes.javascript.StatusController.stopSequencer,
      controllers.routes.javascript.StatusController.resetSequencer,
      controllers.routes.javascript.StatusController.nextStep,
      controllers.routes.javascript.StatusController.previousStep
    )).as(MimeTypes.JAVASCRIPT) //"text/javascript"
  }
}

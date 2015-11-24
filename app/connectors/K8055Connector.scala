package connectors

import model.Device._
import model.{DeviceCollection, RawDeviceCollection}
import play.api.libs.json.{JsError, JsSuccess, Json, JsValue}
import play.api.libs.ws.{WSRequest, WS, WSResponse}
import Configuration._
import sun.font.TrueTypeFont

import scala.concurrent.Future

//had to add these two in, to get WS stuff to work
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current


object K8055Connector extends K8055Connector

trait K8055Connector {

  def doGet(url: String): Option[Future[WSResponse]] = {
    val holder: WSRequest = WS.url(url)
    try {Some(holder.get())}
    catch {
      case _: java.lang.NullPointerException => None
    }
  }


  def k8055State:Future[DeviceCollection]  = {

    def buildEmptyDeviceCollection(errorText:String):DeviceCollection = {
      val rdc:RawDeviceCollection = RawDeviceCollection(errorText, "Error", List())
      DeviceCollection.rawToDeviceCollection(rdc)
    }

    doGet(k8055Host + k8055Devices).fold(Future(buildEmptyDeviceCollection("No response from server"))) {
      theFuture => theFuture.map { wsresponse =>           // get the WSResponse out of the Future using map
        wsresponse.status match {                          // match on the response status code (int)
          case 200 => DeviceCollection.rawToDeviceCollection(parseDeviceCollection(wsresponse.body).get)
          case 404 => buildEmptyDeviceCollection("Devices not Found")
          case _ => buildEmptyDeviceCollection("Server Error??")
        }
      }
    }
  }

  def parseDeviceCollection(jsonSource:String):Option[RawDeviceCollection] = {
    //println("#############jsonSource: " + jsonSource)
    val json: JsValue = Json.parse(jsonSource)
    json.validate[RawDeviceCollection] match {
      case s: JsSuccess[RawDeviceCollection] => Some(s.get)
      case e: JsError => None
    }
  }


  def setK8055State(deviceId: String, state: String):Future[Boolean]  = {
    Future(true)
  }

}

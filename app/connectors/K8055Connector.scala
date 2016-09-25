package connectors

import model.Device._
import model.{DeviceState, DeviceCollection, RawDeviceCollection}
import play.api.http.HttpVerbs
import play.api.libs.json.{JsError, JsSuccess, Json, JsValue}
import play.api.libs.ws.{WSRequest, WS, WSResponse}
import Configuration._
import play.api.http.Status._
import play.mvc.Http.HeaderNames._
import play.mvc.Http.MimeTypes._
import play.api.Logger
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


  def doPut(url: String, body:DeviceState): Option[Future[WSResponse]] = {
    val request: WSRequest = WS.url(url).withMethod(HttpVerbs.PUT).withHeaders(CONTENT_TYPE -> JSON)
    try {Some(request.put(Json.toJson(body)))}
    catch {
      case e: java.lang.NullPointerException => {
        Logger.error(s"Null pointer in K8055Connector.doPut: url:$url; body:$body; request: $request; exception: $e")
        None
      }
    }
  }


  def k8055State:Future[DeviceCollection]  = {

    def buildEmptyDeviceCollection(errorText:String):DeviceCollection = {
      DeviceCollection.rawToDeviceCollection(buildEmptyRawDeviceCollection(errorText))
    }

    doGet(k8055Host + k8055Devices).fold(Future(buildEmptyDeviceCollection("No response from server"))) {
      theFuture => theFuture.map { wsresponse =>           // get the WSResponse out of the Future using map
        wsresponse.status match {                          // match on the response status code (int)
          case OK => DeviceCollection.rawToDeviceCollection(parseDeviceCollection(wsresponse.body).
            getOrElse(buildEmptyRawDeviceCollection("Couldn't parse device collection")))
          case NOT_FOUND => buildEmptyDeviceCollection("Devices not Found")
          case _ => buildEmptyDeviceCollection("Server Error??")
        }
      }
    }
  }


  def buildEmptyRawDeviceCollection(errorText:String):RawDeviceCollection = {
    RawDeviceCollection(errorText, errorText, List())
  }


  def parseDeviceCollection(jsonSource:String):Option[RawDeviceCollection] = {
    val json: JsValue = Json.parse(jsonSource)
    json.validate[RawDeviceCollection] match {
      case s: JsSuccess[RawDeviceCollection] => Some(s.getOrElse{
        Logger.error(s"K8055Connector.parseDeviceCollection: parsed Json, but it was empty: $jsonSource")
        buildEmptyRawDeviceCollection("Empty Device Collection")
      })
      case e: JsError => None
    }
  }


  def setK8055State(deviceId: String, state: String):Future[Boolean]  = {
    val deviceState:DeviceState = state match {
      case Configuration.up          => DeviceState(deviceId, None, Some(Configuration.increment))
      case Configuration.doubleUp    => DeviceState(deviceId, None, Some(Configuration.big_increment))
      case Configuration.down        => DeviceState(deviceId, None, Some(Configuration.decrement))
      case Configuration.doubleDown  => DeviceState(deviceId, None, Some(Configuration.big_decrement))
      case Configuration.up2         => DeviceState(deviceId, None, None, Some(Configuration.increment))
      case Configuration.doubleUp2   => DeviceState(deviceId, None, None, Some(Configuration.big_increment))
      case Configuration.down2       => DeviceState(deviceId, None, None, Some(Configuration.decrement))
      case Configuration.doubleDown2 => DeviceState(deviceId, None, None, Some(Configuration.big_decrement))
      case Configuration.isTrue      => DeviceState(deviceId, Some(true), None)
      case Configuration.isFalse     => DeviceState(deviceId, Some(false), None)
      case _                         => DeviceState("", None, None)
    }

    doPut(k8055Host + k8055DeviceStateDelta, deviceState).fold(Future(false)) {
      theFuture => theFuture.flatMap { wsresponse =>    // get the WSResponse out of the Future using map
        wsresponse.status match {                       // match on the response status code (int)
          case OK => Future(true)
          case _ => {
            Logger.error(s"K8055Connector: Status response from K8055 was $wsresponse")
            Future(false)}
        }
      }
    }
  }

}

package connectors

import model.Device._
import model.{DeviceCollection, RawDeviceCollection}
import play.api.libs.json.{JsError, JsSuccess, Json, JsValue}
import play.api.libs.ws.{WSRequest, WS, WSResponse}
import Configuration._

import scala.concurrent.Future

//had to add these two in, to get WS stuff to work
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current


object K8055Connector extends K8055Connector

trait K8055Connector {

  def doGet(url: String): Option[Future[WSResponse]] = {
    val holder: WSRequest = WS.url(url)
    try {
      Some(holder.get())
    }
    catch {
      case _: java.lang.NullPointerException => None
    }
  }


  def k8055State:Future[DeviceCollection]  = {
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

  def buildEmptyDeviceCollection(errorText:String):DeviceCollection = {
    val rdc:RawDeviceCollection = RawDeviceCollection(errorText, "Error", List())
    DeviceCollection.rawToDeviceCollection(rdc)
  }


//  def k8055State:DeviceCollection = {
//    val rPump = RawDevice("DO-1", "Pump", DIGITAL_OUT, 1, digitalState = Some(true))
//    val rHeater = RawDevice("AO-1", "Heater", ANALOGUE_OUT, 1, Some("%"), Some(0), analogueState = Some(22))
//    val rSwitch = RawDevice("DI-1", "Switch", DIGITAL_IN, 1, digitalState = Some(true))
//    val rThermometer = RawDevice("AI-1", "Thermometer", ANALOGUE_IN, 1, Some("°c"), Some(0), analogueState = Some(17))
//    val rThermostat = RawDevice("MO-1", "Thermostat", MONITOR, 1, Some("c"), None, None, None, Some("AI-1"),
//      Some("AO-1"), None, Some(true), Some(56) )
//    val rawDevices: List[RawDevice] = List(rPump, rHeater, rSwitch, rThermometer, rThermostat)
//    val rdc:RawDeviceCollection = RawDeviceCollection("My Device Collection", "For Brewing", rawDevices)
//
//    DeviceCollection.rawToDeviceCollection(rdc)
//  }

}

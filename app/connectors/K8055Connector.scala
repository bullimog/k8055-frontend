package connectors

import java.io.FileNotFoundException

import model.Device._
import model.{DeviceCollection, RawDeviceCollection, RawDevice}
import play.api.libs.json.{JsError, JsSuccess, Json, JsValue}
import play.api.libs.ws.{WSRequest, WS, WSRequestHolder, WSResponse}
import play.api.mvc.Result

import scala.concurrent.Future
import scala.io.Source

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

    doGet("http://localhost:9001/devices") match {
      case Some(theFuture) => theFuture.map { wsresponse =>           // get the WSResponse out of the Future using map
        wsresponse.status match {                                     // match on the response status code (int)
          case 200 => DeviceCollection.rawToDeviceCollection(parseDeviceCollection(wsresponse.body).get)
          case 404 => buildDeviceCollection("Server is found")
          case _ => buildDeviceCollection("Server is found")
        }
      }
      case None => Future(buildDeviceCollection("Server is found"))
    }
  }

  def parseDeviceCollection(jsonSource:String):Option[RawDeviceCollection] = {
    val json: JsValue = Json.parse(jsonSource)
    json.validate[RawDeviceCollection] match {
      case s: JsSuccess[RawDeviceCollection] => Some(s.get)
      case e: JsError => None
    }
  }



//  def get8055State:DeviceCollection = {
//    doGet("http://www.google.com").fold(buildDeviceCollection("No response from server")) {
//      theFuture => theFuture.map { wsresponse =>           // get the WSResponse out of the Future using map
//        wsresponse.status match {                          // match on the response status code (int)
//          case 200 => buildDeviceCollection("Server is found") //wsresponse.body
//          case 404 => buildDeviceCollection("Server not found")
//          case _ => buildDeviceCollection("Huh?")
//        }
//      }
//    }
//  }

  def buildDeviceCollection(errorText:String):DeviceCollection = {
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

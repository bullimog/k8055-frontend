package connectors

import model.Device._
import model.{RawDeviceCollection, RawDevice}
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import play.api.http.Status._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object WSMock extends org.specs2.mock.Mockito  {

  def getMockWSResponse(status:Int):Option[Future[WSResponse]] = {
    val response = mock[WSResponse]

    import play.api.http.HeaderNames
    response.header(HeaderNames.CONTENT_TYPE) returns Some("application/json")
    response.body returns Json.toJson(makeRawDeviceCollection).toString()
    response.status returns status
    response.body returns Json.toJson(makeRawDeviceCollection).toString()


    Option(Future(response))
  }

  def makeRawDeviceCollection:RawDeviceCollection = {
    val rPump = RawDevice("DO-1", "Pump", DIGITAL_OUT, 1, digitalState = Some(true))
    val rHeater = RawDevice("AO-1", "Heater", ANALOGUE_OUT, 1, Some("%"), Some(0), analogueState = Some(22))
    val rSwitch = RawDevice("DI-1", "Switch", DIGITAL_IN, 1, digitalState = Some(true))
    val rThermometer = RawDevice("AI-1", "Thermometer", ANALOGUE_IN, 1, Some("°c"), Some(0), analogueState = Some(17))
    val rThermostat = RawDevice("MO-1", "Thermostat", MONITOR, 1, Some("c"), None, None, None, Some("AI-1"),
      Some("AO-1"), None, Some(true), Some(56))
    val rawDevices: List[RawDevice] = List(rPump, rHeater, rSwitch, rThermometer, rThermostat)
    RawDeviceCollection("My Fake Device Collection", "For Brewing", rawDevices)
  }
}

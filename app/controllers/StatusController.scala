package controllers

import model.Device._
import model.{RawDeviceCollection, RawDevice, Device, DeviceCollection}
import play.api.mvc._

import scala.concurrent.Future

class StatusController extends Controller {

  def present = Action.async {
    implicit request => {
      val rPump = RawDevice("DO-1", "Pump", DIGITAL_OUT, 1, digitalState = Some(false))
      val rHeater = RawDevice("AO-1", "Heater", ANALOGUE_OUT, 1, Some("%"), Some(0), analogueState = Some(0))
      val rSwitch = RawDevice("DI-1", "Switch", DIGITAL_IN, 1, digitalState = Some(false))
      val rThermometer = RawDevice("AI-1", "Thermometer", ANALOGUE_IN, 1, Some("%"), Some(0), analogueState = Some(0))
      val rThermostat = RawDevice("MO-1", "Thermostat", MONITOR, 1, Some("c"), None, None, None, Some("AI-1"), Some("AO-1"), None, Some(false), Some(0) )
      val rawDevices: List[RawDevice] = List(rPump, rHeater, rSwitch, rThermometer, rThermostat)
      val rdc:RawDeviceCollection = RawDeviceCollection("My Device Collection", "For Brewing", rawDevices)


      val dc = DeviceCollection.rawToDeviceCollection(rdc)
      Future.successful(Ok(views.html.index(dc)))
    }
  }

}

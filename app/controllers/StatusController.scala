package controllers

import model.Device._
import model.{Device, DeviceCollection}
import play.api.mvc._

import scala.concurrent.Future

class StatusController extends Controller {

  def present = Action.async {
    implicit request => {
      val pump = Device("DO-1", "Pump", DIGITAL_OUT, 1, digitalState = Some(false))
      val heater = Device("AO-1", "Heater", ANALOGUE_OUT, 1, Some("%"), Some(0), analogueState = Some(0))
      val switch = Device("DI-1", "Switch", DIGITAL_IN, 1, digitalState = Some(false))
      val thermometer = Device("AI-1", "Thermometer", ANALOGUE_IN, 1, Some("%"), Some(0), analogueState = Some(0))
      val thermostat = Device("MO-1", "Thermostat", MONITOR, 1, Some("c"), None, None, None, Some(thermometer), Some(heater), None, Some(false), Some(0) )
      val devices: List[Device] = List(pump, heater, switch, thermometer, thermostat)

      val dc:DeviceCollection = DeviceCollection("My Device Collection", "For Brewing", devices)
      Future.successful(Ok(views.html.index(dc)))
    }
  }

}

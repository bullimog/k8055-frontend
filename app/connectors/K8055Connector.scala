package connectors

import model.Device._
import model.{DeviceCollection, RawDeviceCollection, RawDevice}

object K8055Connector extends K8055Connector

trait K8055Connector {
  def k8055State:DeviceCollection = {
    val rPump = RawDevice("DO-1", "Pump", DIGITAL_OUT, 1, digitalState = Some(true))
    val rHeater = RawDevice("AO-1", "Heater", ANALOGUE_OUT, 1, Some("%"), Some(0), analogueState = Some(22))
    val rSwitch = RawDevice("DI-1", "Switch", DIGITAL_IN, 1, digitalState = Some(true))
    val rThermometer = RawDevice("AI-1", "Thermometer", ANALOGUE_IN, 1, Some("%"), Some(0), analogueState = Some(17))
    val rThermostat = RawDevice("MO-1", "Thermostat", MONITOR, 1, Some("c"), None, None, None, Some("AI-1"),
      Some("AO-1"), None, Some(true), Some(56) )
    val rawDevices: List[RawDevice] = List(rPump, rHeater, rSwitch, rThermometer, rThermostat)
    val rdc:RawDeviceCollection = RawDeviceCollection("My Device Collection", "For Brewing", rawDevices)

    DeviceCollection.rawToDeviceCollection(rdc)
  }

}

package model


import play.api.mvc.Results
import play.api.test.PlaySpecification


object DeviceSpec extends PlaySpecification with Results {

  "The Device model" should {
    "construct a Device, from a RawDevice" in {

      val rawThermometer = RawDevice("id-1", "thermometer", Device.ANALOGUE_IN, 1, Some("c"), None, None, None, None, None, None, None, Some(55))
      val rawHeater = RawDevice("id-2", "heater", Device.ANALOGUE_OUT, 1, None, None, None, None, None, None, None, None, Some(45))
      val rawThermostat = RawDevice("id-3", "thermostat", Device.MONITOR, 0, Some("c"),None, None, None, Some("id-1"), Some("id-2"), None, Some(true), Some(22))
      val rawDeviceCollection = RawDeviceCollection("test", "test", List(rawThermometer, rawHeater, rawThermostat))
      val thermostat:Device = Device.rawToDevice(rawThermostat, rawDeviceCollection)

      val themoSensor = thermostat.monitorSensor.get

      thermostat.monitorSensor.map(thermometer => thermometer.id must equalTo("id-1"))
      thermostat.monitorIncreaser.map(heater => heater.id must equalTo("id-2"))
      thermostat.id must equalTo("id-3")
    }
  }
}


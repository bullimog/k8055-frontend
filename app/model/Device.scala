package model

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads}

import scala.annotation.tailrec


case class Device(id: String, description: String, deviceType: Int, channel:Int, units:Option[String] = None,
                  conversionFactor:Option[Double] = None, conversionOffset:Option[Double] = None,
                  decimalPlaces:Option[Int] = None, monitorSensor:Option[Device] = None,
                  monitorIncreaser:Option[Device] = None, monitorDecreaser:Option[Device] = None,
                  digitalState:Option[Boolean] = None, analogueState:Option[Int] = None)

object Device {
  val TIMER = 0         // e.g. Clock
  val ANALOGUE_IN = 1   // e.g. Thermometer
  val ANALOGUE_OUT = 2  // e.g. Heater or Cooler
  val DIGITAL_IN = 3    // e.g. Button or Switch
  val DIGITAL_OUT = 4   // e.g. Pump
  val MONITOR = 5       // e.g. Thermostat

  implicit val deviceReads: Reads[Device] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "description").read[String] and
    (JsPath \ "deviceType").read[Int] and
    (JsPath \ "channel").read[Int] and
    (JsPath \ "units").readNullable[String] and
    (JsPath \ "conversionFactor").readNullable[Double] and
    (JsPath \ "conversionOffset").readNullable[Double] and
    (JsPath \ "decimalPlaces").readNullable[Int] and
    (JsPath \ "monitorSensor").readNullable[Device] and
    (JsPath \ "monitorIncreaser").readNullable[Device] and
    (JsPath \ "monitorDecreaser").readNullable[Device] and
    (JsPath \ "digitalState").readNullable[Boolean] and
    (JsPath \ "analogueState").readNullable[Int]
  )(Device.apply _)

  implicit val deviceWrites = Json.writes[Device]


  def rawToDevice(rawDevice:RawDevice, rawDeviceCollection:RawDeviceCollection):Device = {

    def innerConvert(ord: Option[String]):Option[Device] ={
      ord.flatMap( rawDeviceIdString =>
        rawDeviceCollection.devices.find(d => d.id == rawDeviceIdString).map( rmd =>
          rawToDevice(rmd, rawDeviceCollection)
        )
      )
    }

    val monitorSensor = innerConvert(rawDevice.monitorSensor)
    val monitorIncreaser = innerConvert(rawDevice.monitorIncreaser)
    val monitorDecreaser = innerConvert(rawDevice.monitorDecreaser)

    Device(rawDevice.id, rawDevice.description, rawDevice.deviceType, rawDevice.channel, rawDevice.units, rawDevice.conversionFactor,
      rawDevice.conversionOffset, rawDevice.decimalPlaces, monitorSensor, monitorIncreaser, monitorDecreaser,
      rawDevice.digitalState, rawDevice.analogueState)
  }
}

/* case classes for ajax data */
case class DeviceStatus(deviceId:Int, deviceType:Int, deviceValue:String, deviceUnit:Option[String])
object  ComponentStatus {
  implicit val formats=Json.writes[DeviceStatus]
}
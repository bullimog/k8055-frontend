package model

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads}

import scala.annotation.tailrec


case class Device(id: String, description: String, deviceType: Int, channel:Int, units:Option[String] = None,
                  monitorSensor:Option[Device] = None, monitorIncreaser:Option[Device] = None,
                  monitorDecreaser:Option[Device] = None, digitalState:Option[Boolean] = None,
                  analogueState:Option[Double] = None, analogueState2:Option[Double] = None)

object Device {
//  val TIMER = 0         // e.g. Clock
  val ANALOGUE_IN = 1   // e.g. Thermometer
  val ANALOGUE_OUT = 2  // e.g. Heater or Cooler
  val DIGITAL_IN = 3    // e.g. Button or Switch
  val DIGITAL_OUT = 4   // e.g. Pump
  val MONITOR = 5       // e.g. Thermostat
  val STROBE = 6        // e.g. pump pulser

  implicit val deviceReads: Reads[Device] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "description").read[String] and
    (JsPath \ "deviceType").read[Int] and
    (JsPath \ "channel").read[Int] and
    (JsPath \ "units").readNullable[String] and
    (JsPath \ "monitorSensor").readNullable[Device] and
    (JsPath \ "monitorIncreaser").readNullable[Device] and
    (JsPath \ "monitorDecreaser").readNullable[Device] and
    (JsPath \ "digitalState").readNullable[Boolean] and
    (JsPath \ "analogueState").readNullable[Double] and
    (JsPath \ "analogueState2").readNullable[Double]
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

    //calculate Analogue state...
    val raw:Double = rawDevice.analogueState.getOrElse(0).toDouble
    val unrounded:Double = raw * rawDevice.conversionFactor.getOrElse(1.0) + rawDevice.conversionOffset.getOrElse(0.0)
    val roundFactor:Double = math.pow(10.0, rawDevice.decimalPlaces.getOrElse(0).toDouble)
    val analogueState:Option[Double] = Some(math.round(unrounded*roundFactor)/roundFactor)

    Device(rawDevice.id, rawDevice.description, rawDevice.deviceType, rawDevice.channel, rawDevice.units,
       monitorSensor, monitorIncreaser, monitorDecreaser, rawDevice.digitalState, analogueState)
  }
}

/* case classes for ajax data */
case class DeviceStatus(deviceId:Int, deviceType:Int, deviceValue:String, deviceUnit:Option[String])
object  ComponentStatus {
  implicit val formats=Json.writes[DeviceStatus]
}
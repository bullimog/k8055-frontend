package model

import play.api.libs.json.{Json, JsPath, Reads}
import play.api.libs.functional.syntax._


case class RawDevice(id: String, description: String, deviceType: Int, channel:Int,  icon: Option[String],
                  units:Option[String] = None,
                  conversionFactor:Option[Double] = None, conversionOffset:Option[Double] = None,
                  decimalPlaces:Option[Int] = None, monitorSensor:Option[String] = None,
                  monitorIncreaser:Option[String] = None, monitorDecreaser:Option[String] = None,
                  digitalState:Option[Boolean] = None, analogueState:Option[Int] = None,
                  strobeOnTime:Option[Int] = None, strobeOffTime:Option[Int] = None)


object RawDevice {

  implicit val rawDeviceReads: Reads[RawDevice] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "description").read[String] and
    (JsPath \ "deviceType").read[Int] and
    (JsPath \ "channel").read[Int] and
    (JsPath \ "icon").readNullable[String] and
    (JsPath \ "units").readNullable[String] and
    (JsPath \ "conversionFactor").readNullable[Double] and
    (JsPath \ "conversionOffset").readNullable[Double] and
    (JsPath \ "decimalPlaces").readNullable[Int] and
    (JsPath \ "monitorSensor").readNullable[String] and
    (JsPath \ "monitorIncreaser").readNullable[String] and
    (JsPath \ "monitorDecreaser").readNullable[String] and
    (JsPath \ "digitalState").readNullable[Boolean] and
    (JsPath \ "analogueState").readNullable[Int] and
    (JsPath \ "strobeOnTime").readNullable[Int] and
    (JsPath \ "strobeOffTime").readNullable[Int]
  )(RawDevice.apply _)

  implicit val rawDeviceWrites = Json.writes[RawDevice]
}
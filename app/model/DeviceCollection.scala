package model

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads}


case class DeviceCollection(name: String, description: String, devices: List[Device])

object DeviceCollection{
  implicit val deviceCollectionReads: Reads[DeviceCollection] = (
      (JsPath \ "name").read[String] and
      (JsPath \ "description").read[String] and
      (JsPath \ "devices").read[List[Device]]
    )(DeviceCollection.apply _)

  implicit val deviceCollectionWrites = Json.writes[DeviceCollection]

  def rawToDeviceCollection(rawDeviceCollection: RawDeviceCollection):DeviceCollection = {
    val devices = rawDeviceCollection.devices.map(rawDevice => Device.rawToDevice(rawDevice, rawDeviceCollection))
    DeviceCollection(rawDeviceCollection.name,rawDeviceCollection.description, devices)
  }
}

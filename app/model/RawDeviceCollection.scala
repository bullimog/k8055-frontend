package model

import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, JsPath, Reads}


case class RawDeviceCollection(name: String, description: String, devices: List[RawDevice])

object RawDeviceCollection{
  implicit val rawDeviceCollectionReads: Reads[RawDeviceCollection] = (
      (JsPath \ "name").read[String] and
      (JsPath \ "description").read[String] and
      (JsPath \ "devices").read[List[RawDevice]]
    )(RawDeviceCollection.apply _)

  implicit val rawDeviceCollectionWrites = Json.writes[RawDeviceCollection]
}

package model

import play.api.libs.json.Json


case class AppStatus(running:Boolean, currentStep:Int, deviceStatuses:List[Device],
                          monitorStatuses:List[Device])
object  AppStatus {
  implicit val formats = Json.writes[AppStatus]
}
package model

import play.api.libs.json.Json


case class AppStatus(sequenceState: SequenceState, deviceStatuses:List[Device],
                          monitorStatuses:List[Device], strobeStatuses:List[Device])
object  AppStatus {
  implicit val formats = Json.writes[AppStatus]
}
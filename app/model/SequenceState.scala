package model

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads}

case class SequenceState(running:Boolean, currentStep:Int)

object SequenceState {
  implicit val sequenceStateReads: Reads[SequenceState] = (
  (JsPath \ "running").read[Boolean] and
  (JsPath \ "currentStep").read[Int]
  )(SequenceState.apply _)

  implicit val sequenceWrites = Json.writes[SequenceState]
}
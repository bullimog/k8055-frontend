package connectors

import connectors.Configuration._
import model.{ReadableSequence, RawDeviceCollection, DeviceCollection, DeviceState}
import play.api.Logger
import play.api.http.HttpVerbs
import play.api.http.Status._
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.libs.ws.{WS, WSRequest, WSResponse}
import play.mvc.Http.HeaderNames._
import play.mvc.Http.MimeTypes._

import scala.concurrent.Future

//had to add these two in, to get WS stuff to work
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current
object SequencerConnector extends SequencerConnector

trait SequencerConnector {

  def doGet(url: String): Option[Future[WSResponse]] = {
    val holder: WSRequest = WS.url(url)
    try {Some(holder.get())}
    catch {
      case _: java.lang.NullPointerException => None
    }
  }

  def sequenceState:Future[ReadableSequence]  = {

    def buildEmptyReadableSequence(errorText:String):ReadableSequence = {
      ReadableSequence("Empty Sequence", List(), 0, running = false)
    }

    doGet(sequencerHost + sequencerState).fold(Future(buildEmptyReadableSequence("No response from server"))) {
      theFuture => theFuture.map { wsresponse =>           // get the WSResponse out of the Future using map
        wsresponse.status match {                          // match on the response status code (int)
          case OK => parseReadableSequence(wsresponse.body).fold(buildEmptyReadableSequence("Couldn't parse sequence"))(rs=>rs)
          case NOT_FOUND => buildEmptyReadableSequence("Sequence not Found")
          case _ => buildEmptyReadableSequence("Server Error??")
        }
      }
    }
  }

  def buildEmptyRawDeviceCollection(errorText:String):RawDeviceCollection = {
    RawDeviceCollection(errorText, errorText, List())
  }

  def parseReadableSequence(jsonSource:String):Option[ReadableSequence] = {
    val json: JsValue = Json.parse(jsonSource)
    json.validate[ReadableSequence] match {
      case s: JsSuccess[ReadableSequence] => s.asOpt
      case e: JsError => None
    }
  }

}

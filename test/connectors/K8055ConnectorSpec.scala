package connectors


import model.DeviceCollection
import play.api.libs.ws.WSResponse
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import scala.concurrent.Future


@RunWith(classOf[JUnitRunner])
class K8055ConnectorSpec extends PlaySpecification {

  val k8055Connector = new K8055Connector {
    override def doGet(url:String) : Option[Future[WSResponse]]= {
      WSMock.getMockWSResponse
    }
  }

  "The K8055 connector" should {
    "construct a DeviceCollection, from a read RawDeviceCollection" in new WithApplication {
      val dc:DeviceCollection = await(k8055Connector.k8055State)
      dc.name must equalTo ("My Fake Device Collection")
    }
  }
}






//object WSMock extends org.specs2.mock.Mockito  {
//
//  def getMockWSResponse:Option[Future[WSResponse]] = {
//    val response = mock[WSResponse]
//
//    //  val urls:collection.mutable.Buffer[String] = new collection.mutable.ArrayBuffer[String]()
//    import play.api.http.HeaderNames
//    response.status returns OK
//    response.header(HeaderNames.CONTENT_TYPE) returns Some("application/json")
//    response.body returns Json.toJson(makeRawDeviceCollection).toString()
//    Option(Future(response))
//  }
//
//  def makeRawDeviceCollection = {
//    val rPump = RawDevice("DO-1", "Pump", DIGITAL_OUT, 1, digitalState = Some(true))
//    val rHeater = RawDevice("AO-1", "Heater", ANALOGUE_OUT, 1, Some("%"), Some(0), analogueState = Some(22))
//    val rSwitch = RawDevice("DI-1", "Switch", DIGITAL_IN, 1, digitalState = Some(true))
//    val rThermometer = RawDevice("AI-1", "Thermometer", ANALOGUE_IN, 1, Some("°c"), Some(0), analogueState = Some(17))
//    val rThermostat = RawDevice("MO-1", "Thermostat", MONITOR, 1, Some("c"), None, None, None, Some("AI-1"),
//      Some("AO-1"), None, Some(true), Some(56))
//    val rawDevices: List[RawDevice] = List(rPump, rHeater, rSwitch, rThermometer, rThermostat)
//    RawDeviceCollection("My Fake Device Collection", "For Brewing", rawDevices)
//  }
//}




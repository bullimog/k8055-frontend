package connectors


import model.{RawDeviceCollection, DeviceCollection}
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import scala.None
import scala.concurrent.Future


@RunWith(classOf[JUnitRunner])
class K8055ConnectorSpec extends PlaySpecification {

  val k8055Connector200 = new K8055Connector {
    override def doGet(url:String) : Option[Future[WSResponse]]= {
      WSMock.getMockWSResponse(status = OK)
    }
  }

  val k8055Connector404 = new K8055Connector {
    override def doGet(url:String) : Option[Future[WSResponse]]= {
      WSMock.getMockWSResponse(status = NOT_FOUND)
    }
  }

  val k8055ConnectorBad = new K8055Connector {
    override def doGet(url:String) : Option[Future[WSResponse]]= {
      WSMock.getMockWSResponse(status = BAD_REQUEST)
    }
  }

  "The K8055 connector" should {
    "construct a DeviceCollection, from a read RawDeviceCollection" in new WithApplication {
      val dc:DeviceCollection = await(k8055Connector200.k8055State)
      dc.name must equalTo ("My Fake Device Collection")
    }
  }

  "The K8055 connector" should {
    "construct a DefaultDeviceCollection, from a not found RawDeviceCollection" in new WithApplication {
      val dc:DeviceCollection = await(k8055Connector404.k8055State)
      dc.name must equalTo ("Devices not Found")
    }
  }

  "The K8055 connector" should {
    "construct a DefaultDeviceCollection, from a bad read RawDeviceCollection" in new WithApplication {
      val dc:DeviceCollection = await(k8055ConnectorBad.k8055State)
      dc.name must equalTo ("Server Error??")
    }
  }

  "successfully parse RawDeviceCollection json" in {
    val j = Json.toJson(WSMock.makeRawDeviceCollection).toString()
    val ordc:Option[RawDeviceCollection] =  K8055Connector.parseDeviceCollection(j)
    val rdc:RawDeviceCollection = ordc.orNull
    rdc.name must equalTo ("My Fake Device Collection")
  }

  "unsuccessfully parse RawDeviceCollection json" in {
    val j = "{\"name\" : \"This will fail\"}"
    val ordc:Option[RawDeviceCollection] =  K8055Connector.parseDeviceCollection(j)
    ordc must be (None)
  }

  //pointless test...?
  "set the device state on the K8055 microservice" in {
    val result = await(K8055Connector.setK8055State("ID-Test", "1"))
    result must equalTo(true)

  }
}



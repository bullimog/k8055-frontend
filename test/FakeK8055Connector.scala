import connectors.K8055Connector
import model.DeviceCollection

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object FakeK8055Connector extends FakeK8055Connector
trait FakeK8055Connector extends K8055Connector {

  override def k8055State:Future[DeviceCollection]  = {
    Future(buildEmptyDeviceCollection("No response from server"))
  }
}

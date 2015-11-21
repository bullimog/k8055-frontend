import connectors.K8055Connector
import controllers.StatusController
//import play.api.mvc.Controller
//import play.mvc.Results

import play.api.mvc._
import play.api.test._
import scala.concurrent.Future

object StatusControllerSpec extends PlaySpecification with Results {

  class TestController() extends Controller with StatusController{    lazy val k8055Connector = FakeK8055Connector}
  "Example Page#index" should {
    "should be valid" in {
      val controller = new TestController
      val result: Future[Result] = controller.present().apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText must contain("Devices")
    }
  }
}
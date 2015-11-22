package controllers

import connectors.FakeK8055Connector
import play.api.mvc._
import play.api.test._

import scala.concurrent.Future

object StatusControllerSpec extends PlaySpecification with Results {

  class TestController() extends Controller with StatusController{lazy val k8055Connector = FakeK8055Connector}
  val controller = new TestController
  "The Status Controller" should {
    "render the status page from the present method" in {

      val result: Future[Result] = controller.present().apply(FakeRequest())
      contentType(result) must equalTo(Some("text/html"))
      val bodyText: String = contentAsString(result)
      bodyText must contain("My Fake Device Collection")
    }

    "provide a list of list of component statuses" in {
      val result: Future[Result] = controller.sequencerStatus().apply(FakeRequest())
      contentType(result) must equalTo(Some("application/json"))
      val bodyText: String = contentAsString(result)
      bodyText must contain("\"deviceStatuses\":[{")
    }

    "allow a component status to be set" in {
      val result: Future[Result] = controller.setDeviceState("deviceId", "state").apply(FakeRequest())
      contentType(result) must equalTo(Some("text/plain"))
      status(result) must equalTo(200)
    }

    "provide javascript routes for AJAX calls" in {
      val result: Future[Result] = controller.javascriptReverseRoutes().apply(FakeRequest())
      contentType(result) must equalTo(Some("text/javascript"))
      val bodyText: String = contentAsString(result)
      bodyText must contain("var jsRoutes = {}")
      status(result) must equalTo(200)
    }
  }
}
package connectors

import play.api.Play

trait Configuration {
  val k8055Host = Play.current.configuration.getString("k8055.host").fold("badHostConfig") (filename => filename)
  val k8055Devices = Play.current.configuration.getString("k8055.devicespath").fold("badDeviceConfig") (filename => filename)
}

object Configuration extends Configuration

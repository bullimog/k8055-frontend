package connectors

import play.api.Play

trait Configuration {
  val k8055Host = Play.current.configuration.getString("k8055.host").fold("badHostConfig") (filename => filename)
  val k8055Devices = Play.current.configuration.getString("k8055.devicespath").fold("badDeviceConfig") (filename => filename)
  val k8055DeviceStateDelta = Play.current.configuration.getString("k8055.deviceStateDelta").fold("badDeviceConfig") (filename => filename)

  val increment = Play.current.configuration.getInt("increment.single").fold(1) (increment => increment)
  val big_increment = Play.current.configuration.getInt("increment.big").fold(10) (increment => increment)
  val decrement = Play.current.configuration.getInt("decrement.single").fold(-1) (decrement => decrement)
  val big_decrement = Play.current.configuration.getInt("decrement.big").fold(-10) (decrement => decrement)

  val up = "up"
  val doubleUp = "dup"
  val down = "down"
  val doubleDown = "ddown"
  val isTrue = "true"
  val isFalse = "false"

}

object Configuration extends Configuration

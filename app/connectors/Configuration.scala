package connectors

import play.api.Play

trait Configuration {
  val k8055Host = Play.current.configuration.getString("k8055.host").fold("badHostConfig") (filename => filename)
  val k8055Devices = Play.current.configuration.getString("k8055.devicespath").fold("badk8055DeviceConfig") (filename => filename)
  val k8055DeviceStateDelta = Play.current.configuration.getString("k8055.deviceStateDelta").fold("badDeviceConfig") (filename => filename)

  val increment = Play.current.configuration.getInt("increment.single").fold(1) (increment => increment)
  val big_increment = Play.current.configuration.getInt("increment.big").fold(25) (increment => increment)
  val decrement = Play.current.configuration.getInt("decrement.single").fold(-1) (decrement => decrement)
  val big_decrement = Play.current.configuration.getInt("decrement.big").fold(-25) (decrement => decrement)

  val onOffBig_increment = Play.current.configuration.getInt("increment.big").fold(10) (increment => increment)
  val onOffBig_decrement = Play.current.configuration.getInt("decrement.big").fold(-10) (decrement => decrement)

  val up = "up"
  val doubleUp = "dup"
  val down = "down"
  val doubleDown = "ddown"

  val onUp = "onUp"
  val onDoubleUp = "onDup"
  val onDown = "onDown"
  val onDoubleDown = "onDdown"

  val offUp = "offUp"
  val offDoubleUp = "offDup"
  val offDown = "offDown"
  val offDoubleDown = "offDdown"

  val isTrue = "true"
  val isFalse = "false"

  val sequencerHost = Play.current.configuration.getString("sequencer.host").fold("badSequencerHostConfig") (filename => filename)
  val sequencerState = Play.current.configuration.getString("sequencer.statePath").fold("badSequencerStateConfig") (filename => filename)
  val sequence = Play.current.configuration.getString("sequencer.sequencePath").fold("badSequenceConfig") (filename => filename)
  val start = Play.current.configuration.getString("sequencer.startPath").fold("badSequencerStartConfig") (filename => filename)
  val stop = Play.current.configuration.getString("sequencer.stopPath").fold("badSequencerStopConfig") (filename => filename)
  val reset = Play.current.configuration.getString("sequencer.resetPath").fold("badSequencerResetConfig") (filename => filename)
  val nextStep = Play.current.configuration.getString("sequencer.nextStepPath").fold("badNextStepConfig") (filename => filename)
  val previousStep = Play.current.configuration.getString("sequencer.previousStepPath").fold("badPreviousStepConfig") (filename => filename)

}

object Configuration extends Configuration

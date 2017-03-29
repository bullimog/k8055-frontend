
  function highlightSteps(current){

  //console.debug("current="+current)

//    if(current >= 0) {
      var steps = $(".program").children().children();
      for(var i=1; i<current; i++){
        $("#"+steps[i].id).removeClass("to-run");
        $("#"+steps[i].id).removeClass("running");
        $("#"+steps[i].id).addClass("ran");
      }

      $("#step"+current).removeClass("to-run");
      $("#step"+current).addClass("running");
      $("#step"+current).removeClass("ran");

      for(var i=current+1; i<steps.length; i++){

        if(steps[i].id == "step-description"+i){
           $("#"+steps[i].id).addClass("comment");
         }
         else{
           $("#"+steps[i].id).addClass("to-run");
         }
         $("#"+steps[i].id).removeClass("running");
         $("#"+steps[i].id).removeClass("ran");
      }
  }

  function highlightStartStopButtons(running){
    if(running==true){
       $("#startButton").addClass("btn-disable")
       $("#startButton").removeClass("btn-enable")
       $("#stopButton").addClass("btn-enable")
       $("#stopButton").removeClass("btn-disable")
    }
    else{
       $("#startButton").addClass("btn-enable")
       $("#startButton").removeClass("btn-disable")
       $("#stopButton").addClass("btn-disable")
       $("#stopButton").removeClass("btn-enable")
    }
  }

  function updateDevices(ss){
     for(i=0; i<ss.deviceStatuses.length; i++){
        var compId = ss.deviceStatuses[i].id
        var compType = ss.deviceStatuses[i].deviceType
        var compAnalogueState = ss.deviceStatuses[i].analogueState
        var compDigitalState = ss.deviceStatuses[i].digitalState
        var compUnit = ss.deviceStatuses[i].units
        //console.debug("comp: "+ compId + " - "  + compType +" - " + compAnalogueState +" - "+ compDigitalState);

        switch(compType){
          case 0: {$("#device-val"+compId).text(formatTimer(compAnalogueState)); break;} //TIMER
          case 1: {$("#device-val"+compId).text(compAnalogueState+" "+compUnit); break;} //ANALOGUE_IN / Thermometer
          case 2: {$("#device-val"+compId).text(compAnalogueState+" "+compUnit); break;} //ANALOGUE OUT / Heater
          case 3: {                                                              //DIGITAL_IN
           // console.debug("case is Digital In, compstate=["+compDigitalState+"]")
            if(compDigitalState) $("#device-true"+compId).prop("checked", true)
            else $("#device-true"+compId).prop("checked", false);
            break;
          }
          case 4: {                                                              //DIGITAL_OUT
            if(compDigitalState) $("#device-true"+compId).prop("checked", true)
            else $("#device-false"+compId).prop("checked", true);
            break;
          }
        }
     }
  }

  function formatTimer(totalSeconds){
    hours = Math.floor(totalSeconds / 3600);
    totalSeconds %= 3600;
    mins= Math.floor(totalSeconds / 60);
    secs = totalSeconds % 60;
    return (""+pad10Zero(hours)+":"+pad10Zero(mins)+":"+pad10Zero(secs))
  }

  function pad10Zero(num){
    if(num<10) return "0"+num
    else return num
  }

  function pad100Zero(num){
    var withDecimals = num.toFixed(2)
    var str = "" + withDecimals
    var pad = "000000"
    return pad.substring(0, pad.length - str.length) + str
  }



  function updateMonitors(ss){
    for(i=0; i<ss.monitorStatuses.length; i++){
       var monitorId = ss.monitorStatuses[i].id
       var monitorEnabled = ss.monitorStatuses[i].digitalState
       var monitorTemp = ss.monitorStatuses[i].analogueState
       var sensorTemp = ss.monitorStatuses[i].monitorSensor.analogueState
       var increaserPower = ss.monitorStatuses[i].monitorIncreaser.analogueState
       var sensorType = ss.monitorStatuses[i].monitorSensor.deviceType
       var sensorUnit = ss.monitorStatuses[i].monitorSensor.units
       var increaserUnit = ss.monitorStatuses[i].monitorIncreaser.units
       var sensorState = ss.monitorStatuses[i].monitorSensor.digitalState
       var increaserState = ss.monitorStatuses[i].monitorIncreaser.digitalState

//       console.debug("monitor: " + sensorType)
    //    console.debug("monitor: "+ monitorId + " - "  + monitorEnabled +" - " + monitorTemp +" - "+ sensorTemp);
       if(monitorEnabled) $("#monitor-true"+monitorId).prop("checked", true)
            else $("#monitor-false"+monitorId).prop("checked", true);

       $("#monitor-temperature"+monitorId).text(monitorTemp+" "+sensorUnit);

       if(sensorType == 1){
         $("#monitor-sensor"+monitorId).text(sensorTemp+" "+ sensorUnit);
         $("#monitor-increaser"+monitorId).text(increaserPower+" "+increaserUnit);
       }
       else{
         if(sensorState) $("#monitor-sensor"+monitorId).prop("checked", true)
         else $("#monitor-sensor"+monitorId).prop("checked", false);

         if(increaserState) $("#monitor-increaser"+monitorId).prop("checked", true)
         else $("#monitor-increaser"+monitorId).prop("checked", false);
       }
    }
  }

    function updateStrobes(ss){
      for(i=0; i<ss.strobeStatuses.length; i++){
         var strobeId = ss.strobeStatuses[i].id
         var strobeEnabled = ss.strobeStatuses[i].digitalState
         var strobeOnTime = ss.strobeStatuses[i].strobeOnTime
         var strobeOffTime = ss.strobeStatuses[i].strobeOffTime
         var increaserState = ss.strobeStatuses[i].monitorIncreaser.digitalState

//         console.debug("strobe: " + strobeEnabled)

//         console.debug("strobe: "+ strobeId + " - "  + strobeEnabled +" - " + strobeOnTime +" - "+ strobeOffTime + " - " + increaserState);
         if(strobeEnabled) $("#strobe-true"+strobeId).prop("checked", true)
              else $("#strobe-false"+strobeId).prop("checked", true);

         $("#strobe-on"+strobeId).text(strobeOnTime+" seconds");
         $("#strobe-off"+strobeId).text(strobeOffTime+" seconds");


         if(increaserState) $("#strobe-increaser"+strobeId).prop("checked", true)
         else $("#strobe-increaser"+strobeId).prop("checked", false);
      }
    }

  function startSequencer(){jsRoutes.controllers.StatusController.startSequencer(null).ajax();};
  function stopSequencer(){jsRoutes.controllers.StatusController.stopSequencer(null).ajax();};
  function resetSequencer(){jsRoutes.controllers.StatusController.resetSequencer(null).ajax();};
  function nextStep(){jsRoutes.controllers.StatusController.nextStep(null).ajax();};
  function previousStep(){jsRoutes.controllers.StatusController.previousStep(null).ajax();};

$(function() {
    console.debug("Initialising...")
    sequencerStatusCall();  //Mute this out for now.
});

function initEvents(){

}

function makeCall(componentId, state){
    console.debug("componentId="+componentId+ "   state="+state)
    jsRoutes.controllers.StatusController.setDeviceState(componentId, state).ajax();
}



var sequencerStatusCall = function() {
    var ajaxCallBack = {
        success : onSuccess,
        error : onError
    }
    jsRoutes.controllers.StatusController.sequencerStatus().ajax(ajaxCallBack);

    setTimeout(sequencerStatusCall, 1000);
};

var  onSuccess = function(strSequenceStatus) {
    //console.debug("json is..."+strSequenceStatus)
    var ss = JSON.parse(strSequenceStatus);
    highlightStartStopButtons(ss.sequenceState.running)
    highlightSteps(ss.sequenceState.currentStep)
    updateDevices(ss)
    updateMonitors(ss)
    updateStrobes(ss)
//    console.debug("======"+formatTimer(ss.sequenceState.timeRemaining))
    $("#clock-val").text(formatTimer(ss.sequenceState.timeRemaining))
}

var onError = function(error) {
    console.debug("Error in ajax Call");
    console.debug(error);
}

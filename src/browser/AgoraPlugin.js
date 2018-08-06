// 2018-08-06 ADD By Wayne
function successCallBack(success, result) {
  var arg = {status: 'success',  result: result};
  success(arg);
}

function errorCallBack(failure, msg) {
  var arg = {status: 'error',  msg: msg};
  failure(arg);
}

var agoraClient;
var localStream;
var remoteStream;
var localUid;
var localDivDom;
var remoteDivDom;

function createVideoDom(){
  if (document.getElementById('agoraLocalVideo')==null){
    localDivDom = document.createElement("div");
    localDivDom.style = "position:absolute; left:10px; top:100px; z-index: 999; width:100px; height:100px;"
    localDivDom.id = "agoraLocalVideo"
    document.body.appendChild(localDivDom);
  }

  if (document.getElementById('agoraRemoteVideo')==null){
    remoteDivDom = document.createElement("div");
    remoteDivDom.style = "position:absolute; left:0; top:0; z-index: 900; width:100%; height: 600px;"
    remoteDivDom.id = "agoraRemoteVideo"
    document.body.appendChild(remoteDivDom);
  }
}

function createRendererView() {
  createVideoDom();

  localStream = AgoraRTC.createStream({
    streamID: localUid,
    audio: true,
    video: true,
    screen: false}
  );

  localStream.init(function() {
    console.log("getUserMedia successfully");
    localStream.play('agoraLocalVideo');
    
    agoraClient.publish(localStream, function (err) {
      console.log("Publish local stream error: " + err);
    });

  }, function (err) {
    console.log("getUserMedia failed", err);
  });

}

function removeAllDom() {
  if (document.getElementById('agoraLocalVideo')!=null){
    document.body.removeChild(localDivDom);
    localDivDom = null;
  }

  if (document.getElementById('agoraRemoteVideo')!=null){
    document.body.removeChild(remoteDivDom);
    remoteDivDom = null;
  }
}

function subscribeStream() {
  agoraClient.on('stream-added', function (evt) {
    var stream = evt.stream;
    console.log("New stream added: " + stream.getId());
  
    agoraClient.subscribe(stream, function (err) {
      console.log("Subscribe stream failed", err);
    });
  });

  agoraClient.on('stream-subscribed', function (evt) {
    remoteStream = evt.stream;
    console.log("Subscribe remote stream successfully: " + remoteStream.getId());
    remoteStream.play("agoraRemoteVideo");
  })
}


var agora = {
	create: function (success, failure,  options) {
    var appKey = options[0].appId;
    agoraClient = AgoraRTC.createClient({mode: 'live', codec: "h264"});
    console.log("AppKey", appKey);
    agoraClient.init(appKey, function () {
      console.log("AgoraRTC client initialized");
      success();
    }, function (err) {
      console.log("AgoraRTC client init failed", err);
      failure(err);
    });
  },

  joinChannel: function (success, failure,  options) {
    var channelKey = options[0];
    var channelName = options[1];
    var uid  = options[2];

    agoraClient.join(channelKey, channelName, uid, function(uid) {
      console.log("User " + uid + " join channel successfully");
      localUid = uid;
      createRendererView();
      subscribeStream();
      success(uid);
    }, function(err) {
      console.log("Join channel failed", err);
      failure(err);
    });
  },

  switchCamera: function(success, failure,  options) {
    console.log("Browser不支持切换摄像头!");
  },

  disableVideo: function(success, failure,  options) {

  },

  leaveChannel: function(success, failure,  options) {

    removeAllDom();

    agoraClient.leave(function () {
      console.log("Leavel channel successfully");
    }, function (err) {
      console.log("Leave channel failed");
    });
  }
  
}


module.exports = agora;

require('cordova/exec/proxy').add("Agora", module.exports);
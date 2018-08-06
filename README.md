### 2.2.3 ###

修改版本号，与Agora的SDK版本号，保持一致。

iOS SDK因为文件太大，没有包含在git中，请自行去https://docs.agora.io/cn/2.3.1/download 下载，然后将以下文件覆盖src/iOS/libs下：
```
AgoraRtcEngineKit.framework
AgoraRtcCryptoLoader.framework
libcrypto.a
```
如果编译报找不到libcrypto.a，需要把"agoraVideo/Plugins/cordova-plugin-agora"路径添加到Library Search Path（添加在Build Settings中）。


界面需要自己来做，如果需要呼叫、接听确认，则需要配合消息（信道）来完成。
插件会在界面中渲染展现本地和远程视频。

调试需要真机，不支持模拟器。

示例代码：

```javascript
var app = {
    switchCamera: function() {
        // 切换摄像头
        agora.switchCamera();
    },
    mute: function() {
        // 静音
        agora.muteLocalVideoStream();
    },
    disableVideo: function() {
        // 关闭视频
        agora.disableVideo();
    },
    endCall: function() {
        // 结束呼叫
        agora.leaveChannel();
    },
    initAgora: function() {
        // 初始化，直接进入频道
        // 默认在初始化时候，设置了音视频的配置
        // 同时渲染了本地的视频，如果需要调整本地视频的位置，可以使用setLocalVideoPosition
        let appId = "xxxxx";
        console.log('initAgora');
        agora.create({appId: appId}, function() {
            console.log('create agora');
            // iOS 下多加一步：（后续优化）
            // agora.createRendererView();  
            agora.joinChannel(null, 'demoChannel1', null, function() {
                }, function() {
                }
            )
        }, function(error) {
            console.error(error.code +":" + error.message);
        });

        agora.addEventListener("onJoinChannelSuccess", function(data) {
            console.log(data);
        });
    },
};
```

###BETA VERSION###

## Notice
For iOS you should add this path of libcrypto.a to Library Search Path

```
Method:
		addEventListener: function(eventName, callback) {},
        removeEventListener: function(eventName, callback) {},
        create: function (config, successCallback, failCallback) {},
        joinChannel: function (channelKey, channelName, uid, successCallback, failCallback) {},
        leaveChannel: function (successCallback, failCallback) {},
        enableVideo: function(successCallback, failCallback) {},
        disableVideo: function(successCallback, failCallback) {},
        enableSpeakerphone: function(successCallback, failCallback) {},
        disableSpeakerphone: function(successCallback, failCallback) {},
        getCallId: function(successCallback, failCallback) {},
		enableAudioVolumeIndication: function(interval, smooth, successCallback, failCallback) {},
        enableAudio: function(successCallback, failCallback) {},
        disableAudio: function(successCallback, failCallback) {},
        setLocalVoicePitch: function(pitch, successCallback, failCallback) {},
        setAudioProfile: function(profile, scenario, successCallback, failCallback) {},
        createRendererView: function(successCallback, failCallback) {},
        setVideoProfile: function(profile, swapWidthAndHeight, successCallback, failCallback) {},
        setupLocalVideo: function(uid, successCallback, failCallback) {},
        setupRemoteVideo: function(uid, successCallback, failCallback) {},
        setVideoQualityParameters: function(prefer, successCallback, failCallback) {},
        startPreview: function(successCallback, failCallback) {},
        stopPreview: function(successCallback, failCallback) {},
        setLocalRenderMode: function(mode, successCallback, failCallback) {},
        setRemoteRenderMode: function(mode, successCallback, failCallback) {},
        switchCamera: function(successCallback, failCallback) {},
        muteLocalVideoStream: function(muted, successCallback, failCallback) {},
        muteAllRemoteVideoStreams: function(muted, successCallback, failCallback) {},
        muteRemoteVideoStream: function(uid, mode, successCallback, failCallback) {},
        setLocalVideoPosition: function(position, successCallback, failCallback) {},
        setRemoteVideoPosition: function(position, successCallback, failCallback) {},
        setWebVidePostion: function(position, successCallback, failCallback) {}
```

```
Event: @see: android document
	加入频道回调 (onJoinChannelSuccess)
	重新加入频道回调 (onRejoinChannelSuccess) 
	发生警告回调(onWarning)
	发生错误回调 (onError)
	离开频道回调 (onLeaveChannel) 
	声音质量回调 (onAudioQuality) 
	说话声音音量提示回调  (onAudioVolumeIndication) 
	其他用户加入当前频道回调 (onUserJoined) 
	其他用户离开当前频道回调 (onUserOffline)
	用户静音回调 (onUserMuteAudio) 
	Rtc Engine 统计数据回调 (onRtcStats)
	网络质量报告回调 (onLastmileQuality)
	连接中断回调 (onConnectionInterrupted)
	连接丢失回调  (onConnectionLost)
	本地视频显示回调 (onFirstLocalVideoFrame) 
	远端视频显示回调 (onFirstRemoteVideoFrame)
	远端视频接收解码回调 (onFirstRemoteVideoDecoded)
	其他用户停止/重启视频回调 (onUserMuteVideo)
	其他用户启用/关闭视频 (onUserEnableVideo)
	本地视频统计回调 (onLocalVideoStat)
	远端视频统计回调 (onRemoteVideoStat)
	摄像头启用回调 (onCameraReady)
	视频功能停止回调(onVideoStopped)
	更新录制服务状态回调 (onRefreshRecordingServiceStatus)
	录制开始/停止/状态查询回调(onApiCallExecuted)
	接收到对方数据流消息的回调(onStreamMessage) 
	接收对方数据流消息错误的回调(onStreamMessageError)
```

```
	sample:
		agora.create({appId: "appId"}, function() {
            agora.joinChannel('channelKey', 'test', null, function() {
                agora.enableSpeakerphone(function() {}, function(error) {
                    console.error(error.code +":" + error.message);
                });
        
                agora.getCallId(function(id) {
                    console.log("Call Id:" + id);
                });
        
        
                setTimeout(function() {
                    agora.leaveChannel();
                }, 10000);
        
            }, function(error) {
                console.log(error);
                console.error(error.code +":" + error.message);
            });
        }, function(error) {
            console.error(error.code +":" + error.message);
        });
		
		agora.addEventListener("onJoinChannelSuccess", function(data) {
			//...
		});
```

for more, please contact the author.
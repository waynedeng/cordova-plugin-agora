//
//  Agora.m
//  YiYouMicroClient
//
//  Created by DTG on 2017/6/24.
//
//
#import "AgoraPlugin.h"
#import <Cordova/CDVPluginResult.h>
#import <Cordova/CDVInvokedUrlCommand.h>
#import <AgoraRtcEngineKit/AgoraRtcEngineKit.h>

@implementation AgoraPlugin 

- (CDVPlugin*) pluginInitialize:(UIWebView*)webView
{
    return self;
}

/**
 *  The warning occurred in SDK. The APP could igonre the warning, and the SDK could try to resume automically.
 *
 *  @param engine      The engine kit
 *  @param warningCode The warning code
 */
- (void)rtcEngine:(AgoraRtcEngineKit *)engine didOccurWarning:(AgoraRtcWarningCode)warningCode {
    NSDictionary *data = @{@"warn" : [NSNumber numberWithLong: warningCode]};
    [self notifyEvent:@"onWarning" :data];
}

/**
 *  The error occurred in SDK. The SDK couldn't resume to normal state, and the app need to handle it.
 *
 *  @param engine    The engine kit
 *  @param errorCode The error code
 */
- (void)rtcEngine:(AgoraRtcEngineKit *)engine didOccurError:(AgoraRtcErrorCode)errorCode {
    NSDictionary *data = @{@"err" : [NSNumber numberWithLong: errorCode]};
    [self notifyEvent:@"onError":data];
}

/**
 *  Event of the user joined the channel.
 *
 *  @param engine  The engine kit
 *  @param channel The channel name
 *  @param uid     The remote user id
 *  @param elapsed The elapsed time (ms) from session beginning
 */
- (void)rtcEngine:(AgoraRtcEngineKit *)engine didJoinChannel:(NSString*)channel withUid:(NSUInteger)uid elapsed:(NSInteger) elapsed {
    NSDictionary *data = @{@"channel" : channel, @"uid" : [NSNumber numberWithLong: uid], @"elaspsed" : [NSNumber numberWithLong: elapsed]};
    [self notifyEvent:@"onJoinChannelSuccess" :data];
}

/**
 *  Event of the user rejoined the channel
 *
 *  @param engine  The engine kit
 *  @param channel The channel name
 *  @param uid     The user id
 *  @param elapsed The elapsed time (ms) from session beginning
 */
- (void)rtcEngine:(AgoraRtcEngineKit *)engine didRejoinChannel:(NSString*)channel withUid:(NSUInteger)uid elapsed:(NSInteger) elapsed {
    NSDictionary *data = @{@"channel" : channel, @"uid" : [NSNumber numberWithLong: uid], @"elaspsed" : [NSNumber numberWithLong: elapsed]};
    [self notifyEvent:@"onRejoinChannelSuccess" :data];
}

/**
 *  The network quality of local user.
 *
 *  @param engine  The engine kit
 *  @param uid     The id of user
 *  @param txQuality The sending network quality
 *  @param rxQuality The receiving network quality
 */
- (void)rtcEngine:(AgoraRtcEngineKit *)engine networkQuality:(NSUInteger)uid txQuality:(AgoraRtcQuality)txQuality rxQuality:(AgoraRtcQuality)rxQuality {
    NSDictionary *data = @{@"uid" : [NSNumber numberWithLong:uid], @"txQuality" : [NSNumber numberWithLong: txQuality], @"rxQuality" : [NSNumber numberWithLong: rxQuality]};
    [self notifyEvent:@"onNetworkQuality" :data];
}

/**
 *  Event of API call executed
 *
 *  @param engine The engine kit
 *  @param api    The API description
 *  @param error  The error code
 */
- (void)rtcEngine:(AgoraRtcEngineKit *)engine didApiCallExecute:(NSString*)api error:(NSInteger)error {
    NSDictionary *data = @{@"api" : api, @"error" : [NSNumber numberWithLong: error]};
    [self notifyEvent:@"onApiCallExecuted" :data];
}

/**
 *  The sdk reports the volume of a speaker. The interface is disable by default, and it could be enable by API "enableAudioVolumeIndication"
 *
 *  @param engine      The engine kit
 *  @param speakers    AgoraRtcAudioVolumeInfos array
 *  @param totalVolume The total volume of speakers
 */
- (void)rtcEngine:(AgoraRtcEngineKit *)engine reportAudioVolumeIndicationOfSpeakers:(NSArray*)speakers totalVolume:(NSInteger)totalVolume {
    
    NSMutableArray* array =  [[NSMutableArray alloc] initWithCapacity:0];
    for(AgoraRtcAudioVolumeInfo* info in speakers) {
        NSDictionary *data = @{@"uid": [NSNumber numberWithLong:[info uid]], @"volume" : [NSNumber numberWithLong: [info volume]]};
        [array addObject:data];
    }
    
    NSDictionary *data = @{@"speakers" : array, @"totalVolume" : [NSNumber numberWithLong: totalVolume]};
    [self notifyEvent:@"onAudioVolumeIndication" :data];
}


/**
 *  Event of remote user joined
 *
 *  @param engine  The engine kit
 *  @param uid     The remote user id
 *  @param elapsed The elapsed time(ms) from the beginning of the session.
 */
- (void)rtcEngine:(AgoraRtcEngineKit *)engine didJoinedOfUid:(NSUInteger)uid elapsed:(NSInteger)elapsed {
    
    NSDictionary *data = @{@"uid" : [NSNumber numberWithLong: uid], @"elapsed" : [NSNumber numberWithLong: elapsed]};
    [self notifyEvent:@"onUserJoined" :data];
}

/**
 *  Event of remote user offlined
 *
 *  @param engine The engine kit
 *  @param uid    The remote user id
 *  @param reason Reason of user offline, quit, drop or became audience
 */
- (void)rtcEngine:(AgoraRtcEngineKit *)engine didOfflineOfUid:(NSUInteger)uid reason:(AgoraRtcUserOfflineReason)reason {
    NSDictionary *data = @{@"uid" : [NSNumber numberWithLong: uid], @"reason": [NSNumber numberWithLong: reason]};
    [self notifyEvent:@"onUserOffline" :data];
}

/**
 *  The audio quality of the user. updated every two seconds.
 *
 *  @param engine  The engine kit
 *  @param uid     The id of user
 *  @param quality The audio quality
 *  @param delay   The delay from the remote user
 *  @param lost    The percentage of lost packets
 */
- (void)rtcEngine:(AgoraRtcEngineKit *)engine audioQualityOfUid:(NSUInteger)uid quality:(AgoraRtcQuality)quality delay:(NSUInteger)delay lost:(NSUInteger)lost {
    NSDictionary *data = @{@"uid" : [NSNumber numberWithLong: uid], @"quality": [NSNumber numberWithLong: quality],
                           @"delay": [NSNumber numberWithLong: delay], @"lost": [NSNumber numberWithLong: lost]};
    [self notifyEvent:@"onAudioQuality" :data];
}


/**
 *  Event of disconnected with server. This event is reported at the moment SDK loses connection with server.
 *  In the mean time SDK automatically tries to reconnect with the server until APP calls leaveChannel.
 *
 *  @param engine The engine kit
 */
- (void)rtcEngineConnectionDidInterrupted:(AgoraRtcEngineKit *)engine {
    NSDictionary *data = [NSDictionary alloc];
    [self notifyEvent:@"onConnectionInterrupted" :data];
}

/**
 *  Event of loss connection with server. This event is reported after the connection is interrupted and exceed the retry period (10 seconds by default).
 *  In the mean time SDK automatically tries to reconnect with the server until APP calls leaveChannel.
 *
 *  @param engine The engine kit
 */
- (void)rtcEngineConnectionDidLost:(AgoraRtcEngineKit *)engine {
    NSDictionary *data = [NSDictionary alloc];
    [self notifyEvent:@"onConnectionLost" :data];
}


- (void) create:(CDVInvokedUrlCommand*)command
{
    
    if(!command.arguments || command.arguments.count <= 0) {
        CDVPluginResult* pluginResult;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
    
    NSDictionary* config = [[command arguments] objectAtIndex:0];
    NSObject* appId = [config objectForKey:@"appId"];
    
    // Check command.arguments here.
    [self.commandDelegate runInBackground:^{
        //NSString* payload = nil;
        // Some blocking logic...
        //CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:payload];
        // The sendPluginResult method is thread-safe.
        //[self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        
        self.agoraKit = [AgoraRtcEngineKit sharedEngineWithAppId:(NSString*)appId delegate:self];
        [self.agoraKit setDefaultAudioRouteToSpeakerphone:true];
        [self.agoraKit disableVideo];
        
        CDVPluginResult* pluginResult;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];

}

- (void) joinChannel:(CDVInvokedUrlCommand*)command
{
    NSString* channelKey = [[command arguments] objectAtIndex:0];
    NSString* channelName = [[command arguments] objectAtIndex:1];
    NSNumber* uid = [[command arguments] objectAtIndex:2];
    NSUInteger UID = [uid unsignedIntegerValue];
    
    int code = [self.agoraKit joinChannelByKey:channelKey channelName:channelName info:@"" uid:UID joinSuccess:nil];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}


- (void) leaveChannel:(CDVInvokedUrlCommand*)command
{
    
    int code = [self.agoraKit leaveChannel:nil];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }

}

- (void) enableVideo:(CDVInvokedUrlCommand*)command
{
    int code = [self.agoraKit enableVideo];
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) disableVideo:(CDVInvokedUrlCommand *)command {
    int code = [self.agoraKit disableVideo];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) enableSpeakerphone:(CDVInvokedUrlCommand *)command {
    int code = [self.agoraKit setEnableSpeakerphone:true];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) disableSpeakerphone:(CDVInvokedUrlCommand *)command {
    int code = [self.agoraKit setEnableSpeakerphone:false];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) getCallId:(CDVInvokedUrlCommand *)command {
    NSString* callId = [self.agoraKit getCallId];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:callId];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) startRecordingService:(CDVInvokedUrlCommand *)command {
    
    NSString* recordingKey = [[command arguments] objectAtIndex:0];
    int code = [self.agoraKit startRecordingService:recordingKey];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) stopRecordingService:(CDVInvokedUrlCommand *)command {
    
    NSString* recordingKey = [[command arguments] objectAtIndex:0];
    int code = [self.agoraKit stopRecordingService:recordingKey];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) enableAudioVolumeIndication:(CDVInvokedUrlCommand *)command {
    NSNumber* interval = [[command arguments] objectAtIndex:0];
    NSNumber* smooth = [[command arguments] objectAtIndex:1];
    
    int code = [self.agoraKit enableAudioVolumeIndication:[interval unsignedIntegerValue] smooth:[smooth unsignedIntegerValue]];

    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) enableAudio:(CDVInvokedUrlCommand *)command {
    int code = [self.agoraKit enableAudio];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
    
}

- (void) disableAudio:(CDVInvokedUrlCommand *)command {
    int code = [self.agoraKit disableAudio];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) setLocalVoicePitch:(CDVInvokedUrlCommand *)command {
    NSNumber* pitchInt = [[command arguments] objectAtIndex:0];
    double pitch;
    NSInteger value = [pitchInt integerValue];
    
    if (value < 50 || value > 200) {
        pitch = value / 100;
    } else {
        pitch = 1.0;
    }
    
    int code = [self.agoraKit setLocalVoicePitch:pitch];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) setAudioProfile:(CDVInvokedUrlCommand *)command {
    NSNumber* profileNumber = [[command arguments] objectAtIndex:0];
    NSNumber* scenarioNumber = [[command arguments] objectAtIndex:1];
    
    AgoraRtcAudioProfile profile = [profileNumber integerValue];
    AgoraRtcAudioScenario scenario = [scenarioNumber integerValue];
    
    int code = [self.agoraKit setAudioProfile:profile scenario:scenario];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) createRendererView:(CDVInvokedUrlCommand *)command {
    _localView = [[UIView alloc] init];
    _remoteView = [[UIView alloc] init];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) setVideoProfile:(CDVInvokedUrlCommand *)command {
    NSNumber* profileNumber = [[command arguments] objectAtIndex:0];
    NSNumber* swapWidthAndHeight = [[command arguments] objectAtIndex:1];
    
    AgoraRtcVideoProfile profile = [profileNumber integerValue];
    Boolean swap = [swapWidthAndHeight boolValue];      // Todo: bool
    
    int code = [self.agoraKit setVideoProfile:profile swapWidthAndHeight:swap];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) setupLocalVideo:(CDVInvokedUrlCommand *)command {
//    CGRect viewRect = CGRectMake(10, 10, 320, 180);
//    UIImage* image = [[UIImage alloc] initWithContentsOfFile:@"/Users/panxiuqing/Desktop/icon_voice.png"];
//    UIImageView* view = [[UIImageView alloc] initWithImage:image];
//
//    [_localView addSubview:view];
    
    AgoraRtcVideoCanvas* canvas = [[AgoraRtcVideoCanvas alloc] init];
    canvas.view = _localView;
    canvas.renderMode = AgoraRtc_Render_Fit;
    canvas.uid = 1;
    
    [self.viewController.view addSubview:_localView];
//    [self.webView.superview addSubview:_localView];
//    [self.webView.superview bringSubviewToFront:_localView];
    
    int code = [self.agoraKit setupLocalVideo:canvas];
//    int code = 0;
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
    
}

- (void) setupRemoteView:(CDVInvokedUrlCommand *)command {
    CGRect viewRect = CGRectMake(340, 10, 320, 180);
    UIView* remoteView = [[UIView alloc] initWithFrame:viewRect];
    
    AgoraRtcVideoCanvas* canvas = [[AgoraRtcVideoCanvas alloc] init];
    canvas.view = remoteView;
    canvas.renderMode = AgoraRtc_Render_Fit;
    canvas.uid = 1;
    
    [self.viewController.view addSubview:remoteView];
    
    int code = [self.agoraKit setupRemoteVideo:canvas];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

// Todo: setVideoQualityParameters

- (void) setVideoQualityParameters:(CDVInvokedUrlCommand *)command {
    NSNumber* prefer = [[command arguments] objectAtIndex:0];
    
    int code = [self.agoraKit setVideoQualityParameters:[prefer boolValue]];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) startPreview:(CDVInvokedUrlCommand *)command {
    int code = [self.agoraKit startPreview];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) stopPreview:(CDVInvokedUrlCommand *)command {
    int code = [self.agoraKit stopPreview];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) setLocalRenderMode:(CDVInvokedUrlCommand *)command {
    NSNumber* modeNumber = [[command arguments] objectAtIndex:0];
    
    AgoraRtcRenderMode renderMode = [modeNumber integerValue];
    
    int code = [self.agoraKit setLocalRenderMode:renderMode];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) setRemoteRenderMode:(CDVInvokedUrlCommand *)command {
    NSNumber* modeNumber = [[command arguments] objectAtIndex:0];
    
    AgoraRtcRenderMode renderMode = [modeNumber integerValue];
    
    int code = [self.agoraKit setLocalRenderMode:renderMode];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) switchCamera:(CDVInvokedUrlCommand *)command {
    int code = [self.agoraKit switchCamera];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) muteLocalVideoStream:(CDVInvokedUrlCommand *)command {
    NSNumber* muted = [[command arguments] objectAtIndex:0];
    
    int code = [self.agoraKit muteLocalVideoStream:[muted boolValue]];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) muteAllRemoteVideoStreams:(CDVInvokedUrlCommand *)command {
    NSNumber* muted = [[command arguments] objectAtIndex:0];
    
    int code = [self.agoraKit muteAllRemoteVideoStreams:[muted boolValue]];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) muteRemoteVideoStream:(CDVInvokedUrlCommand *)command {
    NSNumber* uid = [[command arguments] objectAtIndex:0];
    NSNumber* muted = [[command arguments] objectAtIndex:1];
    
    int code = [self.agoraKit muteRemoteVideoStream:[uid unsignedIntegerValue] mute:[muted boolValue]];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) rate:(CDVInvokedUrlCommand*)command {
    NSString* callId = [[command arguments] objectAtIndex:0];
    NSNumber* rating = [[command arguments] objectAtIndex:1];
    NSString* description = [[command arguments] objectAtIndex:2];
    
    int code = [self.agoraKit rate:callId rating:[rating integerValue] description:description];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) complain:(CDVInvokedUrlCommand *)command {
    NSString* callId = [[command arguments] objectAtIndex:0];
    NSString* description = [[command arguments] objectAtIndex:1];
    
    int code = [self.agoraKit complain:callId description:description];
    
    if(code == 0) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:code];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) setLocalVideoPosition:(CDVInvokedUrlCommand *)command {
    NSDictionary* position = [[command arguments] objectAtIndex:0];
    
    NSInteger x = [[position objectForKey:@"x"] integerValue];
    NSInteger y = [[position objectForKey:@"y"] integerValue];
    NSInteger width = [[position objectForKey:@"width"] integerValue];
    NSInteger height = [[position objectForKey:@"height"] integerValue];
    BOOL zIndexTop = [[position objectForKey:@"zIndexTop"] boolValue];
    
    CGRect frame = CGRectMake(x, y, width, height);
    
    [_localView setFrame:frame];
    if (zIndexTop == true) {
        [self.viewController.view bringSubviewToFront:_localView];
    } else {
        [self.viewController.view sendSubviewToBack:_localView];
    }
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) setRemoteVideoPosition:(CDVInvokedUrlCommand *)command {
    NSDictionary* position = [[command arguments] objectAtIndex:0];
    
    NSInteger x = [[position objectForKey:@"x"] integerValue];
    NSInteger y = [[position objectForKey:@"y"] integerValue];
    NSInteger width = [[position objectForKey:@"width"] integerValue];
    NSInteger height = [[position objectForKey:@"height"] integerValue];
    BOOL zIndexTop = [[position objectForKey:@"zIndexTop"] boolValue];
    
    CGRect frame = CGRectMake(x, y, width, height);
    
    [_remoteView setFrame:frame];
    if (zIndexTop == true) {
        [self.viewController.view bringSubviewToFront:_remoteView];
    } else {
        [self.viewController.view sendSubviewToBack:_remoteView];
    }
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) setWebViewPosition:(CDVInvokedUrlCommand *)command {
    NSDictionary* position = [[command arguments] objectAtIndex:0];
    
    NSInteger x = [[position objectForKey:@"x"] integerValue];
    NSInteger y = [[position objectForKey:@"y"] integerValue];
    NSInteger width = [[position objectForKey:@"width"] integerValue];
    NSInteger height = [[position objectForKey:@"height"] integerValue];
    BOOL zIndexTop = [[position objectForKey:@"zIndexTop"] boolValue];
    
    CGRect frame = CGRectMake(x, y, width, height);
    
    [self.webView setFrame:frame];
    if (zIndexTop == true) {
        [self.viewController.view bringSubviewToFront:self.webView];
    } else {
        [self.viewController.view sendSubviewToBack:self.webView];
    }
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

-(void) listenEvents:(CDVInvokedUrlCommand *)command
{
    NSString* callbackId = [command callbackId];
    void (^handler)(NSDictionary*) = ^(NSDictionary* data) {
        CDVPluginResult* res = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:data];
        [res setKeepCallbackAsBool:YES];
        [self.commandDelegate sendPluginResult:res callbackId:callbackId];
    };
    __eventHandler = handler;
}

- (void) notifyEvent:(NSString*)event :(NSDictionary*)data
{
    NSDictionary *dict = @{@"event" : event, @"data" : data};
    __eventHandler(dict);
}


- (void) getClipboardContent:(CDVInvokedUrlCommand*)command {
    UIPasteboard* pasteboard = [UIPasteboard generalPasteboard];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:pasteboard.string];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)onReset {
    if(self.agoraKit != nil) {
        [AgoraRtcEngineKit destroy];
        self.agoraKit = nil;
    }
}


@end

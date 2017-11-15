//
//  Agora.h
//  YiYouMicroClient
//
//  Created by DTG on 2017/6/24.
//
//

#ifndef Agora_h
#define Agora_h

#import <Cordova/CDVPlugin.h>
#import <AgoraRtcEngineKit/AgoraRtcEngineKit.h>

@interface AgoraPlugin : CDVPlugin <AgoraRtcEngineDelegate>
@property (nonatomic, retain) NSString *key;
@property (strong, nonatomic) AgoraRtcEngineKit *agoraKit;
@property (nonatomic, retain) void (^_eventHandler)(NSDictionary* data);
@property (nonatomic, retain) UIView* localView;
@property (nonatomic, retain) UIView* remoteView;

- (void) create:(CDVInvokedUrlCommand*)command;
- (void) joinChannel:(CDVInvokedUrlCommand*)command;
- (void) leaveChannel:(CDVInvokedUrlCommand *)command;
- (void) enableVideo:(CDVInvokedUrlCommand *)command;
- (void) disableVideo:(CDVInvokedUrlCommand *)command;
- (void) enableSpeakerphone:(CDVInvokedUrlCommand*)command;
- (void) disableSpeakerphone:(CDVInvokedUrlCommand*)command;
- (void) getCallId:(CDVInvokedUrlCommand*)command;
- (void) startRecordingService:(CDVInvokedUrlCommand *)command;
- (void) stopRecordingService:(CDVInvokedUrlCommand *)command;
- (void) enableAudioVolumeIndication:(CDVInvokedUrlCommand *)command;
- (void) enableAudio:(CDVInvokedUrlCommand*)command;
- (void) disableAudio:(CDVInvokedUrlCommand*)command;
- (void) setLocalVoicePitch:(CDVInvokedUrlCommand*)command;
- (void) setAudioProfile:(CDVInvokedUrlCommand*)command;
- (void) createRenderView:(CDVInvokedUrlCommand*)command;
- (void) setVideoProfile:(CDVInvokedUrlCommand*)command;
- (void) setupLocalVideo:(CDVInvokedUrlCommand*)command;
- (void) setupRemoteView:(CDVInvokedUrlCommand*)command;
- (void) setVideoQualityParameters:(CDVInvokedUrlCommand*)command;
- (void) startPreview:(CDVInvokedUrlCommand*)command;
- (void) stopPreview:(CDVInvokedUrlCommand*)command;
- (void) setLocalRenderMode:(CDVInvokedUrlCommand*)command;
- (void) setRemoteRenderMode:(CDVInvokedUrlCommand*)command;
- (void) switchCamera:(CDVInvokedUrlCommand*)command;
- (void) muteLocalVideoStream:(CDVInvokedUrlCommand*)command;
- (void) muteAllRemoteVideoStreams:(CDVInvokedUrlCommand*)command;
- (void) muteRemoteVideoStream:(CDVInvokedUrlCommand*)command;
- (void) rate:(CDVInvokedUrlCommand*)command;
- (void) complain:(CDVInvokedUrlCommand*)command;

- (void) setLocalVideoPosition:(CDVInvokedUrlCommand*)command;
- (void) setRemoteVideoPosition:(CDVInvokedUrlCommand*)command;
- (void) setWebViewPosition:(CDVInvokedUrlCommand*)command;

- (void) listenEvents:(CDVInvokedUrlCommand *)command;
- (void) notifyEvent:(NSString*)event :(NSDictionary*)data;
- (void) getClipboardContent:(CDVInvokedUrlCommand*)command;



@end

#endif /* Agora_h */

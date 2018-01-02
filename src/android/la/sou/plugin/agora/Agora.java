package la.sou.plugin.agora;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import io.agora.rtc.*;
import io.agora.rtc.video.VideoCanvas;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Agora extends CordovaPlugin {

    public static final String TAG = "CDVAgora";

    protected Activity appActivity;

    protected Context appContext;

    protected FrameLayout webView;
    protected FrameLayout appLayout;
    protected SurfaceView surfaceViewLocal;
    protected SurfaceView surfaceViewRemote;

    private static CallbackContext eventCallbackContext;

    @Override
    protected void pluginInitialize() {

        appContext = this.cordova.getActivity().getApplicationContext();
        appActivity = cordova.getActivity();
        webView = appActivity.findViewById(100);
        appLayout = (FrameLayout) webView.getParent();
        super.pluginInitialize();
    }


//    @Override
//    public void onNewIntent(Intent intent) {
//        //RtcEngine.destroy();
//    }

    /**
     * Called when the WebView does a top-level navigation or refreshes.
     *
     * Plugins should stop any long-running processes and clean up internal state.
     *
     * Does nothing by default.
     */
    @Override
    public void onReset() {
        RtcEngine.destroy();
    }

    @Override
    public void onDestroy() {
        RtcEngine.destroy();
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        Log.d(TAG, action + " Called");

        if (action.equals("prepare")) {
            return true;
        }

        if (action.equals("create")) {
            try {
                JSONObject config = args.getJSONObject(0);
                if(!config.has("appId")) {
                    callbackContext.error(ClientError.Build(ClientError.ERR_PARAMETER_ERROR, "配置项中需要 appId。"));
                } else {

                    final String appId = config.getString("appId");
                    final Context context = this.cordova.getActivity().getApplicationContext();
                    appActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AgoraClient.Create(appId, context);
							
                            //禁止视频
                            AgoraClient.getInstance().getRtcEngine().disableVideo();
                            AgoraClient.getInstance().getRtcEngine().setDefaultAudioRoutetoSpeakerphone(true);
                            callbackContext.success();
                        }
                    });
                }
            } catch (Exception e) {
                callbackContext.error(ClientError.Build(ClientError.ERR_PARAMETER_ERROR, "第一个参数必须是对象。"));

            }
            return true;
        }

        if (action.equals("enableAudio")) {
            AgoraClient.getInstance().getRtcEngine().enableAudio();
            return true;
        }

        if (action.equals("disableAudio")) {
            AgoraClient.getInstance().getRtcEngine().disableAudio();
            return true;
        }

        if (action.equals("joinChannel")) {
            final String channelKey = args.getString(0);
            final String channelName = args.getString(1);
            final int uid = args.getInt(2);
            appActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AgoraClient.getInstance().getRtcEngine()
                            .joinChannel(channelKey, channelName, null, uid);
                    callbackContext.success();
                }
            });
            return true;
        }

        if (action.equals("enableAudioVolumeIndication")) {
            final int interval = args.getInt(0);
            final int smooth = args.getInt(1);
            int result = AgoraClient.getInstance().getRtcEngine().enableAudioVolumeIndication(interval, smooth);
            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec enableAudioVolumeIndication failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if (action.equals("leaveChannel")) {
            int result =  AgoraClient.getInstance().getRtcEngine().leaveChannel();

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec leaveChannel failed!"));
            } else {
                callbackContext.success();
            }

            return true;
        }

        if (action.equals("setLocalVoicePitch")) {
            final int pitchInt = args.getInt(0);
            final double pitch;
            if (pitchInt < 50 || pitchInt > 200) {
                pitch = 1.0;
            } else {
                pitch = pitchInt / 100;
            }

            int result =  AgoraClient.getInstance().getRtcEngine().setLocalVoicePitch(pitch);
            
            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec setLocalVoicePicth failed!"));
            } else {
                callbackContext.success();
            }

            return true;
        }

        // Todo: set audio profile
        // startEchoTest | stopEchoTest
        // enableLastmileTest | disableLastmileTest
        if (action.equals("setAudioProfile")) {
            final int profile = args.getInt(0);
            final int scenario = args.getInt(1);

            int result = AgoraClient.getInstance().getRtcEngine().setAudioProfile(profile, scenario);

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec setAudioProfile failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if (action.equals("createRendererView")) {
            appActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    surfaceViewLocal = AgoraClient.getInstance().getRtcEngine()
                            .CreateRendererView(appContext);
                    surfaceViewRemote = AgoraClient.getInstance().getRtcEngine()
                            .CreateRendererView(appContext);

                    surfaceViewRemote.setZOrderOnTop(true);
                    surfaceViewLocal.setZOrderOnTop(true);

                    if (surfaceViewLocal != null && surfaceViewRemote != null) {
                        appLayout.addView(surfaceViewLocal);
                        appLayout.addView(surfaceViewRemote);
                        callbackContext.success();
                    }
                }
            });
            return true;
        }

        if (action.equals("setWebViewPosition")) {
            try {
                JSONObject position = args.getJSONObject(0);
                final int x = position.getInt("x");
                final int y = position.getInt("y");
                final int width = position.getInt("width");
                final int height = position.getInt("height");
                appActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
                        params.leftMargin = x;
                        params.topMargin = y;
                        webView.setLayoutParams(params);
                        callbackContext.success();
                    }
                });
            } catch (Exception e) {
                callbackContext.error(ClientError.Build(ClientError.ERR_PARAMETER_ERROR, "设置local view位置错误。"));
            }
            return true;
        }

        if (action.equals("setLocalVideoPosition")) {
            try {
                JSONObject position = args.getJSONObject(0);
                final int x = position.getInt("x");
                final int y = position.getInt("y");
                final int width = position.getInt("width");
                final int height = position.getInt("height");
                final boolean zIndexTop = position.getBoolean("zIndexTop");
                appActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
                        params.leftMargin = x;
                        params.topMargin = y;
                        surfaceViewLocal.setLayoutParams(params);
                        if (zIndexTop) {
                            appLayout.removeView(surfaceViewLocal);
                            appLayout.addView(surfaceViewLocal);
                        }
                        callbackContext.success();
                    }
                });
            } catch (Exception e) {
                callbackContext.error(ClientError.Build(ClientError.ERR_PARAMETER_ERROR, "设置local view位置错误。"));
            }
            return true;
        }

        if (action.equals("setRemoteVideoPosition")) {
            try {
                JSONObject position = args.getJSONObject(0);
                final int x = position.getInt("x");
                final int y = position.getInt("y");
                final int width = position.getInt("width");
                final int height = position.getInt("height");
                final boolean zIndexTop = position.getBoolean("zIndexTop");
                appActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
                        params.leftMargin = x;
                        params.topMargin = y;
                        surfaceViewRemote.setLayoutParams(params);
                        if (zIndexTop) {
                            appLayout.removeView(surfaceViewRemote);
                            appLayout.addView(surfaceViewRemote);
                        }
                        callbackContext.success();
                    }
                });
            } catch (Exception e) {
                callbackContext.error(ClientError.Build(ClientError.ERR_PARAMETER_ERROR, "设置remote video位置错误。"));
            }
            return true;
        }

        if (action.equals("setVideoProfile")) {
            final int profile = args.getInt(0);
            final boolean swapWidthAndHeight = args.getBoolean(1);

            int result = AgoraClient.getInstance().getRtcEngine().setVideoProfile(profile, swapWidthAndHeight);

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec setVideoProfile failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if (action.equals("setupLocalVideo")) {
            final int uid = args.getInt(0);

            VideoCanvas videoCanvas = new VideoCanvas(surfaceViewLocal, 2, uid);

            int result = AgoraClient.getInstance().getRtcEngine().setupLocalVideo(videoCanvas);

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec setupLocalVideo failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if (action.equals("setupRemoteVideo")) {
            final int uid = args.getInt(0);

            VideoCanvas videoCanvas = new VideoCanvas(surfaceViewRemote, 2, uid);

            int result = AgoraClient.getInstance().getRtcEngine().setupRemoteVideo(videoCanvas);

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec setupRemoteVideo failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if (action.equals("disableVideo")) {
            int result =  AgoraClient.getInstance().getRtcEngine().disableVideo();

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec disableVideo failed!"));
            } else {
                callbackContext.success();
            }

            return true;
        }

        if (action.equals("enableVideo")) {
            int result =  AgoraClient.getInstance().getRtcEngine().enableVideo();

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec enableVideo failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if (action.equals("setVideoQualityParameters")) {
            final boolean prefer = args.getBoolean(0);

            int result =  AgoraClient.getInstance().getRtcEngine().setVideoQualityParameters(prefer);
            
            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec setVideoQualityParameters failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if (action.equals("startPreview")) {
            int result =  AgoraClient.getInstance().getRtcEngine().startPreview();

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec startPreview failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if (action.equals("stopPreview")) {
            int result =  AgoraClient.getInstance().getRtcEngine().stopPreview();

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec stopPreview failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if (action.equals("setLocalRenderMode")) {
            return true;
        }

        if (action.equals("setRemoteRenderMode")) {
            return true;
        }

        if (action.equals("switchCamera")) {
            int result =  AgoraClient.getInstance().getRtcEngine().switchCamera();
            
            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec switchCamera failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if (action.equals("muteLocalVideoStream")) {
            final boolean muted = args.getBoolean(0);

            int result =  AgoraClient.getInstance().getRtcEngine().muteLocalVideoStream(muted);
            
            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec muteLocalVideoStream failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if (action.equals("muteAllRemoteVideoStream")) {
            final boolean muted = args.getBoolean(0);

            int result =  AgoraClient.getInstance().getRtcEngine().muteAllRemoteVideoStreams(muted);
            
            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec muteAllRemoteVideoStream failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if (action.equals("muteRemoteVideoStream")) {
            final int uid = args.getInt(0);
            final boolean muted = args.getBoolean(1);

            int result =  AgoraClient.getInstance().getRtcEngine().muteRemoteVideoStream(uid, muted);
            
            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec muteRemoteVideoStream failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if (action.equals("rate")) {
            final String callId = args.getString(0);
            final int rating = args.getInt(1);
            final String description = args.getString(2);
            
            int result = AgoraClient.getInstance().getRtcEngine().rate(callId, rating, description);

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec rate failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if (action.equals("complain")) {
            final String callId = args.getString(0);
            final String description = args.getString(1);
            
            int result = AgoraClient.getInstance().getRtcEngine().complain(callId, description);

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec complain failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if (action.equals("enableSpeakerphone")) {
            int result =  AgoraClient.getInstance().getRtcEngine().setEnableSpeakerphone(true);

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec setEnableSpeakerphone failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if(action.equals("disableSpeakerphone")) {

            int result =  AgoraClient.getInstance().getRtcEngine().setEnableSpeakerphone(false);

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec setEnableSpeakerphone failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if(action.equals("startRecordingService")) {

            final String recordKey = args.getString(0);
            int result =  AgoraClient.getInstance().getRtcEngine().startRecordingService(recordKey);

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec startServerRecord failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if(action.equals("stopRecordingService")) {

            final String recordKey = args.getString(0);
            int result =  AgoraClient.getInstance().getRtcEngine().stopRecordingService(recordKey);

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec stopServerRecord failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if(action.equals("getCallId")) {
            String result =  AgoraClient.getInstance().getRtcEngine().getCallId();
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        }

        if (action.equals("listenEvents")) {
            eventCallbackContext = callbackContext;
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, 0);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        }

        return super.execute(action, args, callbackContext);
    }

    public static void notifyEvent(String event, JSONObject data) {

        JSONObject json = new JSONObject();

        try {
            json.put("event", event);
            json.put("data", data);
        } catch (JSONException ignored) {

        }

        if (eventCallbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, json);
            result.setKeepCallback(true);
            eventCallbackContext.sendPluginResult(result);
        }
    }
}

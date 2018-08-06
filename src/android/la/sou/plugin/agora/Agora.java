package la.sou.plugin.agora;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.view.ViewGroup;
import android.view.Gravity;

import io.agora.rtc.*;
import io.agora.rtc.video.VideoCanvas;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class Agora extends CordovaPlugin {

    public static final String TAG = "CDVAgora";

    protected Activity appActivity;

    protected Context appContext;

    protected FrameLayout webView;
    protected FrameLayout appLayout;
    private SurfaceView surfaceViewLocal;
    private SurfaceView surfaceViewRemote;

    private RtcEngine mRtcEngine;

    private static CallbackContext eventCallbackContext;

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private static final int PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1;



    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i("agora", "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(appContext,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(appActivity,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView1) {
        super.initialize(cordova, webView1);
        Log.d("agora", "pluginInitialize...");
        appContext = cordova.getActivity().getApplicationContext();
        appActivity = cordova.getActivity();
        // webView = appActivity.findViewById(100);
        // appLayout = (FrameLayout) webView.getParent();
        // your init code here
    }

    // @Override
    // protected void pluginInitialize() {

    //     appContext = this.cordova.getActivity().getApplicationContext();
    //     appActivity = cordova.getActivity();
    //     webView = (FrameLayout) appActivity.findViewById(100);
    //     appLayout = (FrameLayout) webView.getParent();
    //     super.pluginInitialize();
    // }


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

        Log.d(TAG, action + " agora Called");

        if (action.equals("prepare")) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
                Log.d(TAG, action + " permission approved");
            }
            return true;
        }

        if (action.equals("create")) {
            try {
                JSONObject config = args.getJSONObject(0);
                if(!config.has("appId")) {
                    callbackContext.error(ClientError.Build(ClientError.ERR_PARAMETER_ERROR, "配置项中需要 appId。"));
                } else {

                    final String appId = config.getString("appId");
                    Log.d(TAG, "appID: " + appId);
//                    final Context context = this.cordova.getActivity().getApplicationContext();
                    appActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            initAgoraEngineAndJoinChannel(appId);
//                            AgoraClient.Create(appId, appContext);
//
                            //禁止视频
//                            AgoraClient.getInstance().getRtcEngine().disableVideo();
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
            mRtcEngine.enableAudio();
            return true;
        }

        if (action.equals("disableAudio")) {
            mRtcEngine.disableAudio();
            return true;
        }

        if (action.equals("joinChannel")) {
            final String channelKey = args.getString(0);
            final String channelName = args.getString(1);
            final int uid = args.getInt(2);
            appActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    joinChannel(channelName);
//                    AgoraClient.getInstance().getRtcEngine()
//                            .joinChannel(channelKey, channelName, null, uid);
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
//            int result =  AgoraClient.getInstance().getRtcEngine().leaveChannel();
//
//            if(AgoraError.ERR_OK != result) {
//                callbackContext.error(ClientError.Build(result, "exec leaveChannel failed!"));
//            } else {
//                callbackContext.success();
//            }
            appActivity.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                  leaveChannel();
              }});

            callbackContext.success();
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

//        if (action.equals("createRendererView")) {
//            appActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                    AgoraClient.getInstance().getRtcEngine().enableVideo();
//                    AgoraClient.getInstance().getRtcEngine().setVideoProfile(30, false);
//                    AgoraClient.getInstance().getRtcEngine().setDefaultAudioRoutetoSpeakerphone(true);
//
//                    surfaceViewLocal = AgoraClient.getInstance().getRtcEngine()
//                            .CreateRendererView(appContext);
//                    surfaceViewRemote = AgoraClient.getInstance().getRtcEngine()
//                            .CreateRendererView(appContext);
//
//                    surfaceViewRemote.setZOrderOnTop(true);
//                    surfaceViewLocal.setZOrderOnTop(true);
//
//
//                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(400, 600);
//                    params.leftMargin = 50;
//                    params.topMargin = 50;
//
//                    FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(
//                            ViewGroup.LayoutParams.MATCH_PARENT,
//                            ViewGroup.LayoutParams.MATCH_PARENT,
//                            Gravity.CENTER);
//                    params1.bottomMargin = 200;
//
//                    if (surfaceViewLocal != null && surfaceViewRemote != null) {
//                        surfaceViewRemote.setLayoutParams(params1);
//                        surfaceViewLocal.setLayoutParams(params);
//                        appActivity.addContentView(surfaceViewRemote, params1);
//                        appActivity.addContentView(surfaceViewLocal, params);
//
//                    VideoCanvas videoCanvas = new VideoCanvas(surfaceViewLocal, 2, 0);
//
//                    AgoraClient.getInstance().getRtcEngine().setupLocalVideo(videoCanvas);
//
//                        callbackContext.success();
//                    }
//                }
//            });
//            return true;
//        }

//        if (action.equals("setWebViewPosition")) {
//            try {
//                JSONObject position = args.getJSONObject(0);
//                final int x = position.getInt("x");
//                final int y = position.getInt("y");
//                final int width = position.getInt("width");
//                final int height = position.getInt("height");
//                final boolean zIndexTop = position.getBoolean("zIndexTop");
//                appActivity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
//                        params.leftMargin = x;
//                        params.topMargin = y;
//                        webView.setLayoutParams(params);
//                        if (zIndexTop) {
//                            appLayout.removeView(webView);
//                            appLayout.addView(webView);
//                        }
//                        callbackContext.success();
//                    }
//                });
//            } catch (Exception e) {
//                callbackContext.error(ClientError.Build(ClientError.ERR_PARAMETER_ERROR, "设置local view位置错误。"));
//            }
//            return true;
//        }

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
                            try {
                                ((ViewGroup) surfaceViewLocal.getParent()).removeView(surfaceViewLocal);
                            } catch (Exception e) {
                                Log.e("removeAllVideo", "surfaceViewLocal");
                            }
                            appActivity.addContentView(surfaceViewLocal, params);
//                            appLayout.removeView(surfaceViewLocal);
//                            appLayout.addView(surfaceViewLocal);
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
//                            appLayout.removeView(surfaceViewRemote);
//                            appLayout.addView(surfaceViewRemote);
                            try {
                                ((ViewGroup) surfaceViewRemote.getParent()).removeView(surfaceViewRemote);
                            } catch (Exception e) {
                                Log.e("removeAllVideo", "surfaceViewRemote");
                            }

                            appActivity.addContentView(surfaceViewRemote, params);
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
//            final int uid = args.getInt(0);

            setupLocalVideo();

            callbackContext.success();
            return true;
        }

        if (action.equals("setupRemoteVideo")) {
            final int uid = args.getInt(0);

            setupRemoteVideo(uid);
            callbackContext.success();
            return true;
        }

        if (action.equals("disableVideo")) {
            int result =  mRtcEngine.disableVideo();

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec disableVideo failed!"));
            } else {
                callbackContext.success();
            }

            return true;
        }

        if (action.equals("enableVideo")) {
            int result =  mRtcEngine.enableVideo();

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
            int result =  mRtcEngine.switchCamera();
            
            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec switchCamera failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if (action.equals("muteLocalVideoStream")) {
            final boolean muted = args.getBoolean(0);

            int result =  mRtcEngine.muteLocalVideoStream(muted);
            
            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec muteLocalVideoStream failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if (action.equals("muteAllRemoteVideoStream")) {
            final boolean muted = args.getBoolean(0);

            int result =  mRtcEngine.muteAllRemoteVideoStreams(muted);
            
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

            int result =  mRtcEngine.muteRemoteVideoStream(uid, muted);
            
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
            
            int result = mRtcEngine.complain(callId, description);

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec complain failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if (action.equals("enableSpeakerphone")) {
            int result =  mRtcEngine.setEnableSpeakerphone(true);

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec setEnableSpeakerphone failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if(action.equals("disableSpeakerphone")) {

            int result =  mRtcEngine.setEnableSpeakerphone(false);

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec setEnableSpeakerphone failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if(action.equals("startRecordingService")) {

            final String recordKey = args.getString(0);
            int result =  mRtcEngine.startRecordingService(recordKey);

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec startServerRecord failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if(action.equals("stopRecordingService")) {

            final String recordKey = args.getString(0);
            int result =  mRtcEngine.stopRecordingService(recordKey);

            if(AgoraError.ERR_OK != result) {
                callbackContext.error(ClientError.Build(result, "exec stopServerRecord failed!"));
            } else {
                callbackContext.success();
            }
            return true;
        }

        if(action.equals("getCallId")) {
            String result =  mRtcEngine.getCallId();
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


//    private RtcEngine mRtcEngine;// Tutorial Step 1

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) { // Tutorial Step 5
            appActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) { // Tutorial Step 7
            appActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
        }

//        @Override
//        public void onUserMuteVideo(final int uid, final boolean muted) { // Tutorial Step 10
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    onRemoteUserVideoMuted(uid, muted);
//                }
//            });
//        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            Log.d("onJoinChannelSuccess", channel + " " + uid);
            try {
                JSONObject data = new JSONObject();
                data.put("channel", channel);
                data.put("uid", uid);
                data.put("elapsed", elapsed);
                Log.d("MessageHandler",   "onJoinChannelSuccess");
                Agora.notifyEvent("onJoinChannelSuccess", data);
                //mRtcEngine.setEnableSpeakerphone(true);
            } catch (JSONException ignored) {}
        }

    };


    private void initAgoraEngineAndJoinChannel(String appId) {
        initializeAgoraEngine(appId);     // Tutorial Step 1
        setupVideoProfile();         // Tutorial Step 2
        setupLocalVideo();           // Tutorial Step 3
    }

    // Tutorial Step 1
    private void initializeAgoraEngine(String appId) {
        try {
            mRtcEngine = RtcEngine.create(appContext, appId, mRtcEventHandler);
        } catch (Exception e) {
            Log.e("Agora", Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    // Tutorial Step 2
    private void setupVideoProfile() {
        mRtcEngine.enableVideo();
        mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P, false);
    }

    // Tutorial Step 3
    private void setupLocalVideo() {
        //local

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(400, 600);
        params.leftMargin = 50;
        params.topMargin = 50;

        surfaceViewLocal = RtcEngine.CreateRendererView(appContext);
        surfaceViewLocal.setZOrderMediaOverlay(true);

        appActivity.addContentView(surfaceViewLocal, params);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceViewLocal, VideoCanvas.RENDER_MODE_ADAPTIVE, 0));
    }

    // Tutorial Step 4
    private void joinChannel(String channelName) {
        mRtcEngine.joinChannel(null, channelName, null, 0); // if you do not specify the uid, we will generate the uid for you
    }


    // Tutorial Step 5
    private void setupRemoteVideo(int uid) {
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        params1.bottomMargin = 200;


        surfaceViewRemote = RtcEngine.CreateRendererView(appContext);
//        surfaceViewRemote.setZOrderMediaOverlay(true);
        appActivity.addContentView(surfaceViewRemote, params1);


//        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceViewRemote, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));

        surfaceViewRemote.setTag(uid); // for mark purpose

    }

    // Tutorial Step 6
    private void leaveChannel() {
        mRtcEngine.leaveChannel();
        RtcEngine.destroy();
        removeAllVideo();
    }

    private void removeAllVideo() {

        Log.d("removeAllVideo", surfaceViewLocal.toString());

        try {
            ((ViewGroup) surfaceViewRemote.getParent()).removeView(surfaceViewRemote);
        } catch (Exception e) {
            Log.e("removeAllVideo", "surfaceViewRemote");
        }

        try {
            ((ViewGroup) surfaceViewLocal.getParent()).removeView(surfaceViewLocal);
        } catch (Exception e) {
            Log.e("removeAllVideo", "surfaceViewLocal");
        }

    }

    private void onRemoteUserLeft() {
        Log.d("onRemoteUserLeft", "onRemoteUserLeft");
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

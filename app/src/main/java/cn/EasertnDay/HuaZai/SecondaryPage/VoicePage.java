package cn.EasertnDay.HuaZai.SecondaryPage;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;
import cn.EasertnDay.HuaZai.AudioSourceType;
import cn.EasertnDay.HuaZai.Config;
import cn.EasertnDay.HuaZai.SdkParam;
import cn.EasertnDay.HuaZai.VoicePresenter;
import cn.EasternDay.HuaZai.R;
import com.unisound.sdk.asr.AsrEvent;
import com.unisound.sdk.asr.UnisoundAsrEngine;
import com.unisound.sdk.asr.impl.IAsrResultListener;
import com.unisound.sdk.tts.TtsOption;
import com.unisound.sdk.tts.UnisoundTtsEngine;
import com.unisound.sdk.tts.audiotrack.AndroidAudioTrack;
import com.unisound.sdk.tts.param.UnisoundTtsPlayMode;
import com.unisound.sdk.utils.AssetsUtils;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VoicePage extends AppCompatActivity implements IAsrResultListener {

    Activity context = this;

    //显示文本
    TextView Say, Ans;

    //语音识别需要的
    private UnisoundAsrEngine unisoundAsrEngine;
    private ToneGenerator beeper;
    private HashSet<String> wakeUpList = new HashSet<>();
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 22) {
                String model = (String) msg.obj;
                Ans.setText(model);
            }
            unisoundAsrEngine.startWakeUp();
        }
    };

    //语音合成需要的
    private UnisoundTtsEngine unisoundTtsEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_page);

        Say = findViewById(R.id.QuestionText);
        Ans = findViewById(R.id.AnwserText);

        WebView webView = findViewById(R.id.WebView);
        webView.loadUrl("http://baidu.com");
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });

        //哔声发声器
        beeper = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        //语音
        unisoundAsrEngine = VoicePresenter.getInstance().getUnisoundAsrEngine();
        VoicePresenter.getInstance().setAsrListener((IAsrResultListener) context);

        if (unisoundAsrEngine.startWakeUp()) {
            unisoundAsrEngine.startAsr(false);
            Toast.makeText(context, "唤醒成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "唤醒失败", Toast.LENGTH_SHORT).show();
        }

        //语音合成
        unisoundTtsEngine = VoicePresenter.getInstance().getUnisoundTtsEngine();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        copyAssetsModel();
        unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_PRINT_DEBUG_LOG, false);
        unisoundTtsEngine.setTtsOption(TtsOption.TTS_PLAY_MODE, UnisoundTtsPlayMode.ONLINE);
        unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_SPEED, 75);
        unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_VOLUME, 1000);
        unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_BRIGHT, 50);
        unisoundTtsEngine.setTtsOption(TtsOption.TTS_SAMPLE_RATE, 16000);
        unisoundTtsEngine.setAudioTrack(new AndroidAudioTrack(16000));
        unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_FRONT_SIL, 50);
        unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_BACK_SIL, 50);
        unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_VOICE_NAME, "tangtang");
        unisoundTtsEngine.playTts("小朋友请在这里向我提问吧~");
    }

    //设置按钮点击监听（全部）
    public void onClick(View v) {
        Intent myIntent;
        switch (v.getId()) {
            //设置按钮的页面跳转-设置页面
            case R.id.button_return:
                finish();
                break;
            default:
                break;
        }
    }


    @Override
    public void onResult(int event, String result) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        Log.d("我带你们打", runningActivity);
        Log.d("我带你们打", String.valueOf(runningActivity.indexOf("LauncherActivity") != -1));
        Log.d("我带你们打", String.valueOf(runningActivity.indexOf("VoicePage") != -1));
        Log.d("我带你们打", String.valueOf(event == AsrEvent.ASR_EVENT_ASR_RESULT));
        Log.d("我带你们打", String.valueOf(unisoundAsrEngine.startAsr(false)));
        if (event == AsrEvent.ASR_EVENT_WAKEUP_RESULT) {
            beeper.startTone(ToneGenerator.TONE_DTMF_S, 30);
            if (SdkParam.getInstance().getAudioSourceType() == AudioSourceType.JNI) {
                if (runningActivity.indexOf("LauncherActivity") != -1) {
                    Intent myIntent = new Intent(context, VoicePage.class);
                    startActivity(myIntent);
                } else {
                    unisoundAsrEngine.startWakeUp();
                    unisoundAsrEngine.startAsr(false);
                }
            }
        }
        if (event == AsrEvent.ASR_EVENT_ASR_RESULT && runningActivity.indexOf("VoicePage") != -1) {
            Log.d("我带你们打", result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String asrResult = jsonObject.getString("asr_recongize");
                if (!TextUtils.isEmpty(asrResult)) {
                    Say.setText(asrResult);

                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                            .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
                            .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
                            .build();
                    final Request request = new Request.Builder()
                            .url("http://39.98.123.42:5000/chatbot?question=" + asrResult)
                            .get()//默认就是GET请求，可以不写
                            .build();
                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String ret = Objects.requireNonNull(response.body()).string();
                            Log.d("我带你们打", "返回的ret: " + ret);
                            try {
                                JSONObject resultObject = new JSONObject(ret);
                                String myAsrResult = resultObject.getString("data");
                                Log.d("我带你们打", "onResponse: " + myAsrResult);
                                if (!ret.isEmpty()) {
                                    //Say.setText(myAsrResult);
                                    handler.sendMessage(handler.obtainMessage(22, myAsrResult));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                //Say.setText("华仔听不懂，请下次再来问我吧！");
                                handler.sendMessage(handler.obtainMessage(22, "华仔听不懂，请下次再来问我吧！"));
                            }
                            unisoundAsrEngine.startWakeUp();
                            unisoundAsrEngine.startAsr(false);
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("我带你们打", "onFailure: " + e.toString());
                        }
                    });
                }
            } catch (Exception e) {
                unisoundAsrEngine.startWakeUp();
                unisoundAsrEngine.startAsr(false);
            }
        }
    }

    @Override
    public void onEvent(int event) {

    }

    @Override
    public void onError(int error) {
        unisoundAsrEngine.startWakeUp();
    }

    @Override
    public void onSessionId(String sessionId) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(null);
        unisoundAsrEngine.cancel(true);
    }

    private void copyTtsModel() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AssetManager assetManager = context.getAssets();
                try {
                    String[] files = assetManager.list("");
                    for (String file : files) {
                        if (file.startsWith("frontend") || file.startsWith("backend")) {
                            if (!(new File(Config.TTS_PATH + file).exists())) {
                                AssetsUtils.copyAssetsFile(context, file, Config.TTS_PATH + file, false);
                            }
                        } else {
                            AssetsUtils.copyAssetsFile(context, file,
                                    Environment.getExternalStorageDirectory() + File.separator + "unisound"
                                            + File.separator + file, false);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void copyAssetsModel() {
        AssetManager assetManager = this.getAssets();
        try {
            String[] files = assetManager.list("");
            for (String file : files) {
                if (file.startsWith("frontend")) {
                    if (!(new File(Config.TTS_PATH + file).exists())) {
                        AssetsUtils.copyAssetsFile(this, file, Config.TTS_PATH + file, false);
                    }
                } else if (file.startsWith("backend")) {
                    if (!(new File(Config.TTS_PATH + file).exists())) {
                        AssetsUtils.copyAssetsFile(this, file, Config.TTS_PATH + file, false);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

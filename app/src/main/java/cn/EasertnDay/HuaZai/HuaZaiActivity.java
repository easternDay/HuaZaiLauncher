package cn.EasertnDay.HuaZai;


import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.*;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.EasternDay.HuaZai.R;
import android.support.v7.app.AppCompatActivity;
import com.unisound.sdk.asr.AsrEvent;
import com.unisound.sdk.asr.AsrOption;
import com.unisound.sdk.asr.UnisoundAsrEngine;
import com.unisound.sdk.asr.impl.IAsrResultListener;
import com.unisound.sdk.utils.AssetsUtils;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author EasternDay
 */
public class HuaZaiActivity extends AppCompatActivity implements IAsrResultListener {

    private Button btnGetToken,btnDemo;
    private TextView txtToken,txtReturn,txtQuestion;
    private Context context;

    private UnisoundAsrEngine unisoundAsrEngine;
    private ToneGenerator beeper;
    private HashSet<String> wakeUpList = new HashSet<>();

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);
            unisoundAsrEngine.startWakeUp();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hua_zai);

        context = this;
        wakeUpList.add("你好华仔");
        wakeUpList.add("你好");

        btnGetToken = findViewById(R.id.getToken);
        btnDemo = findViewById(R.id.ToDemo);
        txtToken = findViewById(R.id.TokenSession);
        txtReturn = findViewById(R.id.ReturnInfo);
        txtQuestion = findViewById(R.id.Question);

        beeper = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

        btnGetToken.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                String token = (String) VoicePresenter.getInstance()
                        .getUnisoundAsrEngine()
                        .getOption(AsrOption.ASR_OPTION_DEVICE_TOKEN);
                txtToken.setText(token);

                unisoundAsrEngine = VoicePresenter.getInstance().getUnisoundAsrEngine();
                VoicePresenter.getInstance().setAsrListener((IAsrResultListener) context);
                unisoundAsrEngine.setWakeUpWord(wakeUpList, wakeUpList);

                if (unisoundAsrEngine.startWakeUp()) {
                    Toast.makeText(context, "唤醒成功", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(context, "唤醒失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDemo.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                startActivity(new Intent(context, BasicMainActivity.class));
            }
        });

        VoicePresenter.getInstance().init(this.getApplicationContext());
        copyTtsModel();
    }

    @Override public void onResult(int event, String result) {
        if (event == AsrEvent.ASR_EVENT_WAKEUP_RESULT) {
            beeper.startTone(ToneGenerator.TONE_DTMF_S, 30);
            if (SdkParam.getInstance().getAudioSourceType() == AudioSourceType.JNI) {
                unisoundAsrEngine.startAsr(false);
            }
        }
        if (event == AsrEvent.ASR_EVENT_ASR_RESULT) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                final String asrResult = jsonObject.getString("asr_recongize");
                if (!TextUtils.isEmpty(asrResult)) {
                    txtQuestion.setText(asrResult);

                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                            .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
                            .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
                            .build();
                    final Request request = new Request.Builder()
                            .url("http://39.98.123.42:5000/chatbot?question="+asrResult)
                            .get()//默认就是GET请求，可以不写
                            .build();
                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String ret = Objects.requireNonNull(response.body()).string();
                            Log.d("共产主义", "onResponse: " + ret);
                            if(!ret.contains("400")){
                                txtReturn.setText(ret);
                            }
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("共产主义", "onFailure: "+e.toString());
                        }
                    });
                    if (result.contains("nlu")) {
                        txtReturn.setText(result);
                        unisoundAsrEngine.startWakeUp();
                        unisoundAsrEngine.startAsr(false);
                    }
                }
            } catch (Exception e) {
                unisoundAsrEngine.startWakeUp();
            }
        }
    }

    @Override public void onEvent(int event) {

    }

    @Override public void onError(int error) {
        unisoundAsrEngine.startWakeUp();
    }

    @Override public void onSessionId(String sessionId) {

    }

    @Override protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(null);
        unisoundAsrEngine.cancel(true);
    }

    private void copyTtsModel() {
        new Thread(new Runnable() {
            @Override public void run() {
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
}

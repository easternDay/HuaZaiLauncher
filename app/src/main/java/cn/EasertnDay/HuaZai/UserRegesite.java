package cn.EasertnDay.HuaZai;

import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.EasertnDay.HuaZai.SecondaryPage.VoicePage;
import cn.EasternDay.HuaZai.R;
import com.unisound.sdk.asr.AsrEvent;
import com.unisound.sdk.asr.UnisoundAsrEngine;
import com.unisound.sdk.asr.impl.IAsrResultListener;
import com.unisound.sdk.tts.TtsOption;
import com.unisound.sdk.tts.UnisoundTtsEngine;
import com.unisound.sdk.tts.audiotrack.AndroidAudioTrack;
import com.unisound.sdk.tts.impl.ITtsEventListener;
import com.unisound.sdk.tts.param.UnisoundTtsPlayMode;
import com.unisound.sdk.utils.SdkLogMgr;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class UserRegesite extends AppCompatActivity {

    Activity context = this;

    String TAG = "纣纣";

    static String UserName;
    static boolean IsChinese = true;
    static int STATUS = 0;

    //语音识别需要的
    private UnisoundAsrEngine unisoundAsrEngine;
    //语音合成需要的
    private UnisoundTtsEngine unisoundTtsEngine;

    TextView Mess;
    ImageView Show;

    //语音交互
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            STATUS++;
                        }
                    }, 3000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_regesite);

        Mess = findViewById(R.id.RegisterMessage);
        Show = findViewById(R.id.FuckingShow);

        //语音识别
        unisoundAsrEngine = VoicePresenter.getInstance().getUnisoundAsrEngine();

        //语音合成
        unisoundTtsEngine = VoicePresenter.getInstance().getUnisoundTtsEngine();
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

        stopService(new Intent(UserRegesite.this, InformationService.class));
    }


    @Override
    protected void onResume() {
        super.onResume();
        //开始收声
        VoicePresenter.getInstance().setAsrListener(iAsrResultListener);
        if (unisoundAsrEngine.startWakeUp()) {
            unisoundAsrEngine.startAsr(false);
        }
    }

    //语音监听
    private IAsrResultListener iAsrResultListener = new IAsrResultListener() {
        @Override
        public void onResult(int event, String result) {
            Log.d(TAG, event + "||onResult: " + result);
            if (event == AsrEvent.ASR_EVENT_ASR_RESULT) {
                try {
                    Log.d(TAG + TAG, String.valueOf(STATUS));
                    JSONObject jsonObject = new JSONObject(result);
                    String asrResult = jsonObject.getString("asr_recongize");
                    Log.d(TAG, String.valueOf(STATUS));
                    if (STATUS == 0) {
                        handler.sendEmptyMessage(0);
                        unisoundTtsEngine.playTts("请选择语言");
                        Mess.setText("请选择语言|Please select language\n【中文|English】");
                        STATUS++;
                    } else if (STATUS == 2 && asrResult != "") {
                        IsChinese = !asrResult.contains("English");
                        STATUS++;
                    } else if (STATUS == 3 && asrResult != "") {
                        unisoundTtsEngine.playTts("好的小朋友你选择了" + (IsChinese ? "中文," : "English,") + "那么，小朋友你叫什么名字呢？");
                        Mess.setText("小朋友你叫什么名字");
                        handler.sendEmptyMessage(0);
                        STATUS++;
                    } else if (STATUS == 5 && asrResult != "") {
                        UserName = asrResult.replace("我叫", "");
                        unisoundTtsEngine.playTts("小朋友你是叫" + UserName + "吗？");
                        STATUS++;
                    } else if (STATUS == 6 && asrResult != "") {
                        if (Pattern.matches(".*不.*", asrResult)
                                || Pattern.matches("退出", asrResult)
                                || Pattern.matches("否定", asrResult)
                                || Pattern.matches(".*no.*", asrResult)
                                || Pattern.matches("否定", asrResult)
                                || Pattern.matches("i'm really want to but.*", asrResult)
                                || Pattern.matches("我还有其他事", asrResult)
                                || Pattern.matches("让我休息一下", asrResult)
                                || Pattern.matches("我累了", asrResult)
                                || Pattern.matches("改天吧", asrResult)
                                || Pattern.matches("错", asrResult)
                                || Pattern.matches("i'm not sure", asrResult)
                                || Pattern.matches("never", asrResult)
                                || Pattern.matches("none", asrResult)
                                || Pattern.matches("can't", asrResult)
                                || Pattern.matches("be a let down", asrResult)
                                || Pattern.matches("turn .* down", asrResult)) {
                            unisoundTtsEngine.playTts("请小朋友你自己设置吧！");
                            STATUS++;
                            Intent myIntent = new Intent(context, UserInformationSet.class);
                            startActivity(myIntent);
                        } else if (Pattern.matches(".*是.*", asrResult)) {
                            unisoundTtsEngine.playTts("好的，" + UserName + "小朋友！");
                            STATUS++;
                            Intent myIntent = new Intent(context, LauncherActivity.class);
                            startActivity(myIntent);
                        }
                    }
                } catch (Exception e) {
                    SdkLogMgr.d(TAG, "error:" + e.toString());
                }
                unisoundAsrEngine.startAsr(false);
            }
            if (event == AsrEvent.ASR_EVENT_WAKEUP_RESULT) {
                unisoundAsrEngine.startAsr(false);
            }
        }

        @Override
        public void onEvent(int event) {
            Log.d(TAG, "onEvent:" + event);
            if (event == AsrEvent.ASR_EVENT_VAD_SPEECH_END) {
                VoicePresenter.getInstance().getUnisoundAsrEngine().stopAsr(false);
            }
        }

        @Override
        public void onError(int error) {
            Log.d(TAG, "onError:" + error);
        }

        @Override
        public void onSessionId(String s) {

        }
    };
}

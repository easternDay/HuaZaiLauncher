package cn.EasertnDay.HuaZai;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import cn.EasternDay.HuaZai.R;
import com.unisound.sdk.asr.AsrEvent;
import com.unisound.sdk.asr.UnisoundAsrEngine;
import com.unisound.sdk.asr.impl.IAsrResultListener;
import com.unisound.sdk.tts.TtsOption;
import com.unisound.sdk.tts.UnisoundTtsEngine;
import com.unisound.sdk.tts.audiotrack.AndroidAudioTrack;
import com.unisound.sdk.tts.param.UnisoundTtsPlayMode;
import org.json.JSONObject;

public class UserRegesite extends AppCompatActivity implements IAsrResultListener {

    Activity context = this;

    //语音识别需要的
    private UnisoundAsrEngine unisoundAsrEngine;
    //语音合成需要的
    private UnisoundTtsEngine unisoundTtsEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_regesite);

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

        //开始收声
        VoicePresenter.getInstance().setAsrListener((IAsrResultListener) context);
        if (unisoundAsrEngine.startWakeUp()) {
            unisoundTtsEngine.playTts("小朋友你叫什么名字？");
        }

    }

    @Override
    public void onResult(int event, String result) {
        if (event == AsrEvent.ASR_EVENT_ASR_RESULT) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String asrResult = jsonObject.getString("asr_recongize");
                if (!TextUtils.isEmpty(asrResult)) {

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
}

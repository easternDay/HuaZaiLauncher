package cn.EasertnDay.HuaZai;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import cn.EasternDay.HuaZai.R;
import android.widget.TextView;
import com.unisound.sdk.asr.AsrEvent;
import com.unisound.sdk.asr.AsrOption;
import com.unisound.sdk.asr.UnisoundAsrEngine;
import com.unisound.sdk.asr.audiosource.FileAudioSource;
import com.unisound.sdk.asr.impl.IAsrResultListener;
import com.unisound.sdk.asr.param.UnisoundAsrInitMode;
import com.unisound.sdk.asr.param.UnisoundAsrMode;
import org.json.JSONObject;

public class FileAsrActivity extends AppCompatActivity implements IAsrResultListener {
  private static final String TAG = "FileAsrActivity";
  private UnisoundAsrEngine unisoundAsrEngine;
  private TextView textResult;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_text_asr);
    textResult = findViewById(R.id.textResult);
    unisoundAsrEngine = VoicePresenter.getInstance().getUnisoundAsrEngine();
    unisoundAsrEngine.setAudioSource(new FileAudioSource("/sdcard/unisound/test.pcm", 2));
    unisoundAsrEngine.setListener(this);

    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_SERVER_VAD_ENABLE, false);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_LOCAL_VAD_ENABLE, false);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_NLU_ENABLE, false);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_INIT_MODE, UnisoundAsrInitMode.ONLINE);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_ASR_MODE, UnisoundAsrMode.ONLINE);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_USE_PUNCTUATED, true);
  }

  public void onStartAsr(View v) {
    unisoundAsrEngine.startAsr(false);
  }

  public void onEndAsr(View v) {
    unisoundAsrEngine.cancel();
  }

  public void onExit(View v) {
    unisoundAsrEngine.exitMultiRoundDialogue();
  }

  @Override public void onResult(int event, String result) {
    Log.d(TAG, "onResult:" + result);
    if (event == AsrEvent.ASR_EVENT_ASR_RESULT) {
      try {
        JSONObject jsonObject = new JSONObject(result);
        String text = jsonObject.getString("asr_recongize");
        if (!TextUtils.isEmpty(text.trim())) {
          textResult.setText(text);
        }
      } catch (Exception e) {

      }
    }
  }

  @Override public void onEvent(int event) {
    Log.d(TAG, "onEvent:" + event);
    if (event == AsrEvent.ASR_EVENT_RECORD_FILE_OVER) {
      unisoundAsrEngine.stopAsr(true);
    }
  }

  @Override public void onError(int error) {
    Log.d(TAG, "onError:" + error);
  }

  @Override public void onSessionId(String sessionId) {

  }

  protected void onDestroy() {
    super.onDestroy();
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_NLU_ENABLE, true);
    VoicePresenter.getInstance().setAudioSource();
    unisoundAsrEngine.cancel();
  }
}

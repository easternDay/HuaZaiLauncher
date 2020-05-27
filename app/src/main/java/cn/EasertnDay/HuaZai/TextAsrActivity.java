package cn.EasertnDay.HuaZai;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.unisound.sdk.asr.AsrEvent;
import com.unisound.sdk.asr.AsrOption;
import com.unisound.sdk.asr.UnisoundAsrEngine;
import com.unisound.sdk.asr.impl.IAsrResultListener;
import com.unisound.sdk.asr.param.UnisoundAsrInitMode;
import com.unisound.sdk.asr.param.UnisoundAsrMode;
import org.json.JSONObject;
import cn.EasternDay.HuaZai.R;

public class TextAsrActivity extends AppCompatActivity implements IAsrResultListener {
  private static final String TAG = "TextAsrActivity";
  private UnisoundAsrEngine unisoundAsrEngine;
  private TextView textResult;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_text_asr);
    textResult = findViewById(R.id.textResult);
    unisoundAsrEngine = VoicePresenter.getInstance().getUnisoundAsrEngine();
    VoicePresenter.getInstance().setAsrListener(this);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_SERVER_VAD_ENABLE, false);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_NLU_ENABLE, false);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_INIT_MODE, UnisoundAsrInitMode.ONLINE);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_USE_PUNCTUATED, true);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_USE_PUNCTUATED_TYPE, "beauty");
  }

  public void onStartAsr(View v) {
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_ASR_MODE, UnisoundAsrMode.ONLINE);
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
        textResult.setText(text);
      } catch (Exception e) {

      }
    }
  }

  @Override public void onEvent(int event) {
    Log.d(TAG, "onEvent:" + event);
  }

  @Override public void onError(int error) {
    Log.d(TAG, "onError:" + error);
  }

  @Override public void onSessionId(String sessionId) {

  }

  protected void onDestroy() {
    super.onDestroy();
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_NLU_ENABLE, true);
    unisoundAsrEngine.cancel();
  }
}
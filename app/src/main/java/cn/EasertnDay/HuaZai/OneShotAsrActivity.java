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
import com.unisound.sdk.asr.param.UnisoundAsrMode;

import java.util.HashSet;
import cn.EasternDay.HuaZai.R;

public class OneShotAsrActivity extends AppCompatActivity implements IAsrResultListener {
  private static final String TAG = "OneShotAsrActivity";
  private UnisoundAsrEngine unisoundAsrEngine;
  private TextView textResult;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_oneshot_asr);
    textResult = findViewById(R.id.textResult);
    unisoundAsrEngine = VoicePresenter.getInstance().getUnisoundAsrEngine();
    VoicePresenter.getInstance().setAsrListener(this);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    HashSet<String> wakeUpList = new HashSet<>();
    wakeUpList.add("你好魔方");
    unisoundAsrEngine.setWakeUpWord(wakeUpList, wakeUpList);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_ONE_SHOT_VERSION, 1);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_LOCAL_VAD_ENABLE, false);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_SERVER_VAD_ENABLE, false);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_USE_PUNCTUATED, true);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_FILTER_NAME, "nlu3");
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_CONFIDENCE_MEASURE, true);
  }

  public void onStartWakeup(View v) {
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_ASR_MODE, UnisoundAsrMode.ONLINE);
    unisoundAsrEngine.startWakeUp();
  }

  public void onEndAsr(View v) {
    unisoundAsrEngine.cancel();
    unisoundAsrEngine.startWakeUp();
  }

  public void onExit(View v) {
    unisoundAsrEngine.exitMultiRoundDialogue();
  }

  @Override public void onResult(int event, String result) {
    Log.d(TAG, "onResult:" + result);
    if (event == AsrEvent.ASR_EVENT_ASR_RESULT) {
      textResult.setText(result);
      if (result.contains("service")) {
        unisoundAsrEngine.startWakeUp();
      }
    }
    if (event == AsrEvent.ASR_EVENT_WAKEUP_RESULT) {
      textResult.setText(result);
      unisoundAsrEngine.startAsr(true);
    }
  }

  @Override public void onEvent(int event) {
    Log.d(TAG, "onEvent:" + event);
  }

  @Override public void onError(int error) {
    Log.d(TAG, "onError:" + error);
    unisoundAsrEngine.startWakeUp();
  }

  @Override public void onSessionId(String sessionId) {

  }

  protected void onDestroy() {
    super.onDestroy();
    unisoundAsrEngine.cancel();
  }
}
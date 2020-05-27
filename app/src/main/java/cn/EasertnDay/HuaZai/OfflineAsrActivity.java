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
import cn.EasternDay.HuaZai.R;

public class OfflineAsrActivity extends AppCompatActivity implements IAsrResultListener {
  private static final String TAG = "OfflineAsrActivity";
  private UnisoundAsrEngine unisoundAsrEngine;
  private TextView txtOffline;
  private TextView txtOnline;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_offline_asr);
    txtOffline = findViewById(R.id.textOffline);
    txtOnline = findViewById(R.id.textOnline);
    unisoundAsrEngine = VoicePresenter.getInstance().getUnisoundAsrEngine();
    VoicePresenter.getInstance().setAsrListener(this);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
  }

  public void onStartAsr(View v) {
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_ASR_MODE, UnisoundAsrMode.MIX);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_SERVER_VAD_ENABLE, false);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_USE_PUNCTUATED, true);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_NLU_ENABLE, true);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_FILTER_NAME, "nlu3");
    unisoundAsrEngine.loadGrammer("/sdcard/unisound/test_grammar.dat");
    unisoundAsrEngine.startAsr("test", false);
  }

  public void onEndAsr(View v) {
    unisoundAsrEngine.cancel();
  }

  public void onExit(View v) {
    unisoundAsrEngine.exitMultiRoundDialogue();
  }

  @Override public void onResult(int event, String result) {
    Log.d(TAG, "onResult:" + result);
    if (event == AsrEvent.ASR_EVENT_OFFLINE_RESULT) {
      txtOffline.setText(result);
    }
    if (event == AsrEvent.ASR_EVENT_ASR_RESULT) {
      txtOnline.setText(result);
      if (result != null && result.contains("service")) {
        unisoundAsrEngine.cancel();
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
    unisoundAsrEngine.cancel();
  }
}
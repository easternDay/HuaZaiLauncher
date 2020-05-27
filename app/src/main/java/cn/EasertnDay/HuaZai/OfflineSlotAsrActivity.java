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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.EasternDay.HuaZai.R;

public class OfflineSlotAsrActivity extends AppCompatActivity implements IAsrResultListener {
  private static final String TAG = "OfflineSlotAsrActivity";
  private UnisoundAsrEngine unisoundAsrEngine;
  private TextView textResult;
  private boolean change = true;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_offline_slot_asr);
    textResult = findViewById(R.id.textResult);
    unisoundAsrEngine = VoicePresenter.getInstance().getUnisoundAsrEngine();
    VoicePresenter.getInstance().setAsrListener(this);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_ASR_MODE, UnisoundAsrMode.OFFLINE);
    unisoundAsrEngine.loadJsgf("/sdcard/unisound/test_jsgf_clg.dat");
    List<String> userCommand = new ArrayList<>();
    userCommand.add("打开空调");
    userCommand.add("打开电视");
    Map<String, List<String>> mapVocab = new HashMap<>();
    mapVocab.put("Domain_userdef_commands_slot", userCommand);
    unisoundAsrEngine.insertVocab("test_jsgf", mapVocab);
  }

  public void onStartAsr(View v) {
    unisoundAsrEngine.startAsr("test_jsgf", false);
  }

  public void onChangeSlot(View v) {
    List<String> userCommand = new ArrayList<>();
    if (change) {
      userCommand.add("打开蓝牙");
      userCommand.add("打开冰箱");
    } else {
      userCommand.add("打开空调");
      userCommand.add("打开电视");
    }
    change = !change;
    Map<String, List<String>> mapVocab = new HashMap<>();
    mapVocab.put("Domain_userdef_commands_slot", userCommand);
    unisoundAsrEngine.insertVocab("test_jsgf", mapVocab);
    unisoundAsrEngine.startAsr("test_jsgf", false);
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
      textResult.setText(result);
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
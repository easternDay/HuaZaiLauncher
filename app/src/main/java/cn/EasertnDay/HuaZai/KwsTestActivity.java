package cn.EasertnDay.HuaZai;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import cn.EasternDay.HuaZai.R;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.unisound.sdk.asr.AsrEvent;
import com.unisound.sdk.asr.UnisoundAsrEngine;
import com.unisound.sdk.asr.impl.IAsrResultListener;
import com.unisound.sdk.utils.SdkLogMgr;
import org.json.JSONObject;

import java.util.HashSet;

public class KwsTestActivity extends AppCompatActivity implements IAsrResultListener {
  private static final String TAG = "KwsTestActivity";

  private int[] color = { Color.RED, Color.GREEN };
  private Button butWakeUp;
  private Button butWakeUpAndAsr;
  private TextView txtWakeUp;
  private TextView txtNlu;
  private TextView txtAsr;
  private View viewBg;
  private int wakeUpCount = 0;
  private UnisoundAsrEngine unisoundAsrEngine;
  private boolean isOnlyWakeUp = true;

  private ToneGenerator beeper;
  private Handler handler = new Handler(Looper.getMainLooper()) {
    @Override public void handleMessage(Message msg) {
      super.handleMessage(msg);
      unisoundAsrEngine.startWakeUp();
    }
  };

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_kws);
    initView();
    beeper = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
    unisoundAsrEngine = VoicePresenter.getInstance().getUnisoundAsrEngine();
    HashSet<String> wakeUpList = new HashSet<>();
    wakeUpList.add("你好华仔");
    unisoundAsrEngine.setWakeUpWord(wakeUpList, wakeUpList);
    VoicePresenter.getInstance().setAsrListener(this);
  }

  private void initView() {
    txtWakeUp = findViewById(R.id.txtWakeUp);
    txtNlu = findViewById(R.id.txtNlu);
    txtAsr = findViewById(R.id.txtAsr);
    viewBg = findViewById(R.id.viewBg);
    butWakeUp = findViewById(R.id.butWakeUp);
    butWakeUpAndAsr = findViewById(R.id.butWakeUpAndAsr);
    butWakeUp.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startWakeUp(true);
      }
    });
    butWakeUpAndAsr.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startWakeUp(false);
      }
    });
  }

  public void startWakeUp(boolean isOnlyWakeUp) {
    this.isOnlyWakeUp = isOnlyWakeUp;
    handler.removeCallbacks(null);
    if (!unisoundAsrEngine.startWakeUp()) {
      Toast.makeText(this, "唤醒失败", Toast.LENGTH_SHORT).show();
    }
  }

  @Override public void onResult(int event, String result) {
    Log.d(TAG, "onResult:" + result);
    if (event == AsrEvent.ASR_EVENT_WAKEUP_RESULT) {
      Log.d(TAG, "kwsTest start beeping");
      beeper.startTone(ToneGenerator.TONE_DTMF_S, 30);
      Log.d(TAG, "KwsTest stop beeping");
      wakeUpCount++;
      txtWakeUp.setText("唤醒次数:" + wakeUpCount + "," + result);
      viewBg.setBackgroundColor(color[wakeUpCount % color.length]);
      if (SdkParam.getInstance().getAudioSourceType() == AudioSourceType.JNI || !isOnlyWakeUp) {
        unisoundAsrEngine.startAsr(false);
        if (isOnlyWakeUp) {
          handler.sendEmptyMessageDelayed(0, 100);
        }
      }
    }
    if (event == AsrEvent.ASR_EVENT_ASR_RESULT) {
      if (isOnlyWakeUp) {
        return;
      }
      try {
        JSONObject jsonObject = new JSONObject(result);
        String asrResult = jsonObject.getString("asr_recongize");
        if (!TextUtils.isEmpty(asrResult)) {
          txtAsr.setText(asrResult);
        }
        if (result.contains("nlu")) {
          txtNlu.setText(result);
          unisoundAsrEngine.startWakeUp();
        }
      } catch (Exception e) {
        SdkLogMgr.d(TAG, "error:" + e.toString());
        unisoundAsrEngine.startWakeUp();
      }
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

  @Override protected void onDestroy() {
    super.onDestroy();
    handler.removeCallbacks(null);
    unisoundAsrEngine.cancel(true);
  }
}
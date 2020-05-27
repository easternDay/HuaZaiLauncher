package cn.EasertnDay.HuaZai;

import android.graphics.Color;
import android.media.AudioFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.unisound.sdk.asr.AsrEvent;
import com.unisound.sdk.asr.AsrOption;
import com.unisound.sdk.asr.UnisoundAsrEngine;
import com.unisound.sdk.asr.impl.IAsrResultListener;
import com.unisound.sdk.utils.AudioTrackPlayerModeStatic;
import com.unisound.sdk.utils.SdkLogMgr;
import cn.EasternDay.HuaZai.R;

public class WakeupActivity extends AppCompatActivity implements IAsrResultListener {
  private static final String TAG = "WakeupActivity";

  private int[] color = { Color.RED, Color.GREEN };
  private int position = 0;
  private TextView txtNlu;
  private TextView txtNluLists;
  private View viewBg;
  private AudioTrackPlayerModeStatic audioTrackPlayer =
      new AudioTrackPlayerModeStatic(22050, AudioFormat.CHANNEL_OUT_STEREO);

  private UnisoundAsrEngine unisoundAsrEngine;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_wakeup);
    initView();
    unisoundAsrEngine = VoicePresenter.getInstance().getUnisoundAsrEngine();
    VoicePresenter.getInstance().setAsrListener(this);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
  }

  private void initView() {
    txtNlu = findViewById(R.id.txtNlu);
    txtNluLists = findViewById(R.id.tv_wakeup_list);
    viewBg = findViewById(R.id.viewBg);
  }

  public void onStartWakeUp(View view) {
    if (!unisoundAsrEngine.startWakeUp()) {
      Toast.makeText(this, "唤醒失败", Toast.LENGTH_SHORT).show();
    }
  }

  @Override public void onResult(int event, String result) {
    Log.d(TAG, "onResult:" + result);
    if (event == AsrEvent.ASR_EVENT_WAKEUP_RESULT) {
      int doaResult = (int) VoicePresenter.getInstance()
          .getUnisoundAsrEngine()
          .getOption(AsrOption.ASR_OPTION_DOA_RESULT);
      SdkLogMgr.d(TAG, "doaResult:" + doaResult);
      audioTrackPlayer.playAssetFile(this, "wake.pcm");
      viewBg.setBackgroundColor(color[position % color.length]);
      txtNlu.setText(result);
      position++;
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

  @Override protected void onDestroy() {
    super.onDestroy();
  }
}
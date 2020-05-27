package cn.EasertnDay.HuaZai;

import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.unisound.sdk.tts.TtsOption;
import com.unisound.sdk.tts.UnisoundTtsEngine;
import com.unisound.sdk.tts.audiotrack.AndroidAudioTrack;
import com.unisound.sdk.tts.param.UnisoundTtsPlayMode;
import com.unisound.sdk.utils.AssetsUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import cn.EasternDay.HuaZai.R;

public class TtsActivity extends AppCompatActivity {

  private LinearLayout llFrontModel;
  private LinearLayout llBackModel;
  private LinearLayout llVoiceName;

  private TextView tvPlayModel;
  private TextView tvFrontModel;
  private TextView tvBackModel;
  private TextView tvVoiceName;

  private SeekBar sbSpeed;
  private SeekBar sbVolume;
  private SeekBar sbBright;

  private EditText etBackSil;
  private EditText etFrontSil;
  private EditText etDefaultName;
  private EditText etSampleRate;

  private UnisoundTtsEngine unisoundTtsEngine;
  private List<String> frontModel = new ArrayList<>();
  private List<String> backModel = new ArrayList<>();
  private static String[] playModel = new String[] { "ONLINE", "OFFLINE" };
  private static String[] voiceName = new String[] {
      "xiaowen", "xiaoming", "tiantian", "xiaoli", "xuanxuan", "kiyo", "lingling", "boy",
      "xiaolijie", "tangtang", "xiaofeng", "sweet", "lzl"
  };
  private int voiceNameChoice = 3;
  private int playModeChoice = 0;
  private int backModelChoice = 1;

  private String lastRate = "";

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_tts);
    initView();
    unisoundTtsEngine = VoicePresenter.getInstance().getUnisoundTtsEngine();
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
  }

  private void initView() {
    llVoiceName = findViewById(R.id.ll_voice_name);
    llFrontModel = findViewById(R.id.ll_front_model);
    llBackModel = findViewById(R.id.ll_back_model);
    tvPlayModel = findViewById(R.id.tv_play_mode);
    tvFrontModel = findViewById(R.id.tv_front_model);
    tvBackModel = findViewById(R.id.tv_back_model);
    tvVoiceName = findViewById(R.id.tv_voice_name);
    sbSpeed = findViewById(R.id.sb_speed);
    sbVolume = findViewById(R.id.sb_volume);
    sbBright = findViewById(R.id.sb_bright);
    etFrontSil = findViewById(R.id.et_front_sil);
    etBackSil = findViewById(R.id.et_back_sil);
    etDefaultName = findViewById(R.id.et_default_text);
    etSampleRate = findViewById(R.id.et_sample_rate);
    copyAssetsModel();
    llFrontModel.setVisibility(View.GONE);
    llBackModel.setVisibility(View.GONE);
    if (frontModel.size() > 0) {
      tvFrontModel.setText(frontModel.get(0));
    }
    if (backModel.size() > 1) {
      tvBackModel.setText(backModel.get(1));
    }
  }

  public void startTts(View view) {
    unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_PRINT_DEBUG_LOG, false);
    unisoundTtsEngine.setTtsOption(TtsOption.TTS_PLAY_MODE,
        playModeChoice == 0 ? UnisoundTtsPlayMode.ONLINE : UnisoundTtsPlayMode.OFFLINE);
    unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_SPEED, sbSpeed.getProgress());
    unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_VOLUME, sbVolume.getProgress());
    unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_BRIGHT, sbBright.getProgress());
    if (etSampleRate.getText() != null && etSampleRate.getText().length() > 0) {
      String sample = etSampleRate.getText().toString();
      if (!sample.equals(lastRate)) {
        lastRate = sample;
        unisoundTtsEngine.setTtsOption(TtsOption.TTS_SAMPLE_RATE, Integer.parseInt(sample));
        unisoundTtsEngine.setAudioTrack(new AndroidAudioTrack(Integer.parseInt(sample)));
      }
    }
    if (etFrontSil.getText() != null && etFrontSil.getText().length() > 0) {
      unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_FRONT_SIL,
          Integer.parseInt(etFrontSil.getText().toString()));
    }
    if (etBackSil.getText() != null && etBackSil.getText().length() > 0) {
      unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_BACK_SIL,
          Integer.parseInt(etBackSil.getText().toString()));
    }
    Log.i("ASir", String.valueOf(unisoundTtsEngine.isInit()));
    unisoundTtsEngine.playTts(etDefaultName.getText().toString());
  }

  public void endTts(View view) {
    unisoundTtsEngine.cancelTts();
  }

  private void copyAssetsModel() {
    AssetManager assetManager = this.getAssets();
    try {
      String[] files = assetManager.list("");
      for (String file : files) {
        if (file.startsWith("frontend")) {
          frontModel.add(file);
          if (!(new File(Config.TTS_PATH + file).exists())) {
            AssetsUtils.copyAssetsFile(this, file, Config.TTS_PATH + file, false);
          }
        } else if (file.startsWith("backend")) {
          backModel.add(file);
          if (!(new File(Config.TTS_PATH + file).exists())) {
            AssetsUtils.copyAssetsFile(this, file, Config.TTS_PATH + file, false);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void onPlayModelClick(View view) {
    showSingleChoiceDialog(playModel, tvPlayModel, "PlayMode", playModeChoice);
  }

  public void onBackModelClick(View view) {
    String[] items = new String[backModel.size()];
    int i = 0;
    for (String str : backModel) {
      items[i++] = str;
    }
    showSingleChoiceDialog(items, tvBackModel, "BackModel", backModelChoice);
  }

  public void onVoiceNameClick(View view) {
    showSingleChoiceDialog(voiceName, tvVoiceName, "VoiceName", voiceNameChoice);
  }

  private void showSingleChoiceDialog(final String[] items, final TextView view, final String title,
      final int checkedItem) {
    unisoundTtsEngine.cancelTts();
    AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(this);
    singleChoiceDialog.setTitle(title);
    singleChoiceDialog.setSingleChoiceItems(items, checkedItem,
        new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            int id = view.getId();
            switch (id) {
              case R.id.tv_play_mode:
                playModeChoice = which;
                break;
              case R.id.tv_back_model:
                backModelChoice = which;
                break;
              case R.id.tv_voice_name:
                voiceNameChoice = which;
                break;
              default:
                break;
            }
          }
        });
    singleChoiceDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        int id = view.getId();
        switch (id) {
          case R.id.tv_play_mode:
            view.setText(items[playModeChoice]);
            if (playModeChoice == 0) {
              llFrontModel.setVisibility(View.GONE);
              llBackModel.setVisibility(View.GONE);
              llVoiceName.setVisibility(View.VISIBLE);
              etSampleRate.setText("16000");
              unisoundTtsEngine.setAudioTrack(new AndroidAudioTrack(16000));
            } else {
              llFrontModel.setVisibility(View.VISIBLE);
              llBackModel.setVisibility(View.VISIBLE);
              llVoiceName.setVisibility(View.GONE);
              unisoundTtsEngine.setAudioTrack(new AndroidAudioTrack(22050));
              etSampleRate.setText("22050");
            }
            break;
          case R.id.tv_back_model:
            view.setText(items[backModelChoice]);
            unisoundTtsEngine.switchTtsModel(Config.TTS_PATH + items[backModelChoice]);
            break;
          case R.id.tv_voice_name:
            view.setText(items[voiceNameChoice]);
            unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_VOICE_NAME, items[voiceNameChoice]);
            break;
          default:
            break;
        }
      }
    });
    singleChoiceDialog.show();
  }

  protected void onDestroy() {
    super.onDestroy();
    unisoundTtsEngine.cancelTts();
  }
}

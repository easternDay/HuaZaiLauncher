package cn.EasertnDay.HuaZai;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.EasternDay.HuaZai.R;
import com.unisound.audio.AudioHttpParam;
import com.unisound.audio.AudioHttpUtils;
import com.unisound.audio.AudioResult;
import com.unisound.http.ResponseCallBack;
import com.unisound.json.JsonTool;
import com.unisound.sdk.asr.AsrEvent;
import com.unisound.sdk.asr.AsrOption;
import com.unisound.sdk.asr.impl.IAsrResultListener;
import com.unisound.sdk.utils.AssetsUtils;
import com.unisound.sdk.utils.SdkLogMgr;

import java.io.File;
import java.io.IOException;

public class BasicMainActivity extends AppCompatActivity implements IAsrResultListener {

  private static final String TAG = "MainActivityUnisoundUsc";

  private Button butKws;
  private Button butDialect;
  private Button butRecord;
  private Button butAudioHttp;
  private Button butWakeUp;
  private Button btnMedicalAsr;
  private Button btnOnlineAsr;
  private Button btnOfflineAsr;
  private Button btnOfflineSlotAsr;
  private Button butOnlineOneShotAsr;
  private Button butStartTts;
  private Button butGetToken;
  private Button butVoiceToText;
  private Button butFileToText;
  private Button butExit;
  private Button butVpr;
  private TextView txtNlu;
  private Context context;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = this;
    SdkLogMgr.d("app version: " + VersionUtils.getVersionName(this));
    setContentView(R.layout.layout_basicmain);
    butAudioHttp = findViewById(R.id.butAudioHttp);
    butKws = findViewById(R.id.butKws);
    butDialect = findViewById(R.id.butDialect);
    butRecord = findViewById(R.id.butRecord);
    butWakeUp = findViewById(R.id.butWakeUpWord);
    btnMedicalAsr = findViewById(R.id.butMedicalAsr);
    btnOnlineAsr = findViewById(R.id.butOnlineAsr);
    btnOfflineAsr = findViewById(R.id.butOfflineAsr);
    btnOfflineSlotAsr = findViewById(R.id.butOfflineSlotAsr);
    butOnlineOneShotAsr = findViewById(R.id.butOnlineOneShotAsr);
    butVoiceToText = findViewById(R.id.butVoiceToText);
    butFileToText = findViewById(R.id.butFileToText);
    butStartTts = findViewById(R.id.butStartTts);
    butGetToken = findViewById(R.id.butGetToken);
    butExit = findViewById(R.id.butExit);
    txtNlu = findViewById(R.id.txtNlu);
    butVpr = findViewById(R.id.butVpr);

    butKws.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        startActivity(new Intent(context, KwsTestActivity.class));
      }
    });
    butDialect.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        startActivity(new Intent(context, DialectTestActivity.class));
      }
    });
    butRecord.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        startActivity(new Intent(context, RecordActivity.class));
      }
    });
    butWakeUp.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        startActivity(new Intent(context, WakeupActivity.class));
      }
    });
    btnMedicalAsr.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        startActivity(new Intent(context, MedicalAsrActivity.class));
      }
    });
    btnOnlineAsr.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        startActivity(new Intent(context, OnlineAsrActivity.class));
      }
    });
    btnOfflineAsr.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        startActivity(new Intent(context, OfflineAsrActivity.class));
      }
    });

    butStartTts.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        startActivity(new Intent(context, TtsActivity.class));
      }
    });

    btnOfflineSlotAsr.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        startActivity(new Intent(context, OfflineSlotAsrActivity.class));
      }
    });

    butOnlineOneShotAsr.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        startActivity(new Intent(context, OneShotAsrActivity.class));
      }
    });

    butVoiceToText.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        startActivity(new Intent(context, TextAsrActivity.class));
      }
    });

    butFileToText.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        startActivity(new Intent(context, FileAsrActivity.class));
      }
    });

    butGetToken.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        String token = (String) VoicePresenter.getInstance()
            .getUnisoundAsrEngine()
            .getOption(AsrOption.ASR_OPTION_DEVICE_TOKEN);
        txtNlu.setText(token);
      }
    });

    butVpr.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startActivity(new Intent(context, VprActivity.class));
      }
    });

    butAudioHttp.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        AudioHttpParam audioHttpParam = new AudioHttpParam("child", "child", "2000130210", 2);
        AudioHttpUtils.getAudioHttpUrl(audioHttpParam, true, new ResponseCallBack<AudioResult>() {
          @Override public void onResponse(AudioResult response) {
            Toast.makeText(BasicMainActivity.this, "response:" + JsonTool.toJson(response),
                Toast.LENGTH_LONG).show();
          }

          @Override public void onError(String error) {
            Toast.makeText(BasicMainActivity.this, "error:" + error, Toast.LENGTH_SHORT).show();
          }
        });
      }
    });
    VoicePresenter.getInstance().init(this.getApplicationContext());
    copyTtsModel();
  }

  @Override protected void onDestroy() {
    VoicePresenter.getInstance().release();
    super.onDestroy();
  }

  @Override public void onResult(int event, String result) {
    Log.d(TAG, "onResult:" + result);
    if (event == AsrEvent.ASR_EVENT_ASR_RESULT) {
      if (result.contains("service")) {
        VoicePresenter.getInstance().getUnisoundAsrEngine().startWakeUp();
        txtNlu.setText(result);
      }
    }
  }

  @Override public void onEvent(int event) {
    Log.d(TAG, "onEvent:" + event);
    if (event == AsrEvent.ASR_EVENT_VAD_SPEECH_END) {
      VoicePresenter.getInstance().getUnisoundAsrEngine().stopAsr(false);
    }
  }

  @Override public void onError(int error) {
    Log.d(TAG, "onError:" + error);
  }

  @Override public void onSessionId(String sessionId) {

  }

  private void copyTtsModel() {
    new Thread(new Runnable() {
      @Override public void run() {
        AssetManager assetManager = context.getAssets();
        try {
          String[] files = assetManager.list("");
          for (String file : files) {
            if (file.startsWith("frontend") || file.startsWith("backend")) {
              if (!(new File(Config.TTS_PATH + file).exists())) {
                AssetsUtils.copyAssetsFile(context, file, Config.TTS_PATH + file, false);
              }
            } else {
              AssetsUtils.copyAssetsFile(context, file,
                  Environment.getExternalStorageDirectory() + File.separator + "unisound"
                      + File.separator + file, false);
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }
}

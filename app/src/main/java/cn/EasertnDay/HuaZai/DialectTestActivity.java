package cn.EasertnDay.HuaZai;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import cn.EasternDay.HuaZai.R;
import com.unisound.sdk.asr.AsrEvent;
import com.unisound.sdk.asr.AsrOption;
import com.unisound.sdk.asr.UnisoundAsrEngine;
import com.unisound.sdk.asr.impl.IAsrResultListener;
import com.unisound.sdk.asr.param.UnisoundAsrMode;
import com.unisound.sdk.utils.SdkLogMgr;
import org.json.JSONObject;
import cn.EasternDay.HuaZai.R;

public class DialectTestActivity extends AppCompatActivity implements IAsrResultListener {
  private static final String TAG = "OnlineAsrActivity";
  private UnisoundAsrEngine unisoundAsrEngine;

  private TextView textResult;
  private TextView nluResult;
  private TextView tvFilterName;

  private CheckBox cbServerVad;
  private CheckBox cbLocalVad;
  private EditText etFrontSil;
  private EditText etBackSil;

  private static String[] arrayLanguage = new String[] { "cantonese", "sichuanese" };

  private int languageChoice = 0;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_dialect);
    initView();
    initEngine();
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
  }

  private void initEngine() {
    unisoundAsrEngine = VoicePresenter.getInstance().getUnisoundAsrEngine();
    unisoundAsrEngine.release();
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_TR_ADDRESS,
        "asr-dialect.hivoice.cn:80");
    unisoundAsrEngine.init();
    VoicePresenter.getInstance().setAsrListener(this);
  }

  private void initView() {
    textResult = findViewById(R.id.txtAsr);
    nluResult = findViewById(R.id.txtNlu);
    tvFilterName = findViewById(R.id.tv_filter_name);
    cbServerVad = findViewById(R.id.cb_server_vad);
    cbLocalVad = findViewById(R.id.cb_local_vad);
    etFrontSil = findViewById(R.id.et_front_sil);
    etBackSil = findViewById(R.id.et_back_sil);
  }

  public void onStartAsr(View v) {
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_LANGUAGE,
        arrayLanguage[languageChoice]);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_ASR_MODE, UnisoundAsrMode.ONLINE);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_NLU_ENABLE, true);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_RET_TIME_POINT, true);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_ASR_DOMAIN, "general");
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_VOICE_FIELD, "near");
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_SERVER_VAD_ENABLE, cbServerVad.isChecked());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_LOCAL_VAD_ENABLE, cbLocalVad.isChecked());
    if (!TextUtils.isEmpty(etFrontSil.getText())) {
      unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_VAD_FRONT_TIME,
          Float.parseFloat(etFrontSil.getText().toString()));
    }
    if (!TextUtils.isEmpty(etBackSil.getText())) {
      unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_VAD_BACK_TIME,
          Float.parseFloat(etBackSil.getText().toString()));
    }
    unisoundAsrEngine.startAsr(false);
  }

  public void onEndAsr(View v) {
    unisoundAsrEngine.cancel();
  }

  public void onExit(View v) {
    unisoundAsrEngine.exitMultiRoundDialogue();
  }

  public void onFilterNameClick(View view) {
    showSingleChoiceDialog(arrayLanguage, tvFilterName, "FilterName", languageChoice);
  }

  private void showSingleChoiceDialog(final String[] items, final TextView view, final String title,
      final int checkedItem) {
    AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(this);
    singleChoiceDialog.setTitle(title);
    singleChoiceDialog.setSingleChoiceItems(items, checkedItem,
        new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            int id = view.getId();
            switch (id) {
              case R.id.tv_filter_name:
                languageChoice = which;
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
          case R.id.tv_filter_name:
            view.setText(items[languageChoice]);
            break;
          default:
            break;
        }
      }
    });
    singleChoiceDialog.show();
  }

  @Override public void onResult(int event, String result) {
    Log.d(TAG, "onResult:" + result);
    if (event == AsrEvent.ASR_EVENT_ASR_RESULT) {
      try {
        JSONObject jsonObject = new JSONObject(result);

        String asrResult = jsonObject.getString("asr_recongize");
        if (!TextUtils.isEmpty(asrResult.trim())) {
          textResult.setText(asrResult);
        }
        if (result.contains("nlu")) {
          nluResult.setText(result);
        }
      } catch (Exception e) {
        SdkLogMgr.d(TAG, "error:" + e.toString());
      }
    }
  }

  @Override public void onEvent(int event) {
    Log.d(TAG, "onEvent:" + event);
    if (event == AsrEvent.ASR_EVENT_VAD_SPEECH_END) {
      unisoundAsrEngine.stopAsr(true);
    }
  }

  @Override public void onError(int error) {
    Log.d(TAG, "onError:" + error);
  }

  @Override public void onSessionId(String sessionId) {
    Log.d(TAG, "onSessionId:" + sessionId);
  }

  protected void onDestroy() {
    super.onDestroy();
    unisoundAsrEngine.cancel();
  }
}
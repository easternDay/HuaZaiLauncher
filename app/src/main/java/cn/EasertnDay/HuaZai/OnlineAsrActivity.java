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
import android.widget.LinearLayout;
import android.widget.TextView;
import com.unisound.sdk.asr.AsrEvent;
import com.unisound.sdk.asr.AsrOption;
import com.unisound.sdk.asr.UnisoundAsrEngine;
import com.unisound.sdk.asr.impl.IAsrResultListener;
import com.unisound.sdk.asr.param.UnisoundAsrMode;
import cn.EasternDay.HuaZai.R;

public class OnlineAsrActivity extends AppCompatActivity implements IAsrResultListener {
  private static final String TAG = "OnlineAsrActivity";
  private UnisoundAsrEngine unisoundAsrEngine;

  private TextView textResult;
  private TextView tvFilterName;
  private TextView tvAsrDomain;
  private TextView tvNluScenario;

  private CheckBox cbServerVad;
  private CheckBox cbLocalVad;
  private EditText etFrontSil;
  private EditText etBackSil;

  private LinearLayout llVad;

  private static String[] arrayDomain = new String[] {
      "kar", "song", "medical", "law", "customized", "fridge", "aux", "poi", "food", "general",
      "home", "movietv", "incar", "ysdw", "home_md", "english", "patientGuide", "hotel", "enrich",
      "eshopping"
  };
  private static String[] arrayFilterName = new String[] { "search", "nlu3" };
  private static String[] arrayScenario = new String[] {
      "child", "incar", "smarthome", "musicDefault", "videoDefault", "webbrowser", "videoAPP"
  };

  private int filterNameChoice = 0;
  private int domainChoice = 0;
  private int scenarioChoice = 0;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_online_asr);
    initView();
    unisoundAsrEngine = VoicePresenter.getInstance().getUnisoundAsrEngine();
    VoicePresenter.getInstance().setAsrListener(this);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
  }

  private void initView() {
    textResult = findViewById(R.id.txtNlu);
    tvFilterName = findViewById(R.id.tv_filter_name);
    tvAsrDomain = findViewById(R.id.tv_asr_domain);
    tvNluScenario = findViewById(R.id.tv_nlu_scenario);
    cbServerVad = findViewById(R.id.cb_server_vad);
    cbLocalVad = findViewById(R.id.cb_local_vad);
    etFrontSil = findViewById(R.id.et_front_sil);
    etBackSil = findViewById(R.id.et_back_sil);
    llVad = findViewById(R.id.ll_vad);
  }

  public void onStartAsr(View v) {
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_FILTER_NAME,
        arrayFilterName[filterNameChoice]);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_ASR_MODE, UnisoundAsrMode.ONLINE);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_NLU_ENABLE, true);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_RET_TIME_POINT, true);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_ASR_DOMAIN, arrayDomain[domainChoice]);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_NLU_SCENARIO, arrayScenario[scenarioChoice]);
    if (filterNameChoice == 0) {
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
    } else {
      unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_LOCAL_VAD_ENABLE, false);
      unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_SERVER_VAD_ENABLE, false);
      unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_USE_PUNCTUATED, true);
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
    showSingleChoiceDialog(arrayFilterName, tvFilterName, "FilterName", filterNameChoice);
  }

  public void onDomainClick(View view) {
    showSingleChoiceDialog(arrayDomain, tvAsrDomain, "Domain", domainChoice);
  }

  public void onScenarioClick(View view) {
    showSingleChoiceDialog(arrayScenario, tvNluScenario, "Scenario", scenarioChoice);
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
                filterNameChoice = which;
                break;
              case R.id.tv_asr_domain:
                domainChoice = which;
                break;
              case R.id.tv_nlu_scenario:
                scenarioChoice = which;
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
            view.setText(items[filterNameChoice]);
            llVad.setVisibility(filterNameChoice == 0 ? View.VISIBLE : View.GONE);
            break;
          case R.id.tv_asr_domain:
            view.setText(items[domainChoice]);
            break;
          case R.id.tv_nlu_scenario:
            view.setText(items[scenarioChoice]);
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
      textResult.setText(result);
      if (result.contains("service")) {
        unisoundAsrEngine.startWakeUp();
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
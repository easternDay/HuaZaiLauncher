package cn.EasertnDay.HuaZai;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.unisound.sdk.config.SdkConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cn.EasternDay.HuaZai.R;

public class MedicalAsrActivity extends AppCompatActivity implements IAsrResultListener {
  private static final String TAG = "TextAsrActivity";
  private UnisoundAsrEngine unisoundAsrEngine;
  private TextView textResult;
  private StringBuffer buffer = new StringBuffer();

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_text_asr);
    textResult = findViewById(R.id.textResult);
    findViewById(R.id.textInfo).setVisibility(View.INVISIBLE);
    VoicePresenter.getInstance().init(this.getApplicationContext());
    unisoundAsrEngine = VoicePresenter.getInstance().getUnisoundAsrEngine();
    VoicePresenter.getInstance().setAsrListener(this);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    SdkConfig.setAppKey(Config.MEDICAL_APP_KEY);
    SdkConfig.setAppSecret(Config.MEDICAL_APP_SECRET);
    unisoundAsrEngine.release();
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_SERVER_VAD_ENABLE, false);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_LOCAL_VAD_ENABLE, true);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_RETURN_ORIGIN_RESULT, true);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_NLU_ENABLE, false);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_INIT_MODE, UnisoundAsrInitMode.ONLINE);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_USE_PUNCTUATED, true);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_ADDITIONAL_SERVICE, "app_dinobot_service");
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_SELF_NLU_PARAMS,
        "returnType=json;version=v2;sessionId=0;userId=test0000;context=0");
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_ASR_DOMAIN, "medical");
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_TR_ADDRESS, "tr.sh.hivoice.cn:80");
    unisoundAsrEngine.init();
  }

  public void onStartAsr(View v) {
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_ASR_MODE, UnisoundAsrMode.ONLINE);
    buffer = new StringBuffer();
    textResult.setText(buffer.toString());
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
        if (result.contains("processWithoutPuncResult")) {
          if (result.contains("processResult")) {
            String processResult = asrResultOperate(result);
            if (!TextUtils.isEmpty(processResult)) {
              buffer.append(processResult);
              textResult.setText(buffer.toString());
              unisoundAsrEngine.startAsr(false);
            } else {
              unisoundAsrEngine.cancel();
            }
          } else {
            unisoundAsrEngine.cancel();
          }
        }
      } catch (Exception e) {

      }
    }
  }

  @Override public void onEvent(int event) {
    Log.d(TAG, "onEvent:" + event);
    if (event == AsrEvent.ASR_EVENT_VAD_SPEECH_END
        || event == AsrEvent.ASR_EVENT_VAD_FRONT_TIME_OUT) {
      unisoundAsrEngine.stopAsr(false);
    }
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

  private String asrResultOperate(String jsonResult) {
    String temp = "[" + jsonResult + "]";
    temp = temp.replace("}{", "},{");
    Log.d(TAG, "onResult temp:" + temp);
    String result = null;
    try {
      JSONArray jsonObj = new JSONArray(temp);
      if (jsonObj.length() > 0) {
        for (int i = 0; i < jsonObj.length(); i++) {
          JSONObject jsonObject = jsonObj.getJSONObject(i);
          if (jsonObject.has("processResult")) {
            result = jsonObject.getString("processResult");
          }
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    } finally {
      return result;
    }
  }
}
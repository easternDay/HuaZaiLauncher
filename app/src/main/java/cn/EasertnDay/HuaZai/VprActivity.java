package cn.EasertnDay.HuaZai;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.unisound.vpr.UnisoundVprEngine;
import com.unisound.sdk.asr.AsrEvent;
import com.unisound.sdk.asr.AsrOption;
import com.unisound.sdk.asr.UnisoundAsrEngine;
import com.unisound.sdk.asr.impl.IAsrResultListener;
import cn.EasternDay.HuaZai.R;

public class VprActivity extends AppCompatActivity implements IAsrResultListener {
  private static final String TAG = "WakeupActivity";

  private UnisoundVprEngine unisoundVprEngine;
  private UnisoundAsrEngine unisoundAsrEngine;

  private Button butIdenVpr;
  private Button butRegisterVpr;
  private Button butDelete;
  private EditText editVpr;
  private boolean inRegister = false;
  private int registerCount = 0;
  private TextView txtResult;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_vpr);
    initView();
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    unisoundVprEngine = new UnisoundVprEngine(this);
    unisoundVprEngine.init();
    unisoundAsrEngine = VoicePresenter.getInstance().getUnisoundAsrEngine();
    unisoundAsrEngine.setUnisoundVprEngine(unisoundVprEngine);
    unisoundAsrEngine.setListener(this);
  }

  private void initView() {
    butIdenVpr = findViewById(R.id.butIdenVpr);
    butRegisterVpr = findViewById(R.id.butRegisterVpr);
    butDelete = findViewById(R.id.butDelete);
    editVpr = findViewById(R.id.editVpr);
    txtResult = findViewById(R.id.txtResult);

    butRegisterVpr.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        String name = editVpr.getText().toString();
        if (TextUtils.isEmpty(name)) {
          Toast.makeText(VprActivity.this, "请输入需要注册的名称", Toast.LENGTH_SHORT).show();
          return;
        }
        if (unisoundVprEngine.isExistName(name)) {
          Toast.makeText(VprActivity.this, "该名称已经注册过 请换一个吧", Toast.LENGTH_SHORT).show();
          return;
        }
        if (unisoundVprEngine.startRegister(name)) {
          unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_USE_VPR_IDENTIFY, false);
          unisoundAsrEngine.startWakeUp();
          Toast.makeText(VprActivity.this, "请说出三遍唤醒词", Toast.LENGTH_SHORT).show();
          inRegister = true;
          registerCount = 0;
        }
      }
    });
    butIdenVpr.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_USE_VPR_IDENTIFY, true);
        unisoundAsrEngine.startWakeUp();
      }
    });

    butDelete.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        unisoundVprEngine.deleteVprRegister(editVpr.getText().toString());
      }
    });
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    unisoundVprEngine.release();
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_USE_VPR_IDENTIFY, false);
    unisoundAsrEngine.setUnisoundVprEngine(null);
    unisoundAsrEngine.setListener(null);
  }

  @Override public void onResult(int event, String jsonResult) {
    if (event == AsrEvent.ASR_EVENT_WAKEUP_RESULT) {
      txtResult.setText(jsonResult);
      if (inRegister && registerCount <= 2) {
        byte[] wakeUpBuffer =
            (byte[]) unisoundAsrEngine.getOption(AsrOption.ASR_OPTION_WAKE_UP_BUFFER);
        if (unisoundVprEngine.register(wakeUpBuffer)) {
          registerCount++;
          int remainCount = 3 - registerCount;
          if (remainCount > 0) {
            Toast.makeText(VprActivity.this, "请再说" + remainCount + "遍唤醒词!", Toast.LENGTH_SHORT)
                .show();
          } else {
            if (unisoundVprEngine.stopRegister()) {
              Toast.makeText(VprActivity.this, "注册成功!", Toast.LENGTH_SHORT).show();
              inRegister = false;
            } else {
              Toast.makeText(VprActivity.this, "注册失败!", Toast.LENGTH_SHORT).show();
              unisoundVprEngine.stopRegister();
              inRegister = false;
            }
          }
        } else {
          Toast.makeText(VprActivity.this, "注册失败!", Toast.LENGTH_SHORT).show();
          unisoundVprEngine.stopRegister();
          inRegister = false;
        }
      }
    }
  }

  @Override public void onEvent(int event) {

  }

  @Override public void onError(int error) {

  }

  @Override public void onSessionId(String sessionId) {

  }
}
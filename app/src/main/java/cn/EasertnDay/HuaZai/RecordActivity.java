package cn.EasertnDay.HuaZai;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.unisound.sdk.asr.audiosource.IAudioSource;
import com.unisound.sdk.asr.audiosource.JniAudioSource;
import com.unisound.sdk.asr.param.AsrParams;
import com.unisound.sdk.utils.ByteArrayPool;
import com.unisound.sdk.utils.PcmUtils;
import com.unisound.sdk.utils.SdkLogMgr;
import com.unisound.sdk.utils.StringUtils;
import cn.EasternDay.HuaZai.R;

public class RecordActivity extends AppCompatActivity {
  private static final String TAG = "RecordActivity";

  private int recordDataChoice = 0;
  private TextView tvRecordData;
  private static String[] recordData = new String[] { "asr", "voip" };
  IAudioSource iAudioSource;
  private String fileName = "";
  private boolean isRun = false;
  private RecordThread thread;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_record);
    tvRecordData = findViewById(R.id.tv_record_data);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    iAudioSource = VoicePresenter.getInstance().getAudioSource();
  }

  public void onDataClick(View view) {
    showSingleChoiceDialog(recordData, tvRecordData, "录音数据", recordDataChoice);
  }

  public void onStartRecordClick(View view) {
    startRecord();
  }

  public void onStopRecordClick(View view) {
    stopRecord();
  }

  private void showSingleChoiceDialog(final String[] items, final TextView view, final String title,
      final int checkedItem) {
    AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(this);
    singleChoiceDialog.setTitle(title);
    singleChoiceDialog.setSingleChoiceItems(items, checkedItem,
        new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            recordDataChoice = which;
          }
        });
    singleChoiceDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        if (!(iAudioSource instanceof JniAudioSource)) {
          return;
        }
        if (recordDataChoice == 0) {
          ((JniAudioSource) iAudioSource).setUseAudioChannel(0);
        } else {
          ((JniAudioSource) iAudioSource).setUseAudioChannel(1);
        }
        view.setText(items[recordDataChoice]);
      }
    });
    singleChoiceDialog.show();
  }

  private String getFileName() {
    StringBuffer name = new StringBuffer();
    name.append(StringUtils.getDateStr());
    name.append("-");
    name.append(recordData[recordDataChoice]);
    name.append(".pcm");
    return name.toString();
  }

  public void startRecord() {
    SdkLogMgr.d(TAG, "startRecord");
    if (isRun) {
      return;
    }
    if (iAudioSource.openAudioIn() == 0) {
      fileName = getFileName();
      PcmUtils.markData(fileName, 1, AsrParams.isSaveRecord());
      isRun = true;
      if (thread == null) {
        thread = new RecordThread();
      }
      thread.start();
    }
  }

  public void stopRecord() {
    SdkLogMgr.d(TAG, "stopRecord:" + isRun);
    if (isRun) {
      try {
        isRun = false;
        thread.join();
        iAudioSource.closeAudioIn();
        PcmUtils.closePcm(fileName, AsrParams.isSaveRecord());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  protected void onDestroy() {
    super.onDestroy();
    stopRecord();
    isRun = false;
  }

  class RecordThread extends Thread {
    @Override public void run() {
      super.run();
      SdkLogMgr.d(TAG, "record run");
      while (isRun) {
        byte[] bytes = ByteArrayPool.getInstance(AsrParams.getRecordBufferSize()).getBuf();
        int size = iAudioSource.readData(bytes, bytes.length);
        SdkLogMgr.pro(TAG, "read buffer:" + size);
        if (size > 0) {
          PcmUtils.savePcm(fileName, bytes, size, AsrParams.isSaveRecord());
        }
      }
    }
  }
}

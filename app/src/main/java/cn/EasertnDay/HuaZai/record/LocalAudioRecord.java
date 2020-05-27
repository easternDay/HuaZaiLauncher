package cn.EasertnDay.HuaZai.record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import com.unisound.sdk.utils.SdkLogMgr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class LocalAudioRecord {
  private static final String TAG = "LocalAudioRecord";
  private AudioRecord audioRecord;
  private int simpleRate;
  private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
  private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
  private static int bufferSizeInBytes;
  private RecordThread recordThread;
  private Handler handler = new Handler(Looper.getMainLooper());

  public LocalAudioRecord(int simpleRate) {
    this.simpleRate = simpleRate;
  }

  public void stopRecord() {
    SdkLogMgr.d(TAG, "stopRecord");
    if (recordThread != null) {
      recordThread.cancel();
    }
    handler.removeCallbacksAndMessages(null);
    if (audioRecord != null) {
      if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
        audioRecord.stop();
      }
      audioRecord.release();
      audioRecord = null;
    }
  }

  public boolean startRecord(String filePath) {
    SdkLogMgr.d(TAG, "startRecord:filePath:" + filePath);
    try {
      stopRecord();
      if (audioRecord == null) {
        bufferSizeInBytes = AudioRecord.getMinBufferSize(this.simpleRate, CHANNEL, ENCODING);
        SdkLogMgr.d(TAG, "bufferSizeInBytes:" + bufferSizeInBytes);
        audioRecord =
            new AudioRecord(MediaRecorder.AudioSource.DEFAULT, simpleRate, CHANNEL, ENCODING,
                bufferSizeInBytes);
      }
      File file = new File(filePath);
      if (file.exists()) {
        file.delete();
        file.createNewFile();
      }
      if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
        audioRecord.startRecording();
        recordThread = new RecordThread(filePath);
        recordThread.start();
        return true;
      }
      return false;
    } catch (Exception e) {
      return false;
    }
  }

  public class RecordThread extends Thread {
    private boolean isRun = true;
    private OutputStream outputStream;
    private byte[] buffer = new byte[1200];

    public RecordThread(String path) {
      try {
        outputStream = new FileOutputStream(path);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override public void run() {
      while (isRun) {
        int read = LocalAudioRecord.this.audioRecord.read(buffer, 0, buffer.length);
        if (read > 0) {
          try {
            outputStream.write(buffer, 0, read);
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else {
          SdkLogMgr.e(TAG, "read error:" + read);
          try {
            Thread.sleep(20);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      if (outputStream != null) {
        try {
          outputStream.flush();
          outputStream.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      LocalAudioRecord.this.recordThread = null;
    }

    public void cancel() {
      try {
        isRun = false;
        join();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public int getSimpleRate() {
    return simpleRate;
  }
}

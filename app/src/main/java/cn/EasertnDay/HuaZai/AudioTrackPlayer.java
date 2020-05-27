package cn.EasertnDay.HuaZai;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.unisound.sdk.asr.impl.IAudioTrackPlayStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class AudioTrackPlayer {
  private static final String TAG = "AudioTrackPlayer";
  private AudioTrack audioTrack;
  private static final int STREAM_TYPE = AudioManager.STREAM_MUSIC;
  private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
  private static final int MODE = AudioTrack.MODE_STREAM;
  private int bufferSizeInBytes;
  private PlayThread playThread;
  private String fileName;
  private IAudioTrackPlayStatus iAudioTrackPlayStatus;
  private Handler handler = new Handler(Looper.getMainLooper());
  private int totalSize;
  private int totalTime;
  private int simpleRate;
  private int channelOut;

  public void setiAudioTrackPlayStatus(IAudioTrackPlayStatus iAudioTrackPlayStatus) {
    this.iAudioTrackPlayStatus = iAudioTrackPlayStatus;
  }

  public AudioTrackPlayer(int simpleRate) {
    this.simpleRate = simpleRate;
    this.channelOut = AudioFormat.CHANNEL_OUT_MONO;
    initBuffer();
  }

  public AudioTrackPlayer(int simpleRate, int channel) {
    this.simpleRate = simpleRate;
    this.channelOut = channel;
    initBuffer();
  }

  private void initBuffer() {
    bufferSizeInBytes = AudioTrack.getMinBufferSize(simpleRate, channelOut, ENCODING);
    Log.d(TAG, "bufferSizeInBytes:" + bufferSizeInBytes);
  }


  public void playSdcardFile(String path) {
    try {
      this.fileName = path.substring(0, path.lastIndexOf(".") - 1);
      File file = new File(path);
      InputStream inputStream = new FileInputStream(file);
      playInputStream(inputStream);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void playInputStream(InputStream inputStream) throws Exception {
    stop();
    audioTrack =
        new AudioTrack(STREAM_TYPE, simpleRate, channelOut, ENCODING, bufferSizeInBytes, MODE);
    totalSize = inputStream.available();
    totalTime = calcTime(totalSize, simpleRate);
    byte[] bytes = new byte[totalSize];
    inputStream.read(bytes);
    inputStream.close();
    playThread = new PlayThread(bytes, this);
    playThread.start();
    resume();
  }

  public void pause() {
    if (audioTrack != null) {
      audioTrack.pause();
      onPlayPause();
    }
  }

  public void resume() {
    if (audioTrack != null) {
      audioTrack.play();
      onPlayStart();
    }
  }

  public void stop() {
    if (playThread != null) {
      playThread.cancel();
      playThread = null;
    }
    handler.removeCallbacksAndMessages(null);
    if (audioTrack != null) {
      audioTrack.release();
      audioTrack = null;
    }
  }

  public static class PlayThread extends Thread {
    private boolean isRun = true;
    private float seekValue = -1.0f;
    private byte[] bytes;
    private int currentSize;
    private AudioTrackPlayer audioTrackPlayer;

    public PlayThread(byte[] bytes, AudioTrackPlayer audioTrackPlayer) {
      this.bytes = bytes;
      this.audioTrackPlayer = audioTrackPlayer;
      this.currentSize = 0;
    }

    public void seek(float value) {
      seekValue = value;
    }

    @Override public void run() {
      Log.d(TAG, "audioTrack.play");
      while (isRun) {
        if (audioTrackPlayer.audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
          try {
            if (seekValue >= 0) {
              currentSize = (int) (bytes.length * seekValue);
              seekValue = -1.0f;
              currentSize = currentSize - currentSize % 2;
            }
            int read = Math.min(audioTrackPlayer.bufferSizeInBytes, bytes.length - currentSize);
            if (read > 0) {
              audioTrackPlayer.audioTrack.write(bytes, currentSize, read);
              currentSize += read;
              audioTrackPlayer.onPlayDuration(currentSize, audioTrackPlayer.totalSize);
            } else {
              audioTrackPlayer.pause();
              audioTrackPlayer.onPlayEnd();
            }
          } catch (Exception e) {
            Log.d(TAG, "audioTrack.Exception:" + e.toString());
            isRun = false;
            e.printStackTrace();
            audioTrackPlayer.onPlayError();
          }
        } else {
          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
      audioTrackPlayer.playThread = null;
    }

    private void cancel() {
      try {
        isRun = false;
        join();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void onPlayError() {
    Log.d(TAG, "onPlayError:" + fileName);
    handler.post(new Runnable() {
      @Override public void run() {
        if (iAudioTrackPlayStatus != null) {
          iAudioTrackPlayStatus.playError(fileName);
        }
      }
    });
  }

  private void onPlayEnd() {
    Log.d(TAG, "onPlayEnd:" + fileName);
    handler.post(new Runnable() {
      @Override public void run() {
        if (iAudioTrackPlayStatus != null) {
          iAudioTrackPlayStatus.playEnd(fileName);
        }
      }
    });
  }

  private void onPlayStart() {
    Log.d(TAG, "onPlayStart:" + fileName);
    handler.post(new Runnable() {
      @Override public void run() {
        if (iAudioTrackPlayStatus != null) {
          iAudioTrackPlayStatus.playStart(fileName);
        }
      }
    });
  }

  private void onPlayPause() {
    Log.d(TAG, "onPlayPause:" + fileName);
    handler.post(new Runnable() {
      @Override public void run() {
        if (iAudioTrackPlayStatus != null) {
          iAudioTrackPlayStatus.playPause(fileName);
        }
      }
    });
  }

  private void onPlayDuration(final int duration, final int total) {
    final int currentTime = calcTime(duration, simpleRate);
    handler.post(new Runnable() {
      @Override public void run() {
        if (iAudioTrackPlayStatus != null) {
          iAudioTrackPlayStatus.playDuration(currentTime, totalTime, fileName);
        }
      }
    });
  }

  public static int calcTime(long size, int simpleRate) {
    return (int) ((1000 * size) / (simpleRate * 1 * (16 / 8)));
  }

  public void seek(float value) {
    if (value < 0) {
      value = 0;
    }
    if (value > 1.0f) {
      value = 1.0f;
    }
    if (playThread != null) {
      playThread.seek(value);
      resume();
    }
  }

  public static int calcTime(String fileName, int simpleRate) {
    try {
      File file = new File(fileName);
      InputStream inputStream = new FileInputStream(file);
      int size = inputStream.available();
      inputStream.close();
      return calcTime(size, simpleRate);
    } catch (Exception e) {
      return 0;
    }
  }
}

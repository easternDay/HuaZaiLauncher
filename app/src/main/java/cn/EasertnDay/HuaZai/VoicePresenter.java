package cn.EasertnDay.HuaZai;

import android.content.Context;
import android.media.AudioFormat;
import android.text.TextUtils;
import com.unisound.sdk.asr.AsrOption;
import com.unisound.sdk.asr.UnisoundAsrEngine;
import com.unisound.sdk.asr.audiosource.AecAudioSource;
import com.unisound.sdk.asr.audiosource.AndroidRecordAudioSource;
import com.unisound.sdk.asr.audiosource.IAudioSource;
import com.unisound.sdk.asr.impl.IAsrResultListener;
import com.unisound.sdk.asr.param.UnisoundAsrInitMode;
import com.unisound.sdk.tts.TtsEvent;
import com.unisound.sdk.tts.TtsOption;
import com.unisound.sdk.tts.UnisoundTtsEngine;
import com.unisound.sdk.tts.audiotrack.AndroidAudioTrack;
import com.unisound.sdk.tts.impl.ITtsEventListener;
import com.unisound.sdk.tts.param.UnisoundTtsInitMode;
import com.unisound.sdk.utils.AssetsUtils;

import java.io.File;

public class VoicePresenter {

  private UnisoundAsrEngine unisoundAsrEngine;
  private IAudioSource iAudioSource;
  private UnisoundTtsEngine unisoundTtsEngine;
  private AndroidAudioTrack androidAudioTrackOnline = new AndroidAudioTrack(16000);
  private AndroidAudioTrack androidAudioTrackOffline = new AndroidAudioTrack(22050);
  private Context context;

  private static VoicePresenter voicePresenter = new VoicePresenter();

  public synchronized static VoicePresenter getInstance() {
    return voicePresenter;
  }

  private VoicePresenter() {
  }

  public void init(Context context) {
    this.context = context;
    initTTS();
    initAsr();
  }

  public void initAsr() {
    if (unisoundAsrEngine != null) {
      return;
    }
    if (SdkParam.getInstance().getAudioSourceType() == AudioSourceType.AEC) {
      iAudioSource =
          new AecAudioSource(new AndroidRecordAudioSource(AudioFormat.CHANNEL_IN_STEREO));
    } else {
      iAudioSource = new AndroidRecordAudioSource(AudioFormat.CHANNEL_IN_MONO);
    }
    unisoundAsrEngine = new UnisoundAsrEngine(context, SdkParam.getInstance().getAppKey(),
        SdkParam.getInstance().getAppSecret(), iAudioSource, true);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_USE_OFFLINE_COMPILE, true);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_INIT_MODE, UnisoundAsrInitMode.MIX);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_SERVER_VAD_ENABLE, true);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_FILTER_NAME,
        SdkParam.getInstance().getFilterName());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_NLU_ENABLE, true);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_LOCAL_VAD_ENABLE, true);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_RESULT_WITH_TYPE, false);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_PRINT_DEBUG_LOG, SdkParam.getInstance().isDebugPro());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_SAVE_RECORD,
        SdkParam.getInstance().isSaveRecord());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_ASR_DOMAIN,
        SdkParam.getInstance().getAsrDomain());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_NLU_SCENARIO,
        SdkParam.getInstance().getNluScenario());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_DEVICE_ACTIVE, true);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_CALLBACK_VOLUME, false);
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_VAD_FRONT_TIME,
        SdkParam.getInstance().getVadStartSil());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_VAD_BACK_TIME,
        SdkParam.getInstance().getVadBackSil());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_FALSE_ALARM,
        SdkParam.getInstance().getFalseAlarm());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_CONFIDENCE_MEASURE,
        SdkParam.getInstance().isConfidenceMeasure());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_CONFIDENCE_THRESHOLD,
        SdkParam.getInstance().getConfidenceThreshold());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_VOICE_FIELD,
        SdkParam.getInstance().getVoiceField());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_ONLINE_WAKE_WORD_SCORE,
        SdkParam.getInstance().getOnlineWakeUpWordScore());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_WAKE_UP_MODEL_ID,
        SdkParam.getInstance().getWakeUpModel());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_WAKE_UP_SCORE,
        SdkParam.getInstance().getWakeUpScore());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_WAKE_UP_SCORE_LOW,
        SdkParam.getInstance().getWakeUpScoreLow());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_WAKE_UP_SCORE_HIGH,
        SdkParam.getInstance().getWakeUpHigh());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_WAKE_UP_INTERVAL,
        SdkParam.getInstance().getWakeUpInterval());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_WAKE_UP_SLEEP_INTERVAL,
        SdkParam.getInstance().getWakeUpSleepTime());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_OPUS_COMPRESS_RATIO,
        SdkParam.getInstance().getOpusCompressRatio());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_BEST_RESULT_RETURN,
        SdkParam.getInstance().isBestResultReturn());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_DOMAINS_PENALTY,
        SdkParam.getInstance().getDomainsPenalty());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_DEVICE_UDID,
        SdkParam.getInstance().getDeviceUdid());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_MAIN_WAKE_UP_WORDS,
        SdkParam.getInstance().getMainWakeUpList());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_ONLINE_CHECK_WAKE_WORD,
        SdkParam.getInstance().isOnlineCheckWakeWord());
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_TR_ADDRESS,
        SdkParam.getInstance().getAsrTrAddress());
    if (!TextUtils.isEmpty(SdkParam.getInstance().getSelfEngineParam())) {
      unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_SELF_ENGINE_PARAMS,
          SdkParam.getInstance().getSelfEngineParam());
    }
    unisoundAsrEngine.setOption(AsrOption.ASR_OPTION_ARPT_KWS_LOG, true);
    unisoundAsrEngine.init();
  }

  public IAudioSource getAudioSource() {
    return iAudioSource;
  }

  private void initTTS() {
    unisoundTtsEngine = new UnisoundTtsEngine(context, SdkParam.getInstance().getAppKey(),
        SdkParam.getInstance().getAppSecret(), androidAudioTrackOnline, false);
    unisoundTtsEngine.setTtsOption(TtsOption.TTS_INIT_MODE, UnisoundTtsInitMode.MIX);
    if (!(new File(Config.TTS_PATH + Config.FRONTEND_MODEL).exists())) {
      AssetsUtils.copyAssetsFile(context, Config.FRONTEND_MODEL,
          Config.TTS_PATH + Config.FRONTEND_MODEL, false);
    }
    unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_FRONT_MODEL_PATH,
        Config.TTS_PATH + Config.FRONTEND_MODEL);
    if (!(new File(Config.TTS_PATH + Config.BACKEND_MODEL).exists())) {
      AssetsUtils.copyAssetsFile(context, Config.BACKEND_MODEL,
          Config.TTS_PATH + Config.BACKEND_MODEL, false);
    }
    unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_BACK_MODEL_PATH,
        Config.TTS_PATH + Config.BACKEND_MODEL);
    unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_SPEED, 70);
    unisoundTtsEngine.setTtsOption(TtsOption.TTS_OPTION_END_DELAY, 0);
    unisoundTtsEngine.setListener(new ITtsEventListener() {
      @Override public void onEvent(int event) {
        if (event == TtsEvent.TTS_EVENT_PLAY_END) {

        }
      }

      @Override public void onError(int error) {

      }
    });
    unisoundTtsEngine.init();
  }

  public void release() {
    if (unisoundAsrEngine != null) {
      unisoundAsrEngine.release();
      unisoundAsrEngine = null;
    }
    if (unisoundTtsEngine != null) {
      unisoundTtsEngine.release();
      unisoundTtsEngine = null;
    }
  }

  public void setAudioSource() {
    unisoundAsrEngine.setAudioSource(iAudioSource);
  }

  public UnisoundAsrEngine getUnisoundAsrEngine() {
    return unisoundAsrEngine;
  }

  public UnisoundTtsEngine getUnisoundTtsEngine() {
    return unisoundTtsEngine;
  }

  public void setAsrListener(IAsrResultListener listener) {
    unisoundAsrEngine.setListener(listener);
  }
}

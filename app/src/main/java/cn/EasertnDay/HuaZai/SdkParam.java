package cn.EasertnDay.HuaZai;

import android.os.Environment;
import com.unisound.json.JsonTool;
import com.unisound.sdk.utils.SdkLogMgr;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SdkParam {
  private static final String TAG = "SdkParam";
  private String appKey = "rxme5kxzyaaujkrdskrufkmcswodxoa4k2ondgqj";
  private String appSecret = "b82a378808c253bdc58ab86413d21068";

  private float wakeUpScoreLow = -1.12f;
  private float wakeUpScore = 0.06f;
  private float wakeUpHigh = 0.95f;
  private float onlineWakeUpWordScore = -1.93f;
  private int wakeUpInterval = 8;
  private int wakeUpSleepTime = 30 * 60;
  private int wakeUpModel = 1054;
  private String filterName = "search";
  private String asrDomain = "general,song,movietv";
  private String nluScenario = "smarthome";
  private String deviceUdid = "abcde";
  private String asrTrAddress ="tr.hivoice.cn:80";
  private AudioSourceType audioSourceType = AudioSourceType.JNI;
  private boolean saveRecord = false;
  private float vadStartSil = 5.0f;
  private float vadBackSil = 0.5f;
  private int falseAlarm = 0;
  private boolean confidenceMeasure = false;
  private float confidenceThreshold = 0.0f;
  private String selfEngineParam = "";
  private String voiceField = "far";
  private int opusCompressRatio = 8;
  private boolean bestResultReturn = false;
  private int domainsPenalty = 1;
  private boolean onlineCheckWakeWord = true;
  private List<String> mainWakeUpList = new ArrayList<>();

  private boolean isDebugPro = false;

  public SdkParam() {
    mainWakeUpList.add("小康小康");
  }

  public boolean isOnlineCheckWakeWord() {
    return onlineCheckWakeWord;
  }

  public void setOnlineCheckWakeWord(boolean onlineCheckWakeWord) {
    this.onlineCheckWakeWord = onlineCheckWakeWord;
  }

  public String getAsrTrAddress() {
    return asrTrAddress;
  }

  public void setAsrTrAddress(String asrTrAddress) {
    this.asrTrAddress = asrTrAddress;
  }

  public List<String> getMainWakeUpList() {
    return mainWakeUpList;
  }

  public void setMainWakeUpList(List<String> mainWakeUpList) {
    this.mainWakeUpList = mainWakeUpList;
  }

  public int getDomainsPenalty() {
    return domainsPenalty;
  }

  public void setDomainsPenalty(int domainsPenalty) {
    this.domainsPenalty = domainsPenalty;
  }

  public boolean isBestResultReturn() {
    return bestResultReturn;
  }

  public void setBestResultReturn(boolean bestResultReturn) {
    this.bestResultReturn = bestResultReturn;
  }

  public int getOpusCompressRatio() {
    return opusCompressRatio;
  }

  public void setOpusCompressRatio(int opusCompressRatio) {
    this.opusCompressRatio = opusCompressRatio;
  }

  public String getAppKey() {
    return appKey;
  }

  public void setAppKey(String appKey) {
    this.appKey = appKey;
  }

  public String getAppSecret() {
    return appSecret;
  }

  public void setAppSecret(String appSecret) {
    this.appSecret = appSecret;
  }

  public String getVoiceField() {
    return voiceField;
  }

  public void setVoiceField(String voiceField) {
    this.voiceField = voiceField;
  }

  public String getSelfEngineParam() {
    return selfEngineParam;
  }

  public void setSelfEngineParam(String selfEngineParam) {
    this.selfEngineParam = selfEngineParam;
  }

  public int getFalseAlarm() {
    return falseAlarm;
  }

  public void setFalseAlarm(int falseAlarm) {
    this.falseAlarm = falseAlarm;
  }

  public boolean isConfidenceMeasure() {
    return confidenceMeasure;
  }

  public void setConfidenceMeasure(boolean confidenceMeasure) {
    this.confidenceMeasure = confidenceMeasure;
  }

  public float getConfidenceThreshold() {
    return confidenceThreshold;
  }

  public void setConfidenceThreshold(float confidenceThreshold) {
    this.confidenceThreshold = confidenceThreshold;
  }

  public float getVadStartSil() {
    return vadStartSil;
  }

  public void setVadStartSil(float vadStartSil) {
    this.vadStartSil = vadStartSil;
  }

  public float getVadBackSil() {
    return vadBackSil;
  }

  public void setVadBackSil(float vadBackSil) {
    this.vadBackSil = vadBackSil;
  }

  public AudioSourceType getAudioSourceType() {
    return audioSourceType;
  }

  public void setAudioSourceType(AudioSourceType audioSourceType) {
    this.audioSourceType = audioSourceType;
  }

  public boolean isSaveRecord() {
    return saveRecord;
  }

  public void setSaveRecord(boolean saveRecord) {
    this.saveRecord = saveRecord;
  }

  public float getWakeUpScoreLow() {
    return wakeUpScoreLow;
  }

  public void setWakeUpScoreLow(float wakeUpScoreLow) {
    this.wakeUpScoreLow = wakeUpScoreLow;
  }

  public float getWakeUpScore() {
    return wakeUpScore;
  }

  public void setWakeUpScore(float wakeUpScore) {
    this.wakeUpScore = wakeUpScore;
  }

  public float getWakeUpHigh() {
    return wakeUpHigh;
  }

  public void setWakeUpHigh(float wakeUpHigh) {
    this.wakeUpHigh = wakeUpHigh;
  }

  public int getWakeUpInterval() {
    return wakeUpInterval;
  }

  public void setWakeUpInterval(int wakeUpInterval) {
    this.wakeUpInterval = wakeUpInterval;
  }

  public int getWakeUpSleepTime() {
    return wakeUpSleepTime;
  }

  public void setWakeUpSleepTime(int wakeUpSleepTime) {
    this.wakeUpSleepTime = wakeUpSleepTime;
  }

  public int getWakeUpModel() {
    return wakeUpModel;
  }

  public void setWakeUpModel(int wakeUpModel) {
    this.wakeUpModel = wakeUpModel;
  }

  public String getFilterName() {
    return filterName;
  }

  public void setFilterName(String filterName) {
    this.filterName = filterName;
  }

  public String getAsrDomain() {
    return asrDomain;
  }

  public void setAsrDomain(String asrDomain) {
    this.asrDomain = asrDomain;
  }

  public String getNluScenario() {
    return nluScenario;
  }

  public void setNluScenario(String nluScenario) {
    this.nluScenario = nluScenario;
  }

  public String getDeviceUdid() {
    return deviceUdid;
  }

  public void setDeviceUdid(String deviceUdid) {
    this.deviceUdid = deviceUdid;
  }

  public boolean isDebugPro() {
    return isDebugPro;
  }

  public void setDebugPro(boolean debugPro) {
    isDebugPro = debugPro;
  }

  private static final String CONFIG_FILE_PATH =
      Environment.getExternalStorageDirectory() + File.separator + "unisound" + File.separator
          + "sdk.ini";

  private static SdkParam build() {
    SdkParam sdkParam = new SdkParam();
    SdkLogMgr.d(TAG, "file path:" + CONFIG_FILE_PATH);
    File file = new File(CONFIG_FILE_PATH);
    if (file.isFile() && file.exists()) {
      SdkLogMgr.d(TAG, "sdk config file exist");
      String value = readTxtFile(CONFIG_FILE_PATH);
      SdkParam sdkParam2 = JsonTool.fromJson(value, SdkParam.class);
      SdkLogMgr.d(TAG, "sdk2:" + JsonTool.toJson(sdkParam2));
      if (sdkParam2 != null) {
        sdkParam = sdkParam2;
      }
    }
    SdkLogMgr.d(TAG, "sdk:" + JsonTool.toJson(sdkParam));
    return sdkParam;
  }

  public static SdkParam sdkParam = build();

  public static SdkParam getInstance() {
    return sdkParam;
  }

  public float getOnlineWakeUpWordScore() {
    return onlineWakeUpWordScore;
  }

  public void setOnlineWakeUpWordScore(float onlineWakeUpWordScore) {
    this.onlineWakeUpWordScore = onlineWakeUpWordScore;
  }

  public static String readTxtFile(String strFilePath) {
    StringBuilder content = new StringBuilder();
    File file = new File(strFilePath);
    if (file.exists() && file.isFile()) {
      try {
        InputStream inputStream = new FileInputStream(file);
        if (inputStream != null) {
          InputStreamReader inputReader = new InputStreamReader(inputStream);
          BufferedReader bufferReader = new BufferedReader(inputReader);
          String line;
          while ((line = bufferReader.readLine()) != null) {
            content.append(line);
          }
          inputStream.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
        SdkLogMgr.e(TAG, e.toString());
      }
    }
    return content.toString();
  }
}

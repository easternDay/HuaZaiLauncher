package cn.EasertnDay.HuaZai;

import android.os.Environment;

import java.io.File;

public class Config {
  private Config() {

  }


  public static final String MEDICAL_APP_KEY = "45gn7md5n44aak7a57rdjud3b5l4xdgv75saomys";
  public static final String MEDICAL_APP_SECRET = "ba24a917a38e11e49c6fb82a72e0d896";

  public static final String TTS_PATH =
      Environment.getExternalStorageDirectory() + File.separator + "unisound" + File.separator
          + "tts" + File.separator;
  public final static String FRONTEND_MODEL = "frontend_model_offline_v10.0.1";
  public final static String BACKEND_MODEL = "backend_kiyo_lpc2wav_22k_pf_mixed_v1.0.0";
}

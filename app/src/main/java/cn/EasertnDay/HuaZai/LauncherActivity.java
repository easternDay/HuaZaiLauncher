package cn.EasertnDay.HuaZai;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.media.session.MediaSession;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.*;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import cn.EasertnDay.HuaZai.SecondaryPage.*;
import cn.EasternDay.HuaZai.R;
import com.unisound.sdk.asr.AsrEvent;
import com.unisound.sdk.asr.AsrOption;
import com.unisound.sdk.asr.UnisoundAsrEngine;
import com.unisound.sdk.asr.impl.IAsrResultListener;
import com.unisound.sdk.utils.AssetsUtils;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class LauncherActivity extends AppCompatActivity implements IAsrResultListener {

    Activity context = this;

    Button SettingButton;//设置按钮
    TextView Say;//华仔提示

    String beforeResult = "";

    SeekBar VolumeSlider;//滑动条
    ImageView VolumeBar;//滑动条背景
    AudioManager audioManager;//媒体音量
    BatteryManager batteryManager;//系统电量
    ImageView BatteryIcon;//电量亮图标
    WifiManager wifiManager;//WIFI信号
    WifiInfo wifiInfo;//WIFI信息
    ImageView WifiIcon;//WIFI图标

    //语音识别需要的
    private UnisoundAsrEngine unisoundAsrEngine;
    private ToneGenerator beeper;
    private HashSet<String> wakeUpList = new HashSet<>();
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 22) {
                String model = (String) msg.obj;
                Say.setText(model);
            }
            unisoundAsrEngine.startWakeUp();
        }
    };

    //定时触发
    /*
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 66;
            handler.sendMessage(message);
        }
    };

     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        SettingButton = findViewById(R.id.button2set);//设置按钮
        Say = findViewById(R.id.textView);//华仔提示

        VolumeSlider = findViewById(R.id.VolumeSlider);//滑动条
        VolumeBar = findViewById(R.id.VolumeBar);//滑动条背景

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);//媒体音量

        batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);//系统电量
        BatteryIcon = findViewById(R.id.batteryIcon);//电量图标

        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);//WIFI信号
        wifiInfo = wifiManager.getConnectionInfo();//WIFI信息
        WifiIcon = findViewById(R.id.wifiIcon);//WIFI图标

        //定时器
        //timer.schedule(task, 1000 * 5, 1000 * 1); //启动timer

        //增加唤醒词
        wakeUpList.add("你好华仔");
        wakeUpList.add("你好");
        //哔声发声器
        beeper = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        //模型复制
        VoicePresenter.getInstance().init(this.getApplicationContext());
        copyTtsModel();
        //延时操作
        String token = (String) VoicePresenter.getInstance()
                .getUnisoundAsrEngine()
                .getOption(AsrOption.ASR_OPTION_DEVICE_TOKEN);
        Log.w("WDNMD", token);

        unisoundAsrEngine = VoicePresenter.getInstance().getUnisoundAsrEngine();
        VoicePresenter.getInstance().setAsrListener((IAsrResultListener) context);
        unisoundAsrEngine.setWakeUpWord(wakeUpList, wakeUpList);

        if (unisoundAsrEngine.startWakeUp()) {
            Toast.makeText(context, "唤醒成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "唤醒失败", Toast.LENGTH_SHORT).show();
        }

        //触摸设置进入调试
        SettingButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent();
                intent.setClass(context, HuaZaiActivity.class);
                //startActivity(intent);
                return false;
            }
        });

        //电量监听
        this.registerReceiver(this.mBatteryReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));

        //WIFI信号监听
        this.registerReceiver(this.mWIfiReceiver, new IntentFilter(
                WifiManager.RSSI_CHANGED_ACTION));
        this.registerReceiver(this.mWIfiReceiver, new IntentFilter(
                WifiManager.NETWORK_STATE_CHANGED_ACTION));
        this.registerReceiver(this.mWIfiReceiver, new IntentFilter(
                WifiManager.WIFI_STATE_CHANGED_ACTION));

        //设置音量条为媒体音量大小
        int nowPro = ((audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) - 1) / 10 + 1;
        VolumeSlider.setProgress(nowPro * 10);
        //Log.w("我带你们打", String.format("当前：%s", nowPro));
        switch (nowPro) {
            case 1:
                VolumeBar.setBackgroundResource(R.drawable.voice1);
                break;
            case 2:
                VolumeBar.setBackgroundResource(R.drawable.voice2);
                break;
            case 3:
                VolumeBar.setBackgroundResource(R.drawable.voice3);
                break;
            case 4:
                VolumeBar.setBackgroundResource(R.drawable.voice4);
                break;
            case 5:
                VolumeBar.setBackgroundResource(R.drawable.voice5);
                break;
            case 6:
                VolumeBar.setBackgroundResource(R.drawable.voice6);
                break;
            case 7:
                VolumeBar.setBackgroundResource(R.drawable.voice7);
                break;
            case 8:
                VolumeBar.setBackgroundResource(R.drawable.voice8);
                break;
            case 9:
                VolumeBar.setBackgroundResource(R.drawable.voice9);
                break;
            case 10:
                VolumeBar.setBackgroundResource(R.drawable.voice10);
                break;
            default:
                VolumeBar.setBackgroundResource(R.drawable.voice0);
                break;
        }

        //音量调滑动更改
        VolumeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float pro = seekBar.getProgress();
                    float max = seekBar.getMax();
                    float result = (pro / max) * 100;
                    //设置音量
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (result * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 100), AudioManager.FLAG_PLAY_SOUND);
                    Log.w("我带你们打", String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)));
                    if (result == 0) {
                        seekBar.setProgress(0);
                    } else if (result > 0 && result <= 10) {
                        VolumeBar.setBackgroundResource(R.drawable.voice1);
                    } else if (result > 10 && result <= 20) {
                        VolumeBar.setBackgroundResource(R.drawable.voice2);
                    } else if (result > 20 && result <= 30) {
                        VolumeBar.setBackgroundResource(R.drawable.voice3);
                    } else if (result > 30 && result <= 40) {
                        VolumeBar.setBackgroundResource(R.drawable.voice4);
                    } else if (result > 40 && result <= 50) {
                        VolumeBar.setBackgroundResource(R.drawable.voice5);
                    } else if (result > 50 && result <= 60) {
                        VolumeBar.setBackgroundResource(R.drawable.voice6);
                    } else if (result > 60 && result <= 70) {
                        VolumeBar.setBackgroundResource(R.drawable.voice7);
                    } else if (result > 70 && result <= 80) {
                        VolumeBar.setBackgroundResource(R.drawable.voice8);
                    } else if (result > 80 && result <= 90) {
                        VolumeBar.setBackgroundResource(R.drawable.voice9);
                    } else if (result > 90 && result <= 100) {
                        VolumeBar.setBackgroundResource(R.drawable.voice10);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    //重写onBackPressed方法组织super即可实现禁止返回上一层页面
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Toast.makeText(this, "小朋友你好，我是华仔机器人，我集成了最新的UI技术！", Toast.LENGTH_SHORT).show();
    }

    //设置按钮点击监听（全部）
    public void onClick(View v) {
        Intent myIntent;
        switch (v.getId()) {
            //设置按钮的页面跳转-设置页面
            case R.id.button2set:
                Toast.makeText(this, "打开了设置页面", Toast.LENGTH_SHORT).show();

                //跳转设置页面
                //TODO
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_SETTINGS);
                context.startActivity(intent);
                break;
            case R.id.button_KWTBX:
                myIntent = new Intent(context, KWTBX.class);
                startActivity(myIntent);
                break;
            case R.id.button_ZXXL:
                myIntent = new Intent(context, ZXXL.class);
                startActivity(myIntent);
                break;
            case R.id.button_TZXX:
                myIntent = new Intent(context, TZXX.class);
                startActivity(myIntent);
                break;
            case R.id.button_GJX:
                myIntent = new Intent(context, GJX.class);
                startActivity(myIntent);
                break;
            case R.id.HuazaiImage:
                myIntent = new Intent(context, VoicePage.class);
                startActivity(myIntent);
                break;
            default:
                break;
        }
    }

    //电量监听服务
    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            //充电状态
            int status = arg1.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
            //电量信息
            int level = arg1.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = arg1.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
            int levelPercent = (int) (((float) level / scale) * 100);

            //Log.w("我带你们打", String.format("电量：%s", levelPercent));

            //状态选择
            switch (status) {
                //充电中&&充电完成
                case BatteryManager.BATTERY_STATUS_CHARGING:
                case BatteryManager.BATTERY_STATUS_FULL:
                    if (levelPercent <= 25) {
                        BatteryIcon.setImageResource(R.drawable.electric7);
                    } else if (levelPercent <= 50) {
                        BatteryIcon.setImageResource(R.drawable.electric4);
                    } else if (levelPercent <= 75) {
                        BatteryIcon.setImageResource(R.drawable.electric5);
                    } else if (levelPercent <= 100) {
                        BatteryIcon.setImageResource(R.drawable.electric6);
                    }
                    break;
                //放电中&&未充电
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    if (levelPercent <= 25) {
                        BatteryIcon.setImageResource(R.drawable.electric0);
                    } else if (levelPercent <= 50) {
                        BatteryIcon.setImageResource(R.drawable.electric1);
                    } else if (levelPercent <= 75) {
                        BatteryIcon.setImageResource(R.drawable.electric2);
                    } else if (levelPercent <= 100) {
                        BatteryIcon.setImageResource(R.drawable.electric3);
                    }
                    break;
            }
        }
    };

    //信号强度监听
    private BroadcastReceiver mWIfiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //信号强度变化
            if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
                if (wifiInfo.getBSSID() != null) {
                    int strength = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 3);
                    //Log.w("我带你们打", String.format("当前信号:%d", strength));
                    switch (strength) {
                        case 0:
                            WifiIcon.setImageResource(R.drawable.wifi1);
                            break;
                        case 1:
                            WifiIcon.setImageResource(R.drawable.wifi2);
                            break;
                        case 2:
                            WifiIcon.setImageResource(R.drawable.wifi3);
                            break;
                        default:
                            WifiIcon.setImageResource(R.drawable.wifierr);
                            break;
                    }
                }
            }
            //wifi连接上与否
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    WifiIcon.setImageResource(R.drawable.wifierr);
                    Log.i("我带你们打", "wifi断开");
                }
            }
            //wifi打开与否
            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
                if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
                    WifiIcon.setImageResource(R.drawable.wifierr);
                    Log.i("我带你们打", "系统关闭wifi");
                }
            }
        }
    };

    @Override
    public void onResult(int event, String result) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        Log.d("我带你们打", runningActivity);
        Log.d("我带你们打", String.valueOf(runningActivity.indexOf("LauncherActivity") != -1));
        Log.d("我带你们打", String.valueOf(runningActivity.indexOf("VoicePage") != -1));
        Log.d("我带你们打", String.valueOf(event == AsrEvent.ASR_EVENT_ASR_RESULT));
        if (event == AsrEvent.ASR_EVENT_WAKEUP_RESULT) {
            beeper.startTone(ToneGenerator.TONE_DTMF_S, 30);
            if (SdkParam.getInstance().getAudioSourceType() == AudioSourceType.JNI) {
                if (runningActivity.indexOf("LauncherActivity") != -1) {
                    Intent myIntent = new Intent(context, VoicePage.class);
                    startActivity(myIntent);
                } else {
                    unisoundAsrEngine.startAsr(false);
                }
            }
        }
        if (event == AsrEvent.ASR_EVENT_ASR_RESULT && runningActivity.indexOf("VoicePage") != -1) {
            Log.d("我带你们打", result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String asrResult = jsonObject.getString("asr_recongize");
                if (!TextUtils.isEmpty(asrResult)) {
                    Say.setText(asrResult);

                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                            .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
                            .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
                            .build();
                    final Request request = new Request.Builder()
                            .url("http://39.98.123.42:5000/chatbot?question=" + asrResult)
                            .get()//默认就是GET请求，可以不写
                            .build();
                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String ret = Objects.requireNonNull(response.body()).string();
                            Log.d("我带你们打", "返回的ret: " + ret);
                            try {
                                JSONObject resultObject = new JSONObject(ret);
                                String myAsrResult = resultObject.getString("data");
                                Log.d("我带你们打", "onResponse: " + myAsrResult);
                                if (!ret.isEmpty()) {
                                    //Say.setText(myAsrResult);
                                    handler.sendMessage(handler.obtainMessage(22, myAsrResult));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                //Say.setText("华仔听不懂，请下次再来问我吧！");
                                handler.sendMessage(handler.obtainMessage(22, "华仔听不懂，请下次再来问我吧！"));
                            }
                            unisoundAsrEngine.startWakeUp();
                            unisoundAsrEngine.startAsr(false);
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("我带你们打", "onFailure: " + e.toString());
                        }
                    });
                }
            } catch (Exception e) {
                unisoundAsrEngine.startWakeUp();
                unisoundAsrEngine.startAsr(false);
            }
        }
    }

    @Override
    public void onEvent(int event) {

    }

    @Override
    public void onError(int error) {
        unisoundAsrEngine.startWakeUp();
    }

    @Override
    public void onSessionId(String sessionId) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(null);
        unisoundAsrEngine.cancel(true);
    }

    private void copyTtsModel() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AssetManager assetManager = context.getAssets();
                try {
                    String[] files = assetManager.list("");
                    for (String file : files) {
                        if (file.startsWith("frontend") || file.startsWith("backend")) {
                            if (!(new File(Config.TTS_PATH + file).exists())) {
                                AssetsUtils.copyAssetsFile(context, file, Config.TTS_PATH + file, false);
                            }
                        } else {
                            AssetsUtils.copyAssetsFile(context, file,
                                    Environment.getExternalStorageDirectory() + File.separator + "unisound"
                                            + File.separator + file, false);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

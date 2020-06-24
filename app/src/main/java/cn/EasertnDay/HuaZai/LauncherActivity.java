package cn.EasertnDay.HuaZai;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.media.session.MediaSession;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.*;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
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

public class LauncherActivity extends AppCompatActivity {

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

        //悬浮窗
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//判断系统版本
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            } else {
                startService(new Intent(LauncherActivity.this, InformationService.class));
            }
        } else {
            startService(new Intent(LauncherActivity.this, InformationService.class));
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

    //悬浮窗判断
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                startService(new Intent(LauncherActivity.this, InformationService.class));
            }
        }
    }
}

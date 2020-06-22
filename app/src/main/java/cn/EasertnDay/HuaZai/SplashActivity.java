package cn.EasertnDay.HuaZai;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.EasertnDay.HuaZai.SecondaryPage.VoicePage;
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
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SplashActivity extends AppCompatActivity implements IAsrResultListener {

    Activity context = this;

    ConstraintLayout mLinearLayout;//总布局
    ImageView SplashMaskImage;//启动图遮罩

    //学生信息
    File file = new File(InformationService.savePath + InformationService.fileName);
    boolean IsInit = false;

    //语音识别需要的
    private UnisoundAsrEngine unisoundAsrEngine;
    private ToneGenerator beeper;
    private HashSet<String> wakeUpList = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //语音引擎
        //增加唤醒词
        wakeUpList.add("你好华仔");
        //哔声发声器
        beeper = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        //模型复制
        VoicePresenter.getInstance().init(this.getApplicationContext());
        copyTtsModel();
        copyAssetsModel();
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

        mLinearLayout = findViewById(R.id.SplashMe);//主界面

        //动态组件通用参数
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(1280, 800);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        Log.d("WWW", InformationService.savePath + InformationService.fileName);
        IsInit = file.exists();

        if (!IsInit) {
            //遮罩
            SplashMaskImage = new ImageView(getApplicationContext());
            SplashMaskImage.setImageResource(R.drawable.splash_mask);
            SplashMaskImage.setLayoutParams(params);

            mLinearLayout.addView(SplashMaskImage); //添加启动图
            SplashMaskImage.bringToFront();//最上层显示
        }

        //悬浮窗
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//判断系统版本
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            } else {
                startService(new Intent(SplashActivity.this, InformationService.class));
            }
        } else {
            startService(new Intent(SplashActivity.this, InformationService.class));
        }

        //延迟五秒钟跳转到主页面
        new Handler(new Handler.Callback() {
            //处理接收到的消息的方法
            @Override
            public boolean handleMessage(Message arg0) {
                //实现页面跳转
                if (IsInit) {
                    startActivity(new Intent(getApplicationContext(), LauncherActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), UserRegesite.class));
                }
                return false;
            }
        }).sendEmptyMessageDelayed(0, 5000);
    }

    //悬浮窗判断
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                startService(new Intent(SplashActivity.this, InformationService.class));
            }
        }
    }

    @Override
    public void onResult(int event, String result) {
        if (event == AsrEvent.ASR_EVENT_WAKEUP_RESULT) {
            beeper.startTone(ToneGenerator.TONE_CDMA_ANSWER, 30);
            //Intent myIntent = new Intent(context, VoicePage.class);
            //startActivity(myIntent);
        }
        if (event == AsrEvent.ASR_EVENT_ASR_RESULT) {
            Log.d("我带你们打", result);
            //unisoundAsrEngine.startWakeUp();
            //unisoundAsrEngine.startAsr(false);
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

    //语音合成
    private void copyAssetsModel() {
        AssetManager assetManager = this.getAssets();
        try {
            String[] files = assetManager.list("");
            for (String file : files) {
                if (file.startsWith("frontend")) {
                    if (!(new File(Config.TTS_PATH + file).exists())) {
                        AssetsUtils.copyAssetsFile(this, file, Config.TTS_PATH + file, false);
                    }
                } else if (file.startsWith("backend")) {
                    if (!(new File(Config.TTS_PATH + file).exists())) {
                        AssetsUtils.copyAssetsFile(this, file, Config.TTS_PATH + file, false);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

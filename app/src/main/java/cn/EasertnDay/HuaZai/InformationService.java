package cn.EasertnDay.HuaZai;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.*;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import cn.EasternDay.HuaZai.R;
import org.json.JSONObject;

import java.io.*;

public class InformationService extends Service{

    public static boolean isStarted = false;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    public static String fileName = "UserInformation.txt";//保存用户信息
    public static String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;//存储路径

    public static String UserName;//用户名
    public static int UseCount;//使用次数

    ConstraintLayout displayView;
    TextView nameUser;

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 300;
        layoutParams.height = 150;
        layoutParams.x = 75;
        layoutParams.y = 75;

        //读取用户名
        File file;
        try {
            Log.i("好的吧", savePath + fileName);
            file = new File(savePath + fileName);
            if (!file.exists()) {
                Log.i("好的吧", "晕了");
                file.createNewFile();
                UserName = "胖虎";
                UseCount = 0;
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("UserName", UserName);
                jsonObject.put("UseCount", UseCount);
                OutputStream out = new FileOutputStream(file);
                out.write(jsonObject.toString().getBytes());
                out.close();
            } else {
                InputStream In = new FileInputStream(file);
                // 将字节缓冲区里的数据转换成utf-8字符串
                byte[] buffer = new byte[In.available()];
                In.read(buffer);
                String content = new String(buffer, "utf8");
                In.close();
                // 基于content字符串创建Json数组
                Log.i("好的吧", "吐了" + content);
                JSONObject jsonObj = new JSONObject(content);
                jsonObj.put("UseCount", jsonObj.getInt("UseCount") + 1);

                UserName = jsonObj.getString("UserName");
                UseCount = jsonObj.getInt("UseCount") + 1;

                OutputStream out = new FileOutputStream(file);
                out.write(jsonObj.toString().getBytes());
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    private void showFloatingWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        displayView = (ConstraintLayout) layoutInflater.inflate(R.layout.activity_information_service, null);
        nameUser = displayView.findViewById(R.id.UserName);
        nameUser.setText(UserName);//长按监听
        nameUser.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Toast.makeText(MainActivity.this,"长按成功",Toast.LENGTH_SHORT).show();
                refreshName(true);
                return true;
            }
        });
        displayView.setOnTouchListener(new FloatingOnTouchListener());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//判断系统版本
            if (Settings.canDrawOverlays(this)) {
                windowManager.addView(displayView, layoutParams);
            }
        } else {
            windowManager.addView(displayView, layoutParams);
        }
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    public void refreshName(boolean flag) {
        /*
        if (flag) {
            Intent intent = new Intent();
            intent.setClass(this, UserInformationSet.class);
            startActivity(intent);

            final Handler TimerHandler = new Handler();                   //创建一个Handler对象
            Runnable myTimerRun = new Runnable()                //创建一个runnable对象
            {
                @Override
                public void run() {
                    File file = new File(savePath + fileName);
                    String content = "";
                    try (InputStream In = new FileInputStream(file)) {
                        // 将字节缓冲区里的数据转换成utf-8字符串
                        byte[] buffer = new byte[In.available()];
                        In.read(buffer);
                        content = new String(buffer, "utf8");
                        In.close();
                        // 基于content字符串创建Json数组
                        JSONObject jsonObj = new JSONObject(content);
                        UserName = jsonObj.getString("UserName");
                        nameUser.setText(UserName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    TimerHandler.postDelayed(this, 2000);      //再次调用myTimerRun对象，实现每两秒一次的定时器操作
                }
            };
            TimerHandler.postDelayed(myTimerRun, 2000);        //使用postDelayed方法，两秒后再调用此myTimerRun对象
        } else {
            File file = new File(savePath + fileName);
            String content = "";
            try (InputStream In = new FileInputStream(file)) {
                // 将字节缓冲区里的数据转换成utf-8字符串
                byte[] buffer = new byte[In.available()];
                In.read(buffer);
                content = new String(buffer, "utf8");
                In.close();
                // 基于content字符串创建Json数组
                JSONObject jsonObj = new JSONObject(content);
                UseCount = jsonObj.getInt("UseCount");
                jsonObj.put("UserName", UserName);
                jsonObj.put("UseCount", UseCount + 1);
                nameUser.setText(UserName);
                OutputStream out = new FileOutputStream(file);
                out.write(jsonObj.toString().getBytes());
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
         */
    }
}

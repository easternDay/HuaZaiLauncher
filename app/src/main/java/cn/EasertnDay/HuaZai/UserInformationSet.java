package cn.EasertnDay.HuaZai;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import cn.EasternDay.HuaZai.R;
import com.unisound.sdk.asr.impl.IAsrResultListener;
import com.unisound.sdk.tts.TtsOption;
import com.unisound.sdk.tts.audiotrack.AndroidAudioTrack;
import com.unisound.sdk.tts.param.UnisoundTtsPlayMode;
import org.json.JSONObject;

import java.io.*;

public class UserInformationSet extends AppCompatActivity {
    EditText UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information_set);

        UserName = findViewById(R.id.editName);
        UserName.setText(InformationService.UserName);
    }
    //设置按钮点击监听（全部）
    public void onClick(View v) {
        Intent myIntent;
        switch (v.getId()) {
            //设置按钮的页面跳转-设置页面
            case R.id.button_return:
                finish();
                break;
            case R.id.buttonUserInfo:
                //读取用户名
                File file;
                try {
                    file = new File(InformationService.savePath + InformationService.fileName);
                    if (!file.exists()) {
                        file.createNewFile();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("UserName", UserName.getText().toString());
                        jsonObject.put("UseCount", 0);
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
                        JSONObject jsonObj = new JSONObject(content);
                        jsonObj.put("UseCount", jsonObj.getInt("UseCount") + 1);
                        jsonObj.put("UserName", UserName.getText().toString());

                        OutputStream out = new FileOutputStream(file);
                        out.write(jsonObj.toString().getBytes());
                        out.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}

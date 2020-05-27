package cn.EasertnDay.HuaZai.SecondaryPage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import cn.EasternDay.HuaZai.R;

public class TZXX extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tzxx);
    }

    //设置按钮点击监听（全部）
    public void onClick(View v) {
        Intent myIntent;
        switch (v.getId()) {
            //设置按钮的页面跳转-设置页面
            case R.id.button_return:
                finish();
                break;
            default:
                break;
        }
    }
}

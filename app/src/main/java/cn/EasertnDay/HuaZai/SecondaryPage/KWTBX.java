package cn.EasertnDay.HuaZai.SecondaryPage;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import cn.EasternDay.HuaZai.R;

public class KWTBX extends AppCompatActivity {

    KWTBX context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kwtbx);
    }


    //设置按钮点击监听（全部）
    public void onClick(View v) {
        Intent myIntent;
        switch (v.getId()) {
            //设置按钮的页面跳转-设置页面
            case R.id.button_return:
                finish();
                break;
            case R.id.button_XZW:
                myIntent = getPackageManager().getLaunchIntentForPackage("com.graduation.learnchinese");
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(myIntent);
                break;
            default:
                break;
        }
    }
}

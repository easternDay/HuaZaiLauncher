package cn.EasertnDay.HuaZai.SecondaryPage;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import cn.EasternDay.HuaZai.R;

public class ZXXL extends AppCompatActivity {

    Activity context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zxxl);
    }


    //设置按钮点击监听（全部）
    public void onClick(View v) {
        Intent myIntent;
        switch (v.getId()) {
            //设置按钮的页面跳转-设置页面
            case R.id.button_return:
                finish();
                break;
            case R.id.button_SLQM:
                myIntent = getPackageManager().getLaunchIntentForPackage("com.unisound.unisoundinitiation");
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(myIntent);
                break;
            case R.id.button_HDXX:
                myIntent = getPackageManager().getLaunchIntentForPackage("com.efercro.uscspeech");
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(myIntent);
                break;
            case R.id.button_ARCH:
                myIntent = getPackageManager().getLaunchIntentForPackage("com.hxh.KingdomOfVocabulary");
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(myIntent);
                break;
            case R.id.button_ARZJ:
                myIntent = getPackageManager().getLaunchIntentForPackage("com.lzh.ARZJ");
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(myIntent);
                break;
            case R.id.button_QJXX:
                myIntent = getPackageManager().getLaunchIntentForPackage("com.chn.QJXX");
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(myIntent);
                break;
            case R.id.button_YDLJ:
                myIntent = getPackageManager().getLaunchIntentForPackage("com.qzf.YDLJ");
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(myIntent);
                break;
            case R.id.button_HBCZ:
                myIntent = getPackageManager().getLaunchIntentForPackage("com.wgx.HBSC");
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(myIntent);
                break;
            case R.id.button_KWDB:
                myIntent = getPackageManager().getLaunchIntentForPackage("com.hqu.HWKW");
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(myIntent);
                break;
            case R.id.button_SCZSBS:
                myIntent = getPackageManager().getLaunchIntentForPackage("com.hqu.xzhdstu");
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(myIntent);
                break;
            case R.id.button_XGYX:
                myIntent = getPackageManager().getLaunchIntentForPackage("yxgl.xt");
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(myIntent);
                break;
            default:
                break;
        }
    }
}

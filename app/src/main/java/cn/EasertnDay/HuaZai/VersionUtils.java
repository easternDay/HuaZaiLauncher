package cn.EasertnDay.HuaZai;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author Tosca
 * @create at 10:56, 2020/3/11
 */
public class VersionUtils {

  /**
   * get App versionCode
   */
  public static String getVersionCode(Context context){
    PackageManager packageManager=context.getPackageManager();
    PackageInfo packageInfo;
    String versionCode="";
    try {
      packageInfo=packageManager.getPackageInfo(context.getPackageName(),0);
      versionCode=packageInfo.versionCode+"";
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return versionCode;
  }

  /**
   * get App versionName
   */
  public static String getVersionName(Context context) {
    PackageManager packageManager = context.getPackageManager();
    PackageInfo packageInfo;
    String versionName = "";
    try {
      packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
      versionName = packageInfo.versionName;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return versionName;
  }
}

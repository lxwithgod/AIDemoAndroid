package com.ai.demo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UrlConfig {
    private static SharedPreferences share;
    private static String configPath = "appConfig";

    public static SharedPreferences getProperties(Context context) {
        try {
            share = context.getSharedPreferences(configPath, Context.MODE_PRIVATE);

        } catch (Exception e) {
            e.printStackTrace();

        }

        return share;
    }

    public static String setProperties(Context context, String keyName, String keyValue) {
        try {
            share = context.getSharedPreferences(configPath, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = share.edit();//取得编辑器
            editor.putString(keyName, keyValue);//存储配置 参数1 是key 参数2 是值
            editor.commit();//提交刷新数据


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("setPropertiesError", e.toString());
            return "修改配置文件失败!";
        }
        return "设置成功";
    }


}

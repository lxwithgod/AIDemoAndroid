package com.ai.demo.utils;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

// 通用函数类
public class CustomTools {
    public Toast toast = null;

    /**
     * 自定义toast
     *
     * @param info
     */
    public void customToast(String info, Activity activity) {
        Log.e("toast消息", info);
        if (toast != null) {
            //toast = null;
            toast.cancel();
        }
        toast = Toast.makeText(activity, info, Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 3000);

    }

}

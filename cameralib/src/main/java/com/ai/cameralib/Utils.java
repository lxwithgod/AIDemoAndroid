package com.ai.cameralib;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

// 通用函数类
public class Utils {
    public Toast toast = null;

    /**
     * 自定义toast
     *
     * @param info
     */
    public void customerToast(String info, Activity activity) {
        Log.e("toast消息", info + " " + String.valueOf(toast));
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
        }, 2000); //specify delay here that is shorter than Toast.LENGTH_SHORT

    }

}

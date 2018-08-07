package com.ai.demo.json;

import android.content.Context;
import android.content.SharedPreferences;

import com.ai.demo.utils.UrlConfig;

/**
 * 网络请求
 */

public class UrlString {

    //private static final String IP = "172.16.64.115:8000";
//    private static final String IP = "192.168.43.180:8000";

    private String threshold_ratio_key = "threshold_ratio";
    private String threshold_ratio = "0.8";
    private String threshold_avg_pool_size_key = "threshold_avg_pool_size";
    private String threshold_avg_pool_size = "9";


    private String contrastEucName = "eucValue";

    public String getEucValue() {
        return eucValue;
    }

    private String eucValue;

    public String getThreshold_ratio(Context context) {

        SharedPreferences proper = UrlConfig.getProperties(context);
        this.threshold_ratio = proper.getString(threshold_ratio_key, "0.9");

        return this.threshold_ratio;

    }

    public String setThreshold_ratio(Context context, String threshold_ratio_value) {

        String result = UrlConfig.setProperties(context, threshold_ratio_key, threshold_ratio_value);
        this.threshold_ratio = threshold_ratio_value;

        return result;
    }

    public String getThreshold_avg_pool_size(Context context) {

        SharedPreferences proper = UrlConfig.getProperties(context);
        this.threshold_avg_pool_size = proper.getString(threshold_avg_pool_size_key, "9");

        return this.threshold_avg_pool_size;

    }

    public String setThreshold_avg_pool_size(Context context, String threshold_avg_pool_size_value) {

        String result = UrlConfig.setProperties(context, threshold_avg_pool_size_key, threshold_avg_pool_size_value);
        this.threshold_avg_pool_size = threshold_avg_pool_size_value;

        return result;
    }

    public void getEuc(Context context) {
        SharedPreferences proper = UrlConfig.getProperties(context);
        this.eucValue = proper.getString(contrastEucName, "");
        // 设置默认值
        if (this.eucValue.equals("")) {
            this.eucValue = "0.4666";
        }
    }

    public String setEuc(Context context, String keyValue) {

        String result = UrlConfig.setProperties(context, contrastEucName, keyValue);
        this.eucValue = keyValue;
        return result;
    }

    public String getThreshold_ratio() {
        return threshold_ratio;
    }

    public String getThreshold_avg_pool_size() {
        return threshold_avg_pool_size;
    }
}

package com.ai.demo.ui;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.ai.demo.R;
import com.ai.demo.json.UrlString;
import com.ai.demo.utils.CustomTools;


public class ConfigActivity extends AppCompatActivity {
    private UrlString urlString = new UrlString();
    private EditText editText;
    private EditText editText2;
    private EditText editText3;
    private CustomTools tools = new CustomTools();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        editText = (EditText) findViewById(R.id.et_threshold_ratio);
        editText2 = (EditText) findViewById(R.id.et_euc);
        editText3 = (EditText) findViewById(R.id.et_threshold_avg_pool_size);

        // 加载配置的信息
        urlString.getThreshold_ratio(ConfigActivity.this);
        editText.setText(urlString.getThreshold_ratio());

        // 加载配置的信息
        urlString.getThreshold_avg_pool_size(ConfigActivity.this);
        editText3.setText(urlString.getThreshold_avg_pool_size());

        // 加载配置的信息
        urlString.getEuc(ConfigActivity.this);
        editText2.setText(urlString.getEucValue());

    }


    /**
     * 设置为竖屏  横屏 SCREEN_ORIENTATION_LANDSCAPE
     */
    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }


    /**
     * 保存参数
     *
     * @param v
     */
    public void saveClick(View v) {
        try {
            String value = editText.getText().toString();
            String result = urlString.setThreshold_ratio(this, value);

            String value2 = editText2.getText().toString();
            urlString.setEuc(this, value2);
            urlString.setThreshold_avg_pool_size(this, editText3.getText().toString());


            tools.customToast(result, ConfigActivity.this);

        } catch (
                Exception e)

        {
            Log.e("异常", "click异常!");
            e.printStackTrace();
        }

    }


    /**
     * 返回
     *
     * @param v
     */
    public void blackClick(View v) {
        try {
            this.finish();

        } catch (
                Exception e)

        {
            Log.e("异常", "click异常!");
            e.printStackTrace();
        }

    }


}

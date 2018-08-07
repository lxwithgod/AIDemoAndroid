package com.ai.demo.ui;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.ai.demo.R;

import com.ai.demo.utils.ConstantManager;
import com.ai.demo.utils.CustomTools;
import com.ai.demo.utils.TfNameEntity;


/**
 * Created by lx on 18-4-10.
 */


public class NameEntityActivity extends BaseActivity {
    private CustomTools tools = new CustomTools();
    private TextView textView;
    private String text;

    private Button btClick;
    private ProgressBar progressDialog;

    private TextView textViewResult;
    TfNameEntity tdne = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tdne = new TfNameEntity(NameEntityActivity.this.getAssets());

//
        setContentView(R.layout.activity_name_entity);
        textView = NameEntityActivity.this.findViewById(R.id.text_input);
        textViewResult = NameEntityActivity.this.findViewById(R.id.text_parser_result);
//        textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        btClick = NameEntityActivity.this.findViewById(R.id.bt_ocr);
        progressDialog = findViewById(com.ai.cameralib.R.id.progressBar);

        setControlEnable(true);

        // 加载配置文件
//        urlString.setIPAddress(this);

//        UrlString urlString = new UrlString();
//        Threshold_ratio=Float.valueOf(urlString.getThreshold_ratio(OcrDectActivity.this));
//
//        Threshold_avg_pool_size=Integer.valueOf(urlString.getThreshold_avg_pool_size(OcrDectActivity.this));

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

//        requestCAMERAPower();

    }


    /**
     * 点击提交
     *
     * @param v
     */
    public void ocrClick(View v) {
        // 识别中禁止点击

        try {

            text = textView.getText().toString();
            if (text.equalsIgnoreCase("")) {
                tools.customToast(ConstantManager.TOST_ADDTEXT, NameEntityActivity.this);
                return;
            }
            text = tdne.paser(text);
            textViewResult.setText(text);

            setControlEnable(false);

        } catch (Exception e) {
            e.printStackTrace();
            tools.customToast(ConstantManager.TOST_JSONPARSEFALSE, NameEntityActivity.this);
        }

        // 激活点击按钮
        setControlEnable(true);

    }

    private void setControlEnable(Boolean bool) {
        if (bool) {
            progressDialog.setVisibility(View.INVISIBLE);
        } else {
            progressDialog.setVisibility(View.VISIBLE);

        }

        btClick.setEnabled(bool);
//        ButtonCard.setButtonEnable(bool);

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
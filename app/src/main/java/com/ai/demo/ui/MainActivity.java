package com.ai.demo.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ai.demo.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions();

    }


    /**
     * 按钮点击事件
     *
     * @param view
     */
    public void click(View view) {
        int bid = view.getId();
        try {
            switch (bid) {

                // 人证对比
//                case R.id.iv12:
//                    Intent intent12 = new Intent(MainActivity.this, ContrastActivity.class);
//                    this.startActivity(intent12);
//                    break;

                // 参数设置
                case R.id.iv22:
                    Intent intent22 = new Intent(MainActivity.this, ConfigActivity.class);
                    this.startActivity(intent22);
                    break;


                // 人证对比
                case R.id.iv21:
                    Intent intent21 = new Intent(MainActivity.this, ContrastOpencvActivity.class);
                    this.startActivity(intent21);
                    break;
                case R.id.iv11:
                    Intent intent11 = new Intent(MainActivity.this, OcrActivity.class);
                    this.startActivity(intent11);
                    break;
                case R.id.iv23:
                    Intent intent23 = new Intent(MainActivity.this, OcrDectActivity.class);
                    this.startActivity(intent23);
                    break;
                case R.id.iv24:
                    Intent intent24 = new Intent(MainActivity.this, NameEntityActivity.class);
                    this.startActivity(intent24);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e("异常", "click异常!");
            e.printStackTrace();
        }

    }


}

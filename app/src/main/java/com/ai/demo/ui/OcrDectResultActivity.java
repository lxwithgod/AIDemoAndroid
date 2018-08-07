package com.ai.demo.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.ai.demo.R;
import com.ai.demo.entity.CustomImageButton;
import com.ai.demo.utils.ConstantManager;


public class OcrDectResultActivity extends AppCompatActivity {


    Bitmap OCR_IMG = null;
    //    TextView OCR_IMG_RESULT = null;
    boolean firstInit = false;
    CustomImageButton OCR_IMAGE_BUTTON = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_dect_result);
//        OCR_IMG_RESULT =  findViewById(R.id.OCR_IMG_RESULT);
//        OCR_IMG_RESULT.setMovementMethod(ScrollingMovementMethod.getInstance());


        OCR_IMAGE_BUTTON = this.findViewById(R.id.OCR_IMG);
        OCR_IMAGE_BUTTON.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
//        OCR_IMAGE_BUTTON.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        final Bundle bundle = this.getIntent().getExtras(); // 从Activity获取的bundle

        assert bundle != null;
//        String ocr_img_result = bundle.getString(ConstantManager.RECEIVEID_OCR_IMAGE_RESULT);
//        OCR_IMG_RESULT.setText(ocr_img_result);

        String filename = getIntent().getStringExtra((ConstantManager.RECEIVEID_OCR_IMAGE));

        OCR_IMG = BitmapFactory.decodeFile(filename);


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
     * view已经初始化完毕了
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!firstInit) {

            Bitmap bmFace = OCR_IMAGE_BUTTON.resizeBitmap(OCR_IMG,
                    OCR_IMAGE_BUTTON.getWidth(),
                    OCR_IMAGE_BUTTON.getHeight());
            OCR_IMAGE_BUTTON.setBitmap(bmFace);

            firstInit = true;
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


    /**
     * 点击回到首页
     *
     * @param v
     */
    public void homeClick(View v) {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            // 返回首页,关闭所有的除首页外的外activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            this.startActivity(intent);

        } catch (Exception e)

        {
            Log.e("异常", "click异常!");
            e.printStackTrace();
        }

    }
}

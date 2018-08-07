package com.ai.demo.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.ai.demo.R;
import com.ai.demo.entity.CustomImageButton;
import com.ai.demo.json.UrlString;
import com.ai.demo.utils.ConstantManager;

import java.text.DecimalFormat;

public class ContrastResultActivity extends BaseActivity {
    private TextView textResult1;
    private TextView textResult2;
    private TextView textResult3;
    private TextView view_face_hw;
    private TextView view_id_hw;
    private CustomImageButton imageCard;
    private CustomImageButton imageFace;
    private String strCosine;
    private String strEdu;
    private String face;
    private String id_face;
    private boolean firstInit = false;
    private String faceHeightWith;
    private String idFaceHeightWith;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrast_result);
        view_face_hw = (TextView) findViewById(R.id.face_hw);
        view_id_hw = (TextView) findViewById(R.id.id_face_hw);

        imageCard = (CustomImageButton) ContrastResultActivity.this.findViewById(R.id.im_card);
        imageFace = (CustomImageButton) ContrastResultActivity.this.findViewById(R.id.im_face);
        imageCard.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        imageFace.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        final Bundle bundle = this.getIntent().getExtras(); // 从Activity获取的bundle
        String cosine = bundle.getString(ConstantManager.RECEIVECOSINE); // 相似度
        String edu = bundle.getString(ConstantManager.RECEIVEEDU); // 小于1为相同
        face = bundle.getString(ConstantManager.RECEIVEFACE);
        id_face = bundle.getString(ConstantManager.RECEIVEID_FACE);
        faceHeightWith = bundle.getString(ConstantManager.RECEIVEFACE_HW);
        idFaceHeightWith = bundle.getString(ConstantManager.RECEIVEID_HW);

        // 转换百分比保留两位小数
        float fcosine = Float.parseFloat(cosine);
        DecimalFormat decimalFormat = new DecimalFormat(".0000"); //构造方法的字符格式这里如果小数不足2位,会以0补足.
        strCosine = decimalFormat.format(fcosine * 100) + "%";

        float fedu = Float.parseFloat(edu);

        UrlString urlString = new UrlString();
        urlString.getEuc(ContrastResultActivity.this);
        String eucStr = urlString.getEucValue();

        if (fedu < Float.parseFloat(eucStr)) {
            strEdu = ConstantManager.RESULTSUCCESS;
        } else {
            strEdu = ConstantManager.RESULTFALSE;
        }

        textResult1 = (TextView) this.findViewById(R.id.textResult1);
        textResult1.setText(strCosine);

        textResult2 = (TextView) this.findViewById(R.id.textResult2);
        textResult2.setText(edu);

        textResult3 = (TextView) this.findViewById(R.id.textResult3);
        textResult3.setText(strEdu);


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


            Bitmap bmFace = imageFace.resizeBitmap(BitmapFactory.decodeFile(face), imageFace.getWidth(), imageFace.getHeight());
            imageFace.setBitmap(bmFace);

            Bitmap bmCard = imageCard.resizeBitmap(BitmapFactory.decodeFile(id_face), imageCard.getWidth(), imageCard.getHeight());
            imageCard.setBitmap(bmCard);

            view_face_hw.setText(faceHeightWith);
            view_id_hw.setText(idFaceHeightWith);

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

        } catch (
                Exception e)

        {
            Log.e("异常", "click异常!");
            e.printStackTrace();
        }

    }
}

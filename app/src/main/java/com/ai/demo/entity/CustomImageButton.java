package com.ai.demo.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomImageButton extends LinearLayout {
    private TextView _textView;
    private ImageView _imageView;

    public CustomImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        _imageView = new ImageView(context, attrs);
        _imageView.setPadding(2, 2, 2, 2);

        _textView = new TextView(context, attrs);
        _textView.setBackgroundColor(Color.TRANSPARENT);
        _textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        _textView.setPadding(0, 0, 0, 0);

        setClickable(true);
        setFocusable(true);
        setOrientation(LinearLayout.VERTICAL);
        addView(_textView);
        addView(_imageView);
    }


    public void setText(String text) {
        _textView.setText(text);
    }

    public void setButtonEnable(Boolean bool) {
        _imageView.setEnabled(bool);
    }

    public void clearnImage() {
        _textView.setVisibility(View.VISIBLE);
        _imageView.setImageDrawable(null);
    }

    public void setBitmap(Bitmap bm) {
        _textView.setVisibility(View.GONE);
        _imageView.setImageDrawable(null);
        _imageView.setImageBitmap(bm);

    }


    public Bitmap resizeBitmap(Bitmap bm, int ivbWidth, int ivbHeight) {
        Bitmap resizeBmp = null;
        try {

            Matrix matrix = new Matrix();

            float scale;
            if (ivbWidth <= ivbHeight) {
                scale = (float) ivbWidth / bm.getWidth();
                matrix.postScale(scale, scale); //长和宽放大缩小的比例
                resizeBmp = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            } else {
                scale = (float) ivbHeight / bm.getHeight();
                matrix.postScale(scale, scale);
                resizeBmp = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            }

            Log.e("scale", scale + "");


        } catch (Exception e) {
            e.printStackTrace();

        }
        return resizeBmp;
    }


}
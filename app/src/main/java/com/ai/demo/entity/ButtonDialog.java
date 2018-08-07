package com.ai.demo.entity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;

import com.ai.demo.R;

public class ButtonDialog extends Dialog {
    public Context context;

    public ButtonDialog(Context context) {
        super(context);
        this.context = context;
    }

    public ButtonDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected ButtonDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_dialog);
        initView();

        findViewById(R.id.btn_cancel).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                ButtonDialog.this.dismiss();
            }
        });
    }

    /**
     * 初始化控件 设置动画
     */
    private void initView() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.bottom_dialog, null);
        //获得dialog的window窗口
        Window window = ButtonDialog.this.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.BOTTOM);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.DialogAnimation);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        //将自定义布局加载到dialog上
        ButtonDialog.this.setContentView(dialogView);

    }


}

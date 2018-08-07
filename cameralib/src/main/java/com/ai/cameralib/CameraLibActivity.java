package com.ai.cameralib;

import Decoder.BASE64Encoder;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 主Activity 入口  拍照传给ChangeActivity
 */
public class CameraLibActivity extends AppCompatActivity implements View.OnClickListener {

    private SurfaceView mSurfaceView;//前面的surfaceview
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;//相机对象
    private ImageView iv_show;//拍照图片的按钮
    private int viewWidth, viewHeight;//mSurfaceView的宽和高
    private ProgressBar progressDialog;
    private TextView tv;
    private Utils utils = new Utils();
    private Boolean cameraAuthority = true;
    private AutoFocusManager autoFocusManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//不显示程序的标题栏
        setContentView(R.layout.camera_lib_activity);
        initView();

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
     * 初始化控件
     */
    private void initView() {

        progressDialog = (ProgressBar) findViewById(R.id.progressBar);
        progressDialog.setVisibility(View.INVISIBLE);
        iv_show = (ImageView) findViewById(R.id.iv_show_camera2_activity);
        tv = (TextView) findViewById(R.id.textView);

        //这里设置相框的比例大小 先获取屏幕尺寸
        WindowManager wm = this.getWindowManager();
        viewWidth = wm.getDefaultDisplay().getWidth();
        viewHeight = wm.getDefaultDisplay().getHeight();


        //mSurfaceView
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view_camera2_activity);


        mSurfaceHolder = mSurfaceView.getHolder();
        // mSurfaceView 不需要自己的缓冲区
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // mSurfaceView添加回调
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) { //SurfaceView创建
                initCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) { //SurfaceView销毁
                Log.e("surfaceDestroyed", "OK");
                if (autoFocusManager != null) {
                    autoFocusManager.stop();
                    autoFocusManager = null;
                }
                // 释放Camera资源
                if (mCamera != null & cameraAuthority) {
                    mCamera.stopPreview();
                    mCamera.release();
                }
            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }

    /**
     * SurfaceHolder 回调接口方法
     */
    private void initCamera() {
        Camera.Parameters parameters;
        try {
            mCamera = Camera.open();//默认开启后置
            mCamera.setDisplayOrientation(90);//摄像头进行旋转90°
            parameters = mCamera.getParameters();
            iv_show.setOnClickListener(this);
            cameraAuthority = true;
            tv.setText(ConstantConfig.TAKEPICTURE);
        } catch (Exception e) {
            iv_show.setOnClickListener(null);
            //Log.e("拍照失败", "请开启相机权限后拍照");
            tv.setText(ConstantConfig.NOCAMERAPERMISSION);
            utils.customerToast(ConstantConfig.NOCAMERAPERMISSION_T, CameraLibActivity.this);
            cameraAuthority = false;
            return;
        }

        if (mCamera != null & cameraAuthority) {
            try {

                // 支持的所有尺寸
                List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
                List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();

                //根据比例设置大小
                float whRatio = (float) viewWidth / viewHeight;
                ArrayList<Integer> ListSize = getCameraSize(supportedPreviewSizes, whRatio);
                //设置预览照片的大小
                parameters.setPreviewSize(ListSize.get(0), ListSize.get(1));

                ListSize = getCameraSize(supportedPictureSizes, whRatio);
                //设置照片的大小 按比例
                parameters.setPictureSize(ListSize.get(0), ListSize.get(1));

                //设置相机预览照片帧数
                //parameters.setPreviewFpsRange(4, 10);
                //设置图片格式
                parameters.setPictureFormat(ImageFormat.JPEG);
                //设置图片的质量
                //parameters.set("jpeg-quality", 15);

                //parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);//1连续对焦
                //parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦

                mCamera.setParameters(parameters);//把上面的设置 赋给摄像头

                //通过SurfaceView显示预览
                mCamera.setPreviewDisplay(mSurfaceHolder);
                //开始预览
                mCamera.startPreview();
                //mCamera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
                autoFocusManager = new AutoFocusManager(mCamera);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取与屏幕比例最相近的比例
     *
     * @param listSizes
     * @param whRatio
     */
    private ArrayList<Integer> getCameraSize(List<Camera.Size> listSizes, float whRatio) {
        //Collections.reverse(listSizes);
        ArrayList<Integer> reSize = new ArrayList<>();
        float deltaRatio = 1, curRatio;
        Camera.Size curSize;
        int size_w = 0, size_h = 0;
        for (int i = 0; i < listSizes.size(); i++) {
            curSize = listSizes.get(i);
            // 判断尺寸超过2000的直接break
            if (curSize.width >= 2000 | curSize.height >= 2000) {
                continue;
            }
            curRatio = Math.abs((float) curSize.width / curSize.height - whRatio);
            if (curRatio <= deltaRatio) {
                deltaRatio = curRatio;
                size_w = curSize.width;
                size_h = curSize.height;
            }
            //考虑横屏竖屏傻傻分不清楚
            curRatio = Math.abs((float) curSize.height / curSize.width - whRatio);
            if (curRatio <= deltaRatio) {
                deltaRatio = curRatio;
                size_w = curSize.width;
                size_h = curSize.height;
            }
        }

        reSize.add(size_w);
        reSize.add(size_h);
        return reSize;
    }

    /**
     * 点击拍照
     */
    @Override
    public void onClick(View v) {
        if (mCamera == null) return;
        progressDialog.setVisibility(View.VISIBLE);
        mCamera.autoFocus(autoFocusCallback); // 按下快门键对焦
        mCamera.takePicture(new Camera.ShutterCallback() {//按下快门
            @Override
            public void onShutter() {
                //按下快门瞬间的操作 禁止按钮
                iv_show.setOnClickListener(null);
            }
        }, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {//是否保存原始图片的信息
            }
        }, pictureCallback);
    }


    /**
     * 自动对焦
     */
    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            try {
                camera.cancelAutoFocus();
                if (success) {
                    //Log.e("对焦", "对焦成功");
                } else {
                    //Log.e("对焦", "对焦失败");

                }
            } catch (Exception e) {
                Log.e("对焦异常", "异常");
                e.printStackTrace();
            }
        }
    };


    /**
     * 获取图片
     */
    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);//将data byte型数组转换成bitmap文件

            final Matrix matrix = new Matrix();//转换成矩阵旋转90度
            matrix.setRotate(90);
            final Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);//旋转图片

            if (!bm.isRecycled()) //如果没有回收
                bm.recycle();


            // 剪切图片
            byte[] cropBitmapByte = bitmapToByte(cropBitmap(bitmap));
            byte[] bitmapByte = bitmapToByte(bitmap);
            BASE64Encoder encoder = new BASE64Encoder();
            String base64 = encoder.encode(bitmapByte);

            if (!bitmap.isRecycled()) //如果没有回收
                bitmap.recycle();

            Intent intent = new Intent();
            intent.putExtra(ConstantConfig.CROPBITMAPBYTE, cropBitmapByte);
            // intent.putExtra(ConstantConfig.BITMAPBASE64, base64);
            CameraLibActivity.this.setResult(200, intent);
            CameraLibActivity.this.finish();

        }
    };


    /**
     * 裁剪图片
     *
     * @param bitmap 传入需要裁剪的bitmap
     * @return 返回裁剪后的bitmap
     */
    private Bitmap cropBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();

        int cropWidth = (int) (w * 0.9);
        int cropHeight = (int) (cropWidth / 1.59);
        int crop_x = (w - cropWidth) / 2;
        int crop_y = (h - cropHeight) / 2;
        return Bitmap.createBitmap(bitmap, crop_x, crop_y, cropWidth, cropHeight, null, false);
    }


    /**
     * bitmap转为base64  912修改变为 1为byte 0为base64
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmapToByte(Bitmap bitmap) {

        byte[] bitmapBytes = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                bitmapBytes = baos.toByteArray();


            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmapBytes;
    }


    /**
     * 处理onDestory方法
     */
    @Override
    protected void onPause() {
        //Log.e("系统方法", "onPause");
        super.onPause();
        if (utils.toast != null) {
            utils.toast.cancel();
        }
    }


    /**
     * 闪光灯开关
     */
    public void flashModeClick(View v) {
        if (mCamera == null | !cameraAuthority) return;
        try {

            Camera.Parameters parameters = mCamera.getParameters();
            ImageView iv = (ImageView) findViewById(R.id.iv_flash_mode);
            TextView tv = (TextView) findViewById(R.id.tv_flash_mode);

            String flashMode = parameters.getFlashMode();
            if (Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
                tv.setText("开启");
                iv.setImageResource(R.mipmap.flash_open);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                utils.customerToast("闪光灯开启", CameraLibActivity.this);
                //Log.e("闪光灯", "闪光灯开启");
            } else {
                tv.setText("关闭");
                iv.setImageResource(R.mipmap.flash_close);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                utils.customerToast("闪光灯关闭", CameraLibActivity.this);
                //Log.e("闪光灯", "闪光灯关闭");
            }

            mCamera.setParameters(parameters);

        } catch (Exception e) {
            Log.e("闪光灯设置异常", "异常");
            e.printStackTrace();
        }
    }


    //返回按钮
    public void back(View view) {
        this.finish();
    }

}


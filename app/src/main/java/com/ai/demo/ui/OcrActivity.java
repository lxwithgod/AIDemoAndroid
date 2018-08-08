package com.ai.demo.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ai.demo.R;
import com.ai.demo.entity.ButtonDialog;
import com.ai.demo.entity.CustomImageButton;
import com.ai.demo.json.UrlString;
import com.ai.demo.utils.BitMapRenderScripts;
import com.ai.demo.utils.BytesIM;
import com.ai.demo.utils.BytesIMs;
import com.ai.demo.utils.ConstantManager;
import com.ai.demo.utils.CustomTools;
import com.ai.demo.utils.ImageUtil;
import com.ai.demo.utils.TfNameEntity;
import com.ai.demo.utils.TfTextDect;
import com.ai.demo.utils.TfTextRec;

import java.io.File;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by lx on 18-4-10.
 */

@RuntimePermissions
public class OcrActivity extends BaseActivity {
    private CustomTools tools = new CustomTools();
    private CustomImageButton ButtonCard;
    private ButtonDialog dialog;
    private Bitmap OCR_IMG;

    private Button btClick;
    private ProgressBar progressDialog;
    private static String CAMERAIMAGENAME = "image.jpg";

    //    private UrlString urlString = new UrlString();
    private TfTextRec ttr = null;
    private TfTextDect tfd = null;

    //    private String OCR_IMG_HW = "";
    private float Threshold_ratio;
    private int Threshold_avg_pool_size;
    private Executor executor = Executors.newSingleThreadExecutor();


    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    tfd = new TfTextDect(getAssets());
                    ttr = new TfTextRec(getAssets());
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initTensorFlowAndLoadModel();

//
        setContentView(R.layout.activity_ocr);
        ButtonCard = OcrActivity.this.findViewById(R.id.bt_get_ocr_image);
        ButtonCard.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        btClick = OcrActivity.this.findViewById(R.id.bt_ocr);
        progressDialog = findViewById(com.ai.cameralib.R.id.progressBar);

        setControlEnable(true);

        // 加载配置文件
//        urlString.setIPAddress(this);

        UrlString urlString = new UrlString();
        Threshold_ratio = Float.valueOf(urlString.getThreshold_ratio(OcrActivity.this));

        Threshold_avg_pool_size = Integer.valueOf(urlString.getThreshold_avg_pool_size(OcrActivity.this));

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

//        requestCAMERAPower();

    }

    /**
     * Open System Camera With PermissionDispatcher
     */
    @NeedsPermission(Manifest.permission.CAMERA)
    public void openSystemCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), CAMERAIMAGENAME));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, ConstantManager.OPENLOCALCAMERA);
    }


    /**
     * Message When Permission Was Denied
     */
    @OnPermissionDenied(Manifest.permission.CAMERA)
    public void showMessageWhenCameraPermissionDenied() {
        Toast.makeText(this, "获取摄像头权限失败，请授权", Toast.LENGTH_SHORT).show();
    }

    /**
     * Message When Camera Permission Was Forever Denied
     */
    @OnNeverAskAgain(Manifest.permission.CAMERA)
    public void showMessageWhenCameraPermissionForeverDenied() {
        Toast.makeText(this, "您已禁用摄像头权限，无法开启摄像头", Toast.LENGTH_SHORT).show();
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
     * 点击添加照片事件
     *
     * @param v
     */
    public void addClick(View v) {

        int bt_id = v.getId();
        switch (bt_id) {
            case R.id.bt_get_ocr_image:
                // 添加照片 - 身份证
                openDialog(ConstantManager.ISCARD);
                break;


            default:
                break;
        }


    }

    /**
     * 打开dialog 并监听点击事件
     *
     * @param getType 身份证or人像
     */
    private void openDialog(final int getType) {
        dialog = new ButtonDialog(OcrActivity.this, R.style.Dialog);
        dialog.show();

        dialog.findViewById(R.id.btn_open_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (getType == ConstantManager.ISCARD) {
                    // 打开本地相机
                    OcrActivityPermissionsDispatcher.openSystemCameraWithPermissionCheck(OcrActivity.this);

                }
            }
        });

        dialog.findViewById(R.id.btn_choose_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // 打开本地相册
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (getType == ConstantManager.ISCARD) {
                    startActivityForResult(intent, ConstantManager.OPENPHOTOS_CARD);
                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bm = null;
        switch (requestCode) {
            // 表示调用本地照相机拍照
            case ConstantManager.OPENLOCALCAMERA:
                if (resultCode == RESULT_OK) {
//                    Bundle bundle = data.getExtras();
//                    bm = (Bitmap) bundle.get("data");

                    bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + CAMERAIMAGENAME);
                    OCR_IMG = bm;
                    ButtonCard.setBitmap(bm);


                }
                break;

            // 选择图片库的图片 身份证
            case ConstantManager.OPENPHOTOS_CARD:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    try {
                        bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        OCR_IMG = bm;
                        ButtonCard.setBitmap(bm);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;


            default:
                break;
        }

    }

    private Bitmap resize_fixed_scale(Bitmap bm, float val) {
        // 设置显示
        int max_scale = Math.max(bm.getHeight(), bm.getWidth());
        if (max_scale > val) {
            double bl = val / max_scale;

            Bitmap resizeBm = ImageUtil.resizeImage(bm, (int) (bm.getWidth() * bl), (int) (bm.getHeight() * bl));

            return resizeBm;

        } else {
            return bm;
        }

    }


    /**
     * 点击提交
     *
     * @param v
     */
    public void ocrClick(View v) {
        // 识别中禁止点击

        try {

            if (OCR_IMG == null) {
                tools.customToast(ConstantManager.TOST_ADDPHOTO, OcrActivity.this);
                return;
            }

            setControlEnable(false);


            RenderScript rs = RenderScript.create(this.getBaseContext());
//            OCR_IMG=BitMapRenderScripts.avg_pool(OCR_IMG,0.9f,64,64,rs);

            // 开始发送数据并接收返回结果
            long startTime = new Date().getTime();

            Bitmap same_ocr_img = resize_fixed_scale(OCR_IMG, 800.0f);
            BytesIM bytesIM = BitMapRenderScripts.bitMap2Bytes(same_ocr_img, "rgb", rs);


            long endTime = new Date().getTime();

            Log.i("BitMap2RGBBytes", String.format("BitMap2RGBBytes :%d/ms", (endTime - startTime)));

            tfd.dect(bytesIM);

            long lastTime1 = new Date().getTime();
            Log.i("Dect", String.format("dect_total_time :%d/ms", lastTime1 - endTime));


            ArrayList<BytesIM> bytesIMs = ImageUtil.getSubImgsBytesIM(OCR_IMG, tfd.getScores_boxes(),
                    tfd.getScore_boxes_shape(),
                    Threshold_ratio, Threshold_avg_pool_size, rs);

            long lastTime2 = new Date().getTime();
            Log.i("getSubImgsBytes", String.format("getSubImgsBytes :%d/ms", lastTime2 - lastTime1));

            StringBuilder res = new StringBuilder();
            int count = (int) Math.ceil(bytesIMs.size() / 10.0);
            Log.i("count", String.format("count:%d", bytesIMs.size()));

            for (int i = 0; i < count; i++) {
                int end_idx = Math.min(10 * (i + 1), bytesIMs.size());
                int start_idx = 10 * i;

                List<BytesIM> subBytesIMs = bytesIMs.subList(start_idx, end_idx);
                subBytesIMs = ImageUtil.BytesIMFrom(subBytesIMs, rs);
                BytesIMs bytesIMs_object = new BytesIMs();
                bytesIMs_object.extend(subBytesIMs);
                String tmp = ttr.reg(bytesIMs_object.getBytes(), bytesIMs_object.getBatch_size(),
                        bytesIMs_object.getHeight(),
                        bytesIMs_object.getWidth(), bytesIMs_object.getChannel());
                res.append(tmp);
//                Log.i("count",String.format("count:%d\tcontent:%s",i,tmp));
//
//                System.gc();
            }
            long lastTime3 = new Date().getTime();
            Log.i("reg", String.format("reg :%d/ms", lastTime3 - lastTime2));


            Bitmap outBitMap = null;
            for (int i = 0; i < bytesIMs.size(); i++) {
                bytesIM = bytesIMs.get(i);

                Bitmap bitmap = BitMapRenderScripts.bytes2BitMap(bytesIM, rs);

                if (i == 0) {
                    outBitMap = bitmap;
                } else {
                    outBitMap = ImageUtil.mergeBitmap_TB(outBitMap, bitmap, true);
                }
            }


            Intent intent = new Intent(OcrActivity.this, OcrResultActivity.class);
            intent.putExtra(ConstantManager.RECEIVEID_OCR_IMAGE_RESULT, res.toString());
            String ocr_filename = Environment.getExternalStorageDirectory() + "/" + ConstantManager.OCR_IMG_FILENAME;
            ImageUtil.saveBitMapAsFile(outBitMap, ocr_filename);
            if (outBitMap != null && !outBitMap.isRecycled()) {
                outBitMap.recycle();
            }


            intent.putExtra(ConstantManager.RECEIVEID_OCR_IMAGE, ocr_filename);
            Log.e("OCR_IMAGE_RESULT", res.toString());


            OcrActivity.this.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            tools.customToast(ConstantManager.TOST_JSONPARSEFALSE, OcrActivity.this);
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
        ButtonCard.setButtonEnable(bool);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        OcrActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


}
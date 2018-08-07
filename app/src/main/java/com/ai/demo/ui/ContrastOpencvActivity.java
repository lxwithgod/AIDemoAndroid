package com.ai.demo.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.ai.cameralib.CameraLibActivity;
import com.ai.demo.R;
import com.ai.demo.entity.ButtonDialog;
import com.ai.demo.entity.CustomImageButton;
import com.ai.demo.json.FileTransJsonCreate;
import com.ai.demo.json.UrlString;
import com.ai.demo.utils.BitMapRenderScripts;
import com.ai.demo.utils.BytesIM;
import com.ai.demo.utils.ConstantManager;
import com.ai.demo.utils.CustomTools;
import com.ai.demo.utils.TSF;
import com.ai.demo.utils.ImageUtil;
import com.ai.demo.utils.TfDect;

import java.io.File;
import java.util.Arrays;

import static com.ai.demo.utils.AIDemoUtils.*;

public class ContrastOpencvActivity extends BaseActivity {
    private CustomTools tools = new CustomTools();
    private CustomImageButton ButtonCard;
    private CustomImageButton ButtonFace;
    private ButtonDialog dialog;
    private Bitmap id_face;
    private Bitmap face;
    private FileTransJsonCreate fileJsonTrans;
    private Button btClick;
    private ProgressBar progressDialog;
    private static String CAMERAIMAGENAME = "image.jpg";
    private static String SDKCAMERARETURNFIELD = "cropBitmapByte";

    private UrlString urlString = new UrlString();
    private TSF tsf = null;
    private TfDect tfd = null;

    private String faceHeightWith = "";
    private String idFaceHeightWith = "";
    private RenderScript renderScript = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tsf = new TSF(getAssets());
        tfd = new TfDect(getAssets());

        setContentView(R.layout.activity_contrast);
        ButtonCard = (CustomImageButton) ContrastOpencvActivity.this.findViewById(R.id.bt_card);
        ButtonFace = (CustomImageButton) ContrastOpencvActivity.this.findViewById(R.id.bt_face);
        ButtonCard.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        ButtonFace.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        btClick = (Button) ContrastOpencvActivity.this.findViewById(R.id.bt_contrast);
        progressDialog = (ProgressBar) findViewById(com.ai.cameralib.R.id.progressBar);


        renderScript = RenderScript.create(this.getBaseContext());
        setControlEnable(true);

        // 加载配置文件
//        urlString.setIPAddress(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();


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
            case R.id.bt_card:
                // 添加照片 - 身份证
                openDialog(ConstantManager.ISCARD);
                break;

            case R.id.bt_face:
                // 添加照片 - 人像
                openDialog(ConstantManager.ISFACE);
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
        dialog = new ButtonDialog(ContrastOpencvActivity.this, R.style.Dialog);
        dialog.show();

//        requestCAMERAPower();

        dialog.findViewById(R.id.btn_open_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (getType == ConstantManager.ISCARD) {
                    // 调用自定义拍照相机
                    Intent intent = new Intent(ContrastOpencvActivity.this, CameraLibActivity.class);
                    startActivityForResult(intent, ConstantManager.OPENCUSTOMCAMERA);
                }
                if (getType == ConstantManager.ISFACE) {
                    // 打开本地相机
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), CAMERAIMAGENAME));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, ConstantManager.OPENLOCALCAMERA);
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
                if (getType == ConstantManager.ISFACE) {
                    startActivityForResult(intent, ConstantManager.OPENPHOTOS_FACE);
                }

            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bm = null;
        switch (requestCode) {
            // 调用SDK
            case ConstantManager.OPENCUSTOMCAMERA:
                if (data != null) {
                    byte[] cropBitmapByte = data.getByteArrayExtra(SDKCAMERARETURNFIELD);

                    // 直接显示
                    bm = BitmapFactory.decodeByteArray(cropBitmapByte, 0, cropBitmapByte.length);

                    id_face = utilRsDectView(bm, ButtonCard);


                }
                break;

            // 表示调用本地照相机拍照
            case ConstantManager.OPENLOCALCAMERA:
                if (resultCode == RESULT_OK) {
                    //Bundle bundle = data.getExtras();
                    //Bitmap bitmap = (Bitmap) bundle.get("data");
                    bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + CAMERAIMAGENAME);
                    face = utilRsDectView(bm, ButtonFace);

                }
                break;

            // 选择图片库的图片 身份证
            case ConstantManager.OPENPHOTOS_CARD:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    try {
                        bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        id_face = utilRsDectView(bm, ButtonCard);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            // 选择图片库的图片 人像
            case ConstantManager.OPENPHOTOS_FACE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    try {
                        bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        face = utilRsDectView(bm, ButtonFace);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            default:
                break;
        }

    }

    private Bitmap utilRsDectView(Bitmap bm, CustomImageButton bt) {
        // 设置显示
        int btWidth = bt.getWidth();
        int btHeight = bt.getHeight();
        Bitmap resizeFace = bt.resizeBitmap(bm, btWidth, btHeight);
        bt.setBitmap(resizeFace);

        // 提取头像
        Bitmap rsBm = resizeBitmap(bm, bm.getWidth() / 2, bm.getHeight() / 2);
        BytesIM img = BitMapRenderScripts.bitMap2Bytes(rsBm, "rgb", renderScript);


        float[] y1x1y2x2 = tfd.dect(img);
        Log.e("y1x1y2x2 >>>", Arrays.toString(y1x1y2x2));
        int x1 = (int) (y1x1y2x2[1] * bm.getWidth());
        int y1 = (int) (y1x1y2x2[0] * bm.getHeight());
        int x2 = (int) (y1x1y2x2[3] * bm.getWidth());
        int y2 = (int) (y1x1y2x2[2] * bm.getHeight());
        Log.e("y2 - y1", String.valueOf(y2 - y1));
        Log.e("x2 - x1", String.valueOf(x2 - x1));
        if (bt == ButtonCard) {
            idFaceHeightWith = String.format("h*w:%d*%d", y2 - y1, x2 - x1);
        } else {
            faceHeightWith = String.format("h*w:%d*%d", y2 - y1, x2 - x1);
        }

        Bitmap reBm = cropBitmap(bm, x1, y1, x2, y2);


        if (!bm.isRecycled())
            bm.recycle();

        if (!rsBm.isRecycled())
            rsBm.recycle();

        if (reBm == null) {
            bt.clearnImage();
            tools.customToast(ConstantManager.TOST_FACENULL, ContrastOpencvActivity.this);
        }

        return reBm;

    }


    /**
     * 点击提交
     *
     * @param v
     */
    public void contrastClick(View v) {
        try {

            if (face == null | id_face == null) {
                tools.customToast(ConstantManager.TOST_ADDPHOTO, ContrastOpencvActivity.this);
                return;
            }

            // 缩放
            id_face = resizeBitmap(id_face, 160, 160);
            face = resizeBitmap(face, 160, 160);

            // 识别中禁止点击
            setControlEnable(false);

            // 开始发送数据并接收返回结果
            RenderScript renderScript = RenderScript.create(this.getBaseContext());

            BytesIM idFace = BitMapRenderScripts.bitMap2Bytes(id_face, "rgb", renderScript);
            BytesIM xcFace = BitMapRenderScripts.bitMap2Bytes(face, "rgb", renderScript);

            byte[] idxcFaces = tsf.concat(idFace.getBytes(), xcFace.getBytes());

            float[] euc_cos = tsf.distance(idxcFaces);


            String edu = Float.toString(euc_cos[0]);
            String cosine = Float.toString(euc_cos[1]);

            Intent intent = new Intent(ContrastOpencvActivity.this, ContrastResultActivity.class);
            intent.putExtra(ConstantManager.RECEIVEEDU, edu);
            intent.putExtra(ConstantManager.RECEIVECOSINE, cosine);
            Log.e("edu", edu);
            Log.e("cosine", cosine);

            String id_face_fliename = Environment.getExternalStorageDirectory() + "/" + ConstantManager.CARD_FACE_FILENAME;
            String face_fliename = Environment.getExternalStorageDirectory() + "/" + ConstantManager.FACE_FILENAME;
            ImageUtil.saveBitMapAsFile(id_face, id_face_fliename);
            ImageUtil.saveBitMapAsFile(face, face_fliename);


            intent.putExtra(ConstantManager.RECEIVEID_FACE, id_face_fliename);
            intent.putExtra(ConstantManager.RECEIVEFACE, face_fliename);

            intent.putExtra(ConstantManager.RECEIVEFACE_HW, faceHeightWith);
            intent.putExtra(ConstantManager.RECEIVEID_HW, idFaceHeightWith);
            ContrastOpencvActivity.this.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            tools.customToast(ConstantManager.TOST_JSONPARSEFALSE, ContrastOpencvActivity.this);
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
        ButtonFace.setButtonEnable(bool);

    }


    /**
     * 返回
     *
     * @param v
     */
    public void blackClick(View v) {
        try {

            renderScript.destroy();
            this.finish();


        } catch (
                Exception e)

        {
            Log.e("异常", "click异常!");
            e.printStackTrace();
        }

    }


}

package com.ai.demo.utils;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import com.ai.demo.json.FileTransObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 通用函数
 */
public class AIDemoUtils {

    /**
     * 传输内容的对象 转换成 json
     *
     * @param fileTransObject 传输内容的对象
     * @return 返回json字符串
     */
    public static String FileTransToJson(FileTransObject fileTransObject) {
        String result = "";
        result = JSON.toJSONString(fileTransObject);
        return result;
    }

    public static JSONObject FileTransToJsonObject(FileTransObject fileTransObject) throws JSONException {
        String str = JSON.toJSONString(fileTransObject);
        return new JSONObject(str);
    }

    public static String getJson(String fileName, AssetManager assetManager) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器

            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap Base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    /*
    缩放 指定高度
     */
    public static Bitmap resizeBitmap(Bitmap bm, int ivbWidth, int ivbHeight) {
        Bitmap resizeBmp = null;
        try {
            int width = bm.getWidth();
            int height = bm.getHeight();

            Matrix matrix = new Matrix();

            float scaleWidth = ((float) ivbWidth) / width;
            float scaleHeight = ((float) ivbHeight) / height;

            matrix.postScale(scaleWidth, scaleHeight); //长和宽放大缩小的比例
            resizeBmp = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return resizeBmp;
    }


    /**
     * 裁剪图片
     *
     * @param bitmap 传入需要裁剪的bitmap
     * @return 返回裁剪后的bitmap
     */
    public static Bitmap cropBitmap(Bitmap bitmap, int x1, int y1, int x2, int y2) {
        if (x1 + x2 + y2 + y1 == 0) {
            return null;
        }
        int cropWidth = x2 - x1;
        int cropHeight = y2 - y1;
        return Bitmap.createBitmap(bitmap, x1, y1, cropWidth, cropHeight, null, false);
    }

    public static Bitmap bitmap2Gray(Bitmap bmSrc) {
        // 得到图片的长和宽
        int width = bmSrc.getWidth();
        int height = bmSrc.getHeight();
        // 创建目标灰度图像
        Bitmap bmpGray = null;
        bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        // 创建画布
        Canvas c = new Canvas(bmpGray);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmSrc, 0, 0, paint);
        return bmpGray;
    }


}

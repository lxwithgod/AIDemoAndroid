package com.ai.demo.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v8.renderscript.RenderScript;
//import org.tensorflow.Graph;
//import org.tensorflow.Output;
//import org.tensorflow.Tensor;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.ai.demo.utils.ConstantManager.OCR_IMAGE_HEIGHT;

/**
 * Created by lx on 17-10-12.
 */
public class ImageUtil {
    /**
     * @param bitmap
     * @return
     */

    public static Bitmap drawRect(Bitmap bitmap, float[] score_boxes, int[] score_boxes_shape) {
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);  //线的宽度
        for (int i = 0; i < score_boxes_shape[0]; i++) {
            int x1 = (int) (score_boxes[i * score_boxes_shape[1] + 1] * bitmap.getWidth());
            int y1 = (int) (score_boxes[i * score_boxes_shape[1] + 2] * bitmap.getHeight());
            int x2 = (int) (score_boxes[i * score_boxes_shape[1] + 3] * bitmap.getWidth());
            int y2 = (int) (score_boxes[i * score_boxes_shape[1] + 4] * bitmap.getHeight());
            canvas.drawRect(x1, y1, x2, y2, paint);

        }

        return bitmap;

    }

    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                height, matrix, false);

        return resizedBitmap;
    }


    public static ArrayList<BytesIM> BytesIMFrom(List<BytesIM> bytesIM_arr, RenderScript rs) {
        int max_heigth = 0;
        int max_width = 0;
        for (BytesIM bytesIM : bytesIM_arr) {
            if (bytesIM.getWidth() > max_width) {
                max_width = bytesIM.getWidth();
            }
            if (bytesIM.getHeight() > max_heigth) {
                max_heigth = bytesIM.getHeight();
            }
        }
        ArrayList<BytesIM> bytesIM_new_arr = new ArrayList<>();

        for (BytesIM bytesIM : bytesIM_arr) {
            bytesIM_new_arr.add(BitMapRenderScripts.bytesIMPadding(bytesIM, max_heigth, max_width, rs));
        }

        return bytesIM_new_arr;


    }

    public static ArrayList<BytesIM> getSubImgsBytesIM(Bitmap bitmap, float[] score_boxes, int[] score_boxes_shape,
                                                       float ratio, int size, RenderScript rs) {
//        Canvas canvas = new Canvas(bitmap);
//        Paint paint=new Paint();
//        paint.setColor(Color.RED);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(3);  //线的宽度


        ArrayList<BytesIM> bytesIMs = new ArrayList<>();
        for (int i = 0; i < score_boxes_shape[0]; i++) {

            int x1 = (int) (score_boxes[i * score_boxes_shape[1] + 1] * bitmap.getWidth());
            int y1 = (int) (score_boxes[i * score_boxes_shape[1] + 2] * bitmap.getHeight());
            int x2 = (int) (score_boxes[i * score_boxes_shape[1] + 3] * bitmap.getWidth());
            int y2 = (int) (score_boxes[i * score_boxes_shape[1] + 4] * bitmap.getHeight());

//            canvas.drawRect(x1,y1,x2,y2,paint);

            Bitmap tmp1 = AIDemoUtils.cropBitmap(bitmap, x1, y1, x2, y2);

            int width = (int) (OCR_IMAGE_HEIGHT * 1.0 / tmp1.getHeight() * tmp1.getWidth());

            Bitmap tmp2 = AIDemoUtils.resizeBitmap(tmp1, width, OCR_IMAGE_HEIGHT);
            Bitmap tmp3 = null;
            if (size != 0) {
                tmp3 = BitMapRenderScripts.avg_pool(tmp2, ratio, size, size, rs);
            } else {
                tmp3 = tmp2;
            }

            BytesIM bytesIM = BitMapRenderScripts.bitMap2Bytes(tmp3, "gray", rs);
//
            BytesIM threshold = BitMapRenderScripts.bytes2ThresholdOTUS(bytesIM, rs);
//
//            BytesIM threshold = BitMapRenderScripts.threshold(tmp2, ratio, size, size, rs);

            if (!tmp1.isRecycled()) {
                tmp1.recycle();
            }
            if (!tmp2.isRecycled()) {
                tmp2.recycle();
            }
            if (tmp3 != null && !tmp3.isRecycled()) {
                tmp3.recycle();
            }
//            bytesIM.recycle();

            bytesIMs.add(threshold);
//            bytesIMs.add(bytesIM);

        }
        return bytesIMs;
    }


    public static void saveBitMapAsFile(Bitmap bitmap, String file_name) {
        File file = new File(file_name);
        FileOutputStream fOut = null;

        try {
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 把两个位图覆盖合成为一个位图，左右拼接
     *
     * @param leftBitmap
     * @param rightBitmap
     * @param isBaseMax   是否以宽度大的位图为准，true则小图等比拉伸，false则大图等比压缩
     * @return
     */
    public static Bitmap mergeBitmap_LR(Bitmap leftBitmap, Bitmap rightBitmap, boolean isBaseMax) {

        if (leftBitmap == null || leftBitmap.isRecycled()
                || rightBitmap == null || rightBitmap.isRecycled()) {
            return null;
        }
        int height = 0; // 拼接后的高度，按照参数取大或取小
        if (isBaseMax) {
            height = leftBitmap.getHeight() > rightBitmap.getHeight() ? leftBitmap.getHeight() : rightBitmap.getHeight();
        } else {
            height = leftBitmap.getHeight() < rightBitmap.getHeight() ? leftBitmap.getHeight() : rightBitmap.getHeight();
        }

        // 缩放之后的bitmap
        Bitmap tempBitmapL = leftBitmap;
        Bitmap tempBitmapR = rightBitmap;

        if (leftBitmap.getHeight() != height) {
            tempBitmapL = Bitmap.createScaledBitmap(leftBitmap, (int) (leftBitmap.getWidth() * 1f / leftBitmap.getHeight() * height), height, false);
        } else if (rightBitmap.getHeight() != height) {
            tempBitmapR = Bitmap.createScaledBitmap(rightBitmap, (int) (rightBitmap.getWidth() * 1f / rightBitmap.getHeight() * height), height, false);
        }

        // 拼接后的宽度
        int width = tempBitmapL.getWidth() + tempBitmapR.getWidth();

        // 定义输出的bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // 缩放后两个bitmap需要绘制的参数
        Rect leftRect = new Rect(0, 0, tempBitmapL.getWidth(), tempBitmapL.getHeight());
        Rect rightRect = new Rect(0, 0, tempBitmapR.getWidth(), tempBitmapR.getHeight());

        // 右边图需要绘制的位置，往右边偏移左边图的宽度，高度是相同的
        Rect rightRectT = new Rect(tempBitmapL.getWidth(), 0, width, height);

        canvas.drawBitmap(tempBitmapL, leftRect, leftRect, null);
        canvas.drawBitmap(tempBitmapR, rightRect, rightRectT, null);
        return bitmap;
    }


    /**
     * 把两个位图覆盖合成为一个位图，上下拼接
     *
     * @param topBitmap
     * @param bottomBitmap
     * @param isBaseMax    是否以高度大的位图为准，true则小图等比拉伸，false则大图等比压缩
     * @return
     */
    public static Bitmap mergeBitmap_TB(Bitmap topBitmap, Bitmap bottomBitmap, boolean isBaseMax) {

        if (topBitmap == null || topBitmap.isRecycled()
                || bottomBitmap == null || bottomBitmap.isRecycled()) {

            return null;
        }
        int width = 0;
        if (isBaseMax) {
            width = topBitmap.getWidth() > bottomBitmap.getWidth() ? topBitmap.getWidth() : bottomBitmap.getWidth();
        } else {
            width = topBitmap.getWidth() < bottomBitmap.getWidth() ? topBitmap.getWidth() : bottomBitmap.getWidth();
        }
        Bitmap tempBitmapT = topBitmap;
        Bitmap tempBitmapB = bottomBitmap;

        if (topBitmap.getWidth() != width) {
            tempBitmapT = Bitmap.createScaledBitmap(topBitmap, width, (int) (topBitmap.getHeight() * 1f / topBitmap.getWidth() * width), false);
        } else if (bottomBitmap.getWidth() != width) {
            tempBitmapB = Bitmap.createScaledBitmap(bottomBitmap, width, (int) (bottomBitmap.getHeight() * 1f / bottomBitmap.getWidth() * width), false);
        }

        int height = tempBitmapT.getHeight() + tempBitmapB.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Rect topRect = new Rect(0, 0, tempBitmapT.getWidth(), tempBitmapT.getHeight());
        Rect bottomRect = new Rect(0, 0, tempBitmapB.getWidth(), tempBitmapB.getHeight());

        Rect bottomRectT = new Rect(0, tempBitmapT.getHeight(), width, height);

        canvas.drawBitmap(tempBitmapT, topRect, topRect, null);
        canvas.drawBitmap(tempBitmapB, bottomRect, bottomRectT, null);

        if (!topBitmap.isRecycled()) {
            topBitmap.recycle();
        }
        if (!bottomBitmap.isRecycled()) {
            bottomBitmap.recycle();
        }

        return bitmap;
    }

}
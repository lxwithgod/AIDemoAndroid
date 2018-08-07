package com.ai.cameralib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-09-04
 * Time: 18:03
 */
public class PreviewBorderView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private float mScreenH;
    private float mScreenW;
    private Canvas mCanvas;
    private Paint mPaint;
    private Paint mPaintLine;
    private SurfaceHolder mHolder;
    private Thread mThread;
    public static String DEFAULT_TIPS_TEXT = "";
    private static final int DEFAULT_TIPS_TEXT_SIZE = 26;
    private static final int DEFAULT_TIPS_TEXT_COLOR = Color.WHITE;
    private static final String TAG = "PreviewBorderView";
    /**
     * 自定义属性
     */
    private float tipTextSize;
    private int tipTextColor;
    private String tipText;

    public PreviewBorderView(Context context) {
        this(context, null);
    }

    public PreviewBorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreviewBorderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init();
    }

    /**
     * 初始化自定义属性
     *
     * @param context Context
     * @param attrs   AttributeSet
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PreviewBorderView);
        try {
            tipTextSize = a.getDimension(R.styleable.PreviewBorderView_tipTextSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TIPS_TEXT_SIZE, getResources().getDisplayMetrics()));
            tipTextColor = a.getColor(R.styleable.PreviewBorderView_tipTextColor, DEFAULT_TIPS_TEXT_COLOR);
            tipText = a.getString(R.styleable.PreviewBorderView_tipText);
            if (tipText == null) {
                tipText = DEFAULT_TIPS_TEXT;
            }
        } finally {
            a.recycle();
        }


    }

    /**
     * 初始化绘图变量
     */
    private void init() {
        this.mHolder = getHolder();
        this.mHolder.addCallback(this);
        this.mHolder.setFormat(PixelFormat.TRANSPARENT);
        setZOrderOnTop(true);
        setZOrderMediaOverlay(true);
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(Color.WHITE);
        this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        this.mPaintLine = new Paint();
        // 消除锯齿
        this.mPaintLine.setAntiAlias(true);
        this.mPaintLine.setColor(Color.GRAY);
        this.mPaintLine.setStyle(Paint.Style.STROKE);
        // 浮雕效果
        float[] direction = new float[]{ 1, 1, 1 };
        //设置环境光亮度
        float light = 0.4f;
        // 选择要应用的反射等级
        float specular = 6;
        // 向mask应用一定级别的模糊
        float blur = 3.5f;
        EmbossMaskFilter emboss=new EmbossMaskFilter(direction,light,specular,blur);
        this.mPaintLine.setMaskFilter(emboss);
        this.mPaintLine.setStrokeWidth(3.0F);
        setKeepScreenOn(true);
    }

    /**
     * 绘制取景框
     */
    private void draw() {
        try {
            //根据身份证比例计算 int强转直接把小数位截掉
            double scale_ratio = 0.9;
            final float tv_width = (float) (scale_ratio * mScreenW);
            final float tv_height = (float) (tv_width / 1.59);

            this.mCanvas = this.mHolder.lockCanvas();
            this.mCanvas.drawARGB(160, 0, 0, 0);
            this.mCanvas.drawRect(new RectF((this.mScreenW - tv_width) / 2, (this.mScreenH - tv_height) / 2,
                    (this.mScreenW - tv_width) / 2 + tv_width, (this.mScreenH - tv_height) / 2 + tv_height), this.mPaint);
            this.mCanvas.drawRect(new RectF((this.mScreenW - tv_width) / 2, (this.mScreenH - tv_height) / 2,
                    (this.mScreenW - tv_width) / 2 + tv_width, (this.mScreenH - tv_height) / 2 + tv_height), this.mPaintLine);

            mPaintLine.setTextSize(tipTextSize);
            mPaintLine.setAntiAlias(true);
            mPaintLine.setDither(true);
            mPaintLine.setTextAlign(Paint.Align.CENTER);
            float length = mPaintLine.measureText(tipText);
            this.mCanvas.drawText(tipText, this.mScreenW / 2, (this.mScreenH - tv_height) / 2 *9 / 10, mPaintLine);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (this.mCanvas != null) {
                this.mHolder.unlockCanvasAndPost(this.mCanvas);
            }
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //获得宽高，开启子线程绘图
        this.mScreenW = getWidth();
        this.mScreenH = getHeight();
        this.mThread = new Thread(this);
        this.mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //停止线程
        try {
            mThread.interrupt();
            mThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //Log.d(TAG, "widthMeasureSpec:" + widthMeasureSpec + ",heightMeasureSpec:" + heightMeasureSpec);
    }

    @Override
    public void run() {
        //子线程绘图
        draw();
    }
}

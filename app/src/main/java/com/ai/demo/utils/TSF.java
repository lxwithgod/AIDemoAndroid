package com.ai.demo.utils;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Trace;
import android.support.annotation.RequiresApi;
import android.util.Log;

import junit.framework.Assert;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by lx on 17-10-11.
 */
public class TSF {
    private static final String MODEL_FILE = "file:///android_asset/model.pb"; //模型存放路径
    private static final String testFile = "file:///android_asset/test.jpg";    //数据的维度
    private static final int HEIGHT = 160;
    private static final int WIDTH = 160;
    private static final int CHANNEL = 3;

    //模型中输出变量的名称
    private static final String inputName = "tiny/inputs";
    private static final String isTrain = "tiny/isTrain";
    //用于存储的模型输入数据
//    private float[] inputs = new float[HEIGHT * WIDTH];

    //模型中输出变量的名称
    private static final String feature_name = "tiny/features";
    private static final String euc_distances = "tiny/euc_distances";
    private static final String euc_distance = "tiny/euc_distance";
    private static final String sim_distances = "tiny/sim_distances";
    private static final String sim_distance = "tiny/sim_distance";
    //用于存储模型的输出数据
    private static final int feature_size = 128;


    TensorFlowInferenceInterface inferenceInterface;

//
//    static {
//        //加载库文件
//        System.loadLibrary("tensorflow_inference");
//    }

    public TSF(AssetManager assetManager) {
        //接口定义

        inferenceInterface = new TensorFlowInferenceInterface(assetManager, MODEL_FILE);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public float[] features(float[] pics) {
        //为输入数据赋值


        int BatchSize = pics.length / WIDTH / HEIGHT / CHANNEL;

        float[] outputs = new float[BatchSize * feature_size];

        Trace.beginSection("feed");
        inferenceInterface.feed(inputName, pics, BatchSize, WIDTH, HEIGHT, CHANNEL);
        Trace.endSection();

        Trace.beginSection("run");
        //,euc_distances,cos_distances
        String[] outputNames = new String[]{feature_name};
        inferenceInterface.run(outputNames);
        Trace.endSection();

        Trace.beginSection("fetch");
        inferenceInterface.fetch(feature_name, outputs);
        Trace.endSection();
        Log.e("sss", Arrays.toString(outputs));
        return outputs;
    }

    public byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public float[] concat(float[] a, float[] b) {
        float[] c = new float[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public void testPerfamance(byte[] pics) {
        pics = this.concat(pics, pics);
//        pics=this.concat(pics,pics);
//        pics=this.concat(pics,pics);
//        pics=this.concat(pics,pics);
        Long start = new Date().getTime();
//        for(int i=0;i<100;i++){
        distance(pics);
//        }
        Log.e("ttt", Long.toString(new Date().getTime() - start));
    }

    public float[] distance(byte[] pics) {

        int BatchSize = pics.length / WIDTH / HEIGHT / CHANNEL;

        float[] euc = new float[1];
        float[] sim = new float[1];


//        Trace.beginSection("feed");
        inferenceInterface.feed(inputName, pics, BatchSize, WIDTH, HEIGHT, CHANNEL);
//        Trace.endSection();

//        Trace.beginSection("run");
        //,euc_distances,cos_distances
        String[] outputNames = new String[]{euc_distance, sim_distance};
//        String[] outputNames = new String[] {"Tiny/red2"};
        inferenceInterface.run(outputNames);
//        Trace.endSection();

//        Trace.beginSection("fetch");
        inferenceInterface.fetch(euc_distance, euc);
        inferenceInterface.fetch(sim_distance, sim);
//        float[] outs=new float[BatchSize*feature_size];
//        inferenceInterface.fetch("Tiny/red2", outs);
//        Trace.endSection();

        float[] outs = this.concat(euc, sim);
//        Log.e("ttt", Arrays.toString(outs));
        return outs;
    }


}
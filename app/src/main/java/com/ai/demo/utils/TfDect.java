package com.ai.demo.utils;

import android.content.res.AssetManager;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import org.tensorflow.types.UInt8;

/**
 * Created by lx on 17-10-17.
 */
public class TfDect {
    private static final String MODEL_FILE = "file:///android_asset/faceDect.pb"; //模型存放路径
    private static final String image_tensor = "image_tensor";
    private static final String detection_boxes = "detection_boxes";
    private static final String detection_scores = "detection_scores";
    private static final String detection_classes = "detection_classes";
    private static final String num_detections = "num_detections";

    TensorFlowInferenceInterface inferenceInterface;

//
//    static {
//        //加载库文件
//        System.loadLibrary("tensorflow_inference");
//    }

    public TfDect(AssetManager assetManager) {
        //接口定义

        inferenceInterface = new TensorFlowInferenceInterface(assetManager, MODEL_FILE);
    }


    public float[] dect(BytesIM bytesIM) {
        inferenceInterface.feed(image_tensor, bytesIM.getBytes(), 1,
                bytesIM.getHeight(), bytesIM.getWidth(), bytesIM.getChannel());

        String[] outputNames = new String[]{detection_boxes, detection_scores, detection_classes, num_detections};
        inferenceInterface.run(outputNames);

        float[] num_dectects = new float[1];
        inferenceInterface.fetch(num_detections, num_dectects);
        int num = (int) num_dectects[0];
        float[] boxes = new float[1 * num * 4];
        inferenceInterface.fetch(detection_boxes, boxes);
        float[] scores = new float[1 * num];
        inferenceInterface.fetch(detection_scores, scores);
        float[] classes = new float[1 * num];

        inferenceInterface.fetch(detection_classes, classes);

        int max_idx = -1;
        float max_score = 0;
        for (int i = 0; i < num_dectects[0]; i++) {
            float tmp = scores[i];
            if (max_score < tmp && (int) classes[i] == 1) {
                max_score = tmp;
                max_idx = i;
            }
        }
        float ymin = 0;
        float xmin = 0;
        float ymax = 0;
        float xmax = 0;
        if (max_idx >= 0 && max_score > 0.5) {
            ymin = boxes[max_idx * 4];
            xmin = boxes[max_idx * 4 + 1];
            ymax = boxes[max_idx * 4 + 2];
            xmax = boxes[max_idx * 4 + 3];
        }

        float[] box = new float[]{ymin, xmin, ymax, xmax};
        return box;


    }


}

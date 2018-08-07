package com.ai.demo.utils;

import android.content.res.AssetManager;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.HashMap;
import java.util.Map;

public class TfTextRec {
    private static final String MODEL_FILE = "file:///android_asset/textRec.pb"; //模型存放路径
    private static final String images_tensor = "imgs";
    private static final String output_tensor_key = "output";

    private static final String output_tensor_shape_key = "output_shape";

    private static final String dict_path = "dict.json";


    TensorFlowInferenceInterface inferenceInterface;
    private int[] output_tensor_shape;
    private int[] output_tensor;

    HashMap<String, Object> id2word = new HashMap<>();
    int maxIdx = -1;

    public TfTextRec(AssetManager assetManager) {
        //接口定义

        inferenceInterface = new TensorFlowInferenceInterface(assetManager, MODEL_FILE);

//            assetManager.open("dict.json");
        String str = AIDemoUtils.getJson(dict_path, assetManager);
        JSONObject jsonObject = JSON.parseObject(str);
        if (jsonObject.containsKey("id2word")) {
            Map<String, Object> tmp = JSON.parseObject(jsonObject.get("id2word").toString()).getInnerMap();
            id2word.putAll(tmp);

            maxIdx = id2word.size() - 1;
        }

    }


    public String reg(byte[] pic, int batchsize, int height, int width, int channels) {

        inferenceInterface.feed(images_tensor, pic, batchsize, height, width, channels);

        String[] outputNames = new String[]{output_tensor_key, output_tensor_shape_key};
        inferenceInterface.run(outputNames);

        output_tensor_shape = new int[2];
        inferenceInterface.fetch(output_tensor_shape_key, output_tensor_shape);

        output_tensor = new int[output_tensor_shape[0] * output_tensor_shape[1]];
        inferenceInterface.fetch(output_tensor_key, output_tensor);


        return decode_output();

    }

    public int[] getOutput_tensor_shape() {
        return output_tensor_shape;
    }

    public int[] getOutput_tensor() {
        return output_tensor;
    }


    public String decode_output() {
        int[] output_tensor = getOutput_tensor();
        int[] output_tensor_shape = getOutput_tensor_shape();

        StringBuilder lines = new StringBuilder();

        for (int batch_idx = 0; batch_idx < output_tensor_shape[0]; batch_idx++) {
            StringBuilder line = new StringBuilder();
            int upIdx = -1;

            for (int j = 0; j < output_tensor_shape[1]; j++) {
                int word_idx = output_tensor[batch_idx * output_tensor_shape[1] + j];

                if (word_idx == maxIdx) {
                    upIdx = -1;
                } else {
                    if (word_idx != upIdx) {
                        line.append(id2word.get(String.valueOf(word_idx)));
                        upIdx = word_idx;
                    }
                }
            }
            lines.append(line.toString() + "\n");

        }
        return lines.toString();
    }
}

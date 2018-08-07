package com.ai.demo.utils;

import android.content.res.AssetManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lx on 17-10-17.
 */
public class TfNameEntity {
    private static final String MODEL_FILE = "file:///android_asset/nameEntityRec.pb"; //模型存放路径
    private static final String fileDict = "nameEntity.json"; //模型存放路径
    private static final String textInputs = "rnn_crf/input/inputs";
    private static final String textLengths = "rnn_crf/input/lengths";
    private static final String outputs = "rnn_crf/net/outputs";
    private final HashMap<String, Object> word2id = new HashMap<String, Object>();
    private final HashMap<Integer, String> id2pos = new HashMap<>();

    TensorFlowInferenceInterface inferenceInterface;

//
//    static {
//        //加载库文件
//        System.loadLibrary("tensorflow_inference");
//    }

    public TfNameEntity(AssetManager assetManager) {
        //接口定义

        inferenceInterface = new TensorFlowInferenceInterface(assetManager, MODEL_FILE);
        String str = AIDemoUtils.getJson(fileDict, assetManager);
        JSONObject jsonObject = JSON.parseObject(str);
        if (jsonObject.containsKey("word2id")) {
            Map<String, Object> tmp = (Map<String, Object>) jsonObject.get("word2id");
            word2id.putAll(tmp);
        }
        if (jsonObject.containsKey("id2pos")) {
            Map<String, Object> tmp = (Map<String, Object>) jsonObject.get("id2pos");
            Iterator<Map.Entry<String, Object>> iter = tmp.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, Object> ent = iter.next();

                int value = Integer.parseInt(ent.getKey());
                String name = ent.getValue().toString();

                id2pos.put(value, name);

            }

        }
    }

    public int[] handle_process(String text) {
        int[] textId = new int[text.length()];
        for (int i = 0; i < text.length(); i++) {
            String word = text.substring(i, i + 1);
            if (word2id.containsKey(word)) {
                textId[i] = Integer.parseInt(word2id.get(word).toString());
            } else {
                textId[i] = Integer.parseInt(word2id.get("pad").toString());
            }
        }
        return textId;
    }

    public String paser(String text) {
        int[] textId = handle_process(text);
        int[] lengths = new int[1];
        int[] ids = new int[textId.length];

        lengths[0] = textId.length;

        inferenceInterface.feed(textInputs, textId, 1L,
                (long) textId.length);
        inferenceInterface.feed(textLengths, lengths, 1L);

        String[] outputNames = new String[]{outputs};
        inferenceInterface.run(outputNames);

        inferenceInterface.fetch(outputs, ids);

        return handle_paser_result(text, ids);
    }

    public String handle_paser_result(String text, int[] ids) {

        String tmp = "";
        String pos = "";
        String sentence = "";
        boolean flag = false;
        for (int i = 0; i < text.length(); i++) {
            String word = text.substring(i, i + 1);
            String pos_name = id2pos.get(ids[i]);

            if (pos_name.endsWith("o")) {
                pos = "o";
                tmp += word;
            } else if (pos_name.endsWith("b")) {
                if (tmp.length() > 0) {
                    sentence += tmp + "/" + pos + "\t";
                    tmp = "";
                }
                pos = pos_name.substring(0, pos_name.length() - 2);
                flag = true;
            } else if (pos_name.endsWith("e")) {
                tmp += word;

                sentence += tmp + "/" + pos_name.substring(0, pos_name.length() - 2) + "\t";
                flag = false;
                tmp = "";
            }
            if (flag) {
                tmp += word;
            }

        }
        if (tmp.length() > 0)
            sentence += tmp + "/" + pos;
        return sentence;
    }

}

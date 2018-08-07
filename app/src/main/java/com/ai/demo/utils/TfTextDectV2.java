package com.ai.demo.utils;

import android.content.res.AssetManager;

public class TfTextDectV2 extends TfTextDect {
    private static final String MODEL_FILE = "file:///android_asset/textDect.pb"; //模型存放路径
    private static final String image_tensor = "img";
    private static final String final_score_boxes = "anchor_boxes_layer_test/score_boxes";

    private static final String final_score_boxes_shape = "anchor_boxes_layer_test/score_boxes_shape";

    public TfTextDectV2(AssetManager assetManager) {
        super(assetManager);
    }


}

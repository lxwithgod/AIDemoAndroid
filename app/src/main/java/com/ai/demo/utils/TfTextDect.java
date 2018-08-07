package com.ai.demo.utils;

import android.content.res.AssetManager;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class TfTextDect {
    private static final String MODEL_FILE = "file:///android_asset/textDect.pb"; //模型存放路径
    private static final String image_tensor = "img";
    //    private static final String final_score_boxes = "score_boxes";
    private static final String final_score_boxes = "score_relative_boxes";
    //    private static final String final_score_boxes = "rois/rpn_rois";
    private static final String final_score_boxes_shape = "score_relative_boxes_shape";
//    private static final String final_score_boxes_shape = "score_relative_boxes_shape";
//    private static final String final_score_boxes_shape = "rois/rpn_rois_shape";
//    private static final String crop_sub_imgs = "crop_sub_imgs";
//    private static final String crop_sub_imgs_shape = "crop_sub_imgs_shape";
//    private static final String draw_img_boxes = "draw_img_boxes";
//    private static final String draw_img_boxes_shape = "draw_img_boxes_shape";


    private byte[] sub_imgs = null;
    private int[] sub_imgs_shape = null;


    TensorFlowInferenceInterface inferenceInterface;
    private int[] score_boxes_shape;
    private float[] scores_boxes;

    public TfTextDect(AssetManager assetManager) {
        //接口定义

        inferenceInterface = new TensorFlowInferenceInterface(assetManager, MODEL_FILE);
    }


    public float[] dect(BytesIM bytesIM) {

        inferenceInterface.feed(image_tensor, bytesIM.getBytes(),
                bytesIM.getHeight(), bytesIM.getWidth(), bytesIM.getChannel());

        String[] outputNames = new String[]{final_score_boxes, final_score_boxes_shape};
//                crop_sub_imgs,crop_sub_imgs_shape};
        inferenceInterface.run(outputNames);

        score_boxes_shape = new int[2];
        inferenceInterface.fetch(final_score_boxes_shape, score_boxes_shape);

        scores_boxes = new float[score_boxes_shape[0] * score_boxes_shape[1]];
        inferenceInterface.fetch(final_score_boxes, scores_boxes);

//        sub_imgs_shape=new int[4];
//        inferenceInterface.fetch(crop_sub_imgs_shape, sub_imgs_shape);
//        sub_imgs = new byte[sub_imgs_shape[0]*sub_imgs_shape[1]*sub_imgs_shape[2]*sub_imgs_shape[3]];
//        inferenceInterface.fetch(crop_sub_imgs_shape, sub_imgs);

//        draw_img_shape=new int[3];
//        inferenceInterface.fetch(draw_img_boxes_shape,draw_img_shape);
//        draw_img=new byte[draw_img_shape[0]*draw_img_shape[1]*draw_img_shape[2]];
//        inferenceInterface.fetch(draw_img_boxes,draw_img);

        return scores_boxes;

    }

    public byte[] getSub_imgs() {
        return sub_imgs;
    }

    public int[] getSub_imgs_shape() {
        return sub_imgs_shape;
    }


    public int[] getScore_boxes_shape() {
        return score_boxes_shape;
    }

    public float[] getScores_boxes() {
        return scores_boxes;
    }

}

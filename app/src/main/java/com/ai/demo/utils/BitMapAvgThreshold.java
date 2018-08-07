package com.ai.demo.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

public class BitMapAvgThreshold {

    private int[][][] ImageIntegeral = null;
    private Bitmap threshold_img = null;
    private Bitmap bitmap = null;
    int width = 0;
    int height = 0;


    public BitMapAvgThreshold(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.height = bitmap.getHeight();
        this.width = bitmap.getWidth();

    }

    public int[][][] calIntergeralImg() {
        ImageIntegeral = new int[height][width][3];


        int val = 0;

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {


                val = bitmap.getPixel(w, h);


                if (w == 0) {//说明第0列
                    ImageIntegeral[h][w][0] = Color.red(val);
                    ImageIntegeral[h][w][1] = Color.green(val);
                    ImageIntegeral[h][w][2] = Color.blue(val);
                } else {

                    ImageIntegeral[h][w][0] = Color.red(val) + ImageIntegeral[h][w - 1][0];
                    ImageIntegeral[h][w][1] = Color.green(val) + ImageIntegeral[h][w - 1][1];
                    ImageIntegeral[h][w][2] = Color.blue(val) + ImageIntegeral[h][w - 1][2];
                }


            }
        }


        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {

                int r = ImageIntegeral[h][w][0];
                int g = ImageIntegeral[h][w][1];
                int b = ImageIntegeral[h][w][2];


                if (h == 0) {//说明第0行
                    ImageIntegeral[h][w][0] = r;
                    ImageIntegeral[h][w][1] = g;
                    ImageIntegeral[h][w][2] = b;
                } else {
                    ImageIntegeral[h][w][0] = r + ImageIntegeral[h - 1][w][0];
                    ImageIntegeral[h][w][1] = g + ImageIntegeral[h - 1][w][1];
                    ImageIntegeral[h][w][2] = b + ImageIntegeral[h - 1][w][2];
                }

            }
        }


        return ImageIntegeral;
    }

    public Bitmap threshold(double ratio, int k_h, int k_w) {
        threshold_img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        int min_x;
        int min_y;
        int max_x;
        int max_y;

        int[] sum = new int[3];


        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                min_x = Math.max(w - k_w, 0);
                min_y = Math.max(h - k_h, 0);
                max_x = Math.min(w + k_w, width - 1);
                max_y = Math.min(h + k_h, height - 1);

                int area = (max_x - min_x + 1) * (max_y - min_y + 1);


                int[] top_left = new int[3];
                if (min_x - 1 >= 0 && min_y - 1 >= 0) {
                    top_left = ImageIntegeral[(min_y - 1)][min_x - 1];
                }
                int[] bottom_left = new int[3];
                if (min_x - 1 >= 0) {
                    bottom_left = ImageIntegeral[max_y][min_x - 1];
                }
                int[] top_right = new int[3];
                if (min_y - 1 >= 0) {
                    top_right = ImageIntegeral[min_y - 1][max_x];
                }
                sum[0] = ImageIntegeral[max_y][max_x][0] + top_left[0]
                        - top_right[0] - bottom_left[0];
                sum[1] = ImageIntegeral[max_y][max_x][1] + top_left[1]
                        - top_right[1] - bottom_left[1];
                sum[2] = ImageIntegeral[max_y][max_x][2] + top_left[2]
                        - top_right[2] - bottom_left[2];


                int data_val = bitmap.getPixel(w, h);

                int red = Color.red(data_val) > ratio * Math.floor(sum[0] * 1.0 / area) ? 255 : 0;
                int green = Color.green(data_val) > ratio * Math.floor(sum[1] * 1.0 / area) ? 255 : 0;
                int blue = Color.blue(data_val) > ratio * Math.floor(sum[2] * 1.0 / area) ? 255 : 0;
                threshold_img.setPixel(w, h, Color.argb(255, red, green, blue));
            }

        }
        return threshold_img;
    }

    public Bitmap getThreshold_img() {
        return threshold_img;
    }

}

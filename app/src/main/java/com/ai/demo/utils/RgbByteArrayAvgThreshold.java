package com.ai.demo.utils;

public class RgbByteArrayAvgThreshold {
    private byte[] data = null;
    private int batch_size = 0;
    private int height = 0;
    private int width = 0;
    private int channel = 0;

    private int[] ImageIntegeral = null;
    private byte[] threshold_img = null;


    public RgbByteArrayAvgThreshold(byte[] data, int batch_size, int height, int width, int channel) {
        this.data = data;
        this.batch_size = batch_size;
        this.height = height;
        this.width = width;
        this.channel = channel;
        threshold_img = null;
    }

    public int[] calIntergeralImg() {
        ImageIntegeral = new int[batch_size * height * width * channel];

        int b_idx = height * width * channel;
        int h_idx = width * channel;
        int val_idx = 0;
        int val = 0;
        for (int i = 0; i < batch_size; i++) {
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {

                    for (int c = 0; c < channel; c++) {
                        val_idx = i * b_idx + h * h_idx + w * channel + c;
                        val = data[val_idx];

                        if (w == 0) {//说明第0列
                            ImageIntegeral[val_idx] = val;
                        } else {
                            int val_prev_idx = i * b_idx + h * h_idx + (w - 1) * channel + c;
                            ImageIntegeral[val_idx] = val + ImageIntegeral[val_prev_idx];
                        }


                    }
                }
            }

            for (int w = 0; w < width; w++) {
                for (int h = 0; h < height; h++) {
                    for (int c = 0; c < channel; c++) {
                        val_idx = i * b_idx + h * h_idx + w * channel + c;
                        val = ImageIntegeral[val_idx];

                        if (h == 0) {//说明第0列
                            ImageIntegeral[val_idx] = val;
                        } else {
                            int val_prev_idx = i * b_idx + (h - 1) * h_idx + w * channel + c;
                            ImageIntegeral[val_idx] = val + ImageIntegeral[val_prev_idx];
                        }

                    }
                }
            }
        }

        return ImageIntegeral;
    }

    public byte[] threshold(double ratio, int k_h, int k_w) {
        threshold_img = new byte[batch_size * height * width * channel];
        int min_x, min_y, max_x, max_y, top_left, bottom_left, top_right, bottom_right, sum, idx;
        byte max_value = Byte.valueOf("-128");
        byte min_value = Byte.valueOf("127");
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                min_x = Math.max(w - k_w, 0);
                min_y = Math.max(h - k_h, 0);
                max_x = Math.min(w + k_w, width - 1);
                max_y = Math.min(h + k_h, height - 1);

                int area = (max_x - min_x + 1) * (max_y - min_y + 1);
                for (int b = 0; b < batch_size; b++) {
                    for (int c = 0; c < channel; c++) {

                        top_left = 0;
                        if (min_x - 1 >= 0 && min_y - 1 >= 0) {
                            top_left = b * height * width * channel + (min_y - 1) * width * channel + (min_x - 1) * channel + c;
                            top_left = ImageIntegeral[top_left];
                        }
                        bottom_left = 0;
                        if (min_x - 1 >= 0) {
                            bottom_left = b * height * width * channel + max_y * width * channel + (min_x - 1) * channel + c;
                            bottom_left = ImageIntegeral[bottom_left];
                        }
                        top_right = 0;
                        if (min_y - 1 >= 0) {
                            top_right = b * height * width * channel + (min_y - 1) * width * channel + max_x * channel + c;
                            top_right = ImageIntegeral[top_right];
                        }
                        bottom_right = b * height * width * channel + max_y * width * channel + max_x * channel + c;

                        sum = ImageIntegeral[bottom_right] + top_left
                                - top_right - bottom_left;

                        idx = b * height * width * channel + h * width * channel + w * channel + c;
                        threshold_img[idx] = data[idx] > ratio * Math.floor(sum * 1.0 / area) ? max_value : min_value;
                    }
                }
            }
        }
        ImageIntegeral = null;
        return threshold_img;


    }

    public byte[] getThreshold_img() {
        return threshold_img;
    }

    public static void main(String[] args) {


        byte[] data = new byte[18];
        for (int i = 0; i < 18; i++) {
            data[i] = Byte.valueOf(i + "");
        }
        int batch_size = 2;
        int height = 3;
        int width = 3;
        int channel = 1;

        RgbByteArrayAvgThreshold rgbByteArrayAvgThreshold =
                new RgbByteArrayAvgThreshold(data, batch_size, height, width, channel);

        rgbByteArrayAvgThreshold.calIntergeralImg();
        byte[] out = rgbByteArrayAvgThreshold.threshold(0.8, 64, 64);

        for (int b = 0; b < batch_size; b++) {
            System.out.print("{");
            for (int h = 0; h < height; h++) {
                System.out.println();
                for (int w = 0; w < width; w++) {
                    System.out.print("[");
                    for (int c = 0; c < channel; c++) {
                        int b_idx = b * height * width * channel;
                        int h_idx = h * width * channel;
                        int w_idx = w * channel;

                        System.out.print(out[b_idx + h_idx + w_idx + c] + ",");
                    }
                    System.out.print("]");
                }

            }
            System.out.println("}");
        }


    }


}

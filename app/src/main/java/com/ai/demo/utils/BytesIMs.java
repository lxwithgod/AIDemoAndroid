package com.ai.demo.utils;

import java.util.ArrayList;
import java.util.List;

public class BytesIMs {
    private byte[] bytes;
    private int height = 0;
    private int width = 0;
    private int channel = 0;
    private int size = 0;
    private String type = null;
    private int batch_size = 0;


    public void append(BytesIM bytesIM) {

        if (height == 0) {
            height = bytesIM.getHeight();
        }
        if (width == 0) {
            width = bytesIM.getWidth();
        }
        if (channel == 0) {
            channel = bytesIM.getChannel();
        }
        if (type == null) {
            type = bytesIM.getType();
        }

        if ((this.height == bytesIM.getHeight())
                && this.width == bytesIM.getWidth()
                && this.type.equalsIgnoreCase(bytesIM.getType())
                ) {
            this.batch_size++;

            byte[] new_bytes = new byte[batch_size * size];
            System.arraycopy(bytes, 0, new_bytes, 0, bytes.length);
            System.arraycopy(bytesIM.getBytes(), 0, new_bytes, bytes.length, bytesIM.getSize());
            this.bytes = new_bytes;
        }
    }

    public void extend(List<BytesIM> bytesIM_arr) {
        ArrayList<BytesIM> bytesIM_arr_new = new ArrayList<>();

        for (BytesIM bytesIM : bytesIM_arr) {

            if (height == 0) {
                height = bytesIM.getHeight();
            }
            if (width == 0) {
                width = bytesIM.getWidth();
            }
            if (channel == 0) {
                channel = bytesIM.getChannel();
            }

            if (size == 0) {
                size = height * width * channel;
            }

            if (type == null) {
                type = bytesIM.getType();
            }

            if (height == bytesIM.getHeight()
                    && width == bytesIM.getWidth()
                    && type.equalsIgnoreCase(bytesIM.getType())
                    ) {
                bytesIM_arr_new.add(bytesIM);
            }
        }

        byte[] new_bytes = new byte[size * bytesIM_arr_new.size()];
        for (int i = 0; i < bytesIM_arr_new.size(); i++) {
            BytesIM tmp = bytesIM_arr_new.get(i);
            System.arraycopy(tmp.getBytes(), 0, new_bytes, i * size, tmp.getSize());
        }
        this.bytes = new_bytes;
        this.batch_size = bytesIM_arr_new.size();
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getChannel() {
        return channel;
    }

    public int getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public int getBatch_size() {
        return batch_size;
    }
}

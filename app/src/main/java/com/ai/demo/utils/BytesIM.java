package com.ai.demo.utils;

public class BytesIM {
    byte[] bytes;
    int height;
    int width;
    int channel;
    int size;
    String type;

    public int getSize() {
        return size;
    }

    BytesIM(byte[] bytes, int height, int width, int channel, String type) {
        this.bytes = bytes;
        this.height = height;
        this.width = width;
        this.channel = channel;
        this.type = type;

        this.size = height * width;
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

    public String getType() {
        return type;
    }

    public void recycle() {
        bytes = null;
    }
}


package com.ai.demo.utils;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.support.v8.renderscript.Type;




public class BitMapRenderScripts {


    public static Bitmap blurBitmap(Bitmap bitmap, float radius, RenderScript rs) {


        //Create allocation from Bitmap
        Allocation allocation = Allocation.createFromBitmap(rs, bitmap);

        Type t = allocation.getType();

        //Create allocation with the same type
        Allocation blurredAllocation = Allocation.createTyped(rs, t);

        //Create script
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        //Set blur radius (maximum 25.0)
        blurScript.setRadius(radius);
        //Set input for script
        blurScript.setInput(allocation);
        //Call script for output allocation
        blurScript.forEach(blurredAllocation);

        //Copy script result into bitmap
        blurredAllocation.copyTo(bitmap);

        //Destroy everything to free memory
        allocation.destroy();
        blurredAllocation.destroy();
        blurScript.destroy();
        t.destroy();

        return bitmap;
    }

    public static Bitmap histogramEqualization(Bitmap image, RenderScript rs) {
        //Get image size
        int width = image.getWidth();
        int height = image.getHeight();

        //Create new bitmap
        Bitmap res = image.copy(image.getConfig(), true);

        //Create allocation from Bitmap
        Allocation allocationA = Allocation.createFromBitmap(rs, res);

        //Create allocation with same type
        Allocation allocationB = Allocation.createTyped(rs, allocationA.getType());

        //Create script from rs file.
        ScriptC_histEq histEqScript = new ScriptC_histEq(rs);

        //Set size in script
        histEqScript.set_size(width * height);

        //Call the first kernel.
        histEqScript.forEach_root(allocationA, allocationB);

        //Call the rs method to compute the remap array
        histEqScript.invoke_createRemapArray();

        //Call the second kernel
        histEqScript.forEach_remaptoRGB(allocationB, allocationA);

        //Copy script result into bitmap
        allocationA.copyTo(res);

        //Destroy everything to free memory
        allocationA.destroy();
        allocationB.destroy();
        histEqScript.destroy();

        return res;
    }

    public static BytesIM bytesIMPadding(BytesIM bytesIM, int padding_height,
                                         int padding_width, RenderScript rs) {

        ScriptC_padding scriptCpadding = new ScriptC_padding(rs);

        scriptCpadding.set_height(bytesIM.getHeight());
        scriptCpadding.set_width(bytesIM.getWidth());

        scriptCpadding.set_padding_heigth(padding_height);
        scriptCpadding.set_padding_width(padding_width);

        Allocation allocation_out = null;
        Allocation allocation_in = null;
        byte[] bytes = null;
        if (bytesIM.getChannel() == 1) {
            allocation_out = Allocation.createSized(rs, Element.U8(rs), padding_height * padding_width);
            allocation_in = Allocation.createSized(rs, Element.U8(rs), bytesIM.getSize());
            allocation_in.copy1DRangeFrom(0, bytesIM.getSize(), bytesIM.getBytes());
            scriptCpadding.set_in(allocation_in);

            scriptCpadding.forEach_padding_channel_1(allocation_out);

            bytes = new byte[padding_height * padding_width];
        } else if (bytesIM.getChannel() == 3) {
            allocation_out = Allocation.createSized(rs, Element.U8_3(rs), padding_height * padding_width);
            allocation_in = Allocation.createSized(rs, Element.U8_3(rs), bytesIM.getSize() / 3);
            allocation_in.copy1DRangeFrom(0, bytesIM.getSize(), bytesIM.getBytes());
            scriptCpadding.set_in(allocation_in);
            scriptCpadding.forEach_padding_channel_3(allocation_out);

            bytes = new byte[padding_height * padding_width * 3];
        }

        if (allocation_in != null) {
            allocation_out.copyTo(bytes);
            allocation_in.destroy();
            allocation_out.destroy();

            return new BytesIM(bytes, padding_height, padding_width, bytesIM.getChannel(), bytesIM.getType());
        } else {
            return null;
        }
    }

    public static BytesIM bytes2ThresholdOTUS(BytesIM bytesIM, RenderScript rs) {

        ScriptC_bytesOTSU scriptCBytesOTSU = new ScriptC_bytesOTSU(rs);
        scriptCBytesOTSU.set_height(bytesIM.getHeight());
        scriptCBytesOTSU.set_width(bytesIM.getWidth());
        scriptCBytesOTSU.invoke_init_histo();

        Allocation allocation_in = Allocation.createSized(rs, Element.U8(rs), bytesIM.getSize());
        allocation_in.copy1DRangeFrom(0, bytesIM.getSize(), bytesIM.getBytes());
        Allocation allocation_out = Allocation.createSized(rs, Element.U8(rs), bytesIM.getSize());

        scriptCBytesOTSU.forEach_calHisto(allocation_in);

        scriptCBytesOTSU.invoke_cal_threshold();

        scriptCBytesOTSU.forEach_threshold(allocation_in, allocation_out);

        byte[] bytes = new byte[bytesIM.getSize()];

        allocation_out.copyTo(bytes);


        return new BytesIM(bytes, bytesIM.getHeight(), bytesIM.getWidth(), 1, "gray");


    }

    public static Bitmap bytes2BitMap(BytesIM bytesIM, RenderScript rs) {

        int width = bytesIM.getWidth();
        int height = bytesIM.getHeight();
        String type = bytesIM.getType();


        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Allocation bitmap_allocation = Allocation.createFromBitmap(rs, bitmap);

        ScriptC_bytesOp scriptCBytesOp = new ScriptC_bytesOp(rs);
        scriptCBytesOp.set_height(height);
        scriptCBytesOp.set_width(width);

        Allocation in;
        if (type.equalsIgnoreCase("gray")) {
            in = Allocation.createSized(rs, Element.U8(rs), height * width);
            in.copy1DRangeFrom(0, height * width, bytesIM.getBytes());
            scriptCBytesOp.set_out(in);
            scriptCBytesOp.forEach_GrayBytes2BitMap(bitmap_allocation);

        } else if (type.equalsIgnoreCase("bgr")) {
            in = Allocation.createSized(rs, Element.U8(rs), height * width * 3);
            in.copy1DRangeFrom(0, height * width * 3, bytesIM.getBytes());
            scriptCBytesOp.set_out(in);

            scriptCBytesOp.forEach_BGRBytes2BitMap(bitmap_allocation);
        } else if (type.equalsIgnoreCase("rgb")) {
            in = Allocation.createSized(rs, Element.U8(rs), height * width * 3);
            in.copy1DRangeFrom(0, height * width * 3, bytesIM.getBytes());
            scriptCBytesOp.set_out(in);

            scriptCBytesOp.forEach_RGBBytes2BitMap(bitmap_allocation);
        } else {
            return null;
        }

        bitmap_allocation.copyTo(bitmap);
        bitmap_allocation.destroy();
        in.destroy();

        return bitmap;

    }

    public static BytesIM bitMap2Bytes(Bitmap image, String type, RenderScript rs) {
        /*
        image:must be argb_8888
        type:must be rgb/bgr/gray
         */


        int count = image.getWidth() * image.getHeight();
        ScriptC_bitMapOp scriptCBitMapOp = new ScriptC_bitMapOp(rs);
        scriptCBitMapOp.set_height(image.getHeight());
        scriptCBitMapOp.set_width(image.getWidth());

        Allocation allocation_out;
        Allocation allocation_bitmap = Allocation.createFromBitmap(rs, image);
        if (type.equalsIgnoreCase("rgb")) {
            allocation_out = Allocation.createSized(rs, Element.U8(rs), count * 3);
            scriptCBitMapOp.set_out(allocation_out);
            scriptCBitMapOp.forEach_BitMap2RGB(allocation_bitmap);


        } else if (type.equalsIgnoreCase("bgr")) {
            allocation_out = Allocation.createSized(rs, Element.U8(rs), count * 3);
            scriptCBitMapOp.set_out(allocation_out);
            scriptCBitMapOp.forEach_BitMap2BGR(allocation_bitmap);


        } else if (type.equalsIgnoreCase("gray")) {
            allocation_out = Allocation.createSized(rs, Element.U8(rs), count);
            scriptCBitMapOp.set_out(allocation_out);
            scriptCBitMapOp.forEach_BitMap2GRAY(allocation_bitmap);

        } else {
            return null;
        }


        byte[] res = type.equalsIgnoreCase("gray") ? new byte[count] : new byte[count * 3];
        int channel = type.equalsIgnoreCase("gray") ? 1 : 3;

        allocation_out.copyTo(res);

        allocation_bitmap.destroy();
        allocation_out.destroy();

        return new BytesIM(res, image.getHeight(), image.getWidth(), channel, type);

    }

    public static BytesIM threshold(Bitmap image, float ratio, int k_h, int k_w, RenderScript rs) {
        int width = image.getWidth();
        int height = image.getHeight();
        int count = width * height;

        ScriptC_avgPool scriptCAvgPool = new ScriptC_avgPool(rs);
        scriptCAvgPool.set_height(height);
        scriptCAvgPool.set_width(width);
        scriptCAvgPool.set_k_h(k_h);
        scriptCAvgPool.set_k_w(k_w);
        scriptCAvgPool.set_ratio(ratio);

        Allocation rawImg_allocation = Allocation.createFromBitmap(rs, image);
        scriptCAvgPool.set_in(rawImg_allocation);

        Allocation minMaxMean_allocation = Allocation.createSized(rs, Element.F32_4(rs), height * 3);
        scriptCAvgPool.set_min_max_mean(minMaxMean_allocation);

        Allocation raw2float4_allocation = Allocation.createSized(rs, Element.F32_4(rs), count);
        scriptCAvgPool.set_in_float4(raw2float4_allocation);
        Allocation out_integral_allocation = Allocation.createSized(rs, Element.F32_4(rs), count);

        scriptCAvgPool.set_in_integral(out_integral_allocation);

        Allocation avg_out_allocation = Allocation.createSized(rs, Element.F32_4(rs), count);
        scriptCAvgPool.set_avg_out(avg_out_allocation);

        scriptCAvgPool.forEach_inCastFloat4(rawImg_allocation);

        Allocation idx_allocation_integeralByRow = Allocation.createSized(rs, Element.F32(rs), height);
        scriptCAvgPool.forEach_calIntegeralByRow(idx_allocation_integeralByRow);

        Allocation idx_allocation_integeralByCol = Allocation.createSized(rs, Element.F32(rs), width);
        scriptCAvgPool.forEach_calIntegeralByCol(idx_allocation_integeralByCol);

        Allocation idx_allocation_avg_pool = Allocation.createSized(rs, Element.F32_4(rs), count);
        scriptCAvgPool.forEach_avg_pool(idx_allocation_avg_pool);


        Allocation res_allocation = Allocation.createSized(rs, Element.U8(rs), count);
        scriptCAvgPool.forEach_threshold(res_allocation);

        byte[] res = new byte[count];

        res_allocation.copyTo(res);

        idx_allocation_avg_pool.destroy();
        res_allocation.destroy();
        idx_allocation_integeralByCol.destroy();
        idx_allocation_integeralByRow.destroy();

        out_integral_allocation.destroy();
        raw2float4_allocation.destroy();
        rawImg_allocation.destroy();
        avg_out_allocation.destroy();
        minMaxMean_allocation.destroy();

        return new BytesIM(res, image.getHeight(), image.getWidth(), 1, "gray");
    }

    public static Bitmap avg_pool(Bitmap image, float ratio, int k_h, int k_w, RenderScript rs) {

        int width = image.getWidth();
        int height = image.getHeight();

        ScriptC_avgPool scriptCAvgPool = new ScriptC_avgPool(rs);
        scriptCAvgPool.set_height(height);
        scriptCAvgPool.set_width(width);
        scriptCAvgPool.set_k_h(k_h);
        scriptCAvgPool.set_k_w(k_w);
        scriptCAvgPool.set_ratio(ratio);

        Allocation rawImg_allocation = Allocation.createFromBitmap(rs, image);
        scriptCAvgPool.set_in(rawImg_allocation);

        Allocation minMaxMean_allocation = Allocation.createSized(rs, Element.F32_4(rs), height * 3);
        scriptCAvgPool.set_min_max_mean(minMaxMean_allocation);

        Allocation raw2float4_allocation = Allocation.createSized(rs, Element.F32_4(rs), width * height);
        scriptCAvgPool.set_in_float4(raw2float4_allocation);
        Allocation out_integral_allocation = Allocation.createSized(rs, Element.F32_4(rs), width * height);

        scriptCAvgPool.set_in_integral(out_integral_allocation);

        Allocation sub_in_avg_allocation = Allocation.createSized(rs, Element.F32_4(rs), width * height);
        scriptCAvgPool.set_sub_in_avg(sub_in_avg_allocation);

        Allocation avg_out_allocation = Allocation.createSized(rs, Element.F32_4(rs), width * height);
        scriptCAvgPool.set_avg_out(avg_out_allocation);

        Allocation out_float4_allocation = Allocation.createSized(rs, Element.F32_4(rs), width * height);
        scriptCAvgPool.set_out_float4(out_float4_allocation);

        scriptCAvgPool.forEach_inCastFloat4(rawImg_allocation);

        Allocation idx_allocation_integeralByRow = Allocation.createSized(rs, Element.F32(rs), height);
        scriptCAvgPool.forEach_calIntegeralByRow(idx_allocation_integeralByRow);

        Allocation idx_allocation_integeralByCol = Allocation.createSized(rs, Element.F32(rs), width);
        scriptCAvgPool.forEach_calIntegeralByCol(idx_allocation_integeralByCol);

        Allocation idx_allocation_avg_pool = Allocation.createSized(rs, Element.F32_4(rs), height * width);
        scriptCAvgPool.forEach_avg_pool(idx_allocation_avg_pool);

        Allocation idx_allocation_sub_in_float4_avg_pool = Allocation.createSized(rs, Element.F32_4(rs), height * width);
        scriptCAvgPool.forEach_sub_in_float4_avg_pool(idx_allocation_sub_in_float4_avg_pool);

        Allocation idx_allocation_minMaxMeanByRow = Allocation.createSized(rs, Element.F32(rs), height);
        scriptCAvgPool.forEach_findMaxMinAvgByRow(idx_allocation_minMaxMeanByRow);

        scriptCAvgPool.invoke_findMaxMinAvg();

        Allocation idx_backTo01 = Allocation.createSized(rs, Element.F32_4(rs), width * height);
        scriptCAvgPool.forEach_backTo01(idx_backTo01);

        Bitmap res = image.copy(image.getConfig(), true);
        Allocation res_allocation = Allocation.createFromBitmap(rs, res);
        scriptCAvgPool.forEach_out_float4_to_bitmap(res_allocation);

        res_allocation.copyTo(res);

        idx_allocation_avg_pool.destroy();
        idx_allocation_sub_in_float4_avg_pool.destroy();
        idx_backTo01.destroy();
        res_allocation.destroy();
        idx_allocation_integeralByCol.destroy();
        idx_allocation_integeralByRow.destroy();
        out_float4_allocation.destroy();
        out_integral_allocation.destroy();
        raw2float4_allocation.destroy();
        rawImg_allocation.destroy();
        avg_out_allocation.destroy();
        sub_in_avg_allocation.destroy();
        idx_allocation_minMaxMeanByRow.destroy();


        return res;

    }


}

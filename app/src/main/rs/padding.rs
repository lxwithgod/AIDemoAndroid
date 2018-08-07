#pragma version(1)
#pragma rs java_package_name(com.ai.demo.utils)
//#pragma rs_fp_relaxed
#pragma rs_fp_imprecise

//#pragma rs reduce(findMinMeanSum)
    //initializer(fMinMaxSumInit) \
  //  accumulator(fMMAccumulator)\
    //combiner(fMMCombiner) \
   // outcoverter(fMMOutConverter)

#include "rs_debug.rsh"
rs_allocation in;
int32_t height;
int32_t width;

int32_t padding_heigth;
int32_t padding_width;


uchar __attribute__ ((kernel)) padding_channel_1(int32_t x){

    int32_t row_id=x/padding_width;
    int32_t col_id=x%padding_width;

    if(col_id<width && row_id<height){
        return rsGetElementAt_uchar(in,row_id*width+col_id);
    }else{
        return 255;
    }
}

uchar3 __attribute__ ((kernel)) padding_channel_3(int32_t x){

    int32_t row_id=x/padding_width;
    int32_t col_id=x%padding_width;

    if(col_id<width && row_id<height){
            return rsGetElementAt_uchar3(in,row_id*width+col_id);
    }else{
            uchar3 res={255,255,255};
            return res;
    }
}
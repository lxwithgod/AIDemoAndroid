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
rs_allocation in_float4;

rs_allocation in_integral;
rs_allocation avg_out;
rs_allocation sub_in_avg;

rs_allocation out_float4;

rs_allocation min_max_mean;

float4 maxVal={-99999999.0,-99999999.0,-99999999.0};
float4 minVal={99999999.0,99999999.0,99999999.0};
float4 meanVal={0,0,0,0};




int32_t width;
int32_t height;

int32_t k_w;
int32_t k_h;

float ratio;

void __attribute__ ((kernel)) inCastFloat4(uchar4 in,int32_t x,int32_t y){

    float4 val = rsUnpackColor8888(in);

    float gray=0.30*val.r+0.59*val.g+0.11*val.b;

    val.r=gray;
    val.g=gray;
    val.b=gray;
    val.a=255;

    rsSetElementAt_float4(in_float4,val,y*width+x);
}

float __attribute__ ((kernel)) calIntegeralByRow(int32_t x){

    float4 val;

    for(int32_t i=0;i<width;i++){

        int32_t idx=x*width+i;
        if(i==0){
            val=rsGetElementAt_float4(in_float4,idx);

        }else{
            val=rsGetElementAt_float4(in_integral,idx-1)+rsGetElementAt_float4(in_float4,idx);
        }
       // rsDebug("val", val);

        rsSetElementAt_float4(in_integral,val,idx);

    }
}

float __attribute__ ((kernel)) calIntegeralByCol(int32_t x){

    float4 val;
    for(int32_t y=0;y<height;y++){
        int32_t idx=y*width+x;
        if(y==0){
            val=rsGetElementAt_float4(in_integral,idx);

        }else{
            val=rsGetElementAt_float4(in_integral,idx-width)+rsGetElementAt_float4(in_integral,idx);
        }
        rsSetElementAt_float4(in_integral,val,idx);

    }
}


static float bound (float val) {
    float m = fmax(0.0f, val);
    return fmin(1.0f, m);
}


float4 __attribute ((kernel)) avg_pool(int32_t x){

        float4 zero_float4={0,0,0,0};
        int32_t h=x/width;
        int32_t w=x%width;

        int32_t min_x = max(w - k_w, 0);
        int32_t min_y = max(h - k_h, 0);
        int32_t max_x = min(w + k_w, width - 1);
        int32_t max_y = min(h + k_h, height - 1);

        int32_t area=(max_y-min_y+1)*(max_x-min_x+1);

        float4 right_bottom = rsGetElementAt_float4(in_integral,max_y*width+max_x);

        float4 left_top = min_x-1>=0 && min_y-1>=0 ? rsGetElementAt_float4(in_integral,(min_y-1)*width+min_x-1): zero_float4;
        float4 left_bottom = min_x-1>=0 ? rsGetElementAt_float4(in_integral,max_y*width+min_x-1): zero_float4;
        float4 right_top = min_y-1>=0 ? rsGetElementAt_float4(in_integral,(min_y-1)*width+max_x): zero_float4;

        float4 avgVal=(right_bottom+left_top-left_bottom-right_top)/area;
        avgVal.a=1.0;

        rsSetElementAt_float4(avg_out,avgVal,x);

        return avgVal;
}

uchar4 __attribute ((kernel)) threshold2BitMap(int32_t x,int32_t y){
    float4 avg_val=rsGetElementAt_float4(avg_out,y*width+x);
    float4 in_val=rsGetElementAt_float4(in_float4,y*width+x);

    float r=in_val.r>ratio*avg_val.r? 1.0:0.0;
    float g=in_val.g>ratio*avg_val.g? 1.0:0.0;
    float b=in_val.b>ratio*avg_val.b? 1.0:0.0;

    uchar4 rs=rsPackColorTo8888(r,g,b,1.0);

    return rs;
}

uchar __attribute ((kernel)) threshold(int32_t x){
    float4 avg_val=rsGetElementAt_float4(avg_out, x);
    float4 in_val=rsGetElementAt_float4(in_float4, x);

    float r=in_val.r>ratio*avg_val.r? 1.0:0.0;
    float g=in_val.g>ratio*avg_val.g? 1.0:0.0;
    float b=in_val.b>ratio*avg_val.b? 1.0:0.0;

    uchar rs=r+g+b>0.0 ? 255:0;

    return rs;
}



float4 __attribute ((kernel)) sub_in_float4_avg_pool(int32_t x){

    float4 val=rsGetElementAt_float4(in_float4,x)-rsGetElementAt_float4(avg_out,x);

    rsSetElementAt_float4(sub_in_avg,val,x);

    return val;
}
float __attribute ((kernel)) findMaxMinAvgByRow(int32_t x){

     float4 maxVal={-999999.0,-999999.0,-999999.0,-999999.0};
     float4 minVal={999999.0,999999.0,999999.0,999999.0};
     float4 meanVal={0.0,0.0,0.0,0.0};

     for (int32_t i=0; i < width; i++){
        float4 val=rsGetElementAt_float4(sub_in_avg,x*width+i);
        maxVal=fmax(val,maxVal);
        minVal=fmin(val,minVal);
        meanVal+=val;
     }
     meanVal=meanVal/width;

     rsSetElementAt_float4(min_max_mean,minVal,x*3);
     rsSetElementAt_float4(min_max_mean,maxVal,x*3+1);
     rsSetElementAt_float4(min_max_mean,meanVal,x*3+2);

}

void findMaxMinAvg(){

    for(int i=0;i<height;i++){
        float4 minVal_row=rsGetElementAt_float4(min_max_mean,i*3);
        float4 maxVal_row=rsGetElementAt_float4(min_max_mean,i*3+1);
        float4 meanVal_row=rsGetElementAt_float4(min_max_mean,i*3+2);

        maxVal=fmax(maxVal_row,maxVal);
        minVal=fmin(minVal_row,minVal);
        meanVal+=meanVal_row;
    }
    meanVal=meanVal/height;

}

float4 __attribute ((kernel)) backTo01(int32_t x){
    //float4 dot5={0.5,0.5,.0.5,0.5};
    float4 val=rsGetElementAt_float4(sub_in_avg,x);

    float4 rs=(val-meanVal)/(maxVal-minVal)+0.5;

    rsSetElementAt_float4(out_float4,rs,x);

    return val;
}


uchar4 __attribute ((kernel)) out_float4_to_bitmap(int32_t x,int32_t y){

    float4 val=rsGetElementAt_float4(out_float4,y*width+x);
    float r=bound(val.r);
    float g=bound(val.g);
    float b=bound(val.b);

    uchar4 rs=rsPackColorTo8888(r,g,b,1.0);

    return rs;
}

uchar4 __attribute ((kernel)) avg_pool2bitMap(int32_t x,int32_t y){

        float4 avgVal=rsGetElementAt_float4(avg_out,y*width+x);
        float r=bound(avgVal.r);
        float g=bound(avgVal.g);
        float b=bound(avgVal.b);
        uchar4 val=rsPackColorTo8888(r,g,b,1.0);
        return val;
}




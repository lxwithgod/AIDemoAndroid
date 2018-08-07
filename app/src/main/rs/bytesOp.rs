#pragma version(1)
#pragma rs java_package_name(com.ai.demo.utils)
//#pragma rs_fp_relaxed
#pragma rs_fp_imprecise

#include "rs_debug.rsh"

rs_allocation out;
int32_t width;
int32_t height;


uchar4 __attribute__ ((kernel)) RGBBytes2BitMap(int x,int y){

     uchar r=rsGetElementAt_uchar(out,y*width*3+3*x);
     uchar g=rsGetElementAt_uchar(out,y*width*3+3*x+1);
     uchar b=rsGetElementAt_uchar(out,y*width*3+3*x+2);
     uchar4 rs;
     rs.r=r;
     rs.g=g;
     rs.b=b;
     rs.a=255;

     return rs;
}


uchar4 __attribute__ ((kernel)) BGRBytes2BitMap(int x,int y){

    uchar b=rsGetElementAt_uchar(out,y*width*3+3*x);
    uchar g=rsGetElementAt_uchar(out,y*width*3+3*x+1);
    uchar r=rsGetElementAt_uchar(out,y*width*3+3*x+2);
    uchar4 rs;
    rs.r=r;
    rs.g=g;
    rs.b=b;
    rs.a=255;

    return rs;
}


uchar4 __attribute__ ((kernel)) GrayBytes2BitMap(int x,int y){

    uchar val=rsGetElementAt_uchar(out,y*width+x);
    uchar4 rs;
    rs.r=val;
    rs.g=val;
    rs.b=val;
    rs.a=255;

    return rs;
}

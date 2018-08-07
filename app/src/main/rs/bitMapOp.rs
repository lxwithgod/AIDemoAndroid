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


int32_t height;
int32_t width;


rs_allocation out;

void __attribute__ ((kernel)) BitMap2RGB(uchar4 in,int x,int y){

    rsSetElementAt_uchar(out,in.b,y*width*3+x*3);
    rsSetElementAt_uchar(out,in.g,y*width*3+x*3+1);
    rsSetElementAt_uchar(out,in.r,y*width*3+x*3+2);

}

void __attribute__ ((kernel)) BitMap2BGR(uchar4 in,int x,int y){
     rsSetElementAt_uchar(out,in.r,y*width*3+x*3);
     rsSetElementAt_uchar(out,in.g,y*width*3+x*3+1);
     rsSetElementAt_uchar(out,in.b,y*width*3+x*3+2);
}


void __attribute__ ((kernel)) BitMap2GRAY(uchar4 in,int x,int y){

    uchar gray=0.30*in.r+0.59*in.g+0.11*in.b;

    rsSetElementAt_uchar(out,gray,y*width+x);

}
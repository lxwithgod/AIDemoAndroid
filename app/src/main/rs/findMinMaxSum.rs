#pragma version(1)
#pragma rs java_package_name(com.ai.demo.utils)
//#pragma rs_fp_relaxed
//#pragma rs reduce(findMinMaxSum) initializer(fMMInit) accumulator(fMMAccumulator) combiner(fMMCombiner) outconverter(fMMOutConverter)
#pragma rs_fp_imprecise


typedef struct{
    float4 min_val,max_val,sum_val;
}MinMaxSum;

static void fMMInit(MinMaxSum *accum){
   float4 min_val={9999999.0,9999999.0,9999999.0,9999999.0};
   float4 sum_val={0,0,0,0};
   float4 max_val={-9999999.0,-9999999.0,-9999999.0,-9999999.0};

   accum->min_val=min_val;
   accum->sum_val=sum_val;
   accum->max_val=max_val;
}

static void fMMAccumulator(MinMaxSum *accum,float4 in,int x){

    accum->max_val=fmax(accum->max_val,in);
    accum->sum_val=accum->sum_val+in;
    accum->min_val=fmin(accum->min_val,in);

}
static void fMMCombiner(MinMaxSum *accum,const MinMaxSum *val){
    accum->min_val=fmin(accum->min_val,val->min_val);
    accum->max_val=fmax(accum->max_val,val->max_val);
    accum->sum_val = accum->sum_val+val->sum_val;
}

static void fMMOutConverter(MinMaxSum *result,const MinMaxSum *val){
    result->sum_val = val->sum_val;
    result->min_val = val->min_val;
    result->max_val = val->max_val;
}

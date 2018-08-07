#pragma version(1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.ai.demo.utils)

//#include "rs_debug.rsh"

int32_t histo[256];


int32_t width;
int32_t height;


uchar threshold_val=0;

float back_ratio=0.0f;

void init_histo() {
    for(int32_t i=0;i<256;i++){
        histo[i]=0;
    }
}

void __attribute__((kernel)) calHisto(uchar in) {

    rsAtomicInc(&histo[in]);

}

void cal_threshold(){

            float size = height * width*1.0;
            float u =0;
            for(int i = 0; i < 256; i++){
                u += i * histo[i];
            }
            u = u / size;

            float maxVariance = 0;
            float w0 = 0, avgValue  = 0;
            for(int i = 0; i < 256; i++){
                w0 += histo[i];//size;
                avgValue  += i * histo[i];//size;
                //rsDebug("w0",w0);


                float t = avgValue/w0 - u;
                float variance = t * t * w0 /(size - w0);

                if(variance > maxVariance){
                    maxVariance = variance;
                    threshold_val = i;
                }
            }

            for(int i=0;i<=threshold_val;i++){
                back_ratio+=histo[i];
            }
            back_ratio = back_ratio/size;
          //rsDebug("threshold_val",threshold_val);
}

uchar __attribute__((kernel)) threshold(uchar in, int32_t x) {


     uchar rs = in > threshold_val ? 255 : 0;
     if(back_ratio>0.5){
        rs=255-rs;
     }

     return rs;
}
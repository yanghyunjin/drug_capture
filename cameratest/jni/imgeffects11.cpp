#include "imgeffects.h"
extern "C" {

    JNIEXPORT jobject JNICALL Java_com_example_cameratest_Preview_getFloodFilledBitmap (JNIEnv * env, jobject obj, jint width, jint height,jbyteArray NV21FrameData, jint targetColor, jint replacementColor ){
    AndroidBitmapInfo info;
    void* pixels;
    int ret;
    
    
	 	 
    //AndroidBitmap_unlockPixels(env, bitmap);
    
    return 0;
}
}
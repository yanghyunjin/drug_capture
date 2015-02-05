#include <jni.h>
#include <android/log.h>
#include <cv.h>
#include <highgui.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc_c.h>

using namespace std;
using namespace cv;

Mat * mCanny = NULL;

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "JNILOG", __VA_ARGS__)

extern "C" {

    JNIEXPORT jobject JNICALL Java_com_example_cameratest_Preview_getFloodFilledBitmap (JNIEnv * env, jobject obj, jint width, jint height,jintArray NV21FrameData, jintArray outPixels){
    
	 	 
        jint * pNV21FrameData = env->GetIntArrayElements(NV21FrameData, 0);
        jint * poutPixels = env->GetIntArrayElements(outPixels, 0);
        
        if (mCanny == NULL) {
            mCanny = new Mat(height, width, CV_8UC1);
        }
        
        Mat mGray(height, width, CV_8UC4, (unsigned char *) pNV21FrameData);
        Mat mResult(height, width, CV_8UC4, (unsigned char *) poutPixels);
        IplImage srcImg = mGray;
        IplImage CannyImg = *mCanny;
        IplImage ResultImg = mResult;
        
        LOGD("%d %d", ResultImg.width, ResultImg.height);
        
        
        cvCanny(&srcImg, &CannyImg, 80, 100, 3);
        cvCvtColor(&CannyImg, &ResultImg, CV_GRAY2BGRA);
        
        env->ReleaseIntArrayElements(NV21FrameData, pNV21FrameData, 0);
        env->ReleaseIntArrayElements(outPixels, poutPixels, 0);
    
    return 0;
}
}

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

    JNIEXPORT jobject JNICALL Java_com_example_cameratest_Preview_getFloodFilledBitmap (JNIEnv * env, jobject obj, jint width, jint height,jintArray NV21FrameData,jintArray Drug_collection, jintArray jleft_top){
    
        double min,max;
        CvPoint left_top;
	 	 
        jint * pNV21FrameData = env->GetIntArrayElements(NV21FrameData, 0);
        jint * pDrug_collection = env->GetIntArrayElements(Drug_collection, 0);
        jint * pleft_top = env->GetIntArrayElements(jleft_top, 0);
     
        
        Mat mTemp(height, width, CV_8UC4, (unsigned char *) pNV21FrameData);
        Mat mSrc(840, 1950, CV_8UC4, (unsigned char *) pDrug_collection);
        
        IplImage srcImg = mSrc;
        IplImage temp = mTemp;
        
        IplImage *coeff = cvCreateImage(cvSize(srcImg.width - temp.width+1,srcImg.height - temp.height+1),IPL_DEPTH_32F,1);
        
        cvMatchTemplate(&srcImg,&temp,coeff,CV_TM_CCOEFF_NORMED);
        
        cvMinMaxLoc(coeff,&min,&max,NULL,&left_top);
        
        pleft_top[0]=left_top.x;
        pleft_top[1]=left_top.y;
        
        cvRectangle(&srcImg,left_top,cvPoint(left_top.x + temp.width, left_top.y + temp.height),CV_RGB(255,0,0));
        

        env->ReleaseIntArrayElements(jleft_top,pleft_top,0);
        env->ReleaseIntArrayElements(NV21FrameData, pNV21FrameData, 0);
        env->ReleaseIntArrayElements(Drug_collection, pDrug_collection, 0);
    
    return 0;
}
}

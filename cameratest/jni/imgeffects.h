#include <jni.h>
/* Header for class com_example_coloring_utils_FloodFillUtils */
#include <android/bitmap.h>
#include <android/log.h>


#define  LOG_TAG    "DEBUG"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#ifndef _Included_com_example_coloring_utils_FloodFillUtils
#define _Included_com_example_coloring_utils_FloodFillUtils
#ifdef __cplusplus
extern "C" {
#endif
    
    JNIEXPORT jobject JNICALL Java_com_example_coloring_utils_FloodFillUtils_getFloodFilledBitmap
    (JNIEnv *, jobject, jobject, jint, jint, jint, jint, jint, jint );
    
#ifdef __cplusplus
}
#endif
#endif
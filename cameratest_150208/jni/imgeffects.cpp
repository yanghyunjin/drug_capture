#include <highgui.h>
#include <cv.h>
#include <stdio.h>
//Blob Library Headers
#include <stdlib.h>
#include <stack>
#include <math.h>
#include <jni.h>
#include <android/log.h>
using namespace std;
using namespace cv;

#define MATH_MIN3(x,y,z)      ( (y) <= (z) ? ((x) <= (y) ? (x) : (y)) : ((x) <= (z) ? (x) : (z)) )
#define MATH_MAX3(x,y,z)      ( (y) >= (z) ? ((x) >= (y) ? (x) : (y)) : ((x) >= (z) ? (x) : (z)) )
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "JNILOG", __VA_ARGS__)



//이미지 받기, 식별기준 4가지 배열 받기
//클러스터 2, 3(현재 3)
//라벨링
//hu값 계산 및 판단
//모양 분석
//색상 추출

extern "C" {
    struct hsv_color {
        unsigned char  h;        // Hue: 0 ~ 255 (red:0, gree: 85, blue: 171)
        unsigned char s;        // Saturation: 0 ~ 255
        unsigned char v;        // Value: 0 ~ 255
    };
    
    hsv_color RGB2HSV(unsigned char r, unsigned char g, unsigned char b);
    void   Labeling    (const IplImage *src, IplImage *dst, int cluster_count, float *h1, float *h2, float *h3,  int color[][3], int index[2]) ;
    Mat KMEANS(Mat src, int cluster_count, int attempts, int color[][3] );
    int center_x = 120, center_y = 45;

    JNIEXPORT jobject JNICALL Java_com_example_cameratest_Preview_getFloodFilledBitmap (JNIEnv * env, jobject obj, jint width, jint height,jintArray NV21FrameData, jintArray Imageresult){
        
         
        jint * pNV21FrameData = env->GetIntArrayElements(NV21FrameData, 0);
        jint * pImageresult = env->GetIntArrayElements(Imageresult, 0);
        
        __android_log_print(ANDROID_LOG_DEBUG, "TAG", "################ Start ##################");
        
        Mat mSrc(height, width, CV_8UC4, (unsigned char *) pNV21FrameData);
        
        IplImage tempImage = mSrc;
        
        IplImage *srcImage = &tempImage;
        IplImage *rgbsrcImage = cvCreateImage(cvGetSize(srcImage), 8, 3);
        cvCvtColor(srcImage,rgbsrcImage,CV_RGBA2RGB);
        
        __android_log_print(ANDROID_LOG_DEBUG, "TAG", "################ 이미지생성됨 ##################");
        
        //roi 이미지
        cvSetImageROI(rgbsrcImage, cvRect(120, 370, 240,90));
        
        //KMEANS로 클러스터링
        Mat MatsrcImage(rgbsrcImage);
        Mat Kmeans2_Image;
        Mat Kmeans3_Image;
        int attempts = 10;
        int k2_color[2][3];
        int k3_color[3][3];
        Kmeans2_Image = KMEANS(MatsrcImage, 2, attempts, k2_color);
        Kmeans3_Image = KMEANS(MatsrcImage, 3, attempts, k3_color);
        
        __android_log_print(ANDROID_LOG_DEBUG, "TAG", "################ Kmean 수행완료##################");
        
        ///라벨링
        int index[2] = {-1, -1};
        IplImage *tmp_2;
        IplImage labeling_temp=IplImage(Kmeans2_Image);
        tmp_2 = &labeling_temp;
        
        IplImage *tmp_3;
        IplImage labeling_temp2=IplImage(Kmeans2_Image);
        tmp_3 = &labeling_temp2;
        float h1 = 0.38, h2 = 0.027, h3 = 0.002;
        
        IplImage *labeldstImage = cvCreateImage(cvGetSize(rgbsrcImage), 8, 1);
        Labeling(tmp_2, labeldstImage, 2, &h1, &h2, &h3, k2_color, index);
        Labeling(tmp_3, labeldstImage, 3, &h1, &h2, &h3, k3_color, index);
        
        

        if(index[0] == -1) {
            //추출 실패
            pImageresult[0]=0;
        }
        else if(h2 < 0.001) {
            //원
            pImageresult[0]=1;
        }
        else {
            //타원 장방형
            pImageresult[0]=2;
        }
        __android_log_print(ANDROID_LOG_DEBUG, "TAG", "################ Labeling 수행완료##################");
        //색상 추출
        int rgb[3] = {0};
        if(index[0] == 3) {
            rgb[0] = k3_color[index[1]][2];
            rgb[1] = k3_color[index[1]][1];
            rgb[2] = k3_color[index[1]][0];
        }
        else if(index[0] == 2){
            rgb[0] = k2_color[index[1]][2];
            rgb[1] = k2_color[index[1]][1];
            rgb[2] = k2_color[index[1]][0];
        }
        hsv_color hsv = RGB2HSV(rgb[0], rgb[1], rgb[2]);

        float hsv_f[3] = {0};
        hsv_f[0] = ((float)hsv.h / 255) * 360;
        hsv_f[1] = ((float)hsv.s / 255) * 100;
        hsv_f[2] = ((float)hsv.v / 255) * 100;
        
        __android_log_print(ANDROID_LOG_DEBUG, "TAG", "#### %f %f %f #######",hsv_f[0],hsv_f[1],hsv_f[2]);
        if(hsv_f[1] < (float)15 && hsv_f[2]  > (float) 50) {
            //흰색 회색 계통
            pImageresult[1]=0;
            __android_log_print(ANDROID_LOG_DEBUG, "TAG", "#### 회색 #######");
            
        }
        else if(hsv_f[1] < (float)17 && hsv_f[2] < (float) 10) {
            //검정 계통
            pImageresult[1]=1;
        }
        else if(hsv_f[0] >= (float) 150 && hsv_f[0] <= (float) 260 ) {
            //파랑 계통
            pImageresult[1]=2;
        }
        else if(hsv_f[0] >= (float) 75 && hsv_f[0] <= (float) 150 ) {
            //초록 계통
            pImageresult[1]=3;
        }
        else if(hsv_f[0] >= (float) 300 || hsv_f[0] <= (float) 30 ) {
            //빨강 계통
            pImageresult[1]=4;
        }
        else if(hsv_f[0] >= (float) 30 && hsv_f[0] <= (float) 65 ) {
            //노랑 계통
            pImageresult[1]=5;
            
        }
        
//        cvReleaseImage(&srcImage); //srcimage 메모리, 헤더 해제
//        cvReleaseImage(&rgbsrcImage); //srcimage 메모리, 헤더 해제
        
        
        env->ReleaseIntArrayElements(NV21FrameData, pNV21FrameData, 0);
        env->ReleaseIntArrayElements(Imageresult, pImageresult, 0);
        
        return 0;
    }
    
    hsv_color RGB2HSV(unsigned char r, unsigned char g, unsigned char b)
    {
        unsigned char rgb_min, rgb_max;
        rgb_min = MATH_MIN3(b, g, r);
        rgb_max = MATH_MAX3(b, g, r);
        
        hsv_color hsv;
        hsv.v = rgb_max;
        if (hsv.v == 0) {
            hsv.h = hsv.s = 0;
            return hsv;
        }
        
        hsv.s = 255*(rgb_max - rgb_min)/hsv.v;
        if (hsv.s == 0) {
            hsv.h = 0;
            return hsv;
        }
        
        if (rgb_max == r) {
            hsv.h = 0 + 43*(g - b)/(rgb_max - rgb_min);
        } else if (rgb_max == g) {
            hsv.h = 85 + 43*(b - r)/(rgb_max - rgb_min);
        } else /* rgb_max == rgb.b */ {
            hsv.h = 171 + 43*(r - g)/(rgb_max - rgb_min);
        }
        
        return hsv;
    }
    
    
    Mat KMEANS(Mat src, int cluster_count, int attempts, int color[][3] ) {
        
        
        

        
        cv::Mat samples(src.total(), 3, CV_32F);
        uchar *src_begin;
        uchar *src_end ;

        float *samples_ptr = samples.ptr<float>(0);

        for( int row = 0; row != src.rows; ++row){
            src_begin = src.ptr<uchar>(row);
            src_end = src_begin + src.cols * src.channels();

            while(src_begin != src_end){
                samples_ptr[0] = src_begin[0];
                samples_ptr[1] = src_begin[1];
                samples_ptr[2] = src_begin[2];
                samples_ptr += 3; src_begin +=3;
            }

        }


        
        cv::Mat labels;
        cv::Mat centers;
        cv::kmeans(samples, cluster_count, labels,cv::TermCriteria(CV_TERMCRIT_ITER | CV_TERMCRIT_EPS, 10, 0.01),
                   attempts, cv::KMEANS_PP_CENTERS, centers);
        
        cv::Mat new_image(src.size(), src.type());
        

        for(int i = 0; i <cluster_count; i++) {
            float *centers_ptr = centers.ptr<float>(i);
            color[i][0] = (int)centers_ptr[0];
            color[i][1] = (int)centers_ptr[1];
            color[i][2] = (int)centers_ptr[2];
        }

        for( int row = 0; row != src.rows; ++row){
            uchar *new_image_begin = new_image.ptr<uchar>(row);
            uchar *new_image_end = new_image_begin + new_image.cols * 3;
            int *labels_ptr = labels.ptr<int>(row * src.cols);
            
            while(new_image_begin != new_image_end){
                int const cluster_idx = *labels_ptr;
                
                float *centers_ptr = centers.ptr<float>(cluster_idx);
                new_image_begin[0] = (int)centers_ptr[0];
                new_image_begin[1] = (int)centers_ptr[1];
                new_image_begin[2] = (int)centers_ptr[2];
                new_image_begin += 3;
                ++labels_ptr;
            }
        }

        return new_image;
    }
    
    void   Labeling    (const IplImage *src, IplImage *dst, int cluster_count, float *h1, float *h2, float *h3,  int color[][3], int index[2]) {
        //// Only 3-Channel
        IplImage *tmp = cvCreateImage(cvGetSize(src), 8, 1);
        IplImage *resultImage = cvCreateImage(cvGetSize(src), 8, 1);
        
        CvMoments moments;
        CvHuMoments hu_moments;
        
        int binary = 1;
        
        // image size load
        int height    = src->height;
        int width    = src->width;
        int Centerx = 0, Centery = 0;
        
        // before labeling prcess, initializing
        cvSetZero(resultImage);
        
        int count = 0;
        for(int i = 0; i < cluster_count; i++) {
            cvSetZero(tmp);
            count = 0;
            for( int y = 0; y < height ; y++ ){
                for( int x = 0; x < width ; x++ ){
                    int rgb_index = (y*src->widthStep + x*(src->nChannels));
                    int gray_index = (y*tmp->widthStep + x);
                    
                    unsigned char B = src->imageData[rgb_index];
                    unsigned char G = src->imageData[rgb_index + 1];
                    unsigned char R = src->imageData[rgb_index + 2];
                    
                    if(color[i][0] == B && color[i][1] == G && color[i][2] == R)
                    {
                        tmp->imageData[gray_index] = 255;
                        count++;
                    }
                }
                
            }
            cvMoments(tmp, &moments, binary);
            cvGetHuMoments(&moments, &hu_moments);
            
            if((count > 240 * 90 / 2) ||  (count < 400))
                continue;
            
            if( (hu_moments.hu1 < 0.38 && hu_moments.hu2 < 0.027 && hu_moments.hu3 < 0.002)) {
                if(hu_moments.hu1 < *h1 && hu_moments.hu2 < *h2 ) {
                    *h1 = hu_moments.hu1 ;
                    *h2 = hu_moments.hu2 ;
                    *h3 = hu_moments.hu3 ;
                    index[0] = cluster_count;
                    index[1] = i;
                    cvCopy(tmp, dst);
                }
            }
        }
    }
}

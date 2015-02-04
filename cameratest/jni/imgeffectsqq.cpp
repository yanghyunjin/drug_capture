/*
 *  ImageProcessing.cpp
 */
#include <jni.h>
#include<android/log.h>
#include <cv.h>
#include <highgui.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc_c.h>

using namespace std;
using namespace cv;

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "JNILOG", __VA_ARGS__)

void hough_line_probabliblistic(IplImage** src, CvMemStorage** storage, CvSeq** lines, int threshold, int param1, int param2);
void sidework_line_detection(CvSeq** lines, IplImage** dst, int min, int max);
void distance_line2(IplImage** src);
void angle_sidework(CvSeq* lines, CvMemStorage** l_storage,  CvMemStorage** r_storage, CvSeq** l_lines,CvSeq** r_lines, int* angle, int *l_count, int *r_count);
void print_sidework(IplImage* src, CvSeq* l_lines, CvSeq* r_lines);

extern "C"
jboolean
Java_com_example_cameratest_Preview_getFloodFilledBitmap(
                                                       JNIEnv* env, jobject thiz,
                                                       jint width, jint height,
                                                       jbyteArray NV21FrameData, jintArray outPixels, jintArray order, jintArray r_l)
{
    jbyte * pNV21FrameData = env->GetByteArrayElements(NV21FrameData, 0);
    jint * poutPixels = env->GetIntArrayElements(outPixels, 0);
    jint *Order = env->GetIntArrayElements(order, 0);
    jint *R_L = env->GetIntArrayElements( r_l, 0);
    Mat mSrc(height, width, CV_8UC4, (unsigned char *)pNV21FrameData);
    Mat mGray(height, width, CV_8UC1, (unsigned char *)pNV21FrameData);
    Mat mResult(height, width, CV_8UC4, (unsigned char *)poutPixels);
    
    //   LOGD("mSrc %d", mSrc. channels());
    //   LOGD("mGray %d", mGray.channels());
    //   LOGD("mResult %d", mResult.channels());
    //
    //   mGray.convertTo(mGray,CV_8UC4);
    //   LOGD("mGray1 %d", mGray.channels());
    
    //������ ���� 1280x720 -> ��������
    IplImage srcImg = mGray;
    IplImage* Gray;
    IplImage* CannyImg;
    IplImage* Sidework_Line;
    IplImage* Affine_Sidework;
    IplImage* Real_Sidework;
    
    
    //IplImage* Hough;// = *mhough_canny_100_150;
    IplImage ResultImg = mResult;
    CvMat *map_matrix = cvCreateMat(3,3, CV_32FC1);
    
    
    Gray = cvCreateImage( cvGetSize(&srcImg), 8, 1);
    cvFlip(&srcImg, Gray,1);
    
    CannyImg = cvCreateImage(cvGetSize(&srcImg), IPL_DEPTH_8U, 1);
    cvCanny(Gray, CannyImg, 100, 150);
    cvSetImageROI(CannyImg, cvRect(2, 390, 1276, 330));;
    CvSeq* lines = 0;
    CvMemStorage* storage = cvCreateMemStorage(0);
    hough_line_probabliblistic(&CannyImg, &storage,  &lines, 20, 20, 3);
    
    Sidework_Line =  cvCreateImage( cvGetSize(&srcImg), IPL_DEPTH_8U, 1);
    cvSetZero(Sidework_Line);
    cvSetImageROI(Sidework_Line, cvRect(2, 390, 1276, 330));
    
    sidework_line_detection(&lines,  &Sidework_Line, 50, 70); //������
    sidework_line_detection(&lines,  &Sidework_Line, 110, 130); //�������� ����
    cvSetImageROI(Sidework_Line, cvRect(0, 0, 1280, 720));
    
    
    ///////////������ ���� ����////////////////////////////////
    CvPoint2D32f src_point[4], dst_point[4];
    //Affine_Sidework = cvCreateImage(cvSize(400 *2, 300*2), IPL_DEPTH_8U, 1);
    Affine_Sidework = cvCreateImage(cvGetSize(&srcImg), IPL_DEPTH_8U, 1);
    LOGD("%d, %d",Affine_Sidework->height, Affine_Sidework->width );
    cvSetZero(Affine_Sidework);
    src_point[0] = cvPoint2D32f(2, 720);
    src_point[1] = cvPoint2D32f(2, 390);
    src_point[2] = cvPoint2D32f(1278, 390);
    src_point[3] = cvPoint2D32f(1278, 720);
    
    dst_point[0] = cvPoint2D32f(155*2, 300*2);
    dst_point[1] = cvPoint2D32f(10*2, 0);
    dst_point[2] = cvPoint2D32f(390*2, 0);
    dst_point[3] = cvPoint2D32f(245*2, 300*2);
    map_matrix = cvCreateMat(3,3, CV_32FC1);
    cvGetPerspectiveTransform(src_point, dst_point, map_matrix);
    cvWarpPerspective(Sidework_Line, Affine_Sidework, map_matrix,
                      CV_INTER_LINEAR + CV_WARP_FILL_OUTLIERS, cvScalarAll(0));
    
    
    LOGD( "done");
    //Real_Sidework = cvCreateImage(cvGetSize(&srcImg), IPL_DEPTH_8U, 1);
    CvSeq* real_lines = 0;
    CvMemStorage* real_storage = cvCreateMemStorage(0);
    hough_line_probabliblistic(&Affine_Sidework, &real_storage, &real_lines, 10, 10, 3);
    
    
    int angle[3] ={ 0};
    Real_Sidework =  cvCreateImage( cvGetSize(&srcImg), IPL_DEPTH_8U, 1);
    CvSeq* l_lines = 0;   //����
    CvSeq* r_lines = 0;
    CvMemStorage* l_storage;
    CvMemStorage* r_storage;
    int l_count = 0;
    int r_count = 0;
    int l_angle = 0;
    int r_angle = 0;
    angle_sidework(real_lines,&l_storage, &r_storage,&l_lines, &r_lines, angle, &l_count, &r_count);
    char angle_str[3][10];
    sprintf(angle_str[0], "%d", angle[0]);
    sprintf(angle_str[1], "%d", angle[1]);
    sprintf(angle_str[2], "%d", angle[2]);
    CvFont font;
    cvInitFont(&font, CV_FONT_HERSHEY_SIMPLEX, 1.0, 1.0);
    cvPutText(Real_Sidework, angle_str[0], cvPoint(30, 30), &font, cvScalar(255, 255,255));
    cvPutText(Real_Sidework, angle_str[1], cvPoint(100, 30), &font, cvScalar(255, 255,255));
    cvPutText(Real_Sidework, angle_str[2], cvPoint(170, 30), &font, cvScalar(255, 255,255));
    LOGD( "done");
    
    print_sidework(Real_Sidework, l_lines, r_lines); //������ ����
    LOGD( "done");
    distance_line2(&Real_Sidework);//������ ����.
    LOGD( "done");
    
    
    cvCvtColor(Real_Sidework, &ResultImg, CV_GRAY2RGB);
    
    cvReleaseImage(&Gray);
    cvReleaseImage(&CannyImg);
    cvReleaseImage(&Sidework_Line);
    cvReleaseImage(&Affine_Sidework);
    cvReleaseImage(&Real_Sidework);
    cvReleaseMat(&map_matrix);
    
    cvClearSeq(lines);
    cvClearSeq(real_lines);
    cvClearSeq(l_lines);
    cvClearSeq(r_lines);
    cvReleaseMemStorage(&storage);
    cvReleaseMemStorage(&real_storage);
    cvReleaseMemStorage(&l_storage);
    cvReleaseMemStorage(&r_storage);
    if(*R_L == 0)
        Order[0] = angle[0]; //����
    else if(*R_L == 1)
        Order[0] = angle[1]; //������ ���� ����
    else if(*R_L == -1)
        Order[0] = angle[2]; //���� ���� ����
    env->ReleaseIntArrayElements(order, Order, 0);
    env->ReleaseIntArrayElements(r_l, R_L, 0);
    env->ReleaseByteArrayElements(NV21FrameData, pNV21FrameData, 0);
    env->ReleaseIntArrayElements(outPixels, poutPixels, 0);
    return true;
}

void hough_line_probabliblistic(IplImage** src, CvMemStorage** storage, CvSeq** lines, int threshold, int param1, int param2){
    *lines = cvHoughLines2(*src, *storage, CV_HOUGH_PROBABILISTIC, 1, CV_PI/180, threshold, param1, param2);
}

void sidework_line_detection(CvSeq** lines, IplImage** dst, int min, int max) {
    for(int i=0; i<(*lines)->total; i++)
    {
        CvPoint* line = (CvPoint*)cvGetSeqElem(*lines, i);
        int   dx = line[1].x - line[0].x;
        int dy = line[1].y - line[0].y;
        double    rad    = atan2((double)dx, (double)dy);    // ������ ����(radian)
        double        degree    = abs(rad * 180) / CV_PI;            // ������ ����(degree) ����
        if(degree >= min && degree <  max)
            cvLine(*dst, line[0], line[1], CV_RGB(255, 255, 255), 1);
    }
    
}

void distance_line2(IplImage** src) {
    cvLine(*src, cvPoint(310, 600), cvPoint(490, 600), cvScalar(200, 200, 200), 2); //1m
    cvLine(*src, cvPoint(310, 600), cvPoint(20, 0), cvScalar(200, 200, 200), 1); //2m
    cvLine(*src, cvPoint(10*2, 0), cvPoint(390*2, 0), cvScalar(200, 200, 200), 2); //3m
    cvLine(*src, cvPoint(490, 600), cvPoint(390*2, 0), cvScalar(200, 200, 200), 1); //4m
    
}

void angle_sidework(CvSeq* lines, CvMemStorage** l_storage,  CvMemStorage** r_storage, CvSeq** l_lines,CvSeq** r_lines, int* angle, int *l_count, int *r_count) {
    *l_storage = cvCreateMemStorage(0);
    *r_storage = cvCreateMemStorage(0);
    *l_lines = cvCreateSeq(lines->flags, lines->header_size, lines->elem_size, *l_storage);
    *r_lines = cvCreateSeq(lines->flags, lines->header_size, lines->elem_size, *r_storage);
    double sum = 0;
    double l_sum = 0;
    double r_sum = 0;
    int count = 0;
    for(int i=0; i< lines->total; i++)
    {    //���� �� ������ ������ ������ ������ �� ����.
        CvPoint* line = (CvPoint*)cvGetSeqElem(lines, i);
        int   dx = line[1].x - line[0].x;
        int dy = line[1].y - line[0].y;
        double    rad    = atan2((double)dx, (double)dy);    // ������ ����(radian)
        double        degree    = abs(rad * 180) / CV_PI;            // ������ ����(degree) ����
        //printf("[%d]\n", line[0].x);
        if(degree>90)
            degree -= 90;
        else
            degree += 90;
        if(degree <= 90 && line[0].x <= 445) {
            cvSeqPush(*l_lines, line);
            l_sum += degree;
            (*l_count)++;
            sum += (int)degree;
        }
        else if(degree > 90 && line[0].x >= 355) {
            cvSeqPush(*r_lines, line);
            r_sum += degree;
            (*r_count)++;
            sum += (int)degree;
        }
    }
    if(*l_count != 0)
        angle[1] = (int)l_sum / *l_count;
    if(*r_count != 0)
        angle[2] = (int)r_sum / *r_count;
    
    if((*l_lines)->total < 7) {
        *l_count = 0;
        angle[1] = 0;
        sum = sum - (int)l_sum;
    }
    else if((*r_lines)->total < 7) {
        *r_count = 0;
        angle[2] = 0;
        sum = sum -(int)r_sum;
    }
    if(*l_count == 0 && *r_count == 0) {
        *l_count = 0;
        *r_count = 0;
        *angle = 0;
    }
    else
        *angle = sum / (*l_count + *r_count);
}

void print_sidework(IplImage* src, CvSeq* l_lines, CvSeq* r_lines){
    for(int i=0; i< l_lines->total; i++)
    {    //���� �� ������ ������ ������ ������ �� ����.
        CvPoint* line = (CvPoint*)cvGetSeqElem(l_lines, i);
        int   dx = line[1].x - line[0].x;
        int dy = line[1].y - line[0].y;
        double    rad    = atan2((double)dx, (double)dy);    // ������ ����(radian)
        double        degree    = abs(rad * 180) / CV_PI;            // ������ ����(degree) ����
        cvLine(src, line[0], line[1], cvScalar(150, 150, 150), 1);
    }
    for(int i=0; i< r_lines->total; i++)
    {    //���� �� ������ ������ ������ ������ �� ����.
        CvPoint* line = (CvPoint*)cvGetSeqElem(r_lines, i);
        int   dx = line[1].x - line[0].x;
        int dy = line[1].y - line[0].y;
        double    rad    = atan2((double)dx, (double)dy);    // ������ ����(radian)
        double        degree    = abs(rad * 180) / CV_PI;            // ������ ����(degree) ����
        cvLine(src, line[0], line[1], cvScalar(255, 255, 255), 1);
    }
    
}

#include <highgui.h>
#include <cv.h>

int main() {
	IplImage *srcImage;
	if (!(srcImage = cvLoadImage("국화.jpg", CV_LOAD_IMAGE_GRAYSCALE)))
		return -1;

	cvNamedWindow("srcImage", 0);//CV_WINDOW_AUTOSIZE); 0일경우 창크기 조절, 오토사이즈일경우 이미지 가로 세로의 맞게 창 조절
	cvShowImage("srcImage", srcImage); //이미지 띄우기


	cvWaitKey(0); //키보드 키 입력 때 까지 대기
	cvReleaseImage(&srcImage); //srcimage 메모리, 헤더 해제
	cvDestroyAllWindows(); //모든 윈도우 창 종료
	return 0;
}
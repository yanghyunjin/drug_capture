#include <highgui.h>
#include <cv.h>

int main() {
	IplImage *srcImage;
	if (!(srcImage = cvLoadImage("��ȭ.jpg", CV_LOAD_IMAGE_GRAYSCALE)))
		return -1;

	cvNamedWindow("srcImage", 0);//CV_WINDOW_AUTOSIZE); 0�ϰ�� âũ�� ����, ����������ϰ�� �̹��� ���� ������ �°� â ����
	cvShowImage("srcImage", srcImage); //�̹��� ����


	cvWaitKey(0); //Ű���� Ű �Է� �� ���� ���
	cvReleaseImage(&srcImage); //srcimage �޸�, ��� ����
	cvDestroyAllWindows(); //��� ������ â ����
	return 0;
}
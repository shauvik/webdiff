#include <opencv/cv.h>
#include <opencv/highgui.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

IplImage* drawRect(IplImage* img1,int x,int y,int w,int h){
	//printf("XXX");
//	IplImage *img1 = cvLoadImage(file, 1);
	/* sets the Region of Interest
 	  Note that the rectangle area has to be __INSIDE__ the image */
	/* draw a blue box */
	    cvRectangle(img1, cvPoint(x,y),cvPoint(w,h),cvScalar(127, 127, 127, 0),CV_FILLED);
return img1;
//cvSaveImage(file ,img1); 
}

int main(int argc, char** argv) {
//printf("%d" , argc);
printf("X");
IplImage* img=cvLoadImage(argv[1],1);
//img=drawRect(img,50,50,100,100);

int currentArgv=2;
printf("%d",argc);
while(currentArgv<argc){
	img=drawRect(img,atoi(argv[currentArgv]),atoi(argv[currentArgv+1]),atoi(argv[currentArgv+2]),atoi(argv[currentArgv+3]));
//printf("WORK");
currentArgv+=4;
}
cvSaveImage(argv[1],img);
}

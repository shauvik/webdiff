#include <opencv/cv.h>
#include <opencv/highgui.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sstream>
#include <iostream>
#include<set>

using namespace std;


IplImage* getROI(char* file,int x,int y,int w,int h){
	IplImage *img1 = cvLoadImage(file, 1);
	/* sets the Region of Interest
 	  Note that the rectangle area has to be __INSIDE__ the image */
	cvSetImageROI(img1, cvRect(x, y, w, h));
 
	/* create destination image
	   Note that cvGetSize will return the width and the height of ROI */
	IplImage *img2 = cvCreateImage(cvGetSize(img1),img1->depth,img1->nChannels);
 
/* copy subimage */
cvCopy(img1, img2, NULL);
 
/* always reset the Region of Interest */
cvResetImageROI(img1);
return img2;

}
void getColors(IplImage* img){
	set<int> list;
	
	for(int i=0;i<img->width;i++){
		for(int j=0;j<img->height;j++){
		CvScalar s;
		s=cvGet2D(img,j,i); // get the (i,j) pixel value
		int final = (((int)s.val[2]*1000000)+((int)s.val[1]*1000)+((int)s.val[0]));
		list.insert(final);	
		}
	}
	float area=img->width*img->height;
	float x=(float)list.size()/area*100;
	printf("%f",x);

}

int main( int argc, char** argv ) {
	//for img1 roi X,Y,W,H
	//getROI(argv[1],atoi(argv[3]),atoi(argv[4]),atoi(argv[5]),atoi(argv[6]))
	//path,x,y,w,h
	getColors(getROI(argv[1],atoi(argv[2]),atoi(argv[3]),atoi(argv[4]),atoi(argv[5])));
	//printf("CORREL (1): %f \n",cvCompareHist(h1,h2,CV_COMP_CORREL));
	//printf("CHISQR (0): %f \n",cvCompareHist(h1,h2,CV_COMP_CHISQR));
	//printf("INTERSECT (1): %f \n",cvCompareHist(h1,h2,CV_COMP_INTERSECT));
	//printf("BHATTACHARYYA (0): %f \n",cvCompareHist(h1,h2,CV_COMP_BHATTACHARYYA));
	//printf("%f \n",cvCompareHist(h1,h2,CV_COMP_BHATTACHARYYA));
}

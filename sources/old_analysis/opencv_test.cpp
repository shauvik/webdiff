#include <opencv/cv.h>
#include <opencv/highgui.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

IplImage* getROI(char* file,int x,int y,int w,int h){
	IplImage *img1 = cvLoadImage(file, 1);
	if(x>img1->width){
		return NULL;
	}
	if(y>img1->height){
		return NULL;
	}
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
CvHistogram* getHistogram(IplImage* img){
	if(img==NULL){
		return NULL;
	}
    IplImage* src;
    src=img;

    

        // Compute the HSV image, and decompose it into separate planes.
        //
        IplImage* hsv = cvCreateImage( cvGetSize(src), 8, 3 ); 
        cvCvtColor( src, hsv, CV_BGR2HSV );

        IplImage* h_plane  = cvCreateImage( cvGetSize(src), 8, 1 );
        IplImage* s_plane  = cvCreateImage( cvGetSize(src), 8, 1 );
        IplImage* v_plane  = cvCreateImage( cvGetSize(src), 8, 1 );
        IplImage* planes[] = { h_plane, s_plane };
        cvCvtPixToPlane( hsv, h_plane, s_plane, v_plane, 0 );

        // Build the histogram and compute its contents.
        //
        int h_bins = 30, s_bins = 30; 
        CvHistogram* hist;
        {
          int    hist_size[] = { h_bins, s_bins };
          float  h_ranges[]  = { 0, 180 };          // hue is [0,180]
          float  s_ranges[]  = { 0, 255 }; 
          float* ranges[]    = { h_ranges, s_ranges };
          hist = cvCreateHist( 
            2, 
            hist_size, 
            CV_HIST_ARRAY, 
            ranges, 
            1 
          ); 
        }
        cvCalcHist( planes, hist, 0, 0 );
	 return hist;
}

float convertHistCallEMD(CvHistogram* hist1, CvHistogram* hist2, int h_bins,int s_bins){
	CvMat* sig1;
	CvMat* sig2;
	int numrows=h_bins*s_bins;
	//Create matrices to store signature
	
	sig1=cvCreateMat(numrows,3, CV_32FC1);
	sig2=cvCreateMat(numrows,3,CV_32FC1);
	
	//Fill Signatures for two histograms
	for(int h=0;h<h_bins;h++){
		for(int s=0; s<s_bins; s++){
			float bin_val= cvQueryHistValue_2D(hist1,h,s);
			cvSet2D(sig1,h*s_bins + s,0,cvScalar(bin_val));
			cvSet2D(sig1,h*s_bins + s,1,cvScalar(h));
			cvSet2D(sig1,h*s_bins + s,2,cvScalar(s));

			bin_val= cvQueryHistValue_2D(hist2,h,s);
			cvSet2D(sig2,h*s_bins + s,0,cvScalar(bin_val));
			cvSet2D(sig2,h*s_bins + s,1,cvScalar(h));
			cvSet2D(sig2,h*s_bins + s,2,cvScalar(s));
		}
	}
	//float emd=cvCalcEMD2(sig1,sig2,CV_DIST_L1);
	float emd1=cvCalcEMD2(sig1,sig2,CV_DIST_L2);
	return emd1;
}
int main( int argc, char** argv ) {
	//for img1 roi X,Y,W,H
	//getROI(argv[1],atoi(argv[3]),atoi(argv[4]),atoi(argv[5]),atoi(argv[6]))
	CvHistogram* h1 = getHistogram(getROI(argv[1],atoi(argv[3]),atoi(argv[4]),atoi(argv[5]),atoi(argv[6])));
	CvHistogram* h2 = getHistogram(getROI(argv[2],atoi(argv[7]),atoi(argv[8]),atoi(argv[9]),atoi(argv[10])));
	if(h1==NULL || h2==NULL)
		printf("%d",-99);
	else
		printf("%f",convertHistCallEMD(h1,h2,30,30));
	//printf("CORREL (1): %f \n",cvCompareHist(h1,h2,CV_COMP_CORREL));
	//printf("CHISQR (0): %f \n",cvCompareHist(h1,h2,CV_COMP_CHISQR));
	//printf("INTERSECT (1): %f \n",cvCompareHist(h1,h2,CV_COMP_INTERSECT));
	//printf("BHATTACHARYYA (0): %f \n",cvCompareHist(h1,h2,CV_COMP_BHATTACHARYYA));
	//printf("%f \n",cvCompareHist(h1,h2,CV_COMP_BHATTACHARYYA));
}

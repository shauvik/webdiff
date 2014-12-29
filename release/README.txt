===================================================================================================
Installation Instructions for WebDiff 1.0-alpha

For latest copy of this document, visit the tool website 
at http://www.cc.gatech.edu/~shauvik/webdiff.php
===================================================================================================

+===========================+
  I. ONE-TIME INITIAL SETUP
+===========================+

1. Install the following on a windows machine.
  i. Install Web Browsers you want to test your web app in - Firefox, Google Chrome etc.
  ii. Python 2.6+ (http://python.org) and the following libraries. Add python to system PATH.
      a. Python Image Library 1.1.7 (http://www.pythonware.com/products/pil/)
      b. Python Win32 extensions (http://sourceforge.net/projects/pywin32/)
      c. Mysql-Python connector library (http://sourceforge.net/projects/mysql-python)
	  d. Twisted - Python networking library (http://twistedmatrix.com) (+ Zope.interface dependency)
  iii. OpenCV 1.1pre (http://sourceforge.net/projects/opencvlibrary/files/opencv-win/1.1pre1/)
  iv. Sun JDK 6.0
  v. Apache-MySQL-PHP (recommended: XAMPP - http://www.apachefriends.org/en/xampp-windows.html)

2. Extract the archive to C:\ such that the tool is in C:\WebDiff
3. Copy the C:\WebDiff\website\webdiff and C:\WebDiff\website\report folders and place under C:\xampp\htdocs\
4. Start Apache and Mysql (using Xampp-control) and create a mysql database (http://localhost/phpmyadmin) and user with all privileges.
5. Enter the information about browsers and other configuration params in C:\WebDiff\config.json
6. Start all considered browsers. Open http://localhost/webdiff/size.html and resize them to obtain the same visible area size.
7. Open a terminal and run C:\WebDiff\capture\setup.py (This captures the browser dimensions).

+==============================+
  II. RUNNING THE TOOL
+==============================+

1. Make sure all browsers are running.
2. Open a terminal and Run C:\WebDiff\webdiff.py <URL> to test the webapp in all the different browsers.
3. After the tool has completed, visit http://localhost/report to see the report!

+==============================+
  III. FEEDBACK/SUPPORT
+==============================+
Please log your issues on our Github issues page: https://github.com/shauvik/webdiff/issues

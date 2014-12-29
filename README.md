webdiff
=======

Old code from ICSM 2010 paper


### NOTES:

WebDiff was built in a pre-selenium (and pre-crawljax) era. It uses Win32API to run browsers, resize them and enter URL in the browser URL space. It was tested on Windows XP but should work on Windows 7 as well.

To get it working, first you need to do the one time setup as described in the README.txt
https://github.com/shauvik/webdiff/tree/master/release/README.txt

This setup saves the config.json file, which contains the browser viewport information along with other settings.
https://github.com/shauvik/webdiff/blob/master/release/capture/setup.py

Then you need to run webdiff.py, which internally calls capture.py to capture the data from different browsers, and it then it runs the analysis part from webdiff.jar. 

The capture script loads the website in the browser, and injects javascript using JS URLs which traverse the DOM and submit it to the serverside running at http://HOST/webdiff/

These requests are intercepted by the webdiff proxy, which sends these requests to http://localhost/webdiff/*.php script that saves it to the mysqlDB, which is then queried by the analysis part.

The proxy is needed to transparently save data from a browser along with handling browser security restrictions (i.e., [same-origin policy](http://en.wikipedia.org/wiki/Same-origin_policy)).


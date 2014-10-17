import os, sys
from capture import capture
logo = '''
________________________________
      __ __  __   __ __ 
 |  ||_ |__)|  \||_ |_  
 |/\||__|__)|__/||  |   

 Automated Cross-Browser Testing
________________________________ 
'''

if __name__ == "__main__":
	print logo
	
	if (len(sys.argv) < 2):
		print "Usage: webdiff.py url"
		exit()
	
	print "Starting WebDiff Phase 1 (Data collection).."
	sessid = capture.doCapture(sys.argv[1])
	#sessid="veUW"
	
	print "\n\nPress enter to proceed.."
	raw_input()
	print "Starting WebDiff Phases 2 and 3 (Variable element identification and Cross-browser comparison).."
	
	os.system("java -jar C:/WebDiff/analysis/webdiff.jar "+sessid)
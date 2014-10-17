import json, winutils, dbutils, subprocess, time, sys
from random import randint
from urllib2 import urlopen

config = None
config_file = "C:/WebDiff/vm_config.json"

def getJSWindowDims():
  f = urlopen("http://"+config['host_machine']+":"+config['web_port'])
  dims = "["+f.read()+"]"
  print dims
  return json.loads(dims)

def getRemoteViewportDims(b):
  winutils.resizeWindow(100,100)
  time.sleep(2)
  winutils.resizeWindow(200,200)
  time.sleep(2)
  return getJSWindowDims()

def adjustWindowSize(b, scrollWidth, scrollHeight):
  vp=config["viewport"][b]
  d1 = scrollWidth + vp[0] + vp[2]
  d2 = scrollHeight + vp[1] + vp[3]
  print (d1), ",", (d2)
  winutils.resizeWindow(50+d1,50+d2)
  time.sleep(2)
  dim2 = getJSWindowDims()

if __name__ == "__main__":
  #Read configuration
  cFile = open(config_file, "r")
  config = json.loads(cFile.read())
  cFile.close()
  bMap = config["vm_config"]["browser_map"]
  #Set Proxy mode  
  winutils.setProxy(config)
  #some initializations
  if (len(sys.argv) < 2):
    print "Usage: run_vm.py url"
    exit()
  url = sys.argv[1]
  cursor,conn,testid=dbutils.mySQLtest(url,bMap.keys(),config) #returns the test for that new entry
  pageDims = None
  i=0
  bMap['ref']=bMap[config["vm_config"]["reference"]]
  for b in bMap:
    dbutils.markTest(testid[i],-1,conn,cursor)
    subprocess.Popen([bMap[b][1]])
    time.sleep(2)
    winutils.getBrowserWindowToFront(bMap[b][0])
    winutils.enterURL(url)
    time.sleep(5)
    clrScrpt = "javascript:var%20bcc=document.createElement('script');"+\
        "bcc.setAttribute('src','http://cheetah.cc.gt.atl.ga.us/webdiff/webdiff.js');"+\
        "document.body.appendChild(bcc);void(bcc);"
    winutils.enterURL(clrScrpt)
    time.sleep(2)
    if(pageDims == None):
        pageDims = getRemoteViewportDims(b)
    adjustWindowSize(b, pageDims[2], pageDims[3]) #pass browser code & scroll dims
    image_filename = config["image_folder"]+"/%d.png"%(testid[i])
    winutils.captureScreenshots(config['viewport'][b],image_filename) #capture screenshots
    idStr=str(testid[i]) 
    clrScrpt = "javascript:bcc.init('"+idStr+"');"
    winutils.enterURL(clrScrpt) #capture dom information
    time.sleep(2)
    dbutils.markTest(testid[i],1,conn,cursor)
    i+=1
  #winutils.resetProxy()
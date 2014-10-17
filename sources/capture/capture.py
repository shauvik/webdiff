import json, winutils, dbutils, subprocess, time, sys, os, shutil
from random import randint
from urllib2 import urlopen

config = None
config_file = "C:/WebDiff/vm_config.json"

def doCapture(url):
  print "**************************************************";
  print " WEBDIFF - Capture Script  ";
  print "**************************************************";
  #Read configuration
  cFile = open(config_file, "r")
  config = json.loads(cFile.read())
  cFile.close()
  bMap = config["vm_config"]["browser_map"]
  #Set Proxy mode  
  winutils.setProxy(config)
  #some initializations
  i=0
  #Populate the reference browser field 
  ref = config["vm_config"]["reference"]
  bMap["ref"]=bMap[ref]
  config["window_size"]["ref"] = config["window_size"][ref]
  config["viewport"]["ref"] = config["viewport"][ref]
  #Init the db entries
  cursor,conn,testid,sessid=dbutils.mySQLtest(url,bMap.keys(),config) #returns the test for that new entry
  print " - Started new capture session: ", sessid
  wd_window = winutils.getFrontWindow()
  for b in bMap:#['ff']:#
    print  " - Capturing data from ", bMap[b][0]
    dbutils.markTest(testid[i],-1,conn,cursor)
    #subprocess.Popen([bMap[b][1]])
    #time.sleep(1)
    winutils.getBrowserWindowToFront(bMap[b][0])
    winutils.enterURL(url)
    winutils.setWindowToFront(wd_window)
    raw_input("   |- Press enter to proceed...")
    winutils.getBrowserWindowToFront(bMap[b][0])    
    #time.sleep(10)
    #if(b == "ie"):
    #    time.sleep(5)
    clrScrpt = "javascript:var%20bcc=document.createElement('script');"+\
        "bcc.setAttribute('src','http://localhost/webdiff/webdiff.js');"+\
        "document.body.appendChild(bcc);void(bcc);"
    winutils.enterURL(clrScrpt)
    time.sleep(5)
    ws = config["window_size"][b]
    #print " - Resizing browser window to", ws
    winutils.resizeWindow(ws[0],ws[1])
    time.sleep(1)
    vp = config['viewport'][b]
    image_filename = config["image_folder"]+"/%d.png"%(testid[i])
    #winutils.captureScreenshot(config['viewport'][b],image_filename) #capture screenshots
    winutils.saveEntireScreenshot(vp,image_filename)
    idStr=str(testid[i]) 
    clrScrpt = "javascript:bcc.init('"+idStr+"');"
    winutils.enterURL(clrScrpt) #capture dom information
    time.sleep(2)
    dbutils.markTest(testid[i],1,conn,cursor)
    i+=1
  winutils.resetProxy()
  moveFiles(config)
  winutils.setWindowToFront(wd_window)
  return sessid

def moveFiles(config):
  #Copy files to web and analysis folders
  files = os.listdir(config["image_folder"])
  for f in files:
    if f[-3:] == "png":
      shutil.copy(config["image_folder"]+"/"+f, "C:/xampp/htdocs/report/screenshots/")
      shutil.copy(config["image_folder"]+"/"+f, "C:/WebDiff/analysis/Screenshots/")

if __name__ == "__main__":
  if (len(sys.argv) < 2):
    print "Usage: capture.py url"
    exit()
  url = sys.argv[1]
  doCapture(url)
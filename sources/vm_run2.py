import json, winutils, dbutils, subprocess, time, sys
from random import randint
from urllib2 import urlopen

config = None
config_file = "C:/WebDiff/vm_config.json"

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
  i=0
  #Populate the reference browser field 
  ref = config["vm_config"]["reference"]
  bMap["ref"]=bMap[ref]
  config["window_size"]["ref"] = config["window_size"][ref]
  config["viewport"]["ref"] = config["viewport"][ref]
  #Init the db entries
  cursor,conn,testid=dbutils.mySQLtest(url,bMap.keys(),config) #returns the test for that new entry
  wd_window = winutils.getFrontWindow()
  for b in bMap:#['ff']:#
    dbutils.markTest(testid[i],-1,conn,cursor)
    #subprocess.Popen([bMap[b][1]])
    #time.sleep(1)
    winutils.getBrowserWindowToFront(bMap[b][0])
    winutils.enterURL(url)
    winutils.setWindowToFront(wd_window)
    raw_input("Press enter to continue...")
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
    print "Resizing browser window to", ws
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
  #winutils.resetProxy()
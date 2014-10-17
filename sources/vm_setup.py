import json, winutils, subprocess, time
from random import randint

config = None
config_file = "C:/WebDiff/vm_config.json"

if __name__ == "__main__":
  #Read configuration
  cFile = open(config_file, "r")
  config = json.loads(cFile.read())
  #Set Proxy mode  
  winutils.setProxy(config)
  bMap = config["vm_config"]["browser_map"]
  config["viewport"] = {}
  for b in bMap:
    subprocess.Popen([bMap[b][1]])
    time.sleep(2)
    winutils.getBrowserWindowToFront(bMap[b][0])
    clrScrpt = "http://cheetah.cc.gt.atl.ga.us/~bc/color.php"
    winutils.enterURL(clrScrpt)
    time.sleep(2)
    c = (50,50,50)
    cHex = (hex(c[0])[2:],hex(c[1])[2:],hex(c[2])[2:])
    while(winutils.isColorPresent(c)):
      c = (randint(0,255),randint(0,255),randint(0,255))
      cHex = (hex(c[0])[2:],hex(c[1])[2:],hex(c[2])[2:])
      winutils.enterURL(clrScrpt+"?color=%s%s%s"%cHex)
      time.sleep(2)
    winutils.enterURL(clrScrpt+"?color=%s%s%s"%cHex)
    time.sleep(2) 
    viewport = winutils.findViewportCoords(c)
    config["viewport"][b] = viewport
  cFile.close()
  cFile = open(config_file, "w")
  cFile.write(json.dumps(config, indent=2))
  cFile.close()
  #winutils.resetProxy()
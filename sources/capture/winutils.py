import win32api, win32gui, win32process, win32con, win32clipboard
import Image, ImageGrab, ImageChops, MySQLdb
import time, os
from random import choice
from ctypes import windll

def captureScreen():
  '''
  @Description: Captures the screenshot
  @Return: Image
  '''
  w = win32gui.GetForegroundWindow()
  s = win32gui.GetWindowRect(w)
  im=ImageGrab.grab(s)
  return im

def captureViewportAndScrollbar(viewport):
    '''
    @Description: Captures the viewport and scrollbar (assumption: 20px wide)
    @Return: viewport and scrollbar images
    '''
    im = captureScreen()
    bounding_box = (viewport[0],viewport[1],im.size[0]-viewport[2],im.size[1]-viewport[3])
    vp = im.crop(bounding_box)
    sb = im.crop((bounding_box[2],bounding_box[1], bounding_box[2]+20, bounding_box[3]))
    return vp, sb

def captureScreenshot(viewport, filename):
  im = captureScreen()
  bounding_box = (viewport[0],viewport[1],im.size[0]-viewport[2],im.size[1]-viewport[3])
  im = im.crop(bounding_box)
  im.save(filename)

def matchTemplate(searchImage, templateImage):
    minScore = -1000
    matching_xs = 0
    matching_ys = 0
    # convert images to "L" to reduce computation by factor 3 "RGB"->"L"
    searchImage = searchImage.convert(mode="L")
    templateImage = templateImage.convert(mode="L")
    searchWidth, searchHeight = searchImage.size
    templateWidth, templateHeight = templateImage.size
    # make a copy of templateImage and fill with color=1
    templateMask = Image.new(mode="L", size=templateImage.size, color=1)
    #loop over each pixel in the search image
    xs = 0
    for ys in range(searchHeight-templateHeight+1):
    #for ys in range(10):
        #set some kind of score variable to "All equal"
        score = templateWidth*templateHeight
        # crop the part from searchImage
        searchCrop = searchImage.crop((xs,ys,xs+templateWidth,ys+templateHeight))
        diff = ImageChops.difference(templateImage, searchCrop)
        notequal = ImageChops.darker(diff,templateMask)
        countnotequal = sum(notequal.getdata())
        score -= countnotequal

        if minScore < score:
            minScore = score
            matching_ys = ys
        elif score == templateWidth*templateHeight :
            print "Conflicting score", score, (matching_xs, matching_ys), (xs, ys)
            return None
        
    #print "  - Location=",(matching_xs, matching_ys), "Score=",minScore
    im1 = Image.new('RGB', (searchWidth, searchHeight), (80, 147, 0))
    im1.paste(templateImage, ((matching_xs), (matching_ys)))
    #searchImage.show()
    #im1.show()
    #im1.save('template_matched_in_search.png')
    return matching_ys

def saveTemp(im, filename, cnt):
    #print "Temp save", cnt
    im.save("%s.%d.png" %(filename,cnt), "PNG")

def saveEntireScreenshot(viewport, filename):
    '''
    @Description: Save a multipage website's screenshot to file
    '''
    #First go to top of page
    win32api.keybd_event(win32con.VK_HOME, 1)
    dummy_time = time.clock() #Wait for keyboard event to have affect
    cnt = 0
    im, sb = captureViewportAndScrollbar(viewport)
    #saveTemp(im, filename, cnt)
    cnt += 1
    tmp = None
    while True:
        win32api.keybd_event(win32con.VK_SPACE, 1) #Space to scroll
        dummy_time = time.clock() # gives enough time for event to occur
        time.sleep(2)
        im2, sb2 = captureViewportAndScrollbar(viewport)
        
        #if scrollbar doesn't change, you have reached end of page
        diff = ImageChops.difference(sb, sb2).getbbox()
        if diff == None:
            break
        else: #merge im and im2 - very less time
            #print "Merging; time=", time.clock()
            h = matchTemplate(im, im2.crop((0,0,im.size[0],1)))
            if h == None:
                h = matchTemplate(im, im2.crop((0,0,im.size[0],20)))
                if h == None:
                    print "ERROR: Can't Stitch; Multiple template matches"
                    exit()
            tmp = Image.new("RGB", (im.size[0], h+im2.size[1]))
            tmp.paste(im, (0,0))
            tmp.paste(im2, (0,h))
            im = tmp
            sb = sb2
            #saveTemp(im2, filename, cnt)
            cnt += 1
    #Save image
    im.save(filename)

def getWindowDims():
  fw = win32gui.GetForegroundWindow()
  wr = win32gui.GetWindowRect(fw)
  cr = win32gui.GetClientRect(fw)
  return wr, cr
  
def resizeWindow(width,height):
  fw = win32gui.GetForegroundWindow()
  wr = win32gui.GetWindowRect(fw)
  cr = win32gui.GetClientRect(fw)
  win32gui.MoveWindow(fw,0,0,width,height,True)
  #print win32gui.GetWindowRect(fw), win32gui.GetClientRect(fw)

def getBrowserWindowToFront(browser):
  '''
  @Description: Get the browser windows by name and set it to foreground
  @Return: list of browser window handles
  '''
  #print "Getting window to front : ",browser
  def callback (hwnd, hwnds):
    if win32gui.IsWindowVisible (hwnd) and win32gui.IsWindowEnabled (hwnd):
      _, found_pid = win32process.GetWindowThreadProcessId (hwnd)
      wText = win32gui.GetWindowText (hwnd)
      #print hwnd, "(", found_pid ,")=>", wText
      if(wText.find(browser) >= 0):
        #print "Found window - ", wText
        hwnds.append(hwnd)
    return True
  hwnds = []
  win32gui.EnumWindows(callback, hwnds)
  if len(hwnds) > 1:
    print " - More than 1 window found. Using the first one"
  win32gui.SetForegroundWindow(hwnds[0])
  time.sleep(1)

def getFrontWindow():
    return win32gui.GetForegroundWindow()

def setWindowToFront(id):
    win32gui.SetForegroundWindow(id)

def isColorPresent(color):
  '''
    Check if a color is present in the browser
  '''
  im = captureScreen()
  c = im.getcolors(500000)
  for count, col in c:
    if col == color:
      return True
  return False

def findViewportCoords(color):
  #print "Looking for ", color
  im = captureScreen()
  i,last,found = (0,0,False)
  dims = ()
  for pixel in im.getdata():
    i+=1
    if(pixel == color):
      if not found:   
        #print "Left top = (", i/im.size[0], ",", i%im.size[0], ")"
        dims = dims + (i%im.size[0], i/im.size[0])
        found = True
      else:
        last = i
  if found:
    dims = dims + (im.size[0]-(last%im.size[0])-1, im.size[1]-(last/im.size[0])-1)
    #print "Right bottom = (", last/im.size[0], ",", last%im.size[0], ")"
  return dims

def setProxy(config):
  command = "C:/WebDiff/hudsuckr.exe (null) true true true true \""+config["host_machine"]+":8080\" \"<local>\" (null)"
  os.system(command + ">>out.txt")

def resetProxy():
  command = "C:/WebDiff/hudsuckr.exe (null) true false false false (null) (null) (null)"
  os.system(command + ">>out.txt")

def enterURL(data):
  #press Ctrl+L
  win32api.keybd_event(win32con.VK_CONTROL,1,0,0)
  win32api.keybd_event(76,1,0, 0)
  win32api.keybd_event(76,1,win32con.KEYEVENTF_KEYUP, 0)
  win32api.keybd_event(win32con.VK_CONTROL,1,win32con.KEYEVENTF_KEYUP, 0)
  time.sleep(1)
  #type the URL
  for letter in data:
    shiftPressed=False
    if letter in '~!@#$%^&*()_+{}|<>?:"ABCDEFGHIJKLMNOPQRSTUVWXYZ':
      win32api.keybd_event(win32con.VK_SHIFT,1,0, 0)
      shiftPressed=True
    win32api.keybd_event(win32api.VkKeyScan(letter),0,0, 0)
    win32api.keybd_event(win32api.VkKeyScan(letter),0,win32con.KEYEVENTF_KEYUP, 0)
    if shiftPressed==True:
      win32api.keybd_event(win32con.VK_SHIFT,1,win32con.KEYEVENTF_KEYUP, 0)
      shiftPressed=False
  #press Enter
  win32api.keybd_event(win32con.VK_RETURN,1,0, 0)
  win32api.keybd_event(win32con.VK_RETURN,1,win32con.KEYEVENTF_KEYUP, 0)

def clickAt(x,y):
    windll.user32.SetCursorPos(x,y)
    win32api.mouse_event(win32con.MOUSEEVENTF_LEFTDOWN, 0, 0)
    win32api.mouse_event(win32con.MOUSEEVENTF_LEFTUP, 0, 0)
    
def waitForPage():
    time.sleep(15)
    #print 'page loaded'
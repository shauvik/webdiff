'''
Python script to take screenshots of browser windows.
@Date: 20 February 2010
@Author: Shauvik Roy Choudhary
'''
import win32api, win32gui, win32clipboard, win32con, win32ui, ImageGrab, Image, ImageChops
import win32process, sys, time, math, operator

global_c = 1

def getBrowserWindow(browser):
    '''
    @Description: Get the browser windows by name
    @Return: list of browser window handles
    '''
    def callback (hwnd, hwnds):
        if win32gui.IsWindowVisible (hwnd) and win32gui.IsWindowEnabled (hwnd):
            _, found_pid = win32process.GetWindowThreadProcessId (hwnd)
            wText = win32gui.GetWindowText (hwnd)
            #print hwnd, "(", found_pid ,")=>", wText
            if(wText.find(browser) >= 0):
                print "Found window - ", wText
                hwnds.append (hwnd)
        return True
    
    hwnds = []
    win32gui.EnumWindows (callback, hwnds)
    return hwnds

def emptyClipboard():
    '''
    @Description: Clears the clipboard
    '''
    win32clipboard.OpenClipboard()
    win32clipboard.EmptyClipboard() #Empty clipboard
    win32clipboard.CloseClipboard()

def captureScreen():
    '''
    @Description: Captures the screenshot
    @Return: Image
    '''
##    global global_c
    emptyClipboard()
    win32api.keybd_event(win32con.VK_SNAPSHOT, 1) #PrintScreen; 1-captures only foreground window
    timer = time.clock()
    print "Capturing at ", timer
    im = None
    while not isinstance(im, Image.Image):
        im = ImageGrab.grabclipboard()
        if (time.clock() - timer) > 5.0:
            print "Error capturing screenshot", time.clock()
            exit()
##    im.save("C:\\Screenshots\\%d.png"%(global_c), "PNG")
##    global_c +=1
    return im

def findBrowserViewport():
    '''
    @Description: Used to find browser viewport
    @Steps:
      1. find a color that is not in the browser controls. My guess is (50,50,50) or #323232 (in hex)
         - for blank page (about:blank) , the "## Found color" statement shouldn't come
      2. now load a page, with background color as (50,50,50) and call this function for browser
         - page source: <html><body bgcolor="#323232"><br/><br/>..more br's for scrollbar</body></html>
    @Output: This prints out the left top and right bottom co-ordinates
    '''
    im = captureScreen()
    c = im.getcolors(500000)
    src = (50,50,50)
    for count, color in c:
        if color == src:
            print "## Found color", src
    i=0
    last = 0
    found = False
    for pixel in im.getdata():
        i+=1
        if(pixel == src):
            if not found:
                print "Left top = (", i%im.size[0], ",", i/im.size[0], ")"
                found = True
            else:
                last = i
    if found:
        print "Right bottom = (", last%im.size[0], ",", last/im.size[0], ")"
  
def saveScreenshot(viewport, filename):
    '''
    @Description: Saves the screenshot viewport to a file
    '''
    im = captureScreen().crop(viewport)
    im.save("C:\\Screenshots\\"+filename+".png", "PNG")
    print "File saved"

def captureViewportAndScrollbar(viewport):
    '''
    @Description: Captures the viewport and scrollbar (assumption: 20px wide)
    @Return: viewport and scrollbar images
    '''
    sc = captureScreen()
    vp = sc.crop(viewport)
    sb = sc.crop((viewport[2],viewport[1], viewport[2]+20, viewport[3] ))
    return vp, sb

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
        
    print "  - Location=",(matching_xs, matching_ys), "Score=",minScore
    im1 = Image.new('RGB', (searchWidth, searchHeight), (80, 147, 0))
    im1.paste(templateImage, ((matching_xs), (matching_ys)))
    #searchImage.show()
    #im1.show()
    #im1.save('template_matched_in_search.png')
    return matching_ys

def saveTemp(im, filename, cnt):
    print "Temp save", cnt
    im.save("C:\\Screenshots\\%s_%d.png" %(filename,cnt), "PNG")

def saveEntireScreenshot(viewport, filename):
    '''
    @Description: Save a multipage website's screenshot to file
    '''
    #First go to top of page
    win32api.keybd_event(win32con.VK_HOME, 1)
    dummy_time = time.clock() #Wait for keyboard event to have affect
    
    cnt = 0
    
    im, sb = captureViewportAndScrollbar(viewport)
    saveTemp(im, filename, cnt)
    cnt += 1
    
    tmp = None
    while True:
        win32api.keybd_event(win32con.VK_SPACE, 1) #Space to scroll
        dummy_time = time.clock() # gives enough time for event to occur
        time.sleep(1)
        im2, sb2 = captureViewportAndScrollbar(viewport)
        
        #if scrollbar doesn't change, you have reached end of page
        diff = ImageChops.difference(sb, sb2).getbbox()
        if diff == None:
            break
        else: #merge im and im2 - very less time
            print "Merging; time=", time.clock()
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
            saveTemp(im2, filename, cnt)
            cnt += 1
            
    if tmp == None:
        print "Error: No change on scrolling (single page or focus on input element?)";
    else:
        #Save image
        im.save("C:\\Screenshots\\"+filename+".png", "PNG")

if __name__ == '__main__':
    '''
        Browser Screenshot capture program
        - Assumes browser viewport size = 1024 X 786 px (scrollbar adjusted)
    '''
    #bmap contains browser name and viewport co-ordinates (left, upper, right+1, lower+1)
    #values obtained from call to findBrowserViewport
	
    bmap =  { "ff":("Mozilla Firefox", (5, 116, 1029, 884)),
              "gc":("Google Chrome", (6,79,1030,847)),
              "ie": ("Internet Explorer", (7,128,1031,896))}
    
    if len(sys.argv) != 2:
        print "Usage: browserView.py {ff|ie|gc}"
        exit()

    b = bmap[sys.argv[1]]
    hwnds = getBrowserWindow(b[0])
    if len(hwnds) > 1:
        print "More than 1 window found. Using the first one";

    wDim = win32gui.GetWindowRect(hwnds[0])
    win32gui.SetForegroundWindow(hwnds[0])
    time.sleep(1)
    
    findBrowserViewport()
    #if "entire" == sys.argv[2]:
     #   saveEntireScreenshot(b[1], sys.argv[1]+"\\"+sys.argv[3])
    #else:
     #   saveScreenshot(b[1], sys.argv[1]+"\\"+sys.argv[3])
    #print "Done !"

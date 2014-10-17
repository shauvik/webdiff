'''
Python script to take screenshots of browser windows.
@Date: 20 February 2010
@Author: Shauvik Roy Choudhary
'''
import win32api, win32gui, win32clipboard, win32con, win32ui, ImageGrab, Image, ImageChops
import win32process, sys, time, math, operator
import MySQLdb
from random import choice
from ctypes import windll
import string

global_c = 1
def typeURL(data):
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

def pressEnter():
    win32api.keybd_event(win32con.VK_RETURN,1,0, 0)
    win32api.keybd_event(win32con.VK_RETURN,1,win32con.KEYEVENTF_KEYUP, 0)

def changeFocus(x,y):
    windll.user32.SetCursorPos(x,y)
    win32api.mouse_event(win32con.MOUSEEVENTF_LEFTDOWN, 0, 0)
    win32api.mouse_event(win32con.MOUSEEVENTF_LEFTUP, 0, 0)
##    emptyClipboard()
##    win32api.keybd_event(win32con.VK_SNAPSHOT, 0) #PrintScreen; 1-captures only foreground window
##    time.sleep(1)
##    im = ImageGrab.grabclipboard()
##    s = im.crop((x-50,y-50,x+50,y+50))
##    s.show()
    
def waitForPage():
    time.sleep(15)
    print 'page loaded'

def pressCtrlL():
    win32api.keybd_event(win32con.VK_CONTROL,1,0, 0)
    win32api.keybd_event(76,1,0, 0)
    win32api.keybd_event(76,1,win32con.KEYEVENTF_KEYUP, 0)
    win32api.keybd_event(win32con.VK_CONTROL,1,win32con.KEYEVENTF_KEYUP, 0)
    time.sleep(1)
    
def dbConnect():
        DB = 'thepin'
        DB_HOST = 'cheetah.cc.gt.atl.ga.us'
        DB_USER = 'thepin'
        DB_PASSWORD = 'hrWVjJxLLtnMG8SS'
        conn = MySQLdb.Connection(db=DB, host=DB_HOST, user=DB_USER,passwd=DB_PASSWORD)
        return conn
    
def getSessions(cursor):
        sql = """select sessid from sessions;"""
        cursor.execute(sql)
        results = cursor.fetchall()
        sessid=list()
        for row in results:
            sessid.append(row[0])
        return sessid

def getTests(cursor,sessid):
        sql = """select testid from tests where sessid='"""+sessid+"""';"""
        cursor.execute(sql)
        results = cursor.fetchall()
        testid=list()
        for row in results:
            testid.append(row[0])
        return testid

def genSessId():
    chars = string.letters + string.digits
    newpasswd=''
    for i in range(4):
        newpasswd = newpasswd + choice(chars)
    return newpasswd

def uniqueSessId(results):
        newSessId=''
        foundId=True
        while foundId==True:
            newSessId=genSessId()
            foundId=newSessId in results
        return newSessId
    
def insertTest(browsers,conn,cursor,sessid, url):
    for test in browsers:
        sql = """INSERT INTO tests(sessid,browser,user_agent) VALUES ('"""+sessid+"""','"""+test+"""','"""+test+"""');"""
        try:
           # Execute the SQL command
           cursor.execute(sql)
           # Commit your changes in the database
           conn.commit()
        except:
           # Rollback in case there is any error
           conn.rollback()

def insertSession(conn,cursor,sessid, url):
    sql = """INSERT INTO sessions(sessid,url) VALUES ('"""+sessid+"""','"""+url+"""');"""
    try:
       # Execute the SQL command
       cursor.execute(sql)
       # Commit your changes in the database
       conn.commit()
    except:
       # Rollback in case there is any error
       conn.rollback()

def stripURL(url):
    stripped_text = ""
    for c in url:
        if c in '\/:*?"<>|':
            c = ""
        stripped_text += c
    return stripped_text

def markTest(testid,no,conn,cursor):
    sql = "UPDATE tests SET status=%d WHERE testid = %d" %(no,testid)
    try:
       # Execute the SQL command
       cursor.execute(sql)
       # Commit your changes in the database
       conn.commit()
    except:
       # Rollback in case there is any error
       conn.rollback()

def runAPIStuff(browsers,url,testid,conn,cursor, click):
        i=0
        for browser in browsers:
            b=mapAndSetFront(browser)
            markTest(testid[i],-1,conn,cursor)
            pressCtrlL()
            typeURL(url)
            pressEnter()
            waitForPage()
            strippedURL=stripURL(url)
            if(click != None):
                changeFocus(b[1][0] + int(click[0]), b[1][1] + int(click[1]))
            callCapture(b,"entire",browser,strippedURL,testid[i])
            pressCtrlL()
            idStr=str(testid[i])
            tempStr="javascript:var%20bcc=document.createElement('script');bcc.setAttribute('src','http://cheetah.cc.gt.atl.ga.us/~bc/bcc1.js');document.body.appendChild(bcc);(function(){if(window.bcc.version){bcc.init('"+idStr+"');}else{setTimeout(arguments.callee);}})();void(bcc);"
            typeURL(tempStr)
            pressEnter()
            markTest(testid[i],1,conn,cursor)
            i+=1

def mySQLtest(url,browsers):
        conn=dbConnect()
        cursor = conn.cursor()
        results=getSessions(cursor)
        sessid=uniqueSessId(results)
        insertSession(conn,cursor,sessid,url)
        insertTest(browsers,conn,cursor,sessid,url)
        tests=getTests(cursor,sessid)
        tests.insert(0,cursor)
        tests.insert(0,conn)
        return tests #set of testid

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
    time.sleep(1)
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
                print "Left top = (", i/im.size[0], ",", i%im.size[0], ")"
                found = True
            else:
                last = i
    if found:
        print "Right bottom = (", last/im.size[0], ",", last%im.size[0], ")"
  
def saveScreenshot(viewport, filename):
    '''
    @Description: Saves the screenshot viewport to a file
    '''
    im = captureScreen().crop(viewport)
    im.save("H:\\"+filename+".png", "PNG")
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
    #im.save("H:\\%s_%d.png" %(filename,cnt), "PNG")

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
        time.sleep(2)
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
        im.save("H:\\"+filename+".png", "PNG")

def mapAndSetFront(browser):
    '''
        Browser Screenshot capture program
        - Assumes browser viewport size = 1024 X 768 px (scrollbar adjusted)
    '''
    #bmap contains browser name and viewport co-ordinates (left, upper, right+1, lower+1)
    #values obtained from call to findBrowserViewport

    bmap =  { "ff":("Mozilla Firefox", (5, 116, 1005, 916)),
              "gc":("Google Chrome", (6,79,1006,879)),
              "ie": ("Internet Explorer", (7,121,1007,921))}
    
    b = bmap[browser]
    hwnds = getBrowserWindow(b[0])
    if len(hwnds) > 1:
        print "More than 1 window found. Using the first one";

    wDim = win32gui.GetWindowRect(hwnds[0])
    win32gui.SetForegroundWindow(hwnds[0])
    time.sleep(2)
    return b

def callCapture(b,arg1,arg2,arg3,testid):
    #gc_filename_20.pngcallCapture(b,"entire",browser,strippedURL,testid[i])
    if "entire" == arg1:
        saveEntireScreenshot(b[1], "%d"%(testid))
    else:
        saveScreenshot(b[1], "_%d"%(testid))
    print "Done !"
    return True
    
if __name__ == '__main__':
    if (len(sys.argv) != 3) and (len(sys.argv) != 5):
        print "Usage: capture.py gc,ff url {click_x click_y)|None}"
        exit()
    browsers=sys.argv[1].split(",")
    testid=mySQLtest(sys.argv[2],browsers) #returns the test for that new entry
    conn=testid[0]
    cursor=testid[1]
    testid.pop(0)
    testid.pop(0)
    click = None
    if(len(sys.argv) == 5):
        click = (sys.argv[3],sys.argv[4])
    runAPIStuff(browsers,sys.argv[2],testid,conn,cursor, click)
    #b=mapAndSetFront(sys.argv[1])
    time.sleep(1)
    #findBrowserViewport()
    #callCapture(b,"entire",sys.argv[1],sys.argv[3])
    

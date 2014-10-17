#
# This file is a utility module for Dragonfly.
# (c) Copyright 2010 by Christo Butcher
# Licensed under the LGPL, see <http://www.gnu.org/licenses/>
#

import time
import win32gui
import win32con

print "Setting foreground lock timeout..."
result = win32gui.SystemParametersInfo(win32con.SPI_SETFOREGROUNDLOCKTIMEOUT, 0, 1)
if result is None:
    print "Success."
else:
    print "Failed:", result

print "Sleeping to give you time to read the message above... :-)"
time.sleep(10)

from twisted.internet import reactor
from twisted.web import http
from twisted.web.proxy import Proxy, ProxyRequest
import urlparse, json, os, sys

config = None
config_file = "host_config.json"
 
class WebDiffProxyRequest(ProxyRequest):
    def __init__(self, ds, *args):
        self.ds = ds
        ProxyRequest.__init__(self, *args)
        
    def process(self):
        print self.uri
        if "/webdiff/" in self.uri:
            data_server = str(config['data_server']) #"cheetah.cc.gt.atl.ga.us"
            self.received_headers['host'] = data_server
            parsed = urlparse.urlparse(self.uri)
            if(parsed.hostname):
                self.uri = self.uri.replace(parsed.hostname, data_server, 1)
        if "/resized/" in self.uri:
            print "Browser resized:", self.uri
            self.transport.write("HTTP/1.0 200 OK\r\n")
            self.transport.write("Content-Type: text/html\r\n")
            self.transport.write("\r\n")
            self.transport.write('''<H1>OK</H1>''')
            self.transport.loseConnection()
            parsed = urlparse.urlparse(self.uri)
            print parsed.path
            self.ds.logData(parsed.path[9:])
        ProxyRequest.process(self)

class WebDiffProxy(Proxy):
    def __init__(self, ds):
        self.ds = ds
        Proxy.__init__(self)

    def requestFactory(self, *args):
        return WebDiffProxyRequest(self.ds, *args)

class WebDiffProxyFactory(http.HTTPFactory):
    def __init__(self, ds):
        self.ds = ds
        http.HTTPFactory.__init__(self)

    def buildProtocol(self, addr):
        protocol = WebDiffProxy(self.ds)
        return protocol
    
class DataStore(object):
    def __init__(self):
        self.data = ""
    def logData(self,data):
        print "Setting data",data
        self.data = data

# classes for web reporting interface
class WebReportRequest(http.Request):
    def __init__(self, ds, *args):
        self.ds = ds
        http.Request.__init__(self, *args)

    def process(self):
        self.setHeader("Content-Type", "text/html")
        print "Reading data", self.ds.data
        self.write("%s" % (self.ds.data))
        self.finish( )

class WebReportChannel(http.HTTPChannel):
    def __init__(self, ds):
        self.ds = ds
        http.HTTPChannel.__init__(self)

    def requestFactory(self, *args):
        return WebReportRequest(self.ds, *args)

class WebReportFactory(http.HTTPFactory):
    def __init__(self, ds):
        self.ds = ds
        http.HTTPFactory.__init__(self)

    def buildProtocol(self, addr):
        return WebReportChannel(self.ds)

def invokeVMScripts():
    import time
    time.sleep(2)
    for vm in config["vm_list"]:
        print "Invoking capture script in VM:", vm
        STAF_CMD = '/Library/staf/bin/STAF '+vm+' process start shell command "c:/python26/python.exe" '+\
                    'parms "c:/webdiff/vm_run.py http://www.google.com" newconsole '+\
                    'stdout "s:/screenshots/staf_out_'+vm+'.txt" stderr "s:/screenshots/staf_err_'+vm+'.txt"'
        os.system(STAF_CMD)

if __name__ == "__main__":
    if (len(sys.argv) < 2):
        print "Usage: webdiff.py url"
        exit()
    #Read configuration
    cFile = open(config_file, "r")
    config = json.loads(cFile.read())
    cFile.close()
    ds = DataStore()
    reactor.listenTCP(config['proxy_port'], WebDiffProxyFactory(ds))
    reactor.listenTCP(config['web_port'], WebReportFactory(ds))
    reactor.callInThread(invokeVMScripts)
    reactor.run()
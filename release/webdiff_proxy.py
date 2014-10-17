from twisted.internet import reactor
from twisted.web import http
from twisted.web.proxy import Proxy, ProxyRequest
import urlparse, json, os, sys
 
class SimpleProxyRequest(ProxyRequest):
    def process(self):
        if "/webdiff/" in self.uri:
            data_server = "localhost" #"cheetah.cc.gt.atl.ga.us"
            self.received_headers['host'] = data_server
            parsed = urlparse.urlparse(self.uri)
            if(parsed.hostname):
                self.uri = self.uri.replace(parsed.hostname, data_server, 1)
        print "URI", self.uri
        ProxyRequest.process(self)
 
class SimpleProxy(Proxy):
    requestFactory = SimpleProxyRequest
 
factory = http.HTTPFactory()
factory.protocol = SimpleProxy
 
reactor.listenTCP(8080, factory)
reactor.run()
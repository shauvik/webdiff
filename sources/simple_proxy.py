from twisted.internet import reactor
from twisted.web import http
from twisted.web.proxy import Proxy, ProxyRequest
 
class SimpleProxyRequest(ProxyRequest):
    def process(self):
        print self.uri
        ProxyRequest.process(self)
 
class SimpleProxy(Proxy):
    requestFactory = SimpleProxyRequest
 
factory = http.HTTPFactory()
factory.protocol = SimpleProxy
 
reactor.listenTCP(8000, factory)
reactor.run()
#!/usr/bin/python
# $Id$
# $Source$
from twisted.internet import defer, protocol, reactor
import sys
import getopt
import string

class ConsoleWriter():

  def write(self, data, type):
    """ Write request (on source port) and response """
    """ (from target host:port) to console """
    if (data):
      lines = data.split("\n")
      prefix = "<" if type == "request" else ">"
      for line in lines:
        sys.stdout.write("%s %s\n" % (prefix, line))
    else:
      sys.stdout.write("No response from server\n")
    return data


class DebugHttpClientProtocol(protocol.Protocol):
  """ Client protocol. Writes out the request to the  """
  """ target HTTP server. Response data is accumulated"""
  """ until all data is received, and calls the       """
  """ factory's onData method with the payload when   """
  """ done.                                           """

  def connectionMade(self):
    self.buf = []
    self.transport.write(self.factory.getCommand())

  def dataReceived(self, data):
    self.buf.append(data)

  def connectionLost(self, _):
    self.factory.onData("".join(self.buf))


class DebugHttpClientFactory(protocol.ClientFactory):
  """ Client factory. Provides custom methods to """
  """ write data to the server and to console.   """
  
  protocol = DebugHttpClientProtocol

  def __init__(self, command):
    self.command = command
    self.d = defer.Deferred()

  def clientConnectionFailed(self, _, reason):
    self.d.errback(reason)

  def onData(self, data):
    self.printMessage(data, "response")
    self.d.callback(data)

  def getCommand(self):
    return self.command + "\r\n\r\n"
  
  def printMessage(self, data, type):
    consoleWriter = ConsoleWriter()
    return defer.succeed(consoleWriter.write(data, type))


class DebugHttpServerProtocol(protocol.Protocol):
  """ Server Protocol. Waits for the request to be sent  """
  """ by the client, then calls the forwardToClient()    """
  """ method on the factory. This creates a new client   """
  """ thread which sends off the data to the target host """

  def dataReceived(self, data):
    d = self.factory.printMessage(data, "request")
    def onError(err):
      return "Internal error on server"
    d.addErrback(onError)
    def writeResponse(data):
      self.transport.write("%s\r\n" % (data))
      self.factory.forwardToClient(data)
      self.transport.loseConnection()
    d.addCallback(writeResponse)


class DebugHttpServerFactory(protocol.ServerFactory):
  """ Server Factory. Prints a debug message to the """
  """ console, and instantiates a client thread for """
  """ each client request to forward to target host """

  protocol = DebugHttpServerProtocol

  def __init__(self, targetHost, targetPort):
    self.targetHost = targetHost
    self.targetPort = targetPort
    
  def printMessage(self, data, type):
    consoleWriter = ConsoleWriter()
    return defer.succeed(consoleWriter.write(data, type))

  def forwardToClient(self, data):
    # instantiate a thread, create a twisted client and hit the
    # target server
    reactor.callFromThread(self._forwardToClient, self.targetHost,
      self.targetPort, data)

  def _forwardToClient(self, host, port, request):
    reactor.connectTCP(host, port, DebugHttpClientFactory(request))


def usage():
  sys.stdout.write("Usage: %s --help|--source port --target host:port\n"
    % (sys.argv[0]))
  sys.stdout.write("-h|--help: Show this message\n")
  sys.stdout.write("-s|--source: The port on the local host on which this \n")
  sys.stdout.write("             proxy listens\n")
  sys.stdout.write("-t|--target: The host:port which this proxy talks to\n")
  sys.stdout.write("Both -s and -t must be specified. There are no defaults.\n")
  sys.stdout.write("To use this proxy between client app A and server app B,\n")
  sys.stdout.write("point A at this proxy's source port, and point this\n")
  sys.stdout.write("proxy's target host:port at B. The request and response\n")
  sys.stdout.write("data flowing through A and B will be written to stdout for\n")
  sys.stdout.write("your visual pleasure.\n")
  sys.stdout.write("To stop the proxy, press CTRL+C\n")
  sys.exit(2)


def main():
  (opts, args) = getopt.getopt(sys.argv[1:], "s:t:h",
    ["source=", "target=", "help"])
  sourcePort, targetHost, targetPort = None, None, None
  for option, argval in opts:
    if (option in ("-h", "--help")):
      usage()
    if (option in ("-s", "--source")):
      sourcePort = int(argval)
    if (option in ("-t", "--target")):
      (targetHost, targetPort) = string.split(argval, ":")
  # remember no defaults?
  if (not(sourcePort and targetHost and targetPort)):
    usage()
  # start twisted reactor
  reactor.listenTCP(sourcePort,
    DebugHttpServerFactory(targetHost, int(targetPort)))
  reactor.run()


if __name__ == "__main__":
  main()
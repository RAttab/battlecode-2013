#!/usr/bin/env python
# -*- coding: utf-8 -*- 

# **********
# Filename:         simulserver.py
# Description:      An http based simulation server for battlecode
# Author:           Marc Vieira Cardinal
# Creation Date:    January 17, 2013
# Revision Date:    January 18, 2013
# **********

# Include the general libraries
import argparse
import signal
import time
import json
import subprocess
import multiprocessing
import threading
import BaseHTTPServer
import urlparse

import CombatRunner

# Doc available at http://docs.python.org/2/library/basehttpserver.html
class ClSimulServerWorker(BaseHTTPServer.BaseHTTPRequestHandler):

    def do_POST(self):
        self.do_GET()

    def do_GET(self):
        parsedUrl = urlparse.urlparse(self.path)
        splitPath = parsedUrl.path.strip("/").split("/")
        parsedQS = urlparse.parse_qs(parsedUrl.query, True) # True specifies to parse blank values

        contentLen = contentRaw = contentParsed = None

        if self.command == "POST":
            contentLen = int(self.headers.getheader("Content-length"))
            contentRaw = self.rfile.read(contentLen)
            #contentParsed = urlparse.parse_qs(contentRaw, True)       # Note: seem to return a dictionary where values are lists like { 'key': ['value'] }
            contentParsed = dict(urlparse.parse_qsl(contentRaw, True)) # Note: not compatible with keys having multi values

        # The following comments are based on a POST made
        # to the address http://127.0.0.1:4020/config/1/7452a?foo=bar&foo2=bar2
        self.args = {
            'client': {
                'host': self.client_address,            # Tuple with host, port -> ('127.0.0.1', 42060)
                'address': self.address_string(),       # Client's host         -> 'localhost'
                'command': self.command,                # GET / POST            -> 'POST'
                'pathRaw': self.path,                   # Raw path              -> '/configuration/1/7452a?foo=bar&foo2=bar2'
                'pathParsed': parsedUrl.path,           # Parsed path           -> '/configuration/1/7452a'
                'pathQuery': parsedUrl.query,           # Raw query string      -> 'foo=bar&foo2=bar2'
                'pathSplit': splitPath,                 # Split path            -> ['configuration', '1', '7452a']
                'urlParsed': parsedUrl,                 # Urlparse object       -> ParseResult(scheme='', netloc='', path='/configuration/1/7452a', params='', query='foo=bar&foo2=bar2', fragment='')
                'qsParsed': parsedQS,                   # Parsed query string   -> {'foo': ['bar'], 'foo2': ['bar2']}
                'reqVersion': self.request_version,     # Request version       -> 'HTTP/1.1'
                'contentLen': contentLen,               # Content len in bytes  -> 101
                'contentRaw': contentRaw,               # Raw content
                'contentParsed': contentParsed          # Parsed content
                },
            'server': {
                'version': self.server_version,         # HTTP server version   -> 'BaseHTTP/0.3'
                'sysVersion': self.sys_version,         # Python version        -> 'Python/2.7.3'
                'protVersion': self.protocol_version    # HTTP protocol version -> 'HTTP/1.0'
                }
            }

        print self.args

        try:
            method = self.args['client']['pathSplit'][0].lower()

            if method == "uptime":
                return self.ReturnJson(subprocess.check_output("uptime").strip())
            elif method == "run_battle":
                combatRunner = CombatRunner.CombatRunner()
                return self.ReturnOK(json.dumps(combatRunner.Run(self.args['client']['contentRaw'])))
        except:
            raise

        return self.ReturnErr("Method not specified or invalid.") # *** #

    def ReturnErr(self, message, code = 400):
        self.send_error(code, message)

    def ReturnOK(self, message):
        self.send_response(200, "OK")                           # Return a 200 OK HTTP response code
        self.send_header("Content-type", "application/json")    # We will be returning json
        self.send_header("Content-length", len(message))
        self.end_headers()                                      # We are done with headers, body will follow
        self.wfile.write(message)                               # Send data back to the client


# Global flag value modified by SignalHandler when receiving
# a SIGINT (Ctrl-C) signal
gSigIntRcvd = False

# Handler for the SIGINT (Ctrl-C) signal
def SignalHandler(signal, frame):
    global gSigIntRcvd
    gSigIntRcvd = True


if __name__ == "__main__":
    argParser = argparse.ArgumentParser(prog = "SimulServer",
                                        description = "An http based battlecode simulation server.")
    argParser.add_argument("--bind",
                           dest = "bind",
                           action = "store",
                           help = "Address to bind to (default: 127.0.0.1)",
                           default = "127.0.0.1")
    argParser.add_argument("--port",
                           dest = "port",
                           action = "store",
                           help = "Port to listen on",
                           type = int)

    args = vars(argParser.parse_args())

    if not args['bind'] or not args['port']:
        argParser.print_help()
        exit(1)

    # Set our custom signal handler to intercept Ctrl-C
    signal.signal(signal.SIGINT, SignalHandler)

    # Setup our http handler thread
    serverAddress = (args['bind'], args['port']) # Adr + Port sent as tuple
    httpServer = BaseHTTPServer.HTTPServer(serverAddress, ClSimulServerWorker)
    httpServer.config = args
    serverThread = threading.Thread(target = httpServer.serve_forever)
    serverThread.setDaemon(True)
    serverThread.start()

    print "Listening on port [%s] of [%s]" % (serverAddress[1], serverAddress[0])

    while not gSigIntRcvd:
        time.sleep(60) # "Infinite" loops are bad without sleep... duh
        continue

    # This point is reached only after receiving Ctrl-C in the SignalHandler
    httpServer.shutdown()

    exit(0)
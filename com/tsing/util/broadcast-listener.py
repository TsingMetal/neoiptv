from socket import *
from time import ctime

PORT = 21567
BUFSIZE = 1024

udpSerSock = socket(AF_INET, SOCK_DGRAM)
udpSerSock.bind(('', PORT))
print "waiting for message..."
while True:
    data, addr = udpSerSock.recvfrom(BUFSIZE)
    print ("...received -> %s %s" % (addr, data))

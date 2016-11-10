#!/home/pi/jython2.7.0/bin/jython

from com.tsing.iptv import MacWriter
from com.tsing.iptv import XmlParser
from com.tsing.iptv import DBConnector

class IptvXmlParser(XmlParser):
    def __init__(self):
        pass

class IptvDBConnector(DBConnector):
    def __init__(self):
        pass

parser = IptvXmlParser()
connector = IptvDBConnector()
writer = MacWriter(parser, connector)
testString = "testMacWriter"
print writer.getRet(testString)

# test OK
# date: Sat 29 Oct 17:05:01 CST 2016

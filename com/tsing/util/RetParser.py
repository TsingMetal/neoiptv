import xml.etree.ElementTree as ET
from xml_cmds import *

class RetParser(object):
  def __init__(self, ret_xml):
    self.ret_xml = ret_xml
    if self.ret_xml.find('?>') != -1:
      # remove <? xml version="1.0" encoding="UTF-8"?>
      self.ret_xml = \
          self.ret_xml[(self.ret_xml.index('?>') + 3) : ]
      self.tree = ET.fromstring(self.ret_xml)

  def parseRet(self, name):
      return self.tree.find(name).text

if __name__ == '__main__':
  parser = RetParser(set_xml)
  print parser.parseRet('cmd')
  print parser.parseRet('sender')
  parser = RetParser(erase_xml)
  print parser.parseRet('pwd')
  print parser.parseRet('cmd')

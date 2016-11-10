import xml.etree.ElementTree as ET

def test_xml(xml_cmd, name):
  tree = ET.fromstring(data)
  print(name, tree.find(name).text)


if __name__ == '__main__':
  data = '''
  <?xml version="1.0" encoding="UTF-8"?>
  <msg>
  <pwd>ff2234asdxcn2s34</pwd>
  <cmd>get</cmd>
  <list>sn,mac </list>
  <sender>pc</sender>
  </msg>
  '''
  data = data[data.index('?>') + 3 : ]
  test_xml(data, 'cmd')
  test_xml(data, 'pwd')
  test_xml(data, 'sender')

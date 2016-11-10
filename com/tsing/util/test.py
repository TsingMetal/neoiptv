from socket import *

def test(cmd_xml):
  ADDR = '<broadcast>'
  PORT = 1300 # target port

  sock = socket(AF_INET, SOCK_DGRAM) # prorocol: UDP
  sock.bind(('', 1301)) # 1301: port for receiving
  sock.setsockopt(SOL_SOCKET, SO_BROADCAST, 1)
  sock.sendto(b'%s' % cmd_xml, (ADDR, PORT)) # turn string into bytes
  print "command sent\n"

  print "waiting for result..."
  data, addr = sock.recvfrom(1024)
  print (addr, data)

  print "\nend."

if __name__ == '__main__':
  data = '''
  <?xml version="1.0" encoding="UTF-8"?>
  <msg>
  <pwd>ff2234asdxcn2s34</pwd>
  <cmd>get</cmd>
  <list>sn,mac</list>
  <sender>pc</sender>
  </msg>
  '''
  test(data)

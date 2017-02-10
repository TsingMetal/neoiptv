package tsing.iptv;

import java.util.LinkedHashMap;

public class IptvLogger implements Logger, MacWritingListener {
  private XmlWriter xmlWriter;

  public IptvLogger(XmlWriter writer) {
    xmlWriter = writer;
  }

  @Override
  public void macWritingPerformed(MacWritingEvent e) {
    Runnable r = () -> {
      if (e.getStatus().equals("FAIL") || e.getStatus().equals("PASS")) {
        LinkedHashMap<String, String> map = 
        e.getResultMap(); // map initialized here
        log(map);
      } else {
        // do nothing;
      }
    };

    Thread thread = new Thread(r);
    thread.start();
  }

  @Override
  public void log(LinkedHashMap<String, String> map) {
    xmlWriter.write(map);
  }
}

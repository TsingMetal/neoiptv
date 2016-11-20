package com.tsing.iptv;

import java.util.LinkedHashMap;

public class IptvLogger implements Logger, MacWritingListener {
  private XmlWriter xmlWriter;

  public IptvLogger(XmlWriter writer) {
    xmlWriter = writer;
  }

  @Override
  public void macWritingPerformed(MacWritingEvent e) {
    System.out.println("logger informed");
    Runnable r = () -> {
      System.out.println("in logging thread");
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
    System.out.println("logger logged");
  }

  @Override
  public void log(LinkedHashMap<String, String> map) {
    xmlWriter.write(map);
  }
}

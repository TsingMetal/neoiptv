package com.tsing.iptv;

import java.util.LinkedHashMap;

public interface Logger extends MacWritingListener {
  public void log(LinkedHashMap<String, String> result);
}

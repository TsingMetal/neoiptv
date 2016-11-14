package com.tsing.iptv;

public class IptvDBConnector implements DBConnector {

  public String checkSN(String sn) {
    return "valid";
  }

  public String getMac(String sn) {
    return "aaaaaaaaaaaa";
  }

  public boolean SNUsed(String sn) {
    return true;
  }

  public boolean validate(String sn) {
    return true;
  }
}

package com.tsing.iptv;

import java.io.*;
import java.net.InetAddress;

public class SFCConnector implements DBConnector {
  
  public static final String WRITE_PATH = "D:/SOURCE FOLDER/";
  public static final String READ_PATH = "D:/SFCRESULT/";

  public String checkSN(String sn) {
    return "valid";
  }

  public String getMac(String sn) {
    String filename = WRITE_PATH + sn + ".txt";
    DataOutputStream out = new DataOutputStream(
        new FileOutputStream(filename));
    out.
    return "aaaaaaaaaaaaaaaaaaaa";
  }

  public boolean SNUsed(String sn) {
    return true;
  }

  public boolean validate(String sn) {
  }
}

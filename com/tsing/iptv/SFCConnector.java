package com.tsing.iptv;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class SFCConnector implements DBConnector {
  
  public static final String WRITE_PATH = "D:/SOURCE FOLDER/";
  public static final String READ_PATH = "D:/SFCRESULT/";
  public static String IP; 

  private String sn;
  private String mac;

  public SFCConnector() {
    // get the IP required by SFC:
    try {
      Enumeration<NetworkInterface> interfaces = 
        NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface ni = interfaces.nextElement();
        Enumeration<InetAddress> addrs = ni.getInetAddresses();
        while (addrs.hasMoreElements()) {
          InetAddress addr = addrs.nextElement();
          String ip = addr.getHostAddress();
          if (ip.startsWith("172.16")) {
            IP = ip; // IP get initialized here
            System.out.println("local IP: " + IP);
            break;
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public String checkSN(String sn) {
    this.sn = sn; // sn get initialized here

    String message = sn + ";;1;OK;" + IP + ";;;";
    sendMessage(message);

    String result = readMessage();
    String[] data = result.split(";");
    mac = data[1]; // mac get initialized here

    if (result.contains("USED")) {
      return "used";
    } else if (result.contains("INVALID")) {
      return "invalid";
    } else if (result.contains("VALID")) {
      return "valid";
    } else {
      return null;
    }
  }

  @Override
  public String getMac(String sn) {
    return mac;
  }

  @Override
  public boolean SNUsed(String sn) {
    String message = sn + ";;2;OK;" + IP + ";;;";
    sendMessage(message);

    String result = readMessage();
    if (result.contains("OK")) {
      return true;
    } else {
       return false;
    }
  }

  @Override
  public boolean validate(String sn) {
    // unimplemented
    return true;
  }

  private void sendMessage(String message) {
    String outFile = WRITE_PATH + sn + ".txt";
    String batFile = WRITE_PATH + sn + ".bat";

    DataOutputStream out = null;
    DataOutputStream outToBat = null;

    try {
      out = new DataOutputStream(
          new FileOutputStream(outFile));
      out.writeUTF(message);
      outToBat = new DataOutputStream(
          new FileOutputStream(batFile));
      outToBat.writeUTF(message);

      Thread.sleep(200);
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      try {
        if (out != null) {
          out.close();
          new File(outFile).delete();
        }
        if (outToBat != null) {
          outToBat.close();
          new File(batFile).delete();
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  private String readMessage() {
    String inFile = READ_PATH + sn + ".txt";

    DataInputStream in = null;

    try {
      in = new DataInputStream(
          new FileInputStream(inFile));
      String result = in.readUTF();
      return result;
    } catch (Exception ex) {
      ex.printStackTrace();
      return "N/A";
    } finally {
      try {
        if (in != null) {
          in.close();
          new File(inFile).delete();
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}

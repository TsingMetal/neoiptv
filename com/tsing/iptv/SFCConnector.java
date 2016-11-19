package com.tsing.iptv;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class SFCConnector implements DBConnector {
  
  public static final String WRITE_PATH = "D:/SOURCE FOLDER/";
  public static final String READ_PATH = "D:/SFCRESULT/";
  public static String IP; 

  public SFCConnector() {
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
            IP = ip;
            System.out.println("local IP: " + IP);
            break;
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public String checkSN(String sn) {
    String message = sn + ";;1;OK;" + IP + ";;;";

    String outFile = WRITE_PATH + sn + ".txt";
    String batFile = WRITE_PATH + sn + ".bat";
    String result = null;

    try {
      DataOutputStream out = new DataOutputStream(
          new FileOutputStream(outFile));
      out.writeUTF(message);
      DataOutputStream outToBat = new DataOutputStream(
          new FileOutputStream(batFile));
      outToBat.writeUTF(message);
      out.close();
      outToBat.close();

      Thread.sleep(200);

      String inFile = READ_PATH + sn + ".txt";
      DataInputStream in = new DataInputStream(
          new FileInputStream(inFile));
      result = in.readUTF();

      in.close();
    } catch (Exception ex) {
      ex.printStackTrace();
      result = "N/A";
    }

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

  public String getMac(String sn) {
    String mac = null;

    try {
      String inFile = READ_PATH + sn + ".txt";
      DataInputStream in = new DataInputStream(
          new FileInputStream(inFile));
      String result = in.readUTF();
      String[] data = result.split(";");
      mac = data[1];
      in.close();
    } catch (Exception ex) {
      ex.printStackTrace();
      mac = "N/A";
    } 

    return mac;
  }

  public boolean SNUsed(String sn) {
    String message = sn + ";;2;OK;" + IP + ";;;";

    String outFile = WRITE_PATH + sn + ".txt";
    String batFile = WRITE_PATH + sn + ".bat";

    try {
      DataOutputStream out = new DataOutputStream(
          new FileOutputStream(outFile));
      out.writeUTF(message);
      DataOutputStream outToBat = new DataOutputStream(
          new FileOutputStream(batFile));
      outToBat.writeUTF(message);
      out.close();
      outToBat.close();

      Thread.sleep(200);
      new File(outFile).delete();
      new File(batFile).delete();

      String inFile = READ_PATH + sn + ".txt";
      DataInputStream in = new DataInputStream(
          new FileInputStream(inFile));
      
      String result = in.readUTF();
      if (result.contains("OK")) {
        return true;
      } else {
         return false;
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  public boolean validate(String sn) {
    return true;
  }
}

package tsing.iptv;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class SFCConnector implements DBConnector {
  
  public static final String WRITE_PATH = "D:/SOURCE FOLDER/";
  public static final String READ_PATH = "D:/SFCRESULT/";
  public static String IP; 

  private String sn;
  private String mac;

  private String outFile;
  private String batFile;
  private String inFile;

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
  public String toString() {
    return "SFC";
  }

  @Override
  public String checkSN(String sn) {
    this.sn = sn; // sn get initialized here

    String message = sn + ";1;OK;" + IP + ";;;";
    sendMessage(message);

    String result = readMessage();

    if (result.contains("USED")) {
      return "used";
    } else if (result.contains("NO FOUND")) {
      return "invalid";
    } else if (result.contains("VALID")) {
      String[] data = result.split(";");
      mac = data[3]; // mac gets initialized here
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
    String message = sn + ";2;OK;" + IP + ";;;";
    sendMessage(message);

    String result = readMessage();
    if (result.contains("2;OK")) {
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
    outFile = WRITE_PATH + sn + ".txt";
    batFile = WRITE_PATH + sn + ".bat";

    PrintWriter out = null;
    PrintWriter outToBat = null;

    try {
      out = new PrintWriter(outFile);
      out.print(message);
      outToBat = new PrintWriter(batFile);
      outToBat.print(message);
      out.close();
      outToBat.close();

      Thread.sleep(1000); // wait 1 sec for SFC to respond
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally { // delete files
      new File(outFile).delete();
      new File(batFile).delete();
    }
  }

  private String readMessage() {
    inFile = READ_PATH + sn + ".txt";

    BufferedReader in = null;

    try {
      Thread.sleep(200);
      in = new BufferedReader(new FileReader(inFile));
      String result = in.readLine();
      in.close();
      return result;
    } catch (Exception ex) {
      ex.printStackTrace();
      return "N/A";
    } finally { // delete file
      // new File(inFile).delete();
    }
  }
}

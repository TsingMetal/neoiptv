package com.tsing.iptv;

import java.net.*;
import java.util.LinkedHashMap;
import java.util.ArrayList;

import com.tsing.util.CRC16;

/**
 * compile OK : 2016/11/10 Thur 21:58:43.56
 * provide functions to:
 * 1, check Mac and SN with database and STB,
 * 2, erase Mac and SN from STB and 
 * 3, write Mac and SN to STB and 
 * 4, others
 * all FAIL information will be kept by Logger;
 * and Logger only concerns WRITE PASS infomation 
 * among other pass infomations
 * ALMOST ALL test information(pass or fail) will keep UI updated
 * @author Tsing
 */ 
public class MacWriter {
  boolean repairMode = false; // default MP mode;

  public static final int STBPORT = 1300; // STB port
  public static final int RECVPORT = 1301; // local port for receiving data
  private InetAddress ADDR;
  private DatagramSocket socket;
  private XmlParser xmlParser; // xmlParser is responsible for parsing cmd xml
  private DBConnector dbConnector;
  private ArrayList<MacWritingListener> listenerList;

  /**
   * initialize socket;
   * pass a XmlParser instance of XmlParser to the constructor 
   * so this class could share it with others
   * @param parser XmlParser
   * @param  connector DBConnector
   */
  public MacWriter(XmlParser parser, DBConnector connector) {
    xmlParser = parser;
    dbConnector = connector;
    try {
      ADDR = InetAddress.getByName("225.0.0.1"); // broadcast address
      socket = new DatagramSocket(1301); // use UDP socket; bind port 1301
      socket.setSoTimeout(1000); // set time for timeout as 1 sec;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * change mode if respair mode needed;
   * @param mode boolean
   */
  public void setRepairMode(boolean bool) {
    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    repairMode = bool;
    result.put("cmd", "set_mode");
    result.put("status", "repairMode=" + new Boolean(bool).toString());
    processEvent(new MacWritingEvent(this, result));
  }

  /** return mode to check if in repair or MP */
  public boolean isRepairMode() {
    return repairMode;
  }

  /**
   * check advanced sercurity before testing
   */
  public boolean checkAdv() {
    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		result.put("cmd", "check_adv_security");
		
		String cmdXml = CmdXml.CHECK_ADV_XML;
		String retXml = getRet(cmdXml); // send request to stb and get result
		xmlParser.parse(retXml);  // parse result returned from stb;
		String advSecurity = xmlParser.getValue("adv_security"); // get value
		if (advSecurity.equals("enable")) {
			result.put("status", "pass");
			processEvent(new MacWritingEvent(this, result));
			return true;
		} else {
			result.put("status", "fail");
			processEvent(new MacWritingEvent(this, result));
			return false;
		}
  } ///^ untested
  
	/** enable advanced security function of stb */
	public boolean setAdv() {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		result.put("cmd", "enable_adv_security");

		String cmdXml = CmdXml.SET_ADV_XML;
		String retXml = getRet(cmdXml); // send cmd and get returned data
		xmlParser.parse(retXml); // parse result returned from stb;
		String status = xmlParser.getValue("result");
		if (status.equals("ok")) {
			result.put("status", "pass");
			processEvent(new MacWritingEvent(this, result));
			return true;
		} else { // if status.equals("failure")
			result.put("status", "fail");
			result.put("err_info", xmlParser.getValue("error"));
			processEvent(new MacWritingEvent(this, result));
			return false;
		}
	} ///^ untested

  /** 
   * compare Mac and SN with DB; 
   * to check if both and crc of each are validate;
   * return true if all of those are validate, false if not;
	 * @param sn String
   */
  public boolean checkSN(String sn) { 
    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		result.put("cmd", "check_sn");
    result.put("sn", sn);
    String status = dbConnector.checkSN(sn);

    if (status.equals("used")) {
      result.put("status", "used"); // UI to show "skipped"
      processEvent(new MacWritingEvent(this, result));
      return false;
    } else if (status.equals("invalid")) {
      result.put("status", "invalid");
      processEvent(new MacWritingEvent(this, result));
      return false;
    } else { // status == "valid"
      result.put("status", "pass");
      processEvent(new MacWritingEvent(this, result));
      return true;
		}
  } ///^ untested

  /**
   * erase Mac and SN from STB;
   * if successfully erased, return true, else return false
   */
  public boolean eraseMac() {
    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    result.put("cmd", "erase_mac");

    //---- in repair mode
    if (repairMode) {
      System.out.println("repairMode");
      String sn = getSN();

      if (sn == null) {
        result.put("status", "fail");
        processEvent(new MacWritingEvent(this, result));
        return false;
      }

      result.put("sn", sn);
      if (dbConnector.checkSN(sn).equals("used")) {
        // inform DB to recircle mac mapping this sn 
        if (!dbConnector.validate("sn"));
        result.put("status", "fail"); // UI show "fail" if not recircled
        processEvent(new MacWritingEvent(this, result));
        return false;
      }
    }
    //----

		// erase even not successfully recircled; need to solve
		String cmdXml = CmdXml.ERASE_XML;
		String retXml = getRet(cmdXml);
    if (retXml == null) {
      result.put("status", "fail");
      processEvent(new MacWritingEvent(this, result));
      return false;
    }

		xmlParser.parse(retXml);
		String status = xmlParser.getValue("result");
		if (status.equals("ok")) {
			result.put("status", "pass");
			processEvent(new MacWritingEvent(this, result));
			return true;
		} else {
			result.put("status", "fail");
			processEvent(new MacWritingEvent(this, result));
			return false;
		}
	} ///^ untested
  
  /**
   * get Mac and SN from STB;
   * needed before erase mac and sn from STB
   */
  public String getSN() { 
    // unimplemented
    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		result.put("cmd", "get_sn_from_stb");

		String cmdXml = CmdXml.GET_XML;
		String retXml = getRet(cmdXml);
    
    if (retXml == null) {
      result.put("status", "fail");
      processEvent(new MacWritingEvent(this, result));
      return null;
    }

		xmlParser.parse(retXml);
		if (xmlParser.getValue("sn") != null && 
				xmlParser.getValue("mac") != null) {
			String sn = xmlParser.getValue("sn");
			result.put("status", "pass");
			processEvent(new MacWritingEvent(this, result));
      return sn;
		} else {
			result.put("status", "fail");
			processEvent(new MacWritingEvent(this, result));
			return null;
		}
	} ///^ untested

  /**
   * write Mac and SN to STB;
   * Mac will be Checked before writing
	 * THE MOST IMPORTANT PART OF THE WHOLE TEST;
   * return true if writing successfully;
	 * arguments are passed in through UI's JTextField using barcode scanner;
	 * @param mac String
	 * @param sn String
   */
  public boolean setMac(String mac, String sn) {
    // unimplemented
    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		result.put("cmd", "write_mac_to_stb");

		String macCRC = getCRC(mac);
		String snCRC = getCRC(sn);

    String cmdXml = String.format(CmdXml.SET_XML, mac, macCRC, sn, snCRC);
    String retXml = getRet(cmdXml);

    if (retXml == null) {
      result.put("status", "fail");
      processEvent(new MacWritingEvent(this, result));
      return false;
    }

    xmlParser.parse(retXml);

    if (xmlParser.getValue("result").equals("ok")) {
      result.put("status", "write_pass");
      result.put("mac", mac);
      result.put("mac_crc", macCRC);
      result.put("sn", sn);
      result.put("sn_crc", snCRC);
      processEvent(new MacWritingEvent(this, result));

      // inform DB the sn has been successfully used:
      if (dbConnector.SNUsed(sn)) {
        result.put("status", "pass");
        processEvent(new MacWritingEvent(this, result));
        //rebootSTB();
				return true;
			} else {
				result.put("status", "update_db_fail");
				processEvent(new MacWritingEvent(this, result));
				return false;
			}
		} else {
      result.put("status", "write_fail");
      processEvent(new MacWritingEvent(this, result));
      return false;
    }
  }

  /**
   * reboot STB 
   */
  public boolean rebootSTB() {
    // unimplemented
    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    processEvent(new MacWritingEvent(this, result));
    return true;
  }

  /**
   * handle sending XML String cmd and
   * receicing XML string result from STB;
   * return XML string received from STB if query OK, 
   * else return warning message
   */
  public String getRet(String cmdXml) {
    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // sapces ares intended here and will be trimed when logging:
    result.put("cmd", "   connect_to_stb");
    int retry = 0; //record retry times;

    DatagramPacket dp = new DatagramPacket(cmdXml.getBytes(),
       cmdXml.length(), ADDR, STBPORT);
    for (int i = 0; i < 5; i++) { // if fails, retry 5 times
      try {
        socket.send(dp); // send cmd xml to STB by multicasting
        System.out.println("dp sent"); /* for debugging */

        // get result from STB:
        byte[] buff = new byte[1024];
        DatagramPacket recvDp = new DatagramPacket(buff, 1024);
        socket.receive(recvDp);
        String ret = new String(buff);
        result.put("ret_xml", ret);
        result.put("status", "pass");
        processEvent(new MacWritingEvent(this, result));
        return ret;
      } catch (Exception ex) {
        ex.printStackTrace();
        retry += 1;
        System.out.println("retry+" + retry); // for debugging
        result.put("status", "retry+"+new Integer(retry).toString());
        processEvent(new MacWritingEvent(this, result));
      }
    } 

    result.put("status", "fail");
    processEvent(new MacWritingEvent(this, result));
    return null;
  }///~ tested OK; date: Wed  2 Nov 08:40:17 CST 2016

	/** get a string's crc */
	public String getCRC(String str) { 
		return CRC16.getCRC(str);
	}

  /** add a listener */
  public void addMacWritingListener(MacWritingListener listener) {
    if (listenerList == null)
      listenerList = 
        new ArrayList<MacWritingListener>(3); // allow 3 listeners
    listenerList.add(listener);
  }
  
  /** fire Event */
  private void processEvent(MacWritingEvent e) {
    ArrayList<MacWritingListener> listeners;

    synchronized (this) {
      if (listenerList == null) return;
      listeners = (ArrayList<MacWritingListener>)listenerList.clone();
    }

    for (MacWritingListener listener : listeners)
      listener.macWritingPerformed(e);
  }
}

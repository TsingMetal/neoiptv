package com.tsing.util;

import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.Native;

public class CRC16 {

	public interface Iptv_CRC16 extends StdCallLibrary {
		Iptv_CRC16 INSTANCE = 
			(Iptv_CRC16)Native.loadLibrary("IPTV_CRC16", Iptv_CRC16.class);

		public int CRC16(byte[] data, int length);
	}

  public static String getCRC(String str) {
    byte[] data = str.getBytes();
    int num = Iptv_CRC16.INSTANCE.CRC16(data, data.length);
    return Integer.toHexString(num);
  }

	public static void main(String[] args) {
		byte[] data = "Hello World!".getBytes();
		int num = Iptv_CRC16.INSTANCE.CRC16(data, data.length);
		System.out.println(num);
		System.out.println(Integer.toHexString(num));
	}
}

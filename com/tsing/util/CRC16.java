package com.tsing.util;

public class CRC16 {
  public static int crc16(byte[] data, int length) {
    int reg_crc = 0x0;
    int s_crcchk = 0;

    int i = 0;
    while (length-- > 0) {
      reg_crc ^= data[i++];

      for (s_crcchk = 0; s_crcchk < 8; s_crcchk++) {
        if ((reg_crc & 0x01) > 0) {
          reg_crc = (reg_crc >> 1) ^ 0xa001;
        } else {
          reg_crc = reg_crc >> 1;
        }
      }
    }
    
    return reg_crc;
  }

  public static String crc16(String str) {
    byte[] data = str.getBytes();
    int num = crc16(data, data.length);
    return Integer.toHexString(num);
  }
}

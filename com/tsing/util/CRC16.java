public class CRC16 {
  public int crc16(byte[] data, int length) {
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

  public static void main(String[] args) {
    CRC16 crc = new CRC16();
    String str = "abcdefg";
    byte[] data = str.getBytes();
    int num = crc.crc16(data, data.length);
    System.out.println(Integer.toHexString(num));
  }
}

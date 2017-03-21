package com.tsing.iptv;

public interface CmdXml {

  public static String SET_XML = 
    "<?xml version='1.0' encoding='UTF-8'?>" +  
    "<msg>" + 
    "<pwd>ff2234asdxcn2s34</pwd>" + 
    "<cmd>set</cmd>" +
    "<mac>%s</mac>" + 
    "<mac_crc>%s</mac_crc>" + 
    "<sn>%s</sn>" + 
    "<sn_crc>%s</sn_crc>" +
    "<hdcp_key>f3685aad885b0700fa288e2b0c13a59aee41e607f7155209dcae0e23638570f072376c5f0a47c4ef11ab87039feb112410713ca727" + 
"a2b66a4c0fc34a9e7307733c6e66c70bff5f55be1d910d2654a307cd2a972c31e1a000d90d91536a581572025800d4d2d059e5fb400636186295" +
"58e0ccc9973efd8fef5b7ed4526ddc1db289de2bc6ee834b3423c94572a8fc86b19b99486d2885ebe63c9cbf7649350b52d78013b29851a934e0" +
"995fd0c81731ff5b8a44ce58d72fbaea4cb63647f3439ab5604c69cf471fd0ba42c6041049996768d5247a955d79f8fcc6482bff667678920ea3" +
"2b0832afdfefc4a21ff947288267b45b6ac161d1c89a4dc23c61b810fa4ca0fc15ebc6fa7fae4158faeb6913475d0b301bc0198b1e66adc9ac32" +
"b67254a3000000000000000000000089460000</hdcp_key>" +
    "<hdcp_key_crc>be0d</hdcp_key_crc>" + 
    "<license>00123445555</license>" +
    "<license_crc>0012</license_crc>" + 
    "<sender>pc</sender>" +
    "</msg>";

  public static String GET_XML = 
    "<?xml version='1.0' encoding='UTF-8'?>" +
    "<msg>" +
    "<pwd>ff2234asdxcn2s34</pwd>" +
    "<cmd>get</cmd>" +
    "<list>sn,mac </list>" +
    "<sender>pc</sender>" +
    "</msg>"; 
  
  public static String ERASE_XML = 
    "<?xml version='1.0' encoding='UTF-8'?>" +
    "<msg>" +
    "<pwd>ff2234asdxcn2s34</pwd>" +
    "<cmd>set</cmd>" +
    "<mac></mac>" +
    "<sn></sn>" +
    "</msg>"; 

  public static String REBOOT_XML = 
    "<?xml version='1.0' encoding='UTF-8'?>" +
    "<msg>" +
    "<pwd>ff2234asdxcn2s34</pwd>" +
    "<cmd>set</cmd>" +
    "<stb>reboot</stb>" +
    "</msg>"; 
    
  public static String CHECK_ADV_XML = 
    "<?xml version='1.0' encoding='UTF-8'?>" +
    "<msg>" +
    "<pwd>ff2234asdxcn2s34</pwd>" +
    "<cmd>get</cmd>" +
    "<list>adv_security</list>" +
    "</msg>"; 

  public static String SET_ADV_XML = 
    "<?xml version='1.0' encoding='UTF-8'?>" +
    "<msg>" +
    "<pwd>ff2234asdxcn2s34</pwd>" +
    "<cmd>set</cmd>" +
    "<adv_security>enable</adv_security>" +
    "<adv_security_crc>0123</adv_security_crc> " +
    "</msg>"; 
}

package tsing.iptv;

public interface DBConnector {

  public String checkSN(String sn);

  public String getMac(String sn);

  public boolean SNUsed(String sn);

  public boolean validate(String sn);
}

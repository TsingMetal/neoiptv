package com.tsing.iptv;

import java.sql.*;

public class IptvDBConnector implements DBConnector {
  private Statement statement;

  public IptvDBConnector() {
    try {
      Class.forName("com.mysql.jdbc.Driver");
    
      Connection connection = DriverManager.getConnection(
          "jdbc:mysql://localhost/iptv", "scott", "tiger");

      statement = connection.createStatement();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  
  @Override
  public String toString() {
    return "LOCAL_DB";
  }

  @Override
  public String checkSN(String sn) {
    String queryString =
      "select status from SnData where sn = '" + sn + "'";

    try {
      ResultSet result = statement.executeQuery(queryString);

      if (result.next()) { 
        String status = result.getString(1);
        return status;
      } else {
        return "invalid";
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public String getMac(String sn) {
    String queryString =
      "select mac from SnData where sn = '" + sn + "'";

    try {
      ResultSet result = statement.executeQuery(queryString);

      if (result.next()) {
        String mac = result.getString(1);
        return mac;
      } else {
        return "N/A";
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public boolean SNUsed(String sn) {
    String updateString = 
      "update SnData set status = 'used' where sn = '" + sn + "'";

    try {
      statement.executeUpdate(updateString);
      return true;
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    
    return false;
  }

  public boolean validate(String sn) {
    String updateString = 
      "update SnData set status = 'valid' where sn = '" + sn + "'";

    try {
      statement.executeUpdate(updateString);
      return true;
    } catch (SQLException ex) {
      ex.printStackTrace();
    }

    return false;
  }
}

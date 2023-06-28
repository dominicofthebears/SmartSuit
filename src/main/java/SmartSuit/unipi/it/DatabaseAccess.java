package SmartSuit.unipi.it;

import java.sql.*;
import java.time.Instant;
import java.util.HashMap;

public class DatabaseAccess {
    private static final String url = "jdbc:mysql://localhost:3306/smartsuit";
    private static final String username = "root";
    private static final String password = "ubuntu";

    public static int updateActuators(String address, String actuatorType, String status) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement("REPLACE INTO actuators (ip, actuator_type, status) VALUES(?,?,?);");
        ps.setString(1, address.substring(1)); //substring(1)
        ps.setString(2, actuatorType);
        ps.setString(3, status);
        ps.executeUpdate();
        return ps.getUpdateCount();
    }

    public static HashMap<String, String> retrieveActuator(String actuatorType) throws SQLException {
        HashMap<String, String> result = new HashMap<>();
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement("SELECT ip, status FROM actuators WHERE actuator_type = ?");
        ps.setString(1, actuatorType);
        ResultSet rs = ps.executeQuery();
        if(!rs.next()){
            return result; //empty at this point
        }else {

            result.put("ip", rs.getString("ip"));
            result.put("status", rs.getString("status"));
            rs.close();

            return result;
        }
    }

    public static int insertData(Long value, String type) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement("INSERT INTO data (value, sensor) VALUES(?,?);");

        ps.setString(1, String.valueOf(value)); //substring(1)
        ps.setString(2, type);
        //ps.setString(3, String.valueOf(Instant.now()));
        ps.execute();
        //System.out.println(ps.getUpdateCount());
        return ps.getUpdateCount();
    }

    public static HashMap<String, Integer> retrieveData() throws SQLException{
        HashMap<String, Integer> result = new HashMap<>();
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement("SELECT sensor, value  FROM data WHERE (sensor, timestamp) IN (SELECT sensor, MAX(timestamp) FROM data GROUP BY sensor)");
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
               result.put(rs.getString("sensor"), rs.getInt("value"));
        }
        rs.close();
        //System.out.println(result);
        return result;
    }
    public static void resetActuators() throws SQLException{
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement("DELETE from actuators");
        ps.executeUpdate();
    }
}
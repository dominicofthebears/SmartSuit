package SmartSuit.unipi.it;

import java.sql.*;
import java.time.Instant;
import java.util.HashMap;

public class DatabaseAccess {
    private static final String url = "jdbc:mysql://localhost:3306/SmartSuit";
    private static final String username = "root";
    private static final String password = "smartsuit";

    public static int updateCollectors(String address, String actuatorType) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement("REPLACE INTO actuators (ip, actuator_type) VALUES(?,?);");
        ps.setString(1, address); //substring(1)
        ps.setString(2, actuatorType);
        ps.executeUpdate();
        return ps.getUpdateCount();
    }

    public static String retrieveActuator(String actuatorType) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement("SELECT ip FROM actuators WHERE actuator_type = ?");
        ps.setString(1, actuatorType);
        ResultSet rs = ps.executeQuery();
        rs.next();
        String result = rs.getString("ip");
        rs.close();
        return result;
    }

    public static int insertData(Integer value, String type) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement("INSERT INTO data (value, sensor, timestamp) VALUES(?,?,?);");
        ps.setString(1, String.valueOf(value)); //substring(1)
        ps.setString(2, type);
        ps.setString(3, String.valueOf(Instant.now()));
        ps.executeUpdate();
        return ps.getUpdateCount();
    }

    public static HashMap<String, Integer> retrieveData() throws SQLException{
        HashMap<String, Integer> result = new HashMap<>();
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement("SELECT value, sensor, MAX(timestamp) FROM data GROUP BY sensor");
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
               result.put(rs.getString("sensor"), rs.getInt("value"));
        }
        rs.close();
        return result;
    }
}
package SmartSuit.unipi.it.cloudApplication;


import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;

public class SQLConnector {
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

    public static int insertData(Float value, String type) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement("INSERT INTO data (value, sensor, timestamp) VALUES(?,?,?);");
        ps.setString(1, String.valueOf(value)); //substring(1)
        ps.setString(2, type);
        ps.setString(3, String.valueOf(Instant.now()));
        ps.executeUpdate();
        return ps.getUpdateCount();
    }
}

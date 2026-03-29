package com.w2a.APITestingFramework.utilities;

import com.w2a.APITestingFramework.setUp.BaseTest;

public class TestConfig {

    // MAIL SERVER DETAILS
    public static String server = "smtp.gmail.com";
    
    // We use BaseTest.getProperty to allow Jenkins to override these at runtime
    public static String from = getSetting("from", "corporate@way2automation.com");
    public static String password = getSetting("password", "Selenium@123");
    
    public static String[] to = {"ssathish1992@gmail.com"};
    public static String subject = "Extent Project Report";
    public static String messageBody = "TestMessage";
    
    // SQL DATABASE DETAILS
    public static String driver = "net.sourceforge.jtds.jdbc.Driver"; 
    public static String dbConnectionUrl = "jdbc:jtds:sqlserver://192.101.44.22;DatabaseName=monitor_eval"; 
    public static String dbUserName = "sa"; 
    public static String dbPassword = "$ql$!!1"; 
    
    // MYSQL DATABASE DETAILS
    public static String mysqldriver = "com.mysql.jdbc.Driver";
    public static String mysqluserName = "root";
    public static String mysqlpassword = "selenium";
    public static String mysqlurl = "jdbc:mysql://localhost:3306/acs";

    /**
     * Helper method to check System properties (Jenkins) first, 
     * then fall back to the hardcoded default.
     */
    private static String getSetting(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null || value.isEmpty() || value.contains("${")) {
            return defaultValue;
        }
        return value;
    }
}
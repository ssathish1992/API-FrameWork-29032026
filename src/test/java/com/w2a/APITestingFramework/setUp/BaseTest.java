package com.w2a.APITestingFramework.setUp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import io.restassured.RestAssured;
import com.w2a.APITestingFramework.utilities.ExcelReader;
import com.w2a.APITestingFramework.utilities.MonitoringMail;
import com.w2a.APITestingFramework.utilities.TestConfig;

public class BaseTest {
    // Public so other classes (like your API classes) can access the loaded keys
    public static Properties config = new Properties();
    private FileInputStream fis; 
    public static ExcelReader excel = new ExcelReader(".\\src\\test\\resources\\excel\\testdata.xlsx");

    @BeforeSuite
    public void setUp() {
        try {
            // 1. Load the physical config.properties file from your project
            fis = new FileInputStream(".\\src\\test\\resources\\properties\\config.properties");
            config.load(fis);
        } catch (IOException e) {
            System.out.println("Error loading config file: " + e.getMessage());
        }

        // 2. Overwrite file values with Jenkins/System properties if they exist
        updateConfigWithSystemProperties();

        // 3. Initialize RestAssured with the FINAL values
        RestAssured.baseURI = config.getProperty("baseURI");
        RestAssured.basePath = config.getProperty("basePath");
        
        // Optional Diagnostic: This will show in Jenkins log if the key was actually replaced
        if (config.getProperty("validSecretKey").contains("$") || config.getProperty("validSecretKey").contains("%")) {
            System.out.println("WARNING: Secret Key still contains placeholder symbols!");
        }
    }

    /**
     * This method checks every key in your config.properties.
     * If Maven/Jenkins passed a value using -D (System Property), 
     * it replaces the placeholder in memory.
     */
    private void updateConfigWithSystemProperties() {
        for (String key : config.stringPropertyNames()) {
            String systemValue = System.getProperty(key);
            
            // Only update if Jenkins actually sent a value that isn't empty
            if (systemValue != null && !systemValue.isEmpty() && !systemValue.contains("${") && !systemValue.contains("%")) {
                config.setProperty(key, systemValue);
            }
        }
    }

    @AfterSuite
    public void tearDown() {
        System.out.println("Initiating post-execution report email...");
        MonitoringMail mail = new MonitoringMail();
        
        try {
            mail.sendMail(
                TestConfig.server, 
                TestConfig.from, 
                TestConfig.to, 
                TestConfig.subject, 
                TestConfig.messageBody
            );
        } catch (Exception e) {
            System.out.println("Automation Error: Could not send report email.");
            e.printStackTrace();
        }
    }
}
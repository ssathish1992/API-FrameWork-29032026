package com.w2a.APITestingFramework.setUp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import io.restassured.RestAssured;
import com.w2a.APITestingFramework.utilities.ExcelReader;

public class BaseTest {
//All initialization will be in this class, and this will be the heart of the framework.
	public static Properties config = new Properties();
	private FileInputStream fis; // Private because this will be accessed to only this class.
	public static ExcelReader excel = new ExcelReader(".\\src\\test\\resources\\excel\\testdata.xlsx");

	@BeforeSuite
	public void setUp() {

		try {
			fis = new FileInputStream(".\\src\\test\\resources\\properties\\config.properties");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			config.load(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RestAssured.baseURI = config.getProperty("baseURI");
		 RestAssured.basePath=config.getProperty("basePath");
	}

	@AfterSuite
	public void tearDown() {

	}

}

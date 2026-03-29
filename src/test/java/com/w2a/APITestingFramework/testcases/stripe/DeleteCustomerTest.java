package com.w2a.APITestingFramework.testcases.stripe;

import static io.restassured.RestAssured.given;

import java.util.Hashtable;

import org.apache.commons.collections4.bag.SynchronizedSortedBag;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.w2a.APITestingFramework.APIs.stripe.CreateCustomerAPI;
import com.w2a.APITestingFramework.APIs.stripe.DeleteCustomerAPI;
import com.w2a.APITestingFramework.listeners.ExtentListeners;
import com.w2a.APITestingFramework.setUp.BaseTest;
import com.w2a.APITestingFramework.utilities.DataUtil;
import com.w2a.APITestingFramework.utilities.TestUtil;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class DeleteCustomerTest extends BaseTest {

	@Test(dataProviderClass = DataUtil.class, dataProvider = "data")
	public void deleteCustomer(Hashtable<String, String> data) {

		Response response = DeleteCustomerAPI.sendDeleteRequestToDeleteCustomerAPIWithValidID(data);
		response.prettyPrint();
		ExtentListeners.testReport.get().info(data.toString());

		System.out.println(response.statusCode());

		// ===================================================================================
		// METHOD 1: Direct Extraction using JsonPath
		// ===================================================================================
		/*
		 * DESCRIPTION: Uses RestAssured's built-in JsonPath to extract a specific field value.
		 * KEY ACTIONS: 
		 * - Converts the extracted object to a String for comparison.
		 * - Uses Assert.assertEquals to verify the value matches the test data.
		 * BEST FOR: Quick, one-off value verifications for specific fields (e.g., ID, Name).
		 */
		/*
		String actual_id_m1 = response.jsonPath().get("id").toString();
		System.out.println("Method 1 - Getting id from Json path: " + actual_id_m1);
		Assert.assertEquals(actual_id_m1, data.get("id"), "ID not matching in Method 1");
		*/

		// ===================================================================================
		// METHOD 2: Presence Validation using JSONObject
		// ===================================================================================
		/*
		 * DESCRIPTION: Uses the org.json library to verify the structural existence of a key.
		 * KEY ACTIONS:
		 * - Parses the entire response body into a JSONObject.
		 * - Uses the .has() method to check if the key exists (returns boolean).
		 * BEST FOR: Schema validation and ensuring required keys are not missing from the response.
		 */
		/*
		JSONObject jsonObject = new JSONObject(response.asString());
		System.out.println("Method 2 - ID key presence: " + jsonObject.has("id"));
		Assert.assertTrue(jsonObject.has("id"), "ID key is not present in the JSON response (Method 2)");
		*/

		// ===================================================================================
		// METHOD 3: Reusable Utility Class (TestUtil)
		// ===================================================================================
		/*
		 * DESCRIPTION: Abstracts JSON parsing into a custom utility class (TestUtil).
		 * KEY ACTIONS:
		 * - jsonHasKey(): A static method to verify key presence without repeating code.
		 * - getJsonKeyValue(): A static method to fetch values directly as Strings.
		 * - Status Code Check: Adds a final layer of validation for the HTTP 200 OK status.
		 * BEST FOR: Scalable frameworks where you want clean, readable, and maintainable test scripts.
		 */
		System.out.println("Presence check for Object Key : " + TestUtil.jsonHasKey(response.asString(), "object"));
		System.out.println("Presence check for Deleted Key : " + TestUtil.jsonHasKey(response.asString(), "deleted"));
		Assert.assertTrue(TestUtil.jsonHasKey(response.asString(), "id"), "ID key is not present in the JSON response");

		String actual_id_m3 = TestUtil.getJsonKeyValue(response.asString(), "id");
		System.out.println("Method 3 - Extracted ID: " + actual_id_m3);
		Assert.assertEquals(actual_id_m3, data.get("id"), "ID not matching");

		System.out.println("Object key value is : " + TestUtil.getJsonKeyValue(response.asString(), "object"));
		System.out.println("Deleted key value is : " + TestUtil.getJsonKeyValue(response.asString(), "deleted"));

		// Final validation of the response status
		Assert.assertEquals(response.statusCode(), 200, "Expected success status code 200");
		

	}

}

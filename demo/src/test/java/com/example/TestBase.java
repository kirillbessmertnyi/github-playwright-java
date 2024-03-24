package com.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.json.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

public class TestBase 
{
  static Playwright playwright;
  static Browser browser;
  static String appUrl = "https://aqua-auto-aqamasterpla.aqua-testing.com/aquawebng/";
  static String bearerToken;
  static APIRequestContext request;
  static BrowserContext context;
  private TestInfo testInfo;
  Page page;

  static void getBearerToken() {
    Map<String, String> headers = new HashMap<>();
    headers.put("content-type", "application/x-www-form-urlencoded");

    request = playwright.request().newContext(new APIRequest.NewContextOptions()
      .setExtraHTTPHeaders(headers));
    APIResponse response = request.post(appUrl + "api/token", RequestOptions.create().setForm(
      FormData.create()
        .set("grant_type", "password")
        .set("username","start")
        .set("password","default")));

    assertTrue(response.ok());

    JsonObject j = new Gson().fromJson(response.text(), JsonObject.class);
    bearerToken = j.get("access_token").getAsString();
  }

  static void createTCExecution(TestInfo tcInfo){
    Map<String, String> headers = new HashMap<>();
    headers.put("authorization", "Bearer " + bearerToken);
    headers.put("content-type", "application/json");
    headers.put("accept", "application/json");
    headers.put("Connection", "keep-alive");
    

    APIRequestContext tcExecutionRequest = playwright.request().newContext(new APIRequest.NewContextOptions()
      .setExtraHTTPHeaders(headers));
      
      JSONArray finalArr = new JSONArray();
      JSONObject json = new JSONObject();

      int tcId = Integer.parseInt(tcInfo.getTags().iterator().next());

      json.put("Guid", JSONObject.NULL);
      json.put("TestCaseId", tcId);
      json.put("TestCaseName", JSONObject.NULL);
      json.put("Finalize", false);
      json.put("ValueSetName", JSONObject.NULL);
      json.put("TestScenarioInfo", JSONObject.NULL);
      
      JSONArray stepsArr = new JSONArray();
      JSONObject jsonSteps = new JSONObject();
      
      jsonSteps.put("Index", 1);
      jsonSteps.put("Name", "Step 1");
      jsonSteps.put("StepType", "Step");
      jsonSteps.put("Status", "Pass");
      jsonSteps.put("Description", JSONObject.NULL);
      jsonSteps.put("ExpectedResults", JSONObject.NULL);
      jsonSteps.put("ActualResults", JSONObject.NULL);
      jsonSteps.put("ActualResultsLastUpdatedBy", JSONObject.NULL);
      jsonSteps.put("ActualResultsLastUpdated","0001-01-01T00:00:00");

      stepsArr.put(jsonSteps);

      json.put("Steps", stepsArr);
      json.put("TestedVersion", JSONObject.NULL);
      json.put("Status", JSONObject.NULL);
      
       JSONObject execDuration = new JSONObject();
       execDuration.put("FieldValueType","TimeSpan");
       execDuration.put("Text","20 second");
       execDuration.put("Value",20);
       execDuration.put("Unit","Second");
       
      json.put("ExecutionDuration", execDuration);
      json.put("AttachedLabels", new JSONArray());
      json.put("CustomFields", new JSONArray());
      json.put("Attachments", JSONObject.NULL);
      json.put("TesterId", JSONObject.NULL);
      json.put("ExecutionDate", JSONObject.NULL);
      json.put("AttachedFiles", new JSONArray());

      finalArr.put(json);

      APIResponse tcExcutionResponse = tcExecutionRequest.post(appUrl + "api/TestExecution", RequestOptions.create().setData(finalArr.toString()));

      assertTrue(tcExcutionResponse.ok());
  }

  @BeforeAll
  static void berforeAll() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setSlowMo(50));
  }

  @AfterAll
  static void closeBrowser() {
    playwright.close();
  }

  @BeforeEach
  void createContextAndPage(TestInfo testInfo) {
    this.testInfo = testInfo;
    context = browser.newContext();
    page = context.newPage();
  }

  @AfterEach
  void closeContext() {
    getBearerToken();
    System.out.println("Aqua TC Id being executed: " + this.testInfo.getTags().iterator().next());
    createTCExecution(this.testInfo);
    context.close();
  }
}

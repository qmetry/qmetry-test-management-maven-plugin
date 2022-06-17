package com.qmetry;

//Java in-built libs
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import static java.lang.System.exit;

//Apache and json libs
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.maven.plugin.logging.Log;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Upload {
	public static List<String> fetchFiles(String filepath, String format) throws FileNotFoundException {
		String extention;
		if (format.equals("junit/xml") || format.equals("testng/xml") || format.equals("hpuft/xml") || format.equals("robot/xml"))
			extention = ".xml";
		else if (format.equals("cucumber/json"))
			extention = ".json";
		else
			return null;
		List<String> list = new ArrayList<String>();
		File file = new File(filepath);
		if (!file.exists()) {
			throw new FileNotFoundException("Cannot find file : " + file.getAbsolutePath());
		}
		File[] farray = file.listFiles();
		String path;

		if (farray != null) {
			for (File f : farray) {
				path = f.getPath();
				if (path.endsWith(extention)) {
					list.add(path);
				}
			}
			return list;
		}
		return null;
	}

	public static String uploadfile(String url, String automationkey, String filepath, String format, String automationHierarchy,
									String testsuitekey, String testsuiteName, String tsFolderPath, String platform, String cycle, String project, String release, String build,
									String testsuiteFields, String testcaseFields, String skipWarning, String isMatchingRequired, Log log) throws Exception {
		String res;

		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpPost uploadFile = new HttpPost(url + "/rest/import/createandscheduletestresults/1");

		uploadFile.addHeader("Accept", "application/json");
		uploadFile.addHeader("apiKey", automationkey);
		uploadFile.addHeader("scope", "default");

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("entityType", format, ContentType.TEXT_PLAIN);

		if (automationHierarchy != null && !automationHierarchy.isEmpty())
			builder.addTextBody("automationHierarchy", automationHierarchy, ContentType.TEXT_PLAIN);

		if (testsuitekey != null && !testsuitekey.isEmpty())
			builder.addTextBody("testsuiteId", testsuitekey, ContentType.TEXT_PLAIN);

		if (testsuiteName != null && !testsuiteName.isEmpty())
			builder.addTextBody("testsuiteName", testsuiteName, ContentType.TEXT_PLAIN);

		if (tsFolderPath != null && !tsFolderPath.isEmpty())
			builder.addTextBody("tsFolderPath", tsFolderPath, ContentType.TEXT_PLAIN);

		if (cycle != null && !cycle.isEmpty())
			builder.addTextBody("cycleID", cycle, ContentType.TEXT_PLAIN);

		if (platform != null && !platform.isEmpty())
			builder.addTextBody("platformID", platform, ContentType.TEXT_PLAIN);

		if (project != null && !project.isEmpty())
			builder.addTextBody("projectID", project, ContentType.TEXT_PLAIN);

		if (release != null && !release.isEmpty())
			builder.addTextBody("releaseID", release, ContentType.TEXT_PLAIN);

		if (build != null && !build.isEmpty())
			builder.addTextBody("buildID", build, ContentType.TEXT_PLAIN);

		if (testcaseFields != null && !testcaseFields.isEmpty())
			builder.addTextBody("testcase_fields", testcaseFields, ContentType.TEXT_PLAIN);

		if (testsuiteFields != null && !testsuiteFields.isEmpty())
			builder.addTextBody("testsuite_fields", testsuiteFields, ContentType.TEXT_PLAIN);

		if (skipWarning != null && !skipWarning.isEmpty())
			builder.addTextBody("skipWarning", skipWarning, ContentType.TEXT_PLAIN);

		if (isMatchingRequired != null && !isMatchingRequired.isEmpty())
			builder.addTextBody("is_matching_required", isMatchingRequired, ContentType.TEXT_PLAIN);

		File f = new File(filepath);
		builder.addPart("file", new FileBody(f));

		HttpEntity multipart = builder.build();
		uploadFile.setEntity(multipart);
		CloseableHttpResponse response = httpClient.execute(uploadFile);
		int code = response.getStatusLine().getStatusCode();
		if (code != 200) {
			log.info("----------Status Code:" + code + "----------");
			if (code == 400) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream content = entity.getContent();
					StringBuilder builder1 = new StringBuilder();
					Reader read = new InputStreamReader(content, StandardCharsets.UTF_8);
					BufferedReader reader = new BufferedReader(read);
					String line;
					try {
						while ((line = reader.readLine()) != null) {
							builder1.append(line);
						}
					} finally {
						reader.close();
						content.close();
					}
					log.info("Error Response-->" + builder1.toString());
				}
			}
			return "false";
		} else {

			JSONObject responsejson = getResponseObject(response.getEntity(), log);

			if (responsejson.toString().contains("requestId") && responsejson.get("requestId") != null) {
				log.info("Response-->" + responsejson.toString().replace("\\/", "/"));
				return getRequeststatus(log, automationkey, url, responsejson, httpClient);
			} else {
				return responsejson.toString().replace("\\/", "/");
			}
		}
	}

	public static JSONObject getResponseObject(HttpEntity entity, Log log) {
		if (entity != null) {
			try {
				InputStream content = entity.getContent();
				StringBuilder builder1 = new StringBuilder();
				Reader read = new InputStreamReader(content, StandardCharsets.UTF_8);
				BufferedReader reader = new BufferedReader(read);
				String line;
				try {
					while ((line = reader.readLine()) != null) {
						builder1.append(line);
					}
				} finally {
					reader.close();
					content.close();
				}
				JSONParser parser = new JSONParser();
				JSONObject responsejson = (JSONObject) parser.parse(builder1.toString());

				return responsejson;
			} catch (Exception e) {
				log.info("Something went wrong: " + e.getStackTrace());
			}
		}
		return null;
	}

	public static String getRequeststatus(Log log, String automationkey, String url, JSONObject responsejson, CloseableHttpClient httpClient) throws Exception {

		HttpGet getStatus = new HttpGet(url + "/rest/admin/status/automation/" + responsejson.get("requestId"));
		getStatus.addHeader("apiKey", automationkey);
		getStatus.addHeader("scope", "default");
		CloseableHttpResponse statusResponse = httpClient.execute(getStatus);
		int status = statusResponse.getStatusLine().getStatusCode();
		JSONObject statusObj = getResponseObject(statusResponse.getEntity(), log);

		if (status != 200) {
			log.info("Couldn't get request details.");
			log.info("Status Code : " + status);
			return responsejson.toString().replace("\\/", "/");
		}
		if (statusObj.get("status").toString().equals("In Progress")) {
			return getRequeststatus(log, automationkey, url, responsejson, httpClient);
		}
		//If status is in Queue call API for 10 mins
		if(statusObj.get("status").toString().equals("In Queue")){
			log.info("Response-->"+statusObj.toString().replace("\\/", "/"));
			//Calling RequestAgain method
			RequestAgain(log, automationkey, url, responsejson, httpClient);
			//Exit after successful build
			exit(0);
		}
		return statusObj.toString().replace("\\/", "/");
	}

	//Request called when Status is in queue
	public static void RequestAgain(Log log, String automationkey, String url, JSONObject responsejson, CloseableHttpClient httpClient) throws IOException {
		//Http Get API call
		HttpGet getStatus = new HttpGet(url + "/rest/admin/status/automation/" + responsejson.get("requestId"));
		getStatus.addHeader("apiKey", automationkey);
		getStatus.addHeader("scope", "default");
		//Timer function for all API 10 mins
		long start = System.currentTimeMillis(); //start time
		long end = start + 10 * 60 * 1000; // 10 mins (60*1000 = 1 min | 1*10 = 10 mins)

		//Loop to start timer
		while (System.currentTimeMillis() < end) {
			//Executing API
			CloseableHttpResponse statusResponse = httpClient.execute(getStatus);
			//Get status Object
			JSONObject statusObj = getResponseObject(statusResponse.getEntity(), log);
			if(statusObj.get("status").toString().equals("In Progress")) {
				log.info("Response-->" + statusObj.toString().replace("\\/", "/"));
				continue;
			}
			//Stop loop if status is Completed or Failed and exit
			if (statusObj.get("status").toString().equals("Completed") || statusObj.get("status").toString().equals("Failed")) {
				log.info("Response-->" + statusObj.toString().replace("\\/", "/"));
				break;
			}
		}

	}
}
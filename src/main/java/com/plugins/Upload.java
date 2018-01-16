package com.plugins;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;

import java.net.ProtocolException;
import org.apache.http.HttpEntity;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.EntityUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import javax.xml.bind.DatatypeConverter;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Upload
{
	public static Map<String,String> getInfo(String url,String apikey,String platformname) throws IOException,ParseException
	{
		Map<String,String> map=new HashMap<String,String>();  
		CloseableHttpClient httpClient = HttpClients.createDefault();
			
		HttpGet get=new HttpGet(url+"/rest/admin/project/getinfo");
			
		get.addHeader("Accept","application/json");
		get.addHeader("apiKey",apikey);
		get.addHeader("scope","default");
			
		CloseableHttpResponse response = httpClient.execute(get);
			
		String resp = EntityUtils.toString(response.getEntity());
		map.put("resp",resp);
			
		httpClient.close();
			
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(resp);
			
		Long currentReleaseId=(Long)obj.get("currentReleaseId");
		//if we can't get info from rest call
		if(currentReleaseId==null)
		{
			return null;
		}
		map.put("currentReleaseId",currentReleaseId.toString());
			
		Long currentBuildId=(Long)obj.get("currentBuildId");
		map.put("currentBuildId",currentBuildId.toString());
			
		Long currentProjectId=(Long)obj.get("currentProjectId");
		map.put("currentProjectId",currentProjectId.toString());
		
		//get currrent userid
		JSONObject currentUser=(JSONObject)obj.get("currentUser");
		Long userId=(Long)currentUser.get("id");
		map.put("userId",userId.toString());
			
		//to get root folder id of testsuite
		JSONObject rootFolders = (JSONObject)obj.get("rootFolders");
		JSONObject ts=(JSONObject)rootFolders.get("TS");
		Long rootTsFolderid=(Long)ts.get("id");
		map.put("rootTsFolderid",rootTsFolderid.toString());
	
		
		//to get platformId
		if(platformname==null || platformname.isEmpty())
		{
			JSONObject views=(JSONObject)obj.get("views");
			JSONObject ts1=(JSONObject)views.get("TS");
			JSONObject tsplatform=(JSONObject)ts1.get("TsPlatform");
			Long platformId=(Long)tsplatform.get("id");
			map.put("platformId",platformId.toString());
		}
		else
		{
			boolean flag=false;
			JSONObject projectPlatforms=(JSONObject)obj.get("projectPlatforms");
			Set keys = projectPlatforms.keySet();
			Iterator a = keys.iterator();
			String key="",value="";
			while(a.hasNext()) 
			{
				key = (String)a.next();
				value = (String)projectPlatforms.get(key);
				if(value.equals(platformname))
				{
					map.put("platformId",key);
					flag=true;
					break;
				}
			}
			if(flag==false)
			{
				String platformId=createPlatform(url,apikey,platformname,currentProjectId.toString()+":"+currentReleaseId.toString()+":"+currentBuildId.toString());
				map.put("platformId",platformId);
			}
		}
		return map;
	}

	public static Map<String,String> createTestsuite(String url,String apikey,String testRunName,String testsuitedescription,String scope,String rootTsFolderid,String format,String projectpath,String filepath,String userId) throws IOException,ParseException
	{
		Map<String,String> map=new HashMap<String,String>();
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
			
		HttpPost post=new HttpPost(url+"/rest/testsuites");
			
		post.addHeader("Accept","application/json");
		post.addHeader("scope",scope);
		post.addHeader("apiKey",apikey);
		post.addHeader("Content-Type","application/json");
			
		JSONObject jsonbody=new JSONObject();
		jsonbody.put("parentFolderId",rootTsFolderid);
		jsonbody.put("name",testRunName);
		if(testsuitedescription!=null)
		{
			jsonbody.put("description",testsuitedescription);
		}
		jsonbody.put("isAutomatedFlag",true);
		jsonbody.put("buildFramework","MAVEN");
		jsonbody.put("frameWork",format);
		jsonbody.put("projectHomeDirectory",projectpath);
		jsonbody.put("projectPath",filepath);
		jsonbody.put("owner",Long.parseLong(userId));
			
		String body=jsonbody.toString();
	   
		post.setEntity(new StringEntity(body));
			
		CloseableHttpResponse response = httpClient.execute(post);
			
		String resp = EntityUtils.toString(response.getEntity());
			
		httpClient.close();
			
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(resp);
		
		JSONArray data=(JSONArray)obj.get("data");
		JSONObject testsuiteobj=(JSONObject)data.get(0);
		
		Long id=(Long)testsuiteobj.get("id");
		String key=(String)testsuiteobj.get("entityKey");
		
		map.put("testsuiteId",id.toString());
		map.put("testsuitekey",key);
		
		return map;
	}
		
	public static String mapReleaseCycle(String url,String apikey,String scope,String testsuiteId,String currentBuildId,String currentReleaseId) throws IOException
	{
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpPost post=new HttpPost(url+"/rest/testsuites/mapReleaseCycle");
		
		post.addHeader("Accept","application/json");
		post.addHeader("scope",scope);
		post.addHeader("apiKey",apikey);
		post.addHeader("Content-Type","application/json");
		
		JSONObject jsonbody=new JSONObject();
		JSONArray jarray=new JSONArray();
		JSONObject data=new JSONObject();
		data.put("tsId",Long.parseLong(testsuiteId));
		data.put("buildID",Long.parseLong(currentBuildId));
		data.put("releaseId",Long.parseLong(currentReleaseId));
		jarray.add(data);
		jsonbody.put("data",jarray);
		
		String body=jsonbody.toString();
		
		post.setEntity(new StringEntity(body));
			
		CloseableHttpResponse response = httpClient.execute(post);
			
		String resp = EntityUtils.toString(response.getEntity());
			
		httpClient.close();
		
		return resp;
	}
	
	public static String getAutomationApiKey(String url,String apikey,String scope) throws IOException,ParseException
	{
		String resp;
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpGet get=new HttpGet(url+"/rest/admin/user/viewAutomationAPIKey");
			
		get.addHeader("Accept","application/json");
		get.addHeader("Content-Type","application/json");
		get.addHeader("scope",scope);
		get.addHeader("apiKey",apikey);
		
		CloseableHttpResponse response = httpClient.execute(get);
		resp = EntityUtils.toString(response.getEntity());
		httpClient.close();
			
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(resp);
			
		String automationkey=(String)obj.get("automationAPIKey");
		
		return "automationkey";
	}
	
	public static List<String> fetchFiles(String filepath)
	{
		String extention;
		if(filepath.endsWith(".xml"))
			extention=".xml";
		else
			extention=".json";
		
		List<String> list=new ArrayList<String>();
		File file=new File(filepath);
		File[] farray=file.getParentFile().listFiles();
		String path;
		
		for(File f:farray)
		{
			path=f.getPath();
			if(path.endsWith(extention))
			{
				list.add(path);
			}
		}
		return list;
	}
	
	public static String createPlatform(String url,String apikey,String platformname,String scope) throws IOException,ParseException,UnsupportedEncodingException
	{
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpPost post=new HttpPost(url+"/rest/admin/platform");
		
		post.addHeader("Accept","application/json");
		post.addHeader("Content-Type","application/json");
		post.addHeader("scope",scope);
		post.addHeader("apiKey",apikey);
		
		JSONObject jsonbody=new JSONObject();
		jsonbody.put("name",platformname);
		jsonbody.put("platformScopeCombo","project");
		jsonbody.put("platformType","single");
		jsonbody.put("groupID",0);
		jsonbody.put("platformID",0);
		
		String body=jsonbody.toString();
		
		post.setEntity(new StringEntity(body));
		
		CloseableHttpResponse response = httpClient.execute(post);
			
		String resp = EntityUtils.toString(response.getEntity());
			
		httpClient.close();
			
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(resp);
		
		Long platformId=(Long)obj.get("platformID");
		
		return platformId.toString();
	}
	
	public static String uploadfile(String url,String automationkey,String filepath,String format,String testsuiteId,String scope,String currentBuildId,String platformId) throws IOException
	{
		String res;
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
			
		HttpPost uploadFile=new HttpPost(url+"/rest/import/createandscheduletestresults/1");
			
		uploadFile.addHeader("Accept","application/json");
		uploadFile.addHeader("apiKey","IuKhGeuzJEeJ3Hsi66CSTpNUU1VGGsyZjS1sCFeF");
		uploadFile.addHeader("scope",scope);
			
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("entityType",format, ContentType.TEXT_PLAIN);
		builder.addTextBody("testsuiteId", testsuiteId, ContentType.TEXT_PLAIN);
		builder.addTextBody("buildID",currentBuildId);
		builder.addTextBody("platformID",platformId);
			
		File f = new File(filepath);
		try {
			builder.addPart("file", new FileBody(f));
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		HttpEntity multipart = builder.build();
		uploadFile.setEntity(multipart);
		CloseableHttpResponse response = httpClient.execute(uploadFile);
		res=EntityUtils.toString(response.getEntity());
			
		httpClient.close();
		
		return res;
	}
}
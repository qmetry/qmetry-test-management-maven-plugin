package com.qmetry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
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

import org.apache.maven.plugin.logging.Log;

public class Upload
{
	public static List<String> fetchFiles(String filepath,String format)
	{
		String extention;
		if(format.equals("junit/xml") || format.equals("testng/xml") || format.equals("hpuft/xml"))
			extention=".xml";
		else if(format.equals("cucumber/json"))
			extention=".json";
		else
			return null;
		
		List<String> list=new ArrayList<String>();
		File file=new File(filepath);
		File[] farray=file.listFiles();
		String path;
		
		if(farray!=null)
		{
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
		return null;
	}
	
	public static String uploadfile(String url,String automationkey,String filepath,String format,String testsuitekey,String platform,String cycle,String project,String release,String build,Log log) throws IOException,ParseException
	{
		String res;
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
			
		HttpPost uploadFile=new HttpPost(url+"/rest/import/createandscheduletestresults/1");
			
		uploadFile.addHeader("Accept","application/json");
		uploadFile.addHeader("apiKey",automationkey);
		uploadFile.addHeader("scope","default");
			
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("entityType",format, ContentType.TEXT_PLAIN);
		if(testsuitekey!=null && !testsuitekey.isEmpty())
			builder.addTextBody("testsuiteId", testsuitekey, ContentType.TEXT_PLAIN);
		if(cycle!=null && !cycle.isEmpty())
			builder.addTextBody("cycleID",cycle,ContentType.TEXT_PLAIN);
		if(platform!=null && !platform.isEmpty())
			builder.addTextBody("platformID",platform,ContentType.TEXT_PLAIN);
		if(project!=null && !project.isEmpty())
			builder.addTextBody("projectID",project,ContentType.TEXT_PLAIN);
		if(release!=null && !release.isEmpty())
			builder.addTextBody("releaseID",release,ContentType.TEXT_PLAIN);
		if(build!=null && !build.isEmpty())
			builder.addTextBody("dropID",build,ContentType.TEXT_PLAIN);
			
		File f = new File(filepath);
		builder.addPart("file", new FileBody(f));
		
			
		HttpEntity multipart = builder.build();
		uploadFile.setEntity(multipart);
		CloseableHttpResponse response = httpClient.execute(uploadFile);
		int code=response.getStatusLine().getStatusCode();
		if(code!=200)
		{
			log.info("----------Status Code:"+code+"----------");
			if(code==400)
			{
				HttpEntity entity = response.getEntity();
				if(entity!=null)
				{
					InputStream content = entity.getContent();
					StringBuilder  builder1 = new StringBuilder();
					Reader read = new InputStreamReader(content, StandardCharsets.UTF_8);
					BufferedReader reader = new BufferedReader(read);
					String line;
					try {
						while ((line = reader.readLine()) != null) {
							builder1.append(line);
						}

					}
					finally{
						reader.close();
						content.close();
					}
					log.info("Error Response-->"+builder1.toString());
				}
				
			}
			return "false";
		}
		else
		{
			HttpEntity entity = response.getEntity();
			if(entity!=null)
			{
				InputStream content = entity.getContent();
				StringBuilder  builder1 = new StringBuilder();
				Reader read = new InputStreamReader(content, StandardCharsets.UTF_8);
				BufferedReader reader = new BufferedReader(read);
				String line;
				try {
					while ((line = reader.readLine()) != null) {
						builder1.append(line);
					}

				}
				finally{
					reader.close();
					content.close();
				}
				JSONParser parser=new JSONParser();
				JSONObject responsejson=(JSONObject)parser.parse(builder1.toString());
				return responsejson.toString();
			}
		}
		
		res=EntityUtils.toString(response.getEntity());
		httpClient.close();
		
		return res;
	}
}
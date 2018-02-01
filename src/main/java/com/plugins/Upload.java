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
	
	public static String uploadfile(String url,String automationkey,String filepath,String format) throws IOException
	{
		String res;
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
			
		HttpPost uploadFile=new HttpPost(url+"/rest/import/createandscheduletestresults/1");
			
		uploadFile.addHeader("Accept","application/json");
		uploadFile.addHeader("apiKey",automationkey);
		uploadFile.addHeader("scope","default");
			
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("entityType",format, ContentType.TEXT_PLAIN);
		/*builder.addTextBody("testSuiteName", testsuitename, ContentType.TEXT_PLAIN);
		builder.addTextBody("buildName",cyclename);
		builder.addTextBody("platformName",platformname);*/
			
		File f = new File(filepath);
		builder.addPart("file", new FileBody(f));
		
			
		HttpEntity multipart = builder.build();
		uploadFile.setEntity(multipart);
		CloseableHttpResponse response = httpClient.execute(uploadFile);
		int code=response.getStatusLine().getStatusCode();
		if(code!=200)
		{
			System.out.println("----------Status Code:"+code+"----------");
			return "false";
		}
		
		res=EntityUtils.toString(response.getEntity());
		httpClient.close();
		
		return res;
	}
}
package com.qmetry;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
/**
 * Goal=uploadresults:Upload test results on Qmetry Test Management.
 *
 */
@Mojo( name = "uploadresults", defaultPhase = LifecyclePhase.TEST )
public class UploadMojo
    extends AbstractMojo
{
    /**
     * QTM url.
     */
    @Parameter( property = "url", required = true )
    String url;
	
	/**
	 * QTM API key(Available in Automation API)
	 */
	@Parameter( property = "apikey", required = true)
	String apikey;
	
	/**
	 * Relative Path of testresult file from project directoty.
	 */
	@Parameter( property = "filepath", required = true)
	String filepath;
	
	/**
	 *Format of test result file.
	 * Valid Format junit/xml , cucumber/json , testng/xml , qas/json
	 */
	@Parameter( property = "format", required = true)
	String format;

	/**
	 *TestSuite Key
	 */
	@Parameter(property="testsuitekey",required=false)
	String testsuitekey;
	 
	/**
	 *Platform name
	 */
	@Parameter(property="platform",required=false)
	String platform;
	
	/**
	 *Cycle name
	 */
	@Parameter(property="cycle",required=false)
	String cycle;
	
	@Parameter(property="buildDir",required=false,defaultValue = "${basedir}",readonly=true)
	String buildDir;
	
    public void execute()
        throws MojoExecutionException
    {
		getLog().info("platform:"+platform);
		try
		{
			String fileformat="";
			String absolutefilepath=buildDir+"/"+filepath;
			
			if(format.equals("cucumber/json")){
				fileformat="CUCUMBER";
			}
								 
			if(format.equals("junit/xml")){
					fileformat="JUNIT";
			}
								 
			if(format.equals("testng/xml")){
				fileformat="TESTNG";
			}
								
        	if(format.equals("qas/json")){
				fileformat="QAS";
			}
			//relative file path PENDING
			if(filepath.endsWith("*.xml") || filepath.endsWith("*.json"))
			{
				String response=null;
				List<String> filelist=Upload.fetchFiles(absolutefilepath);
				if(filelist!=null)
				{
					for(String file:filelist)
					{
						//upload test results
						getLog().info("Uploading Testcase.....");
						response=Upload.uploadfile(url,apikey,file,fileformat,testsuitekey,platform,cycle);
						if(response.equals("false"))
						{
							//getLog().info("Couldn't upload testcase.For more information contact QMetry Support.");
							throw new MojoExecutionException("Couldn't upload testcase.For more information contact QMetry Support.");
						}
						else
						{
							JSONParser parser=new JSONParser();
							JSONObject responsejson=(JSONObject)parser.parse(response);
							JSONArray data=(JSONArray)responsejson.get("data");
							getLog().info("Testcase uploaded successfully");
							getLog().info("Response-->"+data.toString());
						}
					}
				}
				else
				{
					throw new MojoExecutionException("Can't find test result file.For more information contact QMetry Support.\n");
				}
			}
			else
			{
				//upload test results
				getLog().info("Uploading Testcase.....");
				String response=Upload.uploadfile(url,apikey,absolutefilepath,fileformat,testsuitekey,platform,cycle);
				if(response.equals("false"))
				{
					//getLog().info("Couldn't upload test result.For more information contact QMetry Support.");
					throw new MojoExecutionException("Couldn't upload test result.For more information contact QMetry Support.");
				}
				else
				{
					JSONParser parser=new JSONParser();
					JSONObject responsejson=(JSONObject)parser.parse(response);
					JSONArray data=(JSONArray)responsejson.get("data");
					getLog().info("Test result uploaded successfully");
					getLog().info("Response-->"+data.toString());
				}
			}
		}
		catch(FileNotFoundException e)
		{
			//getLog().info("Can't find test result file.For more information contact QMetry Support.");
			//getLog().error(e.getMessage());
			e.printStackTrace();
			throw new MojoExecutionException("Can't find test result file.For more information contact QMetry Support.\n");
		}
		catch(ParseException e)
		{
			//getLog().error(e.getMessage());
			e.printStackTrace();
			throw new MojoExecutionException("JSON Parse Exception has occured.For more information contact QMetry Support.\n");
		}
		catch(Exception e)
		{
			//getLog().info("Some unknown error occured.For more information contact QMetry Support.");
			if(e instanceof MojoExecutionException)
			{
				getLog().error(e.getMessage());
				throw new MojoExecutionException("Couldn't upload test result.For more information contact QMetry Support.");
			}
			else
			{
				e.printStackTrace();
				throw new MojoExecutionException("Some error has occured.For more information contact QMetry Support.\n");
			}
		}	
    }
	
	
}

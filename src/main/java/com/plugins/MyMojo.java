package com.plugins;

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

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.json.simple.parser.ParseException;
/**
 * Goal=uploadresults-->upload test results on QTM.
 *
 * @deprecated Don't use!
 */
@Mojo( name = "uploadresults", defaultPhase = LifecyclePhase.TEST )
public class MyMojo
    extends AbstractMojo
{
    /**
     * QTM url.
     */
    @Parameter( property = "apiurl", required = true )
    String apiurl;
	
	/**
	 * QTM API key(Available in user profile)
	 */
	@Parameter( property = "apikey", required = true)
	String apikey;
	
	/**
	 * Testrun name
	 */
	@Parameter( property = "testsuite", required = true)
	String testsuite;
	
	/**
	 * Testrun description
	 */
	@Parameter( property = "testsuiteDescription",required = false)
	String testsuitedescription;
	
	/**
	 * Absolute Path of testresult file.
	 */
	@Parameter( property = "testResultFilePath", required = true)
	String testResultFilePath;
	
	/**
	 *Absolute path of project directory.
	 */
	@Parameter( property="projectpath", required=true)
	String projectpath;
	/**
	 *Format of test result file.
	 * Valid Format JUNIT\CUCUMBER\TESTNG\QAS
	 */
	@Parameter( property = "format", required = true)
	String format;
	
	/**
	 *Platform 
	 */
	@Parameter( property="platform", required=false)
	String platform;
	
    public void execute()
        throws MojoExecutionException
    {
		try
		{
			/*getLog().info("url:"+apiurl);
			getLog().info("apiKey:"+apikey);
			getLog().info("testrun:"+testsuite);
			getLog().info("testrunDescription:"+testsuitedescription);
			getLog().info("testResulttestResultFilePath:"+testResultFilePath);
			getLog().info("format:"+format);*/
			
			Map<String,String> map=Upload.getInfo(apiurl,apikey,platform);
			if(map!=null)
			{
				String rootTsFolderid=map.get("rootTsFolderid");
				String currentBuildId=map.get("currentBuildId");
				String currentProjectId=map.get("currentProjectId");
				String currentReleaseId=map.get("currentReleaseId");
				String platformId=map.get("platformId");
				String userId=map.get("userId");
				String scope=currentProjectId+":"+currentReleaseId+":"+currentBuildId;
				
				//get automation key
				String automationkey=Upload.getAutomationApiKey(apiurl,apikey,scope);
				if(automationkey==null)
				{
					getLog().info("You don't have automation pack");
				}
				else
				{
					//Uploading multiple result files
					if(testResultFilePath.endsWith("*.xml") || testResultFilePath.endsWith("*.json"))
					{
						String response=null;
						List<String> filelist=Upload.fetchFiles(testResultFilePath);
						for(String file:filelist)
						{
							//create testsuiteid
							getLog().info("Creating testsuite.....");
							Map<String,String> testsuitemap=Upload.createTestsuite(apiurl,apikey,testsuite,testsuitedescription,scope,rootTsFolderid,format,projectpath,testResultFilePath,userId);
						
							String testsuiteId=testsuitemap.get("testsuiteId");
							String testsuitekey=testsuitemap.get("testsuitekey");
							getLog().info("Testsuite created.");
							getLog().info("Testsuite name:"+testsuite);
							getLog().info("Testsuite key:"+testsuitekey);
							getLog().info("TestsuiteId:"+testsuiteId);
							
							//map ReleaseCycle
							String mres=Upload.mapReleaseCycle(apiurl,apikey,scope,testsuiteId,currentBuildId,currentReleaseId);
							//getLog().info("map response:"+mres);
							
							//upload test results
							getLog().info("Uploading Testcase");
							response=Upload.uploadfile(apiurl,automationkey,file,format,testsuiteId,scope,currentBuildId,platformId);
							//getLog().info("file response:"+response);
							getLog().info("Testcase Uploaded successfully");
						}
					}
					else
					{
						//create testsuiteid
						getLog().info("Creating testsuite.....");
						Map<String,String> testsuitemap=Upload.createTestsuite(apiurl,apikey,testsuite,testsuitedescription,scope,rootTsFolderid,format,projectpath,testResultFilePath,userId);
						
						String testsuiteId=testsuitemap.get("testsuiteId");
						String testsuitekey=testsuitemap.get("testsuitekey");
						getLog().info("Testsuite created.");
						getLog().info("Testsuite name:"+testsuite);
						getLog().info("Testsuite key:"+testsuitekey);
						getLog().info("TestsuiteId:"+testsuiteId);
							
						//map ReleaseCycle
						String mres=Upload.mapReleaseCycle(apiurl,apikey,scope,testsuiteId,currentBuildId,currentReleaseId);
						//getLog().info("map response:"+mres);
						
						//upload test results
						getLog().info("Uploading Testcase");
						String response=Upload.uploadfile(apiurl,automationkey,testResultFilePath,format,testsuiteId,scope,currentBuildId,platformId);
						//getLog().info("file response:"+response);
						getLog().info("Testcase Uploaded successfully");
					}
				}
			}
			else
			{	
				getLog().info("Invalid url or API key");
			}
		}
		catch(Exception e)
		{
			getLog().info(e.toString());
		}
    }
	
	
}

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
 * Goal=uploadresults-->Upload test results on Qmetry Test Management.
 *
 */
@Mojo( name = "uploadresults", defaultPhase = LifecyclePhase.TEST )
public class UploadMojo
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
	 * Absolute Path of testresult file.
	 */
	@Parameter( property = "testResultFilePath", required = true)
	String testResultFilePath;
	
	/**
	 *Format of test result file.
	 * Valid Format junit/xml , cucumber/json , testng/xml , qas/json
	 */
	@Parameter( property = "format", required = true)
	String format;
	
    public void execute()
        throws MojoExecutionException
    {
		try
		{
			String fileformat="";
			
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
			
			if(testResultFilePath.endsWith("*.xml") || testResultFilePath.endsWith("*.json"))
			{
				String response=null;
				List<String> filelist=Upload.fetchFiles(testResultFilePath);
				for(String file:filelist)
				{
					//upload test results
					getLog().info("Uploading Testcase.....");
					response=Upload.uploadfile(apiurl,apikey,file,fileformat);
					if(response.equals("false"))
					{
						getLog().info("Can't upload testcase");
					}
					else
					{
						getLog().info("Testcase uploaded successfully");
					}
				}
			}
			else
			{
				//upload test results
				getLog().info("Uploading Testcase.....");
				String response=Upload.uploadfile(apiurl,apikey,testResultFilePath,fileformat);
				if(response.equals("false"))
				{
					getLog().info("Can't upload testcase");
				}
				else
				{
					getLog().info("Testcase uploaded successfully");
				}
			}
		}
		catch(IOException e)
		{
			getLog().info("Can't find test result file.");
			getLog().info(e.toString());
		}
		catch(Exception e)
		{
			getLog().info(e.toString());
		}	
    }
	
	
}

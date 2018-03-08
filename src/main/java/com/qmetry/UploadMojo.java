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
	 * Valid Format junit/xml , cucumber/json , testng/xml , qas/json , hpuft/xml
	 */
	@Parameter( property = "format", required = true)
	String format;

	/**
	 *TestSuite Key
	 */
	@Parameter(property="testsuite",required=false)
	String testsuite;
	 
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
	
	/**
	 *Project Name or or Key Id
	 */
	@Parameter(property="project",required=true)
	String project;
	
	/**
	 *Release Name or Id
	 */
	@Parameter(property="release",required=false)
	String release;
	
	/**
	 *Build Name or Id
	 */
	@Parameter(property="build",required=false)
	String build;
	
	@Parameter(property="buildDir",required=false,defaultValue = "${basedir}",readonly=true)
	String buildDir;
	
    public void execute()
        throws MojoExecutionException
    {
		
		try
		{
			if(cycle!=null && !cycle.isEmpty())
			{
				if(!(release!=null && !release.isEmpty()))
				{
					throw new MojoExecutionException("Release is Required when Cycle is provided.\n");
				}
			}
			String fileformat="";
			String absolutefilepath=buildDir+"/"+filepath;
			absolutefilepath=absolutefilepath.replace("\\","/");
			
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
			if(format.equals("hpuft/xml")){
				fileformat="HPUFT";
			}
			
			getLog().info("Format:"+format);
			getLog().info("File Path:"+absolutefilepath);
			if(project!=null && !project.isEmpty())
			{
				getLog().info("Project:"+project);
			}
			if(release!=null && !release.isEmpty())
			{
				getLog().info("Release:"+release);
			}
			if(cycle!=null && !cycle.isEmpty())
			{
				getLog().info("Cycle:"+cycle);
			}
			if(build!=null && !build.isEmpty())
			{
				getLog().info("Build:"+build);
			}
			if(testsuite!=null && !testsuite.isEmpty())
			{
				getLog().info("Testsuite:"+testsuite);
			}
			if(platform!=null && !platform.isEmpty())
			{
				getLog().info("Platform:"+platform);
			}
			
			//relative file path PENDING
			if(format.equals("qas/json"))
			{
				File sourceDir=new File(absolutefilepath);
				getLog().info("Creating Zip file..........");
				if(sourceDir.isFile())
				{
					//getLog().info("In qas/json enter path to directory not file.");
					throw new MojoExecutionException("In qas/json enter path to directory not file.\n");
				}
				String zipfilepath=CreateZip.createZip(absolutefilepath,format);
				getLog().info("Created Zip File:"+zipfilepath);
				getLog().info("Uploading Test Results..........");
				String response=Upload.uploadfile(url,apikey,zipfilepath,fileformat,testsuite,platform,cycle,project,release,build,getLog());
				if(response.equals("false"))
				{
					throw new MojoExecutionException("Couldn't upload testcase.For more information contact QMetry Support.\n");
				}
				else
				{
					getLog().info("Test results uploaded successfully");
					getLog().info("Response-->"+response);
				}
			}
			else if(filepath.endsWith("*.xml") || filepath.endsWith("*.json"))
			{
				if(format.equals("junit/xml") || format.equals("testng/xml") || format.equals("hpuft/xml"))
				{
					if(filepath.endsWith("*.json"))
					{
						throw new MojoExecutionException("Can not upload json files when format is "+format);
					}
				}
				if(format.equals("cucumber/json") && filepath.endsWith("*.xml"))
				{
					throw new MojoExecutionException("Can not upload xml files when format is "+format);
				}
				String response=null;
				File f=new File(absolutefilepath);
				String parentDir=f.getParentFile().getPath();
				List<String> filelist=Upload.fetchFiles(parentDir,format);
				if(filelist!=null)
				{
					for(String file:filelist)
					{
						//upload test results
						getLog().info("Uploading Test Results..........");
						getLog().info("File:"+file);
						response=Upload.uploadfile(url,apikey,file,fileformat,testsuite,platform,cycle,project,release,build,getLog());
						if(response.equals("false"))
						{
							//getLog().info("Couldn't upload testcase.For more information contact QMetry Support.");
							throw new MojoExecutionException("Couldn't upload testcase.For more information contact QMetry Support.\n");
						}
						else
						{
							getLog().info("Testcase uploaded successfully");
							getLog().info("Response-->"+response);
						}
					}
				}
				else
				{
					throw new MojoExecutionException("Can not find Files to be uploaded.Check if you have entered valid Path and Format.For more information contact QMetry support.\n");
				}
			}
			else
			{
				File f=new File(absolutefilepath);
				if(f.isDirectory())
				{
					/*String response=null;
					getLog().info("Fetching files from the Directory............");
					List<String> filelist=Upload.fetchFiles(absolutefilepath,format);
					if(filelist!=null)
					{
						for(String file:filelist)
						{
							//upload test results
							getLog().info("Uploading Test Result..........");
							getLog().info("File:"+file);
							response=Upload.uploadfile(url,apikey,file,fileformat,testsuite,platform,cycle,project,release,build,getLog());
							if(response.equals("false"))
							{
								//getLog().info("Couldn't upload testcase.For more information contact QMetry Support.");
								throw new MojoExecutionException("Couldn't upload testcase.For more information contact QMetry Support.\n");
							}
							else
							{
								getLog().info("Testcase uploaded successfully");
								getLog().info("Response-->"+response);
							}
						}
					}
					else
					{
						throw new MojoExecutionException("Can not find Files to be uploaded.Check if you have entered valid Path and Format.For more information contact QMetry support.\n");
					}*/
					getLog().info("Creating Zip file..........");
					String zipfilepath=CreateZip.createZip(absolutefilepath,format);
					getLog().info("Created Zip File:"+zipfilepath);
					getLog().info("Uploading Test Results..........");
					String response=Upload.uploadfile(url,apikey,zipfilepath,fileformat,testsuite,platform,cycle,project,release,build,getLog());
					if(response.equals("false"))
					{
						throw new MojoExecutionException("Couldn't upload testcase.For more information contact QMetry Support.\n");
					}
					else
					{
						getLog().info("Test results uploaded successfully");
						getLog().info("Response-->"+response);
					}
				}
				else
				{
					//upload test results
					getLog().info("Uploading Test Results..........");
					String response=Upload.uploadfile(url,apikey,absolutefilepath,fileformat,testsuite,platform,cycle,project,release,build,getLog());
					if(response.equals("false"))
					{
						//getLog().info("Couldn't upload test result.For more information contact QMetry Support.");
						throw new MojoExecutionException("Couldn't upload test result.For more information contact QMetry Support.\n");
					}
					else
					{
						getLog().info("Testcase uploaded successfully");
						getLog().info("Response-->"+response);
					}
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
				//getLog().error(e.getMessage());
				throw new MojoExecutionException(e.getMessage());
			}
			else
			{
				e.printStackTrace();
				throw new MojoExecutionException("Some error has occured.For more information contact QMetry Support.\n");
			}
		}	
    }
	
	
}

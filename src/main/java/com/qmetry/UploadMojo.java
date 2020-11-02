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

import java.io.File;
import java.io.FileNotFoundException;

import java.util.List;

import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
/**
 * Goal=uploadresults:Upload test results on Qmetry Test Management.
 *
 */
@Mojo( name = "uploadresults", defaultPhase = LifecyclePhase.TEST )
public class UploadMojo extends AbstractMojo {
    /**
     * QTM Base Url.
     */
    @Parameter( property = "url", required = true)
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
	 * Valid Format junit/xml, cucumber/json, testng/xml, qas/json, hpuft/xml, robot/xml
	 */
	@Parameter( property = "format", required = true)
	String format;

	/**
	 *Hierarchy which will be used to parse test result files on QTM for JUnit and TestNG 
	 */
	@Parameter( property = "automationHierarchy", required = false)
	String automationHierarchy;
	/**
	 *Target TestSuite Key or Id
	 */
	@Parameter(property="testsuite",required=false)
	String testsuite;
	 
	/**
	 *Testsuite Name
	 */
	@Parameter(property="testsuiteName",required=false)
	String testsuiteName;
	
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
	 *Project Name or Key or Id
	 */
	@Parameter(property="project",required=false)
	String project;

	/**
	 *Project Name or Key or Id
	 */
	@Parameter(property="qtmProject",required=false)
	String qtmProject;
	
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
	
	/**
	 *Testcase fields in json format
	 */
	@Parameter(property="testcaseFields", required=false)
	String testcaseFields;
	
	/**
	 *Testsuite fields in json format
	 */
	@Parameter(property="testsuiteFields", required=false)
	String testsuiteFields;

	@Parameter(property="skipWarning", required=false)
	String skipWarning;
	
    public void execute() throws MojoExecutionException {
		String projectId;
		
		try {
			if (qtmProject!=null && !qtmProject.isEmpty()) {
				projectId = qtmProject;
			} else if (project!=null && !project.isEmpty()) {
				projectId = project;
			} else {
				throw new MojoExecutionException("Please provide Project name, key or id in 'project' or 'qtmProject' parameter");
			}
			if(cycle!=null && !cycle.isEmpty()) {
				if(!(release!=null && !release.isEmpty())) {
					throw new MojoExecutionException("Release is Required when Cycle is provided.\n");
				}
			}
			if(build!=null && !build.isEmpty()) {
				if(!(release!=null && !release.isEmpty()) || !(cycle!=null && !cycle.isEmpty())) {
					throw new MojoExecutionException("Release and Cycle are required when Build is provided\n");
				}
			}
			
			if(testcaseFields !=null && !testcaseFields.isEmpty()) {
				try {
					JSONParser parse = new JSONParser();
					parse.parse(testcaseFields);
				} catch (Exception e) {
					throw new MojoExecutionException("Provide valid json for Testcase fields\n");
				}
			}
			
			if(testsuiteFields !=null && !testsuiteFields.isEmpty()) {
				try {
					JSONParser parse = new JSONParser();
					parse.parse(testsuiteFields);
				} catch (Exception e) {
					throw new MojoExecutionException("Provide valid json for Testsuite fields\n");
				}
			}

			if (skipWarning !=null && !skipWarning.isEmpty()) {
				if ( !(skipWarning.equals("0") || skipWarning.equals("1"))) {
					throw new MojoExecutionException("Skip warning must be 0 or 1.\n");
				}
			}
			
			String fileformat="";
			String absolutefilepath=buildDir+"/"+filepath;
			absolutefilepath=absolutefilepath.replace("\\","/");
			String autoHierarchy = "";
			
			if(format.equals("cucumber/json")) {
				
				fileformat="CUCUMBER";
				if(automationHierarchy!=null && !automationHierarchy.isEmpty()) {
					getLog().info("Skipping automationHierarchy because it is not supported for framework: " + format);
				}
			} else if(format.equals("junit/xml")) {
				
				fileformat="JUNIT";
				if(automationHierarchy!=null && !automationHierarchy.isEmpty()) {
					if(automationHierarchy.equals("1") || automationHierarchy.equals("2") || automationHierarchy.equals("3")) {
						autoHierarchy = automationHierarchy;
					} else {
						throw new MojoExecutionException("Please provide valid automationHierarchy value for framework: " + format);
					}
				}
			} else if(format.equals("testng/xml")) {
				
				fileformat="TESTNG";
				if(automationHierarchy!=null && !automationHierarchy.isEmpty()) {
					if(automationHierarchy.equals("1") || automationHierarchy.equals("2") || automationHierarchy.equals("3")) {
						autoHierarchy = automationHierarchy;
					} else {
						throw new MojoExecutionException("Please provide valid automationHierarchy value for framework: " + format);
					}
				}
			} else if(format.equals("qas/json")) {
				
				fileformat="QAS";
				if(automationHierarchy!=null && !automationHierarchy.isEmpty()) {
					getLog().info("Skipping automationHierarchy because it is not supported for framework: " + format);
				}
			} else if(format.equals("hpuft/xml")) {
				
				fileformat="HPUFT";
				if(automationHierarchy!=null && !automationHierarchy.isEmpty()) {
					getLog().info("Skipping automationHierarchy because it is not supported for framework: " + format);
				}
			} else if(format.equals("robot/xml")) {
				
				fileformat="ROBOT";
				if(automationHierarchy!=null && !automationHierarchy.isEmpty()) {
					getLog().info("Skipping automationHierarchy because it is not supported for framework: " + format);
				}
			}
			
			getLog().info("Format:" + format);
			getLog().info("File Path:" + absolutefilepath);
			
			if (autoHierarchy != null && !autoHierarchy.isEmpty()) {
				getLog().info("Automation Hierarchy:" + autoHierarchy);
			}
			if (projectId != null && !projectId.isEmpty()) {
				getLog().info("ProjectId:" + projectId);
			}
			if (release != null && !release.isEmpty()) {
				getLog().info("Release:" + release);
			}
			if (cycle != null && !cycle.isEmpty()) {
				getLog().info("Cycle:" + cycle);
			}
			if (build != null && !build.isEmpty()) {
				getLog().info("Build:" + build);
			}
			if (testsuite != null && !testsuite.isEmpty()) {
				getLog().info("Testsuite:" + testsuite);
			}
			if (testsuiteName != null && !testsuiteName.isEmpty()) {
				getLog().info("Testsuite Nameddd:" + testsuiteName);
			}
			if (platform != null && !platform.isEmpty()) {
				getLog().info("Platform:" + platform);
			}
			if (testsuiteFields != null && !testsuiteFields.isEmpty()) {
				getLog().info("Testsuite Fields: " + testsuiteFields);
			}
			if (testcaseFields != null && !testcaseFields.isEmpty()) {
				getLog().info("Testcase Fields: " + testcaseFields);
			}
			if (skipWarning != null && !skipWarning.isEmpty()) {
				getLog().info("Skip Warning: " + skipWarning);
			}
			
			if(format.equals("qas/json")) {
				
				File sourceDir=new File(absolutefilepath);
				if(!sourceDir.exists()) {
					throw new FileNotFoundException("Can't find file specified : "+sourceDir.getAbsolutePath());
				}
				
				getLog().info("Creating Zip file........");
				if(sourceDir.isFile()) {
					//getLog().info("In qas/json enter path to directory not file.");
					throw new MojoExecutionException("In qas/json enter path to directory not file.\n");
				}
				
				String zipfilepath=CreateZip.createZip(absolutefilepath,format);
				getLog().info("Created Zip File:"+zipfilepath);
				getLog().info("Uploading Test Results..........");
				String response=Upload.uploadfile(url,apikey,zipfilepath,fileformat,autoHierarchy,testsuite,testsuiteName,platform,cycle,projectId,release,build, testsuiteFields, testcaseFields, skipWarning, getLog());
				
				if(response.equals("false")) {
					throw new MojoExecutionException("Couldn't upload testcase. Please send these logs to qtmprofessional@qmetrysupport.atlassian.net for more information\n");
				} else {
					getLog().info("Test results uploaded successfully");
					getLog().info("Response-->"+response);
				}
			} else if(filepath.endsWith("*.xml") || filepath.endsWith("*.json")) {
				
				if(format.equals("junit/xml") || format.equals("testng/xml") || format.equals("hpuft/xml") || format.equals("robot/xml")) {
					if(filepath.endsWith("*.json")) {
						throw new MojoExecutionException("Can not upload json files when format is " + format);
					}
				} else if(format.equals("cucumber/json") && filepath.endsWith("*.xml")) {
					throw new MojoExecutionException("Can not upload xml files when format is " + format);
				}
				
				String response = null;
				File f = new File(absolutefilepath);
				File file1 = f.getParentFile();
				if (!file1.exists()) {
					throw new FileNotFoundException("Can't find file specified : "+file1.getAbsolutePath());
				}
				String parentDir = file1.getPath();
				List<String> filelist = Upload.fetchFiles(parentDir,format);
				if(filelist != null) {
					for(String file : filelist) {
						//upload test results
						getLog().info("Uploading Test Results..........");
						getLog().info("File:"+file);
						response = Upload.uploadfile(url,apikey,file,fileformat,autoHierarchy,testsuite,testsuiteName,platform,cycle,projectId,release,build, testsuiteFields, testcaseFields, skipWarning, getLog());
						if (response.equals("false")) {
							//getLog().info("Couldn't upload testcase.Please send these logs to qtmprofessional@qmetrysupport.atlassian.net for more information");
							throw new MojoExecutionException("Couldn't upload testcase. Please send these logs to qtmprofessional@qmetrysupport.atlassian.net for more information\n");
						} else {
							getLog().info("Testcase uploaded successfully");
							getLog().info("Response-->"+response);
						}
					}
				} else {
					throw new MojoExecutionException("Can not find Files to be uploaded.Check if you have entered valid Path and Format.Please send these logs to qtmprofessional@qmetrysupport.atlassian.net for more information\n");
				}
			} else {
				
				File f=new File(absolutefilepath);
				if(!f.exists()) {
					throw new FileNotFoundException("Can't find file specified : " + f.getAbsolutePath());
				}
				
				if(f.isDirectory()) {
					getLog().info("Creating Zip file............");
					String zipfilepath=CreateZip.createZip(absolutefilepath,format);
					getLog().info("Created Zip File:"+zipfilepath);
					getLog().info("Uploading Test Results..........");
					String response=Upload.uploadfile(url,apikey,zipfilepath,fileformat,autoHierarchy,testsuite,testsuiteName,platform,cycle,projectId,release,build, testsuiteFields, testcaseFields, skipWarning, getLog());
					if(response.equals("false")) {
						throw new MojoExecutionException("Couldn't upload testcase.Please send these logs to qtmprofessional@qmetrysupport.atlassian.net for more information\n");
					} else {
						getLog().info("Test results uploaded successfully");
						getLog().info("Response-->"+response);
					}
				} else {
					
					if (absolutefilepath.endsWith(".xml") && !(fileformat.equals("HPUFT") || fileformat.equals("JUNIT") || fileformat.equals("TESTNG") || fileformat.equals("ROBOT"))) {
						throw new MojoExecutionException("Cannot upload xml files when format is " + format + "\nPlease send these logs to qtmprofessional@qmetrysupport.atlassian.net for more information\n");
					} else if(absolutefilepath.endsWith(".json") && !fileformat.equals("CUCUMBER")) {
						throw new MojoExecutionException("Cannot upload json files when format is " + format + "\nPlease send these logs to qtmprofessional@qmetrysupport.atlassian.net for more information\n");
					}
					
					//upload test results
					getLog().info("Uploading Test Results..........");
					String response=Upload.uploadfile(url,apikey,absolutefilepath,fileformat,autoHierarchy,testsuite,testsuiteName,platform,cycle,projectId,release,build, testsuiteFields, testcaseFields, skipWarning, getLog());
					if(response.equals("false")) {
						//getLog().info("Couldn't upload test result.Please send these logs to qtmprofessional@qmetrysupport.atlassian.net for more information");
						throw new MojoExecutionException("Couldn't upload test result.Please send these logs to qtmprofessional@qmetrysupport.atlassian.net for more information\n");
					} else {
						getLog().info("Testcase uploaded successfully");
						getLog().info("Response-->" + response);
					}
				}
			}
		} catch(FileNotFoundException e) {
			//getLog().info("Can't find test result file.Please send these logs to qtmprofessional@qmetrysupport.atlassian.net for more information");
			//getLog().error(e.getMessage());
			e.printStackTrace();
			throw new MojoExecutionException("Can't find test result file.Please send these logs to qtmprofessional@qmetrysupport.atlassian.net for more information\n");
		} catch(ParseException e) {
			//getLog().error(e.getMessage());
			e.printStackTrace();
			throw new MojoExecutionException("JSON Parse Exception has occured.Please send these logs to qtmprofessional@qmetrysupport.atlassian.net for more information\n");
		} catch(Exception e) {
			//getLog().info("Some unknown error occured.Please send these logs to qtmprofessional@qmetrysupport.atlassian.net for more information");
			if(e instanceof MojoExecutionException) {
				//getLog().error(e.getMessage());
				throw new MojoExecutionException(e.getMessage());
			} else {
				e.printStackTrace();
				throw new MojoExecutionException("Some error has occured.Please send these logs to qtmprofessional@qmetrysupport.atlassian.net for more information\n");
			}
		}	
    }
}

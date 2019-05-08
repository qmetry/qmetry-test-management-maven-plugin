# qmetry-test-management-maven-plugin
QMetry Test Management plugin for Maven has been designed to seamlessly integrate your CI/CD pipeline with QMetry Test Management.

QMetry Test Management Maven Plugin uploads result file(s) generated in a Maven project to QMetry Test Management. The plugin, if used in a maven project, provides an additional maven goal 'uploadresults'

## How to use the plugin?

### Step 1:Getting data from QMetry Test Management

To get qmetry configuration from QMetry Test Management :-

1) Login to QMetry Test Management application.
2) Choose an existing project or create new.
3) Goto Apps > Automation App > Automation API
OR directly visit https://testmanagement.qmetry.com/#/automation-api

Open the pom.xml and add the configurations as described on the Automation API screen for Maven.

### Step 2:Add following to the pom.xml:
```
<pluginRepositories>
	<pluginRepository>
		<id>qmetry-test-management-maven-plugin-mvn-repo</id>
		<url>https://raw.github.com/qmetry/qmetry-test-management-maven-plugin/mvn-repo/</url>
		<snapshots>
			<enabled>true</enabled>
			<updatePolicy>always</updatePolicy>
		</snapshots>
	</pluginRepository>
</pluginRepositories>
```

### Step 3:Add the following to the <build> -> <plugins> block in your pom.xml:
```
<plugin>
	<groupId>com.qmetry</groupId>
	<artifactId>qmetry-test-management-maven-plugin</artifactId>
	<configuration>
		<url>https://testmanagement.qmetry.com/</url>
		<apikey>zEzs7iy7D8ARWX8xMFzJRZTzb66W0LCyaK6xde</apikey>
		<filepath>test-results\</filepath>
		<format>qas/json</format>
		<project>Demo Project</project> <!-- or <qtmProject>Demo Project</qtmProject> -->
		<automationHierarchy>1</automationHierarchy>
		<testsuite>Testsuite Key</testsuite>
		<testsuiteName>Demo Test suite</testsuiteName>
		<release>Demo Release</release>
		<cycle>Demo Cycle</cycle>
		<build>Demo Build</build>
		<platform>Demo Platform</platform>
	</configuration>
</plugin>
```

* **url (required)** - URL of your QMetry instance
* **apikey (required)** - Automation API Key
* **filepath (required)** - path to result file (or directory for multiple files) relative to build directory
* **format (required)** - junit/xml or testng/xml or cucucmber/json or qas/json or hpuft/xml
* **project or qtmProject (required)** - Project ID or Project Key or Project name
* **automationHierarchy (optional)** - Hierarchy which will be used to parse test result files on QTM for JUnit and TestNG (In case of other frameworks automationHierarchy will be skipped if provided)
  * JUnit 
    * 1 - Use current Hierarchy in which JUnit Testcase is treated as TestStep and Testsuite is treated as Testcase
    * 2 - Use Junit Testcase as Testcase and link all those (from all testsuites and all files of Junit) to one Testsuite
    * 3 - Create Multiple Testsuites and then link their respective testcases in corresponding Testsuites
  * TestNG
    * 1 - Use 'class' tag as Testcase and 'test-method' tag as TestStep
    * 2 - Use 'test-method' tag as Testcase
    * 3 - Use 'test' tag as Testcase and 'test-method' tag as TestStep
* **release (optional)** - Release ID or Release name
* **cycle (optional)** - Cycle Id or Cycle Name
* **build (optional)** - Build ID or Build name
* **testsuite (optional)** - Test suite ID or Entity Key (This will upload results to existing test suite)
* **testsuiteName (optional)** - Test suite Name (This will create a new test suite with given name)
* **platform (optional)** - Platform Id or Platform Name

#### Important Points
* Project name, key or id can be provided in 'project' or 'qtmProject' parameter, if both are provided then 'project' will be ignored.
* Automation Hierarchy which will be used to parse test result files on QTM is only supported for TestNG and JUnit framework.
* cycle refers to the Cycle Name in QMetry Test Management, and must be included in Release specified by release field of your project.
* testsuite should include Test Suite Key or Id from your QMetry Test Management project. Ignore the field if you want to create a new Test Suite for the results.
* platform (if specified), must be included in your QMetry Test Management project, before the task is executed.
* In case of QAS enter path to the folder in which testresult folder will be created. Plugin will automatically fetch testresult folder and upload results.
* Enter directory path to upload zip file plugin will create zip file and upload it to QMetry Test Management.

### Step 4:Genetrate Test Result(s) for your project.

### Step 5:Upload Test Result(s) to QMetry Test Management by executing following goal:
```
mvn qtm:uploadresults
```

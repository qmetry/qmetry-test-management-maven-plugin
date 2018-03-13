<h1>qtm-maven-plugin</h1>
<h2>QMetry Test Management plugin for Maven has been designed to seamlessly integrate your CI/CD pipeline with QMetry Test Management.

QMetry Test Management Maven Plugin uploads result file(s) generated in a Maven project to QMetry Test Management. The plugin, if used in a maven project, provides an additional maven goal 'uploadresults'</h2>

<h3>How to use the plugin?</h3>

<p><br><h4>Step 1:</h4>Getting data from QMetry Test Management</br>
<br>To get qmetry configuration from QMetry Test Management :-</br>

<br>1) Login to QMetry Test Management application.</br>
<br>2) Choose an existing project or create new.</br>
<br>3) Goto Apps > Automation App > Automation API</br>
<br>OR directly visit https://testmanagement.qmetry.com/#/automation-api</br>

<br>Open the pom.xml and add the configurations as described on the Automation API screen for Maven.</br> </p>

<p><h4>Step 2:</h4>Add following to the POM.xml:
<br><pluginRepositories></br>
    <br><pluginRepository></br>
        <br><id>qmetry-test-management-maven-plugin-mvn-repo</id></br>
        <br><url>https://raw.github.com/qmetry/qmetry-test-management-maven-plugin/mvn-repo/</url></br>
        <br><snapshots></br>
            <br><enabled>true</enabled></br>
            <br><updatePolicy>always</updatePolicy></br>
        <br></snapshots></br>
    <br></pluginRepository></br>
<br></pluginRepositories></br>
</p>

<p><br><h4>Step 3:</h4>Add the following to the <build> -> <plugins> block in your pom.xml:</br>
<br><plugin></br>
	<br><groupId>com.qmetry</groupId></br>
	<br><artifactId>qmetry-test-management-maven-plugin</artifactId></br>
	<br><version>1.0.0</version></br>
	<br><configuration></br>
		<br><url>https://testmanagement.qmetry.com</url></br>
		<br><apikey>zEzs7iy7D8ARWX8xMFzJRZTzb66W0LCyaK6xde</apikey></br>
		<br><filepath>target\surefire-reports\TEST-demoApp.xml</filepath></br>
		<br><format>junit/xml</format></br>
		<br><project>Demo Project</project></br>
		<br><release>Demo Release</release></br>
		<br><cycle>Demo Cycle</cycle></br>
		<br><build>Demo Build</build></br>
		<br><testsuite>Testsuite Key</testsuite></br>
		<br><platform>Demo Platform</platform></br>	
	<br></configuration></br>
<br></plugin></br>

<br>url - URL of your QMetry instance</br>
<br>apikey - Automation API Key</br>
<br>filepath - path to result file (or directory for multiple files) relative to build directory</br>
<br>format - junit/xml or testng/xml or cucucmber/json or qas/json or hpuft/xml</br>
<br>project - Project ID or Project Key or Project name</br>
<br>release (optional) - Release ID or Release name</br>
<br>cycle (optional) - Cycle Id or Cycle Name</br>
<br>build (optional) - Build ID or Build name</br>
<br>testsuite (optional) - Test Suite ID or Entity Key</br>
<br>platform (optional) - Platform Id or Platform Name</br>

<br><h4>Important Points</h4></br>
<br>cycle refers to the Cycle Name in QMetry Test Management, and must be included in Release specified by release field of your project.</br>
<br>testsuite should include Test Suite Key or Id from your QMetry Test Management project. Ignore the field if you want to create a new Test Suite for the results.</br>
<br>platform (if specified), must be included in your QMetry Test Management project, before the task is executed.</br>
<br>In case of QAS enter path to the folder in which testresult folder will be created. Plugin will automatically fetch testresult folder and upload results.</br>
<br>Enter directory path to upload zip file plugin will create zip file and upload it to QMetry Test Management.</br></p>

<p><br><h4>Step 4:</h4>Genetrate Test Result(s) for your project.</br></p>

<p><br><h4>Step 5:</h4>Upload Test Result(s) to QMetry Test Management by executing following goal:</br>
<br><b>mvn qtm:uploadresults<b></br></p> 

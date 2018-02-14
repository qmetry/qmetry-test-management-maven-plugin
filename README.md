qtm-maven-plugin
QMetry Test Management plugin for Maven has been designed to seamlessly integrate your CI/CD pipeline with QMetry Test Management.

QMetry Test Management Maven Plugin uploads result file(s) generated in a Maven project to QMetry Test Management. The plugin, if used in a maven project, provides an additional maven goal 'uploadresults'

How to use the plugin?

Step 1:Getting data from QMetry Test Management
To get qmetry configuration from QMetry Test Management :-

1) Login to QMetry Test Management application.
2) Choose an existing project or create new.
3) Goto Apps > Automation App > Automation API
OR directly visit https://testmanagement.qmetry.com/#/automation-api

Open the pom.xml and add the configurations as described on the Automation API screen for Maven. 

Step 2:Add following to the POM.xml:
<pluginRepositories>
    <pluginRepository>
        <id></id>
        <url></url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </pluginRepository>
</pluginRepositories>

Step 3: Add the following to the <build> -> <plugins> block in your pom.xml:
<plugin>
	<groupId>com.qmetry</groupId>
	<artifactId>qtm-maven-plugin</artifactId>
	<version>1.0.0</version>
	<configuration>
		<url>https://testmanagement.qmetry.com</url>
		<apikey>zEzs7iy77D8ARWX8xMFzJRZTzb66W0LCyaK6xdec</apikey>
		<filepath>target\surefire-reports\TEST-demoApp.xml</filepath>
		<format>junit/xml</format>
		<testsuitekey>JPI-TS-1</testsuitekey>
		<platform>IOS</platform>
		<cycle>MyCycle</cycle>
	</configuration>
</plugin>

url - URL to your QMetry instance
apikey - Automation API Key
filepath - path to result file (or directory for multiple files) relative to build directory
format - junit/xml testng/xml cucucmber/json qas/json
testsuitekey (optional) - Key of test suite.
cycle (optional) - Name of cycle linked to test suite
platform (optional) - Name of the platform to connect the suite

Important Points
cycle refers to the Cycle Name in QMetry Test Management, and must be included in Default Release of your project.
testsuitekey should include Test Suite Key from your QMetry Test Management project. Ignore the field if you want to create a new Test Suite for the results.
platform (if specified), must be included in your QMetry Test Management project, before the task is executed.

Step 4:Genetrate Test Result(s) for your project.

Step 5:Upload Test Result(s) to QMetry Test Management by executing following goal:
mvn qtm-maven-plugin:uploadresults 
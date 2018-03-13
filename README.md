<h1>qtm-maven-plugin</h1>
QMetry Test Management plugin for Maven has been designed to seamlessly integrate your CI/CD pipeline with QMetry Test Management.

QMetry Test Management Maven Plugin uploads result file(s) generated in a Maven project to QMetry Test Management. The plugin, if used in a maven project, provides an additional maven goal 'uploadresults'

<h2>How to use the plugin?</h2>

<p><br><h4>Step 1:</h4>Getting data from QMetry Test Management</br>
<br>To get qmetry configuration from QMetry Test Management :-</br>

<br>1) Login to QMetry Test Management application.</br>
<br>2) Choose an existing project or create new.</br>
<br>3) Goto Apps > Automation App > Automation API</br>
<br>OR directly visit https://testmanagement.qmetry.com/#/automation-api</br>

<br>Open the pom.xml and add the configurations as described on the Automation API screen for Maven.</br> </p>

<p><h4>Step 2:</h4>Add following to the POM.xml:
<br>&lt;pluginRepositories&gt;</br>
    <br>&lt;pluginRepository&gt;</br>
        <br>&lt;id&gt;qmetry-test-management-maven-plugin-mvn-repo&lt;/id&gt;</br>
        <br>&lt;url&gt;https://raw.github.com/qmetry/qmetry-test-management-maven-plugin/mvn-repo/&lt;/url&gt;</br>
        <br>&lt;snapshots&gt;</br>
            <br>&lt;enabled>true&lt;/enabled&gt;</br>
            <br>&lt;updatePolicy&gt;always&lt;/updatePolicy&gt;</br>
        <br>&lt;/snapshots&gt;</br>
    <br>&lt;/pluginRepository&gt;</br>
<br>&lt;/pluginRepositories&gt;</br>
</p>

<p><br><h4>Step 3:</h4>Add the following to the <build> -> <plugins> block in your pom.xml:</br>
<br>&lt;plugin&gt;</br>
	<br>&lt;groupId&gt;com.qmetry&lt;/groupId&gt;</br>
	<br>&lt;artifactId&gt;qmetry-test-management-maven-plugin&lt;/artifactId&gt;</br>
	<br>&lt;version&gt;1.0.0&lt;/version&gt;</br>
	<br>&lt;configuration&gt;</br>
		<br>&lt;url&gt;https://testmanagement.qmetry.com&lt;/url&gt;</br>
		<br>&lt;apikey&gt;zEzs7iy7D8ARWX8xMFzJRZTzb66W0LCyaK6xde&lt;/apikey&gt;</br>
		<br>&lt;filepath&gt;target\surefire-reports\TEST-demoApp.xml&lt;/filepath&gt;</br>
		<br>&lt;format&gt;junit/xml&lt;/format&gt;</br>
		<br>&lt;project&gt;Demo Project&lt;/project&gt;</br>
		<br>&lt;release&gt;Demo Release&lt;/release&gt;</br>
		<br>&lt;cycle&gt;Demo Cycle&lt;/cycle&gt;</br>
		<br>&lt;build&gt;Demo Build&lt;/build&gt;</br>
		<br>&lt;testsuite&gt;Testsuite Key&lt;/testsuite&gt;</br>
		<br>&lt;platform&gt;Demo Platform&lt;/platform&gt;</br>	
	<br>&lt;/configuration&gt;</br>
<br>&lt;/plugin&gt;</br>

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

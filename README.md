IBPMiddleware
============

Overview
----------
The Middleware API is a library that is used to provide access to Integrated Breeding Program Databases. 
It consists of managers corresponding to different systems including Genealogy Management System, Data Management System, 
Genotypic Data Management System, Germplasm List Management and Seed Inventory Management System. 

The library is used by different projects such as the IB Workflow System, List Manager, Fieldbook, GDMS, etc.
 
To Build
----------
To build the Middleware API, run the following command in the IBPMiddleware directory:  
<pre>
    mvn clean install
</pre>
    
To build using a specific configuration, run the following:  
<pre>
    mvn clean install -DenvConfig=dev-config-dir  
</pre>   

The directory with specific configuration properties <dev-config-dir> must be placed in /pipeline/config/dev-config-dir. Copy the files from an existing config directory (e.g. /pipeline/config/ci) and modify to match your local environment.

To build using Eclipse, right-click on the IBPMiddleware project, select Run As --> Maven build..., then input any of the following:
<pre>
    clean install
</pre>
<pre>
    clean install -DenvConfig=dev-config-dir  
</pre>  

  
To Run Tests
--------------
To run JUnit tests using the command line, issue the following commands in the IBPMiddleware directory:
  1.  To run all tests: <pre>mvn clean test</pre>
  2.  To run a specific test class: <pre>mvn clean test -Dtest=TestClassName</pre>
  3.  To run a specific test function: <pre>mvn clean test -Dtest=TestClassName#testFunctionName</pre>

You need to specify the IBDB database to connect to in the testDatabaseConfig.properties file. 

All JUnit test suites require the rice database, except for GenotypicDataManager that uses the groundnut crop in testing.

Similar to building the Middleware, add the -DenvConfig parameter to use a specific configuration.

To run JUnit tests using Eclipse, right-click on the specific JUnit test suite in the IBPMiddleware project, select Run As --> JUnit test.


 
To Use
-----------
To add Middleware as a dependency to your project using Apache Maven, add the following to your list of dependencies:  
<pre>
    groupId: org.generationcp
    artifactId: middleware
    version: 2.1.0
</pre>

Take note of the version.  Use of the latest version is recommended.  


API Documentation
-------------------
The Java API documentation of Middleware can be found here:   
<pre>
    http://gcp.efficio.us.com:8080/gcpdocs/
</pre>

Checking out the Middleware Project
-------------------
The project is stored in the GIT repository hosted at github.com.  The URL for the repository is: 
<pre>
    https://github.com/digitalabs/IBPMiddleware   
</pre>

An anonymous account may be used to checkout the project.  

No username and password is required.  You can also browse the content of the repository using the same URL.  


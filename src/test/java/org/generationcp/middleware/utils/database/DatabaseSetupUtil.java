package org.generationcp.middleware.utils.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.util.ResourceFinder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc2.SvnCheckout;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnTarget;

public class DatabaseSetupUtil{
	
	protected static final Logger LOG = LoggerFactory.getLogger(DatabaseSetupUtil.class);
	
	private static final String TEST_DATABASE_CONFIG_PROPERTIES = "testDatabaseConfig.properties";
	private static final String prefixDirectory = "./updatedIbdbScripts";
	private static final String DEFAULT_IBDB_GIT_URL = "https://github.com/digitalabs/IBDBScripts";


	private static DatabaseConnectionParameters centralConnectionParams, localConnectionParameters, workbenchConnectionParameters;
	
	private static String SQL_SCRIPTS_FOLDER = "sql";
		
	private static String LOCAL_SCRIPT = "/local";
	private static String CENTRAL_SCRIPT = "/central";
	private static String WORKBENCH_SCRIPT = "/workbench";	
	private static String MYSQL_PATH = "";
	private static String TEST_DB_REQUIRED_PREFIX = "test_";
	
	private static String gitUrl;
	
	
	@Test
	public void testSetupDatabase() throws Exception{
		setupTestDatabases();
	}
	 
    private static void setUpMysqlConfig() throws Exception{
		Class.forName("com.mysql.jdbc.Driver");
		localConnectionParameters = new DatabaseConnectionParameters(TEST_DATABASE_CONFIG_PROPERTIES, "local");
		centralConnectionParams = new DatabaseConnectionParameters(TEST_DATABASE_CONFIG_PROPERTIES, "central");
		workbenchConnectionParameters = new DatabaseConnectionParameters(TEST_DATABASE_CONFIG_PROPERTIES, "workbench");
		
		InputStream in = new FileInputStream(new File(ResourceFinder.locateFile(TEST_DATABASE_CONFIG_PROPERTIES).toURI()));
		Properties prop = new Properties();
		prop.load(in);

		MYSQL_PATH = prop.getProperty("mysql.path", "");
    }

    
	private static Map<String, List<File>> setupScripts() throws FileNotFoundException, URISyntaxException{
		Map<String, List<File>> scriptsMap = new HashMap<String, List<File>>();		
		
		try{
			File centralFile = new File(ResourceFinder.locateFile(SQL_SCRIPTS_FOLDER+CENTRAL_SCRIPT).toURI());
			if(centralFile != null && centralFile.isDirectory()){
				scriptsMap.put(CENTRAL_SCRIPT, Arrays.asList(centralFile.listFiles()));	
			}
		}catch(FileNotFoundException e){
			scriptsMap.put(CENTRAL_SCRIPT, new ArrayList<File>());
		}
		try{
			File localFile = new File(ResourceFinder.locateFile(SQL_SCRIPTS_FOLDER+LOCAL_SCRIPT).toURI());
			if(localFile != null && localFile.isDirectory()){
				scriptsMap.put(LOCAL_SCRIPT,  Arrays.asList(localFile.listFiles()));	
			}
		}catch(FileNotFoundException e){
			scriptsMap.put(LOCAL_SCRIPT, new ArrayList<File>());
		}
		try{
			File wbFile = new File(ResourceFinder.locateFile(SQL_SCRIPTS_FOLDER+WORKBENCH_SCRIPT).toURI());
			if(wbFile != null && wbFile.isDirectory()){
				scriptsMap.put(WORKBENCH_SCRIPT,  Arrays.asList(wbFile.listFiles()));	
			}
		}catch(FileNotFoundException e){
			scriptsMap.put(WORKBENCH_SCRIPT, new ArrayList<File>());
		}		
		return scriptsMap;
	}
	
	private static boolean isTestDatabase(String dbName){
		if(dbName != null && dbName.startsWith(TEST_DB_REQUIRED_PREFIX)){
			return true;
		}
		return false;
	}
	
	
	private static void initializeCentralDatabase() throws Exception {
		// copy and execute central/common scripts
		String checkoutURL = prefixDirectory+"/database/central/common";    	
		String centralCommonGitURL = gitUrl + "/trunk/central/common";
		checkoutAndRunIBDBScripts(checkoutURL, centralCommonGitURL, centralConnectionParams);
		
		//copy and execute central/common-update scripts
		checkoutURL = prefixDirectory+"/database/central/common-update";    	
		centralCommonGitURL = gitUrl + "/trunk/central/common-update";
		checkoutAndRunIBDBScripts(checkoutURL, centralCommonGitURL, centralConnectionParams);
		
		LOG.debug("  >>> Central DB initialized - all scripts from IBDBScripts ran successfully.");
	}
	
	private static void initializeLocalDatabase() throws Exception {
		// copy and execute central/common scripts
		String checkoutURL = prefixDirectory+"/database/local/common";    	
		String centralCommonGitURL = gitUrl + "/trunk/local/common";
		checkoutAndRunIBDBScripts(checkoutURL, centralCommonGitURL, localConnectionParameters);
		
		//copy and execute central/common-update scripts
		checkoutURL = prefixDirectory+"/database/local/common-update";    	
		centralCommonGitURL = gitUrl + "/trunk/local/common-update";
		checkoutAndRunIBDBScripts(checkoutURL, centralCommonGitURL, localConnectionParameters);
		
		LOG.debug("  >>> Local DB initialized - all scripts from IBDBScripts ran successfully.");
	}

	private static void initializeWorkbenchDatabase() throws Exception {
		// copy and execute central/common scripts
		String checkoutURL = prefixDirectory+"/database/workbench";    	
		String centralCommonGitURL = gitUrl + "/trunk/workbench";
		checkoutAndRunIBDBScripts(checkoutURL, centralCommonGitURL, workbenchConnectionParameters);
		
		LOG.debug("  >>> Workbench DB initialized - all scripts from IBDBScripts ran successfully.");
	}

	private static void setupIBDBScriptsConfig() throws FileNotFoundException, URISyntaxException,
			IOException {
		InputStream in = new FileInputStream(new File(ResourceFinder.locateFile(TEST_DATABASE_CONFIG_PROPERTIES).toURI()));
	    Properties prop = new Properties();
	    prop.load(in);
		         
		String ibdbScriptsGitUrl = prop.getProperty("test.ibdb.scripts.git.url", null);
		if(ibdbScriptsGitUrl == null) {
			 //we use the default url
			gitUrl = DEFAULT_IBDB_GIT_URL;
		} else {
			gitUrl = ibdbScriptsGitUrl;
		}
	}

	private static void checkoutAndRunIBDBScripts(String checkoutURL, String gitUrl, DatabaseConnectionParameters connection) throws SVNException, Exception {
		File scriptsDir = new File(checkoutURL);    	
		scriptsDir.mkdir(); 
		
		SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
    	try {
    	    SvnCheckout checkout = svnOperationFactory.createCheckout();
    	    checkout.setSingleTarget(SvnTarget.fromFile(scriptsDir));
    	    SVNURL url = SVNURL.parseURIEncoded(gitUrl);
    	    checkout.setSource(SvnTarget.fromURL(url));
    	    checkout.run();
    	} catch (SVNException e) {
			throw e;
    	} finally {
    	    svnOperationFactory.dispose();
    	}
    	LOG.debug("  >>> Checkout from " + gitUrl + " successful.");
    	
    	File[] files = scriptsDir.listFiles();
    	Arrays.sort(files, new Comparator<File>() {
    			     public int compare(File a, File b) {
    			       return a.getName().compareTo(b.getName());
    			     }
    			   });
    	
    	LOG.info("Running files : " + Arrays.asList(files));
    	
    	runAllSetupScripts(Arrays.asList(files), connection);
	}
	
	/**
	 * Creates test central, local and workbench databases with creation scripts coming from IBDBScripts
	 * along with init data files in src\test\resources\sql
	 * 
	 * @return true if all test databases created successfully
	 * @throws Exception
	 */
	public static boolean setupTestDatabases() throws Exception{
		try {						
			setUpMysqlConfig();
			setupIBDBScriptsConfig();
			
			// drop database to ensure fresh DB
			dropTestDatabases();
			
			Map<String, List<File>> scriptsMap = setupScripts();
			
			createTestDatabase(centralConnectionParams, scriptsMap.get(CENTRAL_SCRIPT));
			createTestDatabase(localConnectionParameters, scriptsMap.get(LOCAL_SCRIPT));
			createTestDatabase(workbenchConnectionParameters, scriptsMap.get(WORKBENCH_SCRIPT));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (ConfigException e) {
			e.printStackTrace();
			return false;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	
	private static void createTestDatabase(DatabaseConnectionParameters connectionParams, List<File> initDataFiles) throws IOException,
			InterruptedException, Exception {
		
		if(isTestDatabase(connectionParams.getDbName())) {
			runSQLCommand("CREATE DATABASE  IF NOT EXISTS `"+connectionParams.getDbName()+"`; USE `"+connectionParams.getDbName()+"`;", connectionParams);
			
			if (connectionParams.equals(centralConnectionParams)) {
				LOG.debug("Creating CENTRAL db ......");
				initializeCentralDatabase();
			
			} else if (connectionParams.equals(localConnectionParameters)) {
				LOG.debug("Creating LOCAL db .......");
				initializeLocalDatabase();
			
			} else {
				LOG.debug("Creating WORKBENCH db .......");
				initializeWorkbenchDatabase();
			}
			
			if (!initDataFiles.isEmpty()){
				runAllSetupScripts(initDataFiles, connectionParams);
				LOG.debug("  >>> Ran init data scripts successfully");
			}
			
		} else {
			throw new Exception("Test Database is not setup, please use a prefix 'test_' ");
		}
	}
	

	/**
	 * Drops all test databases (central, local, workbench)
	 * 
	 * @return
	 * @throws Exception
	 */
	public static boolean dropTestDatabases() throws Exception{
		try {
			setUpMysqlConfig();
			
			dropTestDatabase(localConnectionParameters);
			dropTestDatabase(centralConnectionParams);
			dropTestDatabase(workbenchConnectionParameters);
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (ConfigException e) {
			e.printStackTrace();
			return false;
		}catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static void dropTestDatabase(DatabaseConnectionParameters connectionParams) throws IOException, InterruptedException, Exception {
		if(isTestDatabase(localConnectionParameters.getDbName())){
			runSQLCommand("DROP DATABASE IF EXISTS `"+connectionParams.getDbName()+"`; ", connectionParams);
		}else{
			throw new Exception("Test Database is not setup, please use a prefix 'test_' ");
		}
	} 
	
		
	private static void runAllSetupScripts(List<File> fileList, DatabaseConnectionParameters connectionParams) throws Exception{		          
    	if(fileList != null && !fileList.isEmpty()){        	        		
    		for(int index = 0 ; index < fileList.size() ; index++) {
    			File sqlFile = fileList.get(index);
    			if (sqlFile.getName().endsWith(".sql")) {
    				if (!runScriptFromFile(sqlFile, connectionParams)) {
    					throw new Exception("Error in executing " + sqlFile.getAbsolutePath());
    				}
    			}
    		}        		
    	}       
	}
	
	private static boolean runScriptFromFile(File sqlFile, DatabaseConnectionParameters connectionParams) throws IOException, InterruptedException {
        ProcessBuilder pb;
        String mysqlAbsolutePath = new File(MYSQL_PATH).getAbsolutePath();
       
        if (connectionParams.getPassword() == null || connectionParams.getPassword().equalsIgnoreCase("")) {
              
            pb = new ProcessBuilder(mysqlAbsolutePath
            		,"--host=" + connectionParams.getHost()
                    ,"--port=" + connectionParams.getPort()
                    ,"--user=" + connectionParams.getUsername()
                    ,"--default-character-set=utf8"
                    ,connectionParams.getDbName()
                    ,"--execute=source " + sqlFile.getAbsoluteFile()
            );
        }
        else {
            pb = new ProcessBuilder(mysqlAbsolutePath
            		 ,"--host=" + connectionParams.getHost()
                     ,"--port=" + connectionParams.getPort()
                     ,"--user=" + connectionParams.getUsername()
                    , "--password=" + connectionParams.getPassword()
                    ,"--default-character-set=utf8"
                    ,connectionParams.getDbName()
                    ,"--execute=source " + sqlFile.getAbsoluteFile()                     
            );
        }

        Process mysqlProcess = pb.start();        
        readProcessInputAndErrorStream(mysqlProcess);
        int exitValue = mysqlProcess.waitFor();        
        if (exitValue != 0) {
            // fail
            return false;
        }
        return true;
    }
	
	private static boolean runSQLCommand(String sqlCommand, DatabaseConnectionParameters connectionParams) throws IOException, InterruptedException {
        ProcessBuilder pb;
        String mysqlAbsolutePath = new File(MYSQL_PATH).getAbsolutePath();
       
        if (connectionParams.getPassword() == null || connectionParams.getPassword().equalsIgnoreCase("")) {
            pb = new ProcessBuilder(mysqlAbsolutePath
                    ,"--host=" + connectionParams.getHost()
                    ,"--port=" + connectionParams.getPort()
                    ,"--user=" + connectionParams.getUsername()
                    ,"--default-character-set=utf8"
                    ,"-e"
                    ,sqlCommand
                  
            );
        }
        else {
            pb = new ProcessBuilder(mysqlAbsolutePath
            		 ,"--host=" + connectionParams.getHost()
                     ,"--port=" + connectionParams.getPort()
                     ,"--user=" + connectionParams.getUsername()
                    , "--password=" + connectionParams.getPassword()
                    ,"--default-character-set=utf8"
                    ,"-e"
                    ,sqlCommand 
            );            
        }

        Process mysqlProcess = pb.start();
        readProcessInputAndErrorStream(mysqlProcess);
        int exitValue = mysqlProcess.waitFor();        
        if (exitValue != 0) {
            // fail
            return false;
        }
        return true;
    }
	 private static String readProcessInputAndErrorStream(Process process) throws IOException {
    	/* Added while loop to get input stream because process.waitFor() has a problem
         * Reference: 
         * http://stackoverflow.com/questions/5483830/process-waitfor-never-returns
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while ( (line = reader.readLine()) != null) {
            //System.out.println(line);
        }
        reader.close();
        /* When the process writes to stderr the output goes to a fixed-size buffer. 
         * If the buffer fills up then the process blocks until the buffer gets emptied. 
         * So if the buffer doesn't empty then the process will hang.
         * http://stackoverflow.com/questions/10981969/why-is-going-through-geterrorstream-necessary-to-run-a-process
         */
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder errorOut = new StringBuilder();
        while ((line = errorReader.readLine()) != null) {
            errorOut.append(line);
        }

        errorReader.close();

        return errorOut.toString();
    }	 


}

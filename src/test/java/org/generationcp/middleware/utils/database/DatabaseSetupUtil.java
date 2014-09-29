package org.generationcp.middleware.utils.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.util.ResourceFinder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DatabaseSetupUtil{
	
	private static DatabaseConnectionParameters centralConnectionParams, localConnectionParameters, workbenchConnectionParameters;
	
	private static String SQL_SCRIPTS_FOLDER = "sql";
	private static String START_SQL_SCRIPTS = "/start";
		
	private static String LOCAL_SCRIPT = "/local";
	private static String CENTRAL_SCRIPT = "/central";
	private static String WORKBENCH_SCRIPT = "/workbench";	
	private static String MYSQL_PATH = "";
	private static String TEST_DB_REQUIRED_PREFIX = "test_";
		
	@Test
	public void testCreateDb() throws Exception{
		Assert.assertTrue("Database should return true if the DB for local, central and workbench has a prefix 'test_'", DatabaseSetupUtil.startSqlScripts());
	}
	@Test
	public void testDestroyDb() throws Exception{
		Assert.assertTrue("Database should return true if the DB for local, central and workbench has a prefix 'test_'", DatabaseSetupUtil.endSqlScripts());
	}
	
    private static void setUp() throws Exception{
		Class.forName("com.mysql.jdbc.Driver");
		localConnectionParameters = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
		centralConnectionParams = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
		workbenchConnectionParameters = new DatabaseConnectionParameters("testDatabaseConfig.properties", "workbench");
		
		InputStream in = new FileInputStream(new File(ResourceFinder.locateFile("testDatabaseConfig.properties").toURI()));
		Properties prop = new Properties();
		prop.load(in);

		MYSQL_PATH = prop.getProperty("mysql.path", "");
    }
	
	private static Map<String, List<File>> setupScripts(String sqlFolderPrefix) throws FileNotFoundException, URISyntaxException{
		Map<String, List<File>> scriptsMap = new HashMap<String, List<File>>();
		scriptsMap.put(CENTRAL_SCRIPT, new ArrayList());
		
		scriptsMap.put(WORKBENCH_SCRIPT, new ArrayList());
		try{
			File centralFile = new File(ResourceFinder.locateFile(SQL_SCRIPTS_FOLDER+sqlFolderPrefix+CENTRAL_SCRIPT).toURI());
			if(centralFile != null && centralFile.isDirectory()){
				scriptsMap.put(CENTRAL_SCRIPT, Arrays.asList(centralFile.listFiles()));	
			}
		}catch(FileNotFoundException e){
			scriptsMap.put(CENTRAL_SCRIPT, new ArrayList());
		}
		try{
			File localFile = new File(ResourceFinder.locateFile(SQL_SCRIPTS_FOLDER+sqlFolderPrefix+LOCAL_SCRIPT).toURI());
			if(localFile != null && localFile.isDirectory()){
				scriptsMap.put(LOCAL_SCRIPT,  Arrays.asList(localFile.listFiles()));	
			}
		}catch(FileNotFoundException e){
			scriptsMap.put(LOCAL_SCRIPT, new ArrayList());
		}
		try{
			File wbFile = new File(ResourceFinder.locateFile(SQL_SCRIPTS_FOLDER+sqlFolderPrefix+WORKBENCH_SCRIPT).toURI());
			if(wbFile != null && wbFile.isDirectory()){
				scriptsMap.put(WORKBENCH_SCRIPT,  Arrays.asList(wbFile.listFiles()));	
			}
		}catch(FileNotFoundException e){
			scriptsMap.put(WORKBENCH_SCRIPT, new ArrayList());
		}		
		return scriptsMap;
	}
	
	private static boolean isTestDatabase(String dbName){
		if(dbName != null && dbName.startsWith(TEST_DB_REQUIRED_PREFIX)){
			return true;
		}
		return false;
	}
	
	public static boolean startSqlScripts() throws Exception{
		try {						
			setUp();
			Map<String, List<File>> scriptsMap = setupScripts(START_SQL_SCRIPTS);
			if(isTestDatabase(centralConnectionParams.getDbName())){
				runSQLCommand("CREATE DATABASE  IF NOT EXISTS `"+centralConnectionParams.getDbName()+"`; USE `"+centralConnectionParams.getDbName()+"`;", centralConnectionParams);
				runAllSetupScripts(scriptsMap.get(CENTRAL_SCRIPT), centralConnectionParams);
			}else{
				throw new Exception("Test Database is not setup, please use a prefix 'test_' ");
			}
			if(isTestDatabase(localConnectionParameters.getDbName())){
				runSQLCommand("CREATE DATABASE  IF NOT EXISTS `"+localConnectionParameters.getDbName()+"`; USE `"+localConnectionParameters.getDbName()+"`;", localConnectionParameters);
				runAllSetupScripts(scriptsMap.get(LOCAL_SCRIPT), localConnectionParameters);
			}else{
				throw new Exception("Test Database is not setup, please use a prefix 'test_' ");
			}
			if(isTestDatabase(workbenchConnectionParameters.getDbName())){
				runSQLCommand("CREATE DATABASE  IF NOT EXISTS `"+workbenchConnectionParameters.getDbName()+"`; USE `"+workbenchConnectionParameters.getDbName()+"`;", workbenchConnectionParameters);
				runAllSetupScripts(scriptsMap.get(WORKBENCH_SCRIPT), workbenchConnectionParameters);
			}else{
				throw new Exception("Test Database is not setup, please use a prefix 'test_' ");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
		
	public static boolean endSqlScripts() throws Exception{
		try {
			setUp();
			if(isTestDatabase(localConnectionParameters.getDbName())){
				runSQLCommand("DROP DATABASE IF EXISTS `"+localConnectionParameters.getDbName()+"`; ", localConnectionParameters);
			}else{
				throw new Exception("Test Database is not setup, please use a prefix 'test_' ");
			}
			if(isTestDatabase(centralConnectionParams.getDbName())){
				runSQLCommand("DROP DATABASE IF EXISTS `"+centralConnectionParams.getDbName()+"`; ", centralConnectionParams);
			}else{
				throw new Exception("Test Database is not setup, please use a prefix 'test_' ");
			}
			if(isTestDatabase(workbenchConnectionParameters.getDbName())){
				runSQLCommand("DROP DATABASE IF EXISTS `"+workbenchConnectionParameters.getDbName()+"`; ", workbenchConnectionParameters);
			}else{
				throw new Exception("Test Database is not setup, please use a prefix 'test_' ");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	} 
	
		
	private static void runAllSetupScripts(List<File> fileList, DatabaseConnectionParameters connectionParams){
		
        try {        	
        	if(fileList != null && !fileList.isEmpty()){        	        		
        		for(int index = 0 ; index < fileList.size() ; index++){
        			File sqlFile = fileList.get(index);
        			runScriptFromFile(sqlFile, connectionParams);
        		}        		
        	}
        } catch (Exception e) {
            System.err.println(e);
        }
		//
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
                    ,"--database=" + connectionParams.getDbName()
                    //,"--execute=source " + sqlFile.getAbsoluteFile()
                  
            );
            //pb.redirectInput(ProcessBuilder.Redirect.from(sqlFile));
        }
        else {
            pb = new ProcessBuilder(mysqlAbsolutePath
            		 ,"--host=" + connectionParams.getHost()
                     ,"--port=" + connectionParams.getPort()
                     ,"--user=" + connectionParams.getUsername()
                    , "--password=" + connectionParams.getPassword()
                    ,"--default-character-set=utf8"
                    ,"--database=" + connectionParams.getDbName()
                    //,"--execute=source " + sqlFile.getAbsoluteFile() 
            );
           // pb.redirectInput(ProcessBuilder.Redirect.from(sqlFile));
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
	            //    System.err.println(line);
	        }

	        errorReader.close();

	        return errorOut.toString();
	    }
	 


		
}

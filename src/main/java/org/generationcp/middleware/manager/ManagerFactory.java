package org.generationcp.middleware.manager;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.TraitDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.util.HibernateUtil;

/**
 * The ManagerFactory gives access to the different Manager implementation classes.  This class takes care of opening and closing the 
 * connection to the databases.  
 *
 */
public class ManagerFactory
{
	private HibernateUtil hibernateUtilForLocal;
	private HibernateUtil hibernateUtilForCentral;
	
	/**
	 * This constructor accepts two DatabaseConnectionParameters objects as parameters.  The first is used to connect to a local instance of IBDB and
	 * the second is used to connect to a central instance of IBDB.  The user can provide both or can provide one of the two.</br>
	 * </br>
	 * For example:</br>
	 * </br>
	 * 1. creating a ManagerFactory which uses connections to both local and central instances</br>
	 * </br>
	 * DatabaseConnectionParameters local = new DatabaseConnectionParameters(...);</br>
	 * DatabaseConnectionParameters central = new DatabaseConnectionParameters(...);</br>
	 * ManagerFactory factory = new ManagerFactory(local, central);</br>
	 * </br>
	 * 2. creating a ManagerFactory which uses a connection to local only</br>
	 * </br>
	 * DatabaseConnectionParameters local = new DatabaseConnectionParameters(...);</br>
	 * ManagerFactory factory = new ManagerFactory(local, null);</br>
	 * </br>
	 * 3. creating a ManagerFactory which uses a connection to central only</br>
	 * </br>
	 * DatabaseConnectionParameters central = new DatabaseConnectionParameters(...);</br>
	 * ManagerFactory factory = new ManagerFactory(null, central);</br>
	 * </br>
	 * @param paramsForLocal
	 * @param paramsForCentral
	 * @throws ConfigException
	 */
	public ManagerFactory(DatabaseConnectionParameters paramsForLocal, DatabaseConnectionParameters paramsForCentral) throws ConfigException
	{
		//instantiate HibernateUtil with given db connection parameters
		//one for local
		if(paramsForLocal != null)
		{
			this.hibernateUtilForLocal = new HibernateUtil(paramsForLocal.getHost(), paramsForLocal.getPort(), paramsForLocal.getDbName(), 
					paramsForLocal.getUsername(), paramsForLocal.getPassword());
		}
		else
		{
			this.hibernateUtilForLocal = null;
		}
		
		//one for central
		if(paramsForCentral != null)
		{
			this.hibernateUtilForCentral = new HibernateUtil(paramsForCentral.getHost(), paramsForCentral.getPort(), paramsForCentral.getDbName(), 
					paramsForCentral.getUsername(), paramsForCentral.getPassword());
		}
		else
		{
			this.hibernateUtilForCentral = null;
		}
		
		if((this.hibernateUtilForCentral == null) && (this.hibernateUtilForLocal == null))
		{
			throw new ConfigException("No connection was established because database connection parameters were null.");
		}
	}
	
	public GermplasmDataManager getGermplasmDataManager()
	{
		return new GermplasmDataManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
	}
	
	public GermplasmListManager getGermplasmListManager()
	{
		return new GermplasmListManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
	}
	
	public TraitDataManager getTraitDataManager()
	{
		return new TraitDataManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
	}
	
	public StudyDataManager getStudyDataManager() throws ConfigException
	{
		return new StudyDataManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
	}
	
	public InventoryDataManager getInventoryDataManager() throws ConfigException
	{
		if(this.hibernateUtilForLocal == null)
		{
			throw new ConfigException("The InventoryDataManager needs a connection to a local IBDB instance which is not provided.");
		}
		else
		{
			return new InventoryDataManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
		}
	}
	
	public WorkbenchDataManager getWorkbenchDataManager() throws ConfigException 
	{
		if(this.hibernateUtilForLocal == null)
		{
			throw new ConfigException("The WorkbenchDataManager needs a connection to a local IBDB instance which is not provided.");
		}
		else
		{
			return new WorkbenchDataManagerImpl(this.hibernateUtilForLocal);
		}
	}
	
	/**
	 * Closes the db connection by shutting down the HibernateUtil object
	 */
	public void close()
	{
		if(this.hibernateUtilForLocal != null)
		{
			this.hibernateUtilForLocal.shutdown();
		}
		
		if(this.hibernateUtilForCentral != null)
		{
			this.hibernateUtilForCentral.shutdown();
		}
	}
}

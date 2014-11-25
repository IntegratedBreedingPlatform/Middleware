/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.manager;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.builder.*;
import org.generationcp.middleware.operation.destroyer.DataSetDestroyer;
import org.generationcp.middleware.operation.saver.*;
import org.generationcp.middleware.operation.searcher.StudySearcherByNameStartSeasonCountry;
import org.generationcp.middleware.util.DatabaseBroker;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The Class DataManager.
 * Superclass of DataManager implementations.
 * Contains generic implementation of retrieval methods. 
 * Supports local-central dynamics.
 * Contains getters of Builder, Saver, Searcher objects.
 * 
 * @author Joyce Avestro
 * @author Glenn Marintes
 */
public abstract class DataManager extends DatabaseBroker{

	private static final Logger LOG = LoggerFactory.getLogger(DataManager.class);

    /**
     * Instantiates a new data manager.
     */
    public DataManager() {
    }

    /**
     * Instantiates a new data manager given session providers for local and central.
     */
    public DataManager(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
    	super(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    /**
     * Instantiates a new data manager given session providers for local and central.
     */
    public DataManager(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral, String localDatabaseName, String centralDatabaseName) {
    	super(sessionProviderForLocal, sessionProviderForCentral, localDatabaseName, centralDatabaseName);
    }

    
    /**
     * Instantiates a new data manager given sessions for local and central.
     * TODO - must remove once all constructors of data managers passes the localDatabaseName and centralDatabaseName 
     * 
     * @param sessionForLocal
     * @param sessionForCentral
     */
    public DataManager(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
    }

    /**
     * Gets the parameter types of given parameters.        <br/>
     * 
     * @param parameters
     * @return Class[] of parameter types
     */
    @SuppressWarnings({ "unused", "rawtypes" })
    private Class[] getParameterTypes(Object[] parameters) {
        if (parameters == null) {
            parameters = new Object[] {};
        }
        Class[] parameterTypes = new Class[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class parameterClass = parameters[i].getClass();
            if (parameterClass.isPrimitive()) {
                String parameterClassName = parameterClass.getName();
                if (parameterClassName.equals("boolean")) {
                    parameterTypes[i] = Boolean.TYPE;
                } else if (parameterClassName.equals("byte")) {
                    parameterTypes[i] = Byte.TYPE;
                } else if (parameterClassName.equals("char")) {
                    parameterTypes[i] = Character.TYPE;
                } else if (parameterClassName.equals("double")) {
                    parameterTypes[i] = Double.TYPE;
                } else if (parameterClassName.equals("float")) {
                    parameterTypes[i] = Float.TYPE;
                } else if (parameterClassName.equals("int")) {
                    parameterTypes[i] = Integer.TYPE;
                } else if (parameterClassName.equals("long")) {
                    parameterTypes[i] = Long.TYPE;
                } else if (parameterClassName.equals("short")) {
                    parameterTypes[i] = Short.TYPE;
                }
                // void?
            } else {
                parameterTypes[i] = parameterClass;
            }
        }
        return parameterTypes;
    }

    /**
     * Returns all the entities from both central and local databases based on the given DAO.   <br/>
     * 
     * @param dao 
     * @return All entities from both local and central
     * @throws MiddlewareQueryException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List getAllFromCentralAndLocal(GenericDAO dao) throws MiddlewareQueryException {
        List toReturn = new ArrayList();
        if (setDaoSession(dao, getCurrentSessionForLocal())) {
            toReturn.addAll(dao.getAll());
        }
        if (setDaoSession(dao, getCurrentSessionForCentral())) {
            toReturn.addAll(dao.getAll());
        }
        return toReturn;
    }

    /**
     * A generic implementation of the getAllXXX(int start, int numOfRows) that calls getAll() of GenericDAO.   <br/>
     * This gets all the records returned by the corresponding DAO.getAll() method for the given DAO.           <br/>
     * Retrieves from both local and central.                                                                   <br/>
     * <br/>
     * Sample usage:<br/>
     * <pre><code>
     *      public List<Location> getAllLocations(int start, int numOfRows) throws MiddlewareQueryException {
     *          return (List<Location>) getFromCentralAndLocal(getLocationDao(), start, numOfRows);
     *      }
     * </code></pre>
     * @param dao   The DAO to call the method from
     * @param start     The start row
     * @param numOfRows     The number of rows to retrieve
     * @return List of all records
     * @throws MiddlewareQueryException
     */
    //TODO BMS-148 : Review for how to safely remove the dual db read pattern without breaking any logic.
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List getFromCentralAndLocal(GenericDAO dao, int start, int numOfRows) throws MiddlewareQueryException {
        
        List toReturn = new ArrayList();

        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        if (setWorkingDatabase(Database.CENTRAL, dao)) {
            centralCount = dao.countAll();
            if (centralCount > start) {
                toReturn.addAll(dao.getAll(start, numOfRows));
                relativeLimit = numOfRows - (centralCount - start);
                if (relativeLimit > 0) {
                    if (setWorkingDatabase(Database.LOCAL, dao)) {
                        localCount = dao.countAll();
                        if (localCount > 0) {
                            toReturn.addAll(dao.getAll(0, (int) relativeLimit));
                        }
                    }
                }
            } else {
                relativeLimit = start - centralCount;
                if (setWorkingDatabase(Database.LOCAL, dao)) {
                    localCount = dao.countAll();
                    if (localCount > relativeLimit) {
                        toReturn.addAll(dao.getAll((int) relativeLimit, numOfRows));
                    }
                }
            }
        } else if (setWorkingDatabase(Database.LOCAL, dao)) {
            localCount = dao.countAll();
            if (localCount > start) {
                toReturn.addAll(dao.getAll(start, numOfRows));
            }
        }
        return toReturn;
    }

    /**
     * A generic implementation of the getXXX(Object parameter, int start, int numOfRows).      <br/>
     * Calls the corresponding getXXX method as specified in the second value in the list of methods parameter.     <br/>
     * <br/>
     * Sample usage:<br/> 
     * <pre><code>
     *      public List<Location> getLocationsByCountry(Country country, int start, int numOfRows) throws MiddlewareQueryException {
     *          List<String> methods = Arrays.asList("countByCountry", "getByCountry");
     *          return (List<Location>) getFromCentralAndLocalByMethod(getLocationDao(), methods, start, numOfRows, new Object[]{country},
     *                                      new Class[]{Country.class});
     *      }
     * </code></pre>
     * @param dao   The DAO to call the methods from
     * @param methods   The methods to call (countXXX and its corresponding getXXX)
     * @param start     The start row
     * @param numOfRows     The number of rows to retrieve
     * @param parameters    The parameters to be passed to the methods
     * @param parameterTypes    The types of the parameters to be passed to the method
     * @return List of all records satisfying the given parameters
     * @throws MiddlewareQueryException
     */
    //TODO BMS-148 : Review for how to safely remove the dual db read pattern without breaking any logic.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List getFromCentralAndLocalByMethod(GenericDAO dao, List<String> methods, int start, int numOfRows, Object[] parameters,
            Class[] parameterTypes) throws MiddlewareQueryException {
        
        List toReturn = new ArrayList();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        // Get count method parameter types and parameters
        Class[] countMethodParameterTypes = parameterTypes;
        Object[] countMethodParameters = parameters;

        // Get get method parameter types and parameters
        Class[] getMethodParameterTypes = new Class[parameters.length + 2];
        Object[] getMethodParameters = new Object[parameters.length + 2];

        int i = 0;
        for (i = 0; i < parameters.length; i++) {
            getMethodParameterTypes[i] = parameterTypes[i];
            getMethodParameters[i] = parameters[i];
        }
        getMethodParameterTypes[i] = Integer.TYPE;
        getMethodParameterTypes[i + 1] = Integer.TYPE;
        getMethodParameters[i] = start;
        getMethodParameters[i + 1] = numOfRows;

        String countMethodName = methods.get(0);
        String getMethodName = methods.get(1);
        try {
            // Get the methods from the dao
            java.lang.reflect.Method countMethod = dao.getClass().getMethod(countMethodName, countMethodParameterTypes);
            java.lang.reflect.Method getMethod = dao.getClass().getMethod(getMethodName, getMethodParameterTypes);

            if (setWorkingDatabase(Database.CENTRAL, dao)) {
                centralCount = (Long) countMethod.invoke(dao, countMethodParameters);
                if (centralCount > start) {
                    toReturn.addAll((Collection) getMethod.invoke(dao, getMethodParameters)); // start, numRows
                    relativeLimit = numOfRows - (centralCount - start);
                    if (relativeLimit > 0) {
                        if (setWorkingDatabase(Database.LOCAL, dao)) {
                            localCount = (Long) countMethod.invoke(dao, countMethodParameters);
                            if (localCount > 0) {
                                getMethodParameters[getMethodParameters.length - 2] = 0;
                                getMethodParameters[getMethodParameters.length - 1] = (int) relativeLimit;
                                toReturn.addAll((Collection) getMethod.invoke(dao, getMethodParameters)); //0, (int) relativeLimit
                            }
                        }
                    }
                } else {
                    relativeLimit = start - centralCount;
                    if (setWorkingDatabase(Database.LOCAL, dao)) {
                        localCount = (Long) countMethod.invoke(dao, countMethodParameters);
                        if (localCount > relativeLimit) {
                            getMethodParameters[getMethodParameters.length - 2] = (int) relativeLimit;
                            getMethodParameters[getMethodParameters.length - 1] = numOfRows;
                            toReturn.addAll((Collection) getMethod.invoke(dao, getMethodParameters)); // (int) relativeLimit, numOfRows
                        }
                    }
                }
            } else if (setWorkingDatabase(Database.LOCAL, dao)) {
                localCount = (Long) countMethod.invoke(dao, countMethodParameters);
                if (localCount > start) {
                    toReturn.addAll((Collection) getMethod.invoke(dao, getMethodParameters)); //start, numOfRows
                }
            }
        } catch (Exception e) { // IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
            logAndThrowException("Error in gettting all from central and local using " + getMethodName + ": " + e.getMessage(), e);
        }
        return toReturn;

    }
    
    /**
     * A generic implementation of the getXXX(Object parameter, int start, int numOfRows).      <br/>
     * Calls the corresponding getXXX method as specified in the second value in the list of methods parameter.     <br/>
     * Both central and local should have the same number of parameters
     * <br/>
     * Sample usage:<br/> 
     * <pre><code>
     *      public List<Observation> getObservationsForTraits(List<Integer> traitIds, List<Integer> environmentIds, int start, int numOfRows) throws MiddlewareQueryException{
		    	List<Observation> centralObservations = new ArrayList<Observation>();
		    	List<Observation> localObservations = new ArrayList<Observation>();
		        getTraitBuilder().buildObservations(centralObservations, localObservations, traitIds, environmentIds);
		        List<String> methods = Arrays.asList("countObservationForTraits", "getObservationForTraits");
		        Object[] centralParameters = new Object[] { centralObservations };
		        Object[] localParameters = new Object[] { localObservations };
		    	return (List<Observation>) getFromCentralAndLocalByMethod(
		    			getPhenotypeDao(), methods, start, numOfRows, 
		    			centralParameters, localParameters, new Class[] { List.class });
		    }
     * </code></pre>
     * @param dao   The DAO to call the methods from
     * @param methods   The methods to call (countXXX and its corresponding getXXX)
     * @param start     The start row
     * @param numOfRows     The number of rows to retrieve
     * @param centralParameters    The parameters to be passed to the central methods
     *  @param localParameters    The parameters to be passed to the local methods
     * @param parameterTypes    The types of the parameters to be passed to the method
     * @return List of all records satisfying the given parameters
     * @throws MiddlewareQueryException
     */
    //TODO BMS-148 : Review for how to safely remove the dual db read pattern without breaking any logic.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List getFromCentralAndLocalByMethod(GenericDAO dao, List<String> methods, int start, int numOfRows, 
    		Object[] centralParameters, Object[] localParameters, Class[] parameterTypes) throws MiddlewareQueryException {
        
        List toReturn = new ArrayList();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        // Get count method parameter types and parameters
        Class[] countMethodParameterTypes = parameterTypes;
        Object[] centralCountMethodParameters = centralParameters;
        Object[] localCountMethodParameters = localParameters;

        // Get get method parameter types and parameters
        int numberOfParams = centralParameters.length;
        Class[] getMethodParameterTypes = new Class[numberOfParams + 2];
        Object[] centralGetMethodParameters = new Object[numberOfParams + 2];
        Object[] localGetMethodParameters = new Object[numberOfParams + 2];

        int i = 0;
        for (i = 0; i < numberOfParams; i++) {
        	getMethodParameterTypes[i] = parameterTypes[i];
        	centralGetMethodParameters[i] = centralParameters[i];
        	localGetMethodParameters[i] = localParameters[i];
        }
        getMethodParameterTypes[i] = Integer.TYPE;
        getMethodParameterTypes[i + 1] = Integer.TYPE;
        centralGetMethodParameters[i] = start;
        centralGetMethodParameters[i + 1] = numOfRows;
        localGetMethodParameters[i] = start;
        localGetMethodParameters[i + 1] = numOfRows;
        
        String countMethodName = methods.get(0);
        String getMethodName = methods.get(1);
        try {
            // Get the methods from the dao
            java.lang.reflect.Method countMethod = dao.getClass().getMethod(countMethodName, countMethodParameterTypes);
            java.lang.reflect.Method getMethod = dao.getClass().getMethod(getMethodName, getMethodParameterTypes);

            if (setWorkingDatabase(Database.CENTRAL, dao)) {
                centralCount = (Long) countMethod.invoke(dao, centralCountMethodParameters);
                if (centralCount > start) {
                    toReturn.addAll((Collection) getMethod.invoke(dao, centralGetMethodParameters)); // start, numRows
                    relativeLimit = numOfRows - (centralCount - start);
                    if (relativeLimit > 0) {
                        if (setWorkingDatabase(Database.LOCAL, dao)) {
                            localCount = (Long) countMethod.invoke(dao, localCountMethodParameters);
                            if (localCount > 0) {
                            	localGetMethodParameters[localGetMethodParameters.length - 2] = 0;
                            	localGetMethodParameters[localGetMethodParameters.length - 1] = (int) relativeLimit;
                                toReturn.addAll((Collection) getMethod.invoke(dao, localGetMethodParameters)); //0, (int) relativeLimit
                            }
                        }
                    }
                } else {
                    relativeLimit = start - centralCount;
                    if (setWorkingDatabase(Database.LOCAL, dao)) {
                        localCount = (Long) countMethod.invoke(dao, localCountMethodParameters);
                        if (localCount > relativeLimit) {
                        	localGetMethodParameters[localGetMethodParameters.length - 2] = (int) relativeLimit;
                        	localGetMethodParameters[localGetMethodParameters.length - 1] = numOfRows;
                            toReturn.addAll((Collection) getMethod.invoke(dao, localGetMethodParameters)); // (int) relativeLimit, numOfRows
                        }
                    }
                }
            } else if (setWorkingDatabase(Database.LOCAL, dao)) {
                localCount = (Long) countMethod.invoke(dao, localCountMethodParameters);
                if (localCount > start) {
                    toReturn.addAll((Collection) getMethod.invoke(dao, localGetMethodParameters)); //start, numOfRows
                }
            }
        } catch (Exception e) { // IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
            logAndThrowException("Error in gettting all from central and local using " + getMethodName + ": " + e.getMessage(), e);
        }
        return toReturn;

    }

    /**
     * A generic implementation of the getXXXByXXXX() method that calls a specific get method from a DAO.     <br/> 
     * Calls the corresponding method that returns list type as specified in the parameter methodName.         <br/>
     *      <br/>
     * Sample usage:     <br/> 
     *  <pre><code>
     *      public List<Location> getLocationsByType(Integer type) throws MiddlewareQueryException {
     *          return (List<Location>) getAllListFromCentralAndLocalByMethod(getLocationDao(), "getByType", new Object[]{type},
     *                      new Class[]{Integer.class});
     *      }
     *  </code></pre>
     *  
     * @param dao   The DAO to call the method from
     * @param methodName    The method to call
     * @param parameters    The parameters to be passed to the method
     * @param parameterTypes    The types of the parameters to be passed to the method
     * @return the List result
     * @throws MiddlewareQueryException
     */
    //TODO BMS-148 : No longer reads from two DBs, rename.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List getAllFromCentralAndLocalByMethod(GenericDAO dao, String methodName, Object[] parameters, Class[] parameterTypes)
            throws MiddlewareQueryException {
        
        List toReturn = new ArrayList();
        try {
            java.lang.reflect.Method method = dao.getClass().getMethod(methodName, parameterTypes);

            if (setWorkingDatabase(Database.LOCAL, dao)) {
                toReturn.addAll((List) method.invoke(dao, parameters));
            }
        } catch (Exception e) { // IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
            logAndThrowException("Error in calling " + methodName + "(): " + e.getMessage(), e);
        }
        return toReturn;
    }

    /**
     * A generic implementation of the getXXXByXXXX(Database instance) method that calls a specific get method from a DAO.     <br/>
     * Calls the corresponding method that returns list type as specified in the parameter methodName.     <br/>
     *      <br/>
     * Sample usage:     <br/>  
     * <pre><code>
     *      public List<Germplasm> getGermplasmByPrefName(String name, int start, int numOfRows, Database instance) throws MiddlewareQueryException {
     *        return (List<Germplasm>) getFromInstanceByMethod(getGermplasmDao(), instance, "getByPrefName", new Object[]{name, start, numOfRows}, 
     *              new Class[]{String.class, Integer.TYPE, Integer.TYPE});
     *    }
     * </code></pre>
     * @param dao   The DAO to call the method from
     * @param methodName    The method to call
     * @param parameters    The parameters to be passed to the method. If the referenced DAO method has parameters start and numOfRows, you may add them to this
     * @param parameterTypes    The types of the parameters passed to the methods
     * @return the List result
     * @throws MiddlewareQueryException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List getFromInstanceByMethod(GenericDAO dao, Database instance, String methodName, Object[] parameters, Class[] parameterTypes)
            throws MiddlewareQueryException {
        List toReturn = new ArrayList();
        try {
            java.lang.reflect.Method method = dao.getClass().getMethod(methodName, parameterTypes);

            if (setWorkingDatabase(instance, dao)) {
                toReturn.addAll((List) method.invoke(dao, parameters));
            }
        } catch (Exception e) { // IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
            logAndThrowException("Error in calling " + methodName + "(): " + e.getMessage(), e);
        }
        return toReturn;
    }

    /**
     * A generic implementation of the getXXXByXXXX(Integer id, ...) method that calls a specific get method from a DAO.     <br/>  
     * This connects to the database corresponding to the value of the id (Local for negative id, Central for positive).     <br/>   
     * Calls the corresponding method that returns list type as specified in the parameter methodName.     <br/>  
     *       <br/>
     * Sample usage:     <br/> 
     * <pre><code>
     *     public List<Integer> getMarkerIdsByDatasetId(Integer datasetId) throws MiddlewareQueryException {
     *        return (List<Integer>) super.getFromInstanceByIdAndMethod(getMarkerMetadataSetDao(), datasetId, "getMarkerIdByDatasetId", 
     *                new Object[]{datasetId}, new Class[]{Integer.class});
     *
     *    }
     * <code></pre>
     * @param dao   The DAO to call the method from
     * @param id    The id used to get the instance to connect to
     * @param methodName    The method to call
     * @param parameters    The parameters to be passed to the method. If the referenced DAO method has parameters start and numOfRows, you may add them to this
     * @param parameterTypes    The types of the parameters passed to the methods
     * @return the List result
     * @throws MiddlewareQueryException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List getFromInstanceByIdAndMethod(GenericDAO dao, Integer id, String methodName, Object[] parameters, Class[] parameterTypes)
            throws MiddlewareQueryException {
        List toReturn = new ArrayList();
        try {
            java.lang.reflect.Method method = dao.getClass().getMethod(methodName, parameterTypes);

            if (setDaoSession(dao, id)) {
                toReturn.addAll((List) method.invoke(dao, parameters));
            }
        } catch (Exception e) { // IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
            logAndThrowException("Error in calling " + methodName + "(): " + e.getMessage(), e);
        }
        return toReturn;
    }

    /**
     * A generic implementation of the countAllXXX() method that calls countAll() from Generic DAO.     <br/>
     * Returns the count of entities from both central and local databases based on the given DAO.     <br/> 
     *      <br/>
     * Sample usage:     <br/>
     * <pre><code>
     *     public long countAllLocations() throws MiddlewareQueryException {
     *          return countAllFromCentralAndLocal(getLocationDao());
     *     }
     * <code></pre>
     * @param dao   The DAO to call the method from
     * @return The number of entities from both central and local instances
     * @throws MiddlewareQueryException
     */
    //TODO BMS-148 : No longer reads from two DBs, rename.
    @SuppressWarnings("rawtypes")
    public long countAllFromCentralAndLocal(GenericDAO dao) throws MiddlewareQueryException {
        long count = 0;
        if (setDaoSession(dao, getCurrentSessionForLocal())) {
            count = count + dao.countAll();
        }
        return count;
    }

    /**
     * A generic implementation of the countByXXXX() method that calls a specific count method from a DAO.     <br/>  
     * Calls the corresponding count method as specified in the parameter methodName.     <br/>                       
     * Retrieves data from both local and central databases.      <br/>             
     *      <br/> 
     * Sample usage:     <br/>
     *  <pre><code>
     *  public long countLocationsByCountry(Country country) throws MiddlewareQueryException { 
     *      return countAllFromCentralAndLocalByMethod(getLocationDao(), "countByCountry", new Object[]{country}, new Class[]{Country.class}); 
     *  }
     *  </code></pre>
     * @param dao   The DAO to call the method from
     * @param methodName    The method to call
     * @param parameters    The parameters to be passed to the method
     * @param parameterTypes    The types of the parameters to be passed to the method
     * @return the count
     * @throws MiddlewareQueryException
     */
    //TODO BMS-148 : No longer reads from two DBs, rename.
    @SuppressWarnings("rawtypes")
    public long countAllFromCentralAndLocalByMethod(GenericDAO dao, String methodName, Object[] parameters, Class[] parameterTypes)
            throws MiddlewareQueryException {
        long count = 0;
        try {
            java.lang.reflect.Method countMethod = dao.getClass().getMethod(methodName, parameterTypes);

            if (setWorkingDatabase(Database.LOCAL, dao)) {
                count = count + ((Long) countMethod.invoke(dao, parameters)).intValue();
            }
        } catch (Exception e) { // IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
            logAndThrowException("Error in counting: " + e.getMessage(), e);
        }
        return count;
    }

    /**
     * A generic implementation of the countByXXXX() method that calls a specific count method from a DAO.     <br/>  
     * Calls the corresponding count method as specified in the parameter methodName.      <br/>
     * Retrieves data from both local and central databases.                             <br/>                        
     * Separates the negative ids from positive ids and passes to local and central instances respectively.      <br/>
     * The ids are stored in the first index of the "parameters" parameter.     <br/>                                  
     *      <br/>
     * Sample usage:     <br/>
     * <pre><code>
     *      public long countGdmsAccMetadatasetByGid(List<Integer> gids) throws MiddlewareQueryException {
     *        return super.countAllFromCentralAndLocalBySignedIdAndMethod(getAccMetadataSetDao(), "countAccMetadataSetByGids",
     *                new Object[] { gids }, new Class[] { List.class });
     *      }
     * </code></pre>
     * @param dao   The DAO to call the method from
     * @param methodName    The method to call
     * @param parameters    The parameters to be passed to the method
     * @param parameterTypes    The types of the parameters to be passed to the method
     * @return the count
     * @throws MiddlewareQueryException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public long countAllFromCentralAndLocalBySignedIdAndMethod(GenericDAO dao, String methodName, Object[] parameters,
            Class[] parameterTypes) throws MiddlewareQueryException {

        List<Integer> ids = (List<Integer>) parameters[0];

        long count = 0;
        try {
            java.lang.reflect.Method countMethod = dao.getClass().getMethod(methodName, parameterTypes);

            if (setWorkingDatabase(Database.LOCAL, dao) && (ids != null) && (!ids.isEmpty())) {
                parameters[0] = ids;
                count = count + ((Long) countMethod.invoke(dao, parameters)).intValue();
            }
        } catch (Exception e) { // IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
            logAndThrowException("Error in counting: " + e.getMessage(), e);
        }
        return count;
    }

    /**
     * A generic implementation of the countAllXXX(Database instance) method that calls countAll() from Generic DAO.     <br/>
     * Returns the count of entities from both central and local databases based on the given DAO.     <br/>
     *      <br/>
     * Sample usage:     <br/>
     * <pre><code>
     *     public long countAllGermplasm(Database instance) throws MiddlewareQueryException {
     *        return super.countFromInstance(getGermplasmDao(), instance);
     *    }
     * </code></pre>
     * @param dao The DAO to call the method from
     * @param instance The database instance to query from
     * @return The number of entities from both central and local instances
     * @throws MiddlewareQueryException
     */
    @SuppressWarnings("rawtypes")
    public long countFromInstance(GenericDAO dao, Database instance) throws MiddlewareQueryException {
        long count = 0;
        Session session = null;

        if (instance == Database.CENTRAL) {
            session = getCurrentSessionForCentral();
        } else if (instance == Database.LOCAL) {
            session = getCurrentSessionForLocal();
        }

        if (setDaoSession(dao, session)) {
            count = count + dao.countAll();
        }
        return count;
    }

    /**
     * A generic implementation of the countByXXXX(Database instance) method that calls a specific count method from a DAO.     <br/>
     * Calls the corresponding count method as specified in the parameter methodName.      <br/>
     * Retrieves data from the specified database instance     <br/>
     *      <br/>
     * Sample usage:     <br/>
     * <pre><code>
     *      public long countGermplasmByName(String name, GetGermplasmByNameModes mode, Operation op, Integer status, GermplasmNameType type,
     *            Database instance) throws MiddlewareQueryException {
     *        String nameToUse = GermplasmDataManagerUtil.getNameToUseByMode(name, mode);
     *        return super.countFromInstanceByMethod(getGermplasmDao(), instance, "countByName", new Object[] { nameToUse, op, status, type },
     *                new Class[] { String.class, Operation.class, Integer.class, GermplasmNameType.class });
     *    }
     * </code></pre>
     * @param dao The DAO to call the method from
     * @param instance The database instance to connect to
     * @param methodName The method to call
     * @param parameters The parameters to be passed to the method
     * @param parameterTypes The types of the parameters to be passed to the method
     * @return The count
     * @throws MiddlewareQueryException
     */
    @SuppressWarnings("rawtypes")
    public long countFromInstanceByMethod(GenericDAO dao, Database instance, String methodName, Object[] parameters, Class[] parameterTypes)
            throws MiddlewareQueryException {
        long count = 0;
        try {
            java.lang.reflect.Method countMethod = dao.getClass().getMethod(methodName, parameterTypes);

            if (setWorkingDatabase(instance, dao)) {
                count = count + ((Long) countMethod.invoke(dao, parameters)).intValue();
            }
        } catch (Exception e) { // IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
            logAndThrowException("Error in counting: " + e.getMessage(), e);
        }
        return count;
    }

    /**
     * A generic implementation of the countByXXXX(Integer id, ...) method that calls a specific count method from a DAO.     <br/>
     * Calls the corresponding count method as specified in the parameter methodName.      <br/>
     * Retrieves data from the corresponding database. If the given id is positive, it connects to Central, otherwise it connects to Local.     <br/>
     *      <br/>
     * Sample usage:     <br/>
     * <pre><code>
     *      public long countMarkerIDsByMapIDAndLinkageBetweenStartPosition(int mapId, String linkageGroup, double startPos, double endPos)
     *            throws MiddlewareQueryException {
     *        return super.countFromInstanceByIdAndMethod(getMarkerDao(), mapId, "countMarkerIDsByMapIDAndLinkageBetweenStartPosition", 
     *                new Object[]{mapId, linkageGroup, startPos, endPos}, new Class[]{Integer.TYPE, String.class, Double.TYPE, Double.TYPE});
     *    }
     * </code></pre>
     * @param dao   The DAO to call the method from
     * @param id    The id used to know the database instance to connect to
     * @param methodName    The method to call
     * @param parameters    The parameters to be passed to the method
     * @param parameterTypes    The types of the parameters to be passed to the method
     * @return The count
     * @throws MiddlewareQueryException
     */
    @SuppressWarnings("rawtypes")
    public long countFromInstanceByIdAndMethod(GenericDAO dao, Integer id, String methodName, Object[] parameters, Class[] parameterTypes)
            throws MiddlewareQueryException {
        long count = 0;
        try {
            java.lang.reflect.Method countMethod = dao.getClass().getMethod(methodName, parameterTypes);

            if (setWorkingDatabase(id, dao)) {
                count = count + ((Long) countMethod.invoke(dao, parameters)).intValue();
            }
        } catch (Exception e) { // IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
            logAndThrowException("Error in counting: " + e.getMessage(), e);
        }
        return count;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object save(GenericDAO dao, Object entity) throws MiddlewareQueryException {
        Session session = requireLocalDatabaseInstance();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            Object recordSaved = dao.save(entity);
            trans.commit();
            return recordSaved;
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered with saving " + entity.getClass() + "(" + entity.toString() + "): \n" + e.getMessage(), 
                    e, LOG);
            return null;
        } finally {
            session.flush();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object saveOrUpdate(GenericDAO dao, Object entity) throws MiddlewareQueryException {
        Session session = requireLocalDatabaseInstance();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            Object recordSaved = dao.saveOrUpdate(entity);
            trans.commit();
            return recordSaved;
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered with saving " + entity.getClass() + "(" + entity.toString() + "): \n" + e.getMessage(), 
                    e, LOG);
            return null;
        } finally {
            session.flush();
        }
    }

    protected void logAndThrowException(String message) throws MiddlewareQueryException {
        LOG.error(message);
        throw new MiddlewareQueryException(message);
    }


    /**
     * Logs an error based on the given message using the given Logger parameter.
     * 
     * @param message   The message to log and to set on the exception
     * @param e     The origin of the exception
     * @throws MiddlewareQueryException
     */
    protected void logAndThrowException(String message, Throwable e) throws MiddlewareQueryException {
        LOG.error(e.getMessage(), e);
        throw new MiddlewareQueryException(message + e.getMessage(), e);
    }


    /**
     * Logs an error based on the given message using the given Logger parameter.     <br/> 
     * Throws a MiddlewarewareQueryException that wraps the origin of the exception.     <br/>
     * 
     * @param message   The message to log and to set on the exception
     * @param e     The origin of the exception
     * @param log   The Logger to use
     * @throws MiddlewareQueryException
     */
    protected void logAndThrowException(String message, Throwable e, Logger log) throws MiddlewareQueryException {
        LOG.error(e.getMessage(), e);
        throw new MiddlewareQueryException(message + e.getMessage(), e);
    }

    /**
     * Retrieves the positive ids from the given list of ids
     * 
     * @param ids   The positive list of ids
     * @return the positive ids from the given list
     */
    protected List<Integer> getPositiveIds(List<Integer> ids) {
        List<Integer> positiveIds = new ArrayList<Integer>();
        for (Integer id : ids) {
            if (id >= 0) {
                positiveIds.add(id);
            }
        }
        return positiveIds;
    }

    /**
     * Retrieves the negative ids from the given list of ids
     * 
     * @param ids   The negative list of ids
     * @return the negative ids from the given list
     */
    protected List<Integer> getNegativeIds(List<Integer> ids) {
        List<Integer> negativeIds = new ArrayList<Integer>();
        for (Integer id : ids) {
            if (id < 0) {
                negativeIds.add(id);
            }
        }
        return negativeIds;
    }

    protected final TermBuilder getTermBuilder() {
    	return new TermBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final MethodBuilder getMethodBuilder() {
    	return new MethodBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final StandardVariableBuilder getStandardVariableBuilder() {
    	return new StandardVariableBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final StudyBuilder getStudyBuilder() {
    	return new StudyBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final DataSetBuilder getDataSetBuilder() {
    	return new DataSetBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final ExperimentBuilder getExperimentBuilder() {
    	return new ExperimentBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final StudyFactorBuilder getStudyFactorBuilder() {
    	return new StudyFactorBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final StudyVariateBuilder getStudyVariateBuilder() {
    	return new StudyVariateBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final StudyReferenceBuilder getStudyNodeBuilder() {
    	return new StudyReferenceBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final StockBuilder getStockBuilder() {
    	return new StockBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final TraitBuilder getTraitBuilder() {
        return new TraitBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final TrialEnvironmentBuilder getTrialEnvironmentBuilder() {
        return new TrialEnvironmentBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final VariableInfoBuilder getVariableInfoBuilder() {
    	return new VariableInfoBuilder();
    }

    protected final VariableTypeBuilder getVariableTypeBuilder() {
    	return new VariableTypeBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final NameSynonymBuilder getNameSynonymBuilder() {
    	return new NameSynonymBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final StudySearcherByNameStartSeasonCountry getProjectSearcher() {
    	return new StudySearcherByNameStartSeasonCountry(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final StudySaver getStudySaver() {
    	return new StudySaver(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final DatasetProjectSaver getDatasetProjectSaver() {
    	return new DatasetProjectSaver(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final ExperimentModelSaver getExperimentModelSaver() {
    	return new ExperimentModelSaver(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final PhenotypeSaver getPhenotypeSaver() {
    	return new PhenotypeSaver(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final GeolocationSaver getGeolocationSaver() {
    	return new GeolocationSaver(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final ProjectSaver getProjectSaver() {
    	return new ProjectSaver(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final ProjectRelationshipSaver getProjectRelationshipSaver() {
        return new ProjectRelationshipSaver(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final ProjectPropertySaver getProjectPropertySaver() {
    	return new ProjectPropertySaver(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final StockSaver getStockSaver() {
    	return new StockSaver(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final StandardVariableSaver getStandardVariableSaver() {
    	return new StandardVariableSaver(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final CvTermSaver getTermSaver() {
    	return new CvTermSaver(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final CvTermRelationshipSaver getTermRelationshipSaver() {
    	return new CvTermRelationshipSaver(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final DataSetDestroyer getDataSetDestroyer() {
    	return new DataSetDestroyer(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final TraitGroupBuilder getTraitGroupBuilder() {
        return new TraitGroupBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final PropertyBuilder getPropertyBuilder() {
        return new PropertyBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final ExperimentPropertySaver getExperimentPropertySaver() {
        return new ExperimentPropertySaver(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final ListDataPropertySaver getListDataPropertySaver(){
    	return new ListDataPropertySaver(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final FolderBuilder getFolderBuilder() {
        return new FolderBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final LocdesSaver getLocdesSaver() {
        return new LocdesSaver(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final GeolocationPropertySaver getGeolocationPropertySaver() {
    	return new GeolocationPropertySaver(sessionProviderForLocal, sessionProviderForCentral);
    }
    
    protected final ListInventoryBuilder getListInventoryBuilder() {
    	return new ListInventoryBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }

    protected final NameBuilder getNameBuilder() {
        return new NameBuilder(sessionProviderForLocal, sessionProviderForCentral);
    }
}

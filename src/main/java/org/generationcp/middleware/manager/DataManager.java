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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class DataManager.
 * Superclass of DataManager implementations.
 * Mainly used for local-central initially.
 * 
 * @author Joyce Avestro
 * @author Glenn Marintes
 */
public abstract class DataManager{

    public final static String NO_LOCAL_INSTANCE_MSG = "There is no connection to a local instance.";
    public final static String NO_CENTRAL_INSTANCE_MSG = "There is no connection to a central instance.";

    private static final Logger LOG = LoggerFactory.getLogger(DataManager.class);

    /**
     * The {@link HibernateSessionProvider} for local database.
     */
    private HibernateSessionProvider sessionProviderForLocal;

    /**
     * The {@link HibernateSessionProvider} for central database.
     */
    private HibernateSessionProvider sessionProviderForCentral;

    /**
     * Hibernate {@link Session} for local.
     */
    private Session sessionForLocal;

    /**
     * Hibernate {@link Session} for central.
     */
    private Session sessionForCentral;

    /** The Constant JDBC_BATCH_SIZE. */
    protected static final int JDBC_BATCH_SIZE = 50;

    /**
     * Contains the current active session - either local or central.
     */
    private Session activeSession;

    /**
     * Instantiates a new data manager.
     */
    public DataManager() {
    }

    /**
     * Instantiates a new data manager given session providers for local and central.
     */
    public DataManager(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        this.sessionProviderForLocal = sessionProviderForLocal;
        this.sessionProviderForCentral = sessionProviderForCentral;
    }

    /**
     * Instantiates a new data manager given sessions for local and central.
     * 
     * @param sessionForLocal
     * @param sessionForCentral
     */
    public DataManager(Session sessionForLocal, Session sessionForCentral) {
        this.sessionForLocal = sessionForLocal;
        this.sessionForCentral = sessionForCentral;
    }

    public HibernateSessionProvider getSessionProviderForLocal() {
        return sessionProviderForLocal;
    }

    public void setSessionProviderForLocal(HibernateSessionProvider sessionProviderForLocal) {
        this.sessionProviderForLocal = sessionProviderForLocal;
    }

    public HibernateSessionProvider getSessionProviderForCentral() {
        return sessionProviderForCentral;
    }

    public void setSessionProviderForCentral(HibernateSessionProvider sessionProviderForCentral) {
        this.sessionProviderForCentral = sessionProviderForCentral;
    }

    /**
     * Returns the current session for local if not null, otherwise returns null
     * 
     */
    public Session getCurrentSessionForLocal() {
        if (sessionForLocal != null) {
            return sessionForLocal;
        } else if (sessionProviderForLocal != null) {
            return sessionProviderForLocal.getSession();
        }

        return null;
    }

    /**
     * Returns the current session for central if not null, otherwise returns null
     * 
     */
    public Session getCurrentSessionForCentral() {
        if (sessionForCentral != null) {
            return sessionForCentral;
        } else if (sessionProviderForCentral != null) {
            return sessionProviderForCentral.getSession();
        }

        return null;
    }

    /**
     * Utility method that returns the appropriate {@link Session} based on the
     * given database instance.
     * 
     * @param instance
     * @return
     * @throws MiddlewareQueryException
     *             if a {@link Session} for the specified database instance is
     *             not available
     */
    protected Session getSession(Database instance) throws MiddlewareQueryException {
        if (instance == Database.CENTRAL) {
            Session session = getCurrentSessionForCentral();
            if (session == null) {
                throw new MiddlewareQueryException("Error in getSession(Database.CENTRAL): The central instance was specified "
                        + "but there is no database connection for central provided.");
            }

            return session;
        } else if (instance == Database.LOCAL) {
            Session session = getCurrentSessionForLocal();
            if (session == null) {
                throw new MiddlewareQueryException("Error in getSession(Database.LOCAL): The local instance was specified "
                        + "but there is no database connection for local provided.");
            }

            return session;
        }

        return null;
    }

    /**
     * Utility method that returns the appropriate {@link Session} based on the
     * specified <code>id</code>.
     * 
     * @param id
     * @return the {@link Session} for the central database if the specified
     *         <code>id</code> is positive or equal to zero, otherwise, this
     *         method returns the {@link Session} for the local database.
     * @throws MiddlewareQueryException
     */
    protected Session getSession(int id) {
        return id >= 0 ? getCurrentSessionForCentral() : getCurrentSessionForLocal();
    }

    /**
     * Checks for the existence of a local database session. Throws an exception if not found.
     * 
     */
    protected void requireLocalDatabaseInstance() throws MiddlewareQueryException {
        if (!setWorkingDatabase(Database.LOCAL)) {
            throw new MiddlewareQueryException(NO_LOCAL_INSTANCE_MSG);
        }
    }

    /**
     * Checks for the existence of a central database session. Throws an exception if not found.
     * 
     */
    protected void requireCentralDatabaseInstance() throws MiddlewareQueryException {
        if (!setWorkingDatabase(Database.CENTRAL)) {
            throw new MiddlewareQueryException(NO_CENTRAL_INSTANCE_MSG);
        }
    }

    /**
     * Sets the session of a given DAO. Returns true if successful. 
     * 
     */
    @SuppressWarnings("rawtypes")
    private boolean setDaoSession(GenericDAO dao, Session session) {
        if (session != null) {
            dao.setSession(session);
            return true;
        }
        return false;
    }

    protected Session getActiveSession() {
        return activeSession;
    }

    protected void setActiveSession(Session session) {
        this.activeSession = session;
    }

    /**
     * Sets the active session based on the given instance. 
     * Returns true if the active session is not null.
     * @param instance 
     *          - The database instance - either Database.LOCAL or Database.CENTRAL
     */
    protected boolean setWorkingDatabase(Database instance) {
        if (instance == Database.LOCAL) {
            activeSession = getCurrentSessionForLocal();
        } else if (instance == Database.CENTRAL) {
            activeSession = getCurrentSessionForCentral();
        }
        if (activeSession != null) {
            return true;
        }
        return false;
    }

    /**
     * Sets the active session based on the given ID.
     * If the id is positive, the central connection is assigned as the active session. 
     * If the id is negative, the local connection is assigned as the active session. 
     * Returns true if the active session is not null.
     * @param id
     */
    protected boolean setWorkingDatabase(Integer id) {
        activeSession = id >= 0 ? getCurrentSessionForCentral() : getCurrentSessionForLocal();
        if (activeSession != null) {
            return true;
        }
        return false;
    }

    /**
     * Sets the active session based on the session.
     * Returns true if the active session is not null.
     * @param session The session to assign
     */
    protected boolean setWorkingDatabase(Session session) {
        activeSession = session;
        if (activeSession != null) {
            return true;
        }
        return false;
    }

    /**
     * Sets the active session based on the given instance. 
     * Returns true if the active session is not null.
     * @param instance 
     *          - The database instance - either Database.LOCAL or Database.CENTRAL
     * @param dao - The DAO to set the active session into
     */
    @SuppressWarnings("rawtypes")
    protected boolean setWorkingDatabase(Database instance, GenericDAO dao) {
        if (instance == Database.LOCAL) {
            activeSession = getCurrentSessionForLocal();
        } else if (instance == Database.CENTRAL) {
            activeSession = getCurrentSessionForCentral();
        }
        if (activeSession != null) {
            return setDaoSession(dao, activeSession);
        }
        return false;
    }

    /**
     * Returns all the entities from both central and local databases based on the given DAO.
     * 
     * @param 
     * @return
     * @throws MiddlewareQueryException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected List getAllFromCentralAndLocal(GenericDAO dao) throws MiddlewareQueryException {
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
     * A generic implementation of the getAllXXX(int start, int numOfRows) that calls getAll() of GenericDAO.
     * This gets all the records returned by the corresponding DAO.getAll() method for the given DAO.
     * Retrieves from both local and central.
     * 
     * Sample usage:
     * 
     *      public List<Location> getAllLocations(int start, int numOfRows) throws MiddlewareQueryException {
     *          return (List<Location>) getAllFromCentralAndLocal(getLocationDao(), start, numOfRows);
     *      }
     * 
     * @param dao - the DAO to call the method from
     * @param start - the start row
     * @param numOfRows - number of rows to retrieve
     * @return List of all records
     * @throws MiddlewareQueryException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List getAllFromCentralAndLocal(GenericDAO dao, int start, int numOfRows) throws MiddlewareQueryException {
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
     * Gets the parameter types of given parameters
     * 
     * @param parameters
     * @return Class[] of parameter types
     */
    @SuppressWarnings({ "unused", "rawtypes" })
    private Class[] getParameterTypes(Object[] parameters){
        Class[] parameterTypes = new Class[parameters.length];
        if (parameters == null) {
            parameters = new Object[] {};
        }
        for (int i = 0; i < parameters.length; i++) {
            Class parameterClass = parameters[i].getClass();
            if (parameterClass.isPrimitive()){
                String parameterClassName = parameterClass.getName(); 
                if (parameterClassName.equals("boolean")){
                    parameterTypes[i] = Boolean.TYPE;
                } else if (parameterClassName.equals("byte")){
                    parameterTypes[i] = Byte.TYPE;
                } else if (parameterClassName.equals("char")){
                    parameterTypes[i] = Character.TYPE;
                } else if (parameterClassName.equals("double")){
                    parameterTypes[i] = Double.TYPE;
                } else if (parameterClassName.equals("float")){
                    parameterTypes[i] = Float.TYPE;
                } else if (parameterClassName.equals("int")){
                    parameterTypes[i] = Integer.TYPE;
                } else if (parameterClassName.equals("long")){
                    parameterTypes[i] = Long.TYPE;
                } else if (parameterClassName.equals("short")){
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
     * A generic implementation of the getXXX(Object parameter, int start, int numOfRows).
     * Calls the corresponding getXXX method as specified in the second value in the list of methods parameter.
     * 
     * Sample usage: 
     * 
     *      public List<Location> getLocationsByCountry(Country country, int start, int numOfRows) throws MiddlewareQueryException {
     *          List<String> methods = Arrays.asList("countByCountry", "getByCountry");
     *          return (List<Location>) getAllFromCentralAndLocalByMethod(getLocationDao(), methods, start, numOfRows, new Object[]{country});
     *      }
     * 
     * @param dao - the DAO to call the methods from
     * @param methods - the methods to call (countXXX and its corresponding getXXX)
     * @param start - the start row
     * @param numOfRows - number of rows to retrieve
     * @param parameters - the parameters to be passed to the methods
     * @return List of all records satisfying the given parameters
     * @throws MiddlewareQueryException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List getAllFromCentralAndLocalByMethod(GenericDAO dao, List<String> methods, int start, int numOfRows, Object[] parameters)
            throws MiddlewareQueryException {
        List toReturn = new ArrayList();
        long centralCount = 0;
        long localCount = 0;
        long relativeLimit = 0;

        // Get count method parameter types and parameters
        Class[] countMethodParameterTypes = getParameterTypes(parameters);
        Object[] countMethodParameters = parameters;

        // Get get method parameter types and parameters
        Class[] getMethodParameterTypes = new Class[parameters.length + 2];
        Object[] getMethodParameters = new Object[parameters.length + 2];

        int i = 0;
        for (i = 0; i < parameters.length; i++) {
            getMethodParameterTypes[i] = parameters[i].getClass();
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
     * A generic implementation of the countAllXXX() method that calls countAll() from Generic DAO.
     * Returns the count of entities from both central and local databases based on the given DAO.
     * 
     * Sample usage:
     * 
     *     public long countAllLocations() throws MiddlewareQueryException {
     *          return countAllFromCentralAndLocal(getLocationDao());
     *     }
     *     
     * @param dao - the DAO to call the method from
     * @return the number of entities from both central and local instances
     * @throws MiddlewareQueryException
     */
    @SuppressWarnings("rawtypes")
    protected long countAllFromCentralAndLocal(GenericDAO dao) throws MiddlewareQueryException {
        long count = 0;
        if (setDaoSession(dao, getCurrentSessionForLocal())) {
            count = count + dao.countAll();
        }
        if (setDaoSession(dao, getCurrentSessionForCentral())) {
            count = count + dao.countAll();
        }
        return count;
    }

    /**
     * A generic implementation of the countByXXXX() method that calls a specific count method from a DAO.
     * Calls the corresponding count method as specified in the parameter methodName. 
     * Retrieves data from both local and central databases.
     * 
     * Sample usage:
     *  
     *  public long countLocationsByCountry(Country country) throws MiddlewareQueryException {
     *      return countByMethod(getLocationDao(), "countByCountry", new Object[]{country});
     *  }
     * 
     * @param dao - the DAO to call the method from
     * @param methodName - the method to call
     * @param parameterTypes - the types of the parameters to be passed to the method
     * @param parameters - the parameters to be passed to the method
     * @return the count
     * @throws MiddlewareQueryException
     */
    @SuppressWarnings("rawtypes")
    public long countByMethod(GenericDAO dao, String methodName, Object[] parameters)
            throws MiddlewareQueryException {
        long count = 0;
        Class[] parameterTypes = getParameterTypes(parameters);
        try {
            java.lang.reflect.Method countMethod = dao.getClass().getMethod(methodName, parameterTypes);

            if (setWorkingDatabase(Database.LOCAL, dao)) {
                count = count + ((Long) countMethod.invoke(dao, parameters)).intValue();
            }
            if (setWorkingDatabase(Database.CENTRAL, dao)) {
                count = count + ((Long) countMethod.invoke(dao, parameters)).intValue();
            }
        } catch (Exception e) { // IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
            logAndThrowException("Error in counting: " + e.getMessage(), e);
        }
        return count;
    }

    /**
     * A generic implementation of the getXXXByXXXX() method that calls a specific get method from a DAO.
     * Calls the corresponding method that returns list type as specified in the parameter methodName.
     * 
     * Sample usage: 
     *  
     *      public List<Location> getLocationsByType(Integer type) throws MiddlewareQueryException {
     *          return (List<Location>) getListByMethod(getLocationDao(), "getByType", new Object[]{type});
     *      }
     * 
     * @param dao - the DAO to call the method from
     * @param methodName - the method to call
     * @param parameterTypes - the types of the parameters to be passed to the method
     * @param parameters - the parameters to be passed to the method
     * @return the List result
     * @throws MiddlewareQueryException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List getListByMethod(GenericDAO dao, String methodName, Object[] parameters)
            throws MiddlewareQueryException {
        List toReturn = new ArrayList();
        Class[] parameterTypes = getParameterTypes(parameters);
        try {
            java.lang.reflect.Method method = dao.getClass().getMethod(methodName, parameterTypes);

            if (setWorkingDatabase(Database.LOCAL, dao)) {
                toReturn.addAll((List) method.invoke(dao, parameters));
            }
            if (setWorkingDatabase(Database.CENTRAL, dao)) {
                toReturn.addAll((List) method.invoke(dao, parameters));
            }
        } catch (Exception e) { // IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
            logAndThrowException("Error in calling " + methodName + "(): " + e.getMessage(), e);
        }
        return toReturn;
    }

    /**
     * Logs an error based on the given message using the given Logger parameter.
     * 
     * @param message - The message to log and to set on the exception
     * @param e - The origin of the exception
     * @throws MiddlewareQueryException
     */
    protected void logAndThrowException(String message, Throwable e) throws MiddlewareQueryException {
        LOG.error(message);
        throw new MiddlewareQueryException(message);
    }

    /**
     * Logs an error based on the given message using the given Logger parameter.
     * 
     * @param message - The message to log and to set on the exception
     * @param log - The Logger to use
     * @throws MiddlewareQueryException
     */
    protected void logAndThrowException(String message, Logger log) throws MiddlewareQueryException {
        log.error(message);
        throw new MiddlewareQueryException(message);
    }

    /**
     * Logs an error based on the given message using the given Logger parameter. 
     * Throws a MiddlewarewareQueryException that wraps the origin of the exception.
     * 
     * @param message - The message to log and to set on the exception
     * @param e - The origin of the exception
     * @param log - The Logger to use
     * @throws MiddlewareQueryException
     */
    protected void logAndThrowException(String message, Throwable e, Logger log) throws MiddlewareQueryException {
        log.error(message + e.getMessage() + "\n" + e.getStackTrace());
        throw new MiddlewareQueryException(message + e.getMessage(), e);
    }

    /**
     * Rolls back a given transaction
     * @param trans
     */
    protected void rollbackTransaction(Transaction trans) {
        if (trans != null) {
            trans.rollback();
        }
    }

}

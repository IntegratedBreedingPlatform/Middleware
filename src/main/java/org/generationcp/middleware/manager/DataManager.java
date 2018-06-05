/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.Work;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.builder.DataSetBuilder;
import org.generationcp.middleware.operation.builder.ExperimentBuilder;
import org.generationcp.middleware.operation.builder.FolderBuilder;
import org.generationcp.middleware.operation.builder.ListInventoryBuilder;
import org.generationcp.middleware.operation.builder.MethodBuilder;
import org.generationcp.middleware.operation.builder.NameBuilder;
import org.generationcp.middleware.operation.builder.NameSynonymBuilder;
import org.generationcp.middleware.operation.builder.StandardVariableBuilder;
import org.generationcp.middleware.operation.builder.StockBuilder;
import org.generationcp.middleware.operation.builder.StudyBuilder;
import org.generationcp.middleware.operation.builder.StudyFactorBuilder;
import org.generationcp.middleware.operation.builder.StudyReferenceBuilder;
import org.generationcp.middleware.operation.builder.StudyVariateBuilder;
import org.generationcp.middleware.operation.builder.TermBuilder;
import org.generationcp.middleware.operation.builder.TraitBuilder;
import org.generationcp.middleware.operation.builder.TraitGroupBuilder;
import org.generationcp.middleware.operation.builder.TrialEnvironmentBuilder;
import org.generationcp.middleware.operation.builder.VariableInfoBuilder;
import org.generationcp.middleware.operation.builder.VariableTypeBuilder;
import org.generationcp.middleware.operation.destroyer.DataSetDestroyer;
import org.generationcp.middleware.operation.destroyer.StudyDestroyer;
import org.generationcp.middleware.operation.saver.CvTermRelationshipSaver;
import org.generationcp.middleware.operation.saver.CvTermSaver;
import org.generationcp.middleware.operation.saver.DatasetProjectSaver;
import org.generationcp.middleware.operation.saver.ExperimentModelSaver;
import org.generationcp.middleware.operation.saver.ExperimentPropertySaver;
import org.generationcp.middleware.operation.saver.GeolocationPropertySaver;
import org.generationcp.middleware.operation.saver.GeolocationSaver;
import org.generationcp.middleware.operation.saver.ListDataPropertySaver;
import org.generationcp.middleware.operation.saver.LocdesSaver;
import org.generationcp.middleware.operation.saver.PhenotypeSaver;
import org.generationcp.middleware.operation.saver.ProjectPropertySaver;
import org.generationcp.middleware.operation.saver.ProjectRelationshipSaver;
import org.generationcp.middleware.operation.saver.ProjectSaver;
import org.generationcp.middleware.operation.saver.StandardVariableSaver;
import org.generationcp.middleware.operation.saver.StockSaver;
import org.generationcp.middleware.operation.saver.StudySaver;
import org.generationcp.middleware.util.DatabaseBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class DataManager. Superclass of DataManager implementations. Contains generic implementation of retrieval methods. Contains getters
 * of Builder, Saver, Searcher objects.
 *
 * @author Joyce Avestro
 * @author Glenn Marintes
 */
public abstract class DataManager extends DatabaseBroker {

	private static final Logger LOG = LoggerFactory.getLogger(DataManager.class);

	public DataManager() {
	}

	public DataManager(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public DataManager(HibernateSessionProvider sessionProvider, String databaseName) {
		super(sessionProvider, databaseName);
	}

	/**
	 * Gets the parameter types of given parameters. <br/>
	 * 
	 * @param parameters
	 * @return Class[] of parameter types
	 */
	@SuppressWarnings({"unused", "rawtypes"})
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
	 * A generic implementation of the getXXX(Object parameter, int start, int numOfRows). <br/>
	 * Calls the corresponding getXXX method as specified in the second value in the list of methods parameter. <br/>
	 * <br/>
	 * Sample usage:<br/>
	 * 
	 * <pre>
	 * <code>
	 *      public List<Location> getLocationsByCountry(Country country, int start, int numOfRows) throws MiddlewareQueryException {
	 *          List<String> methods = Arrays.asList("countByCountry", "getByCountry");
	 *          return (List<Location>) getFromCentralAndLocalByMethod(getLocationDao(), methods, start, numOfRows, new Object[]{country},
	 *                                      new Class[]{Country.class});
	 *      }
	 * </code>
	 * </pre>
	 *
	 * @param dao The DAO to call the methods from
	 * @param methods The methods to call (countXXX and its corresponding getXXX)
	 * @param start The start row
	 * @param numOfRows The number of rows to retrieve
	 * @param parameters The parameters to be passed to the methods
	 * @param parameterTypes The types of the parameters to be passed to the method
	 * @return List of all records satisfying the given parameters
	 * @throws MiddlewareQueryException
	 * @deprecated
	 */
	// TODO BMS-148 : Review for how to safely remove the dual db read pattern without breaking any logic.
	// Logic changed to simply invoke the getMethod by reflection and not do any cursor positioning using count methods.
	// Ideally this method is removed entirely but is referenced in may places - something for later..
	@Deprecated
	@SuppressWarnings({"rawtypes", "unchecked"})
	public List getFromCentralAndLocalByMethod(GenericDAO dao, List<String> methods, int start, int numOfRows, Object[] parameters,
			Class[] parameterTypes) throws MiddlewareQueryException {

		List toReturn = new ArrayList();

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

		String getMethodName = methods.get(1);
		try {
			java.lang.reflect.Method getMethod = dao.getClass().getMethod(getMethodName, getMethodParameterTypes);
			dao.setSession(this.getActiveSession());
			toReturn.addAll((Collection) getMethod.invoke(dao, getMethodParameters));

		} catch (Exception e) { // IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException,
			// NoSuchMethodException
			this.logAndThrowException("Error in gettting all from central and local using " + getMethodName + ": " + e.getMessage(), e);
		}
		return toReturn;

	}

	/**
	 * A generic implementation of the getXXXByXXXX() method that calls a specific get method from a DAO. <br/>
	 * Calls the corresponding method that returns list type as specified in the parameter methodName. <br/>
	 * <br/>
	 * Sample usage: <br/>
	 *
	 * <pre>
	 * <code>
	 *      public List<Location> getLocationsByType(Integer type) throws MiddlewareQueryException {
	 *          return (List<Location>) getAllByMethod(getLocationDao(), "getByType", new Object[]{type},
	 *                      new Class[]{Integer.class});
	 *      }
	 *  </code>
	 * </pre>
	 *
	 * @param dao The DAO to call the method from
	 * @param methodName The method to call
	 * @param parameters The parameters to be passed to the method
	 * @param parameterTypes The types of the parameters to be passed to the method
	 * @return the List result
	 * @throws MiddlewareQueryException
	 * @deprecated
	 */
	@Deprecated
	@SuppressWarnings({"rawtypes", "unchecked"})
	public List getAllByMethod(GenericDAO dao, String methodName, Object[] parameters, Class[] parameterTypes)
			throws MiddlewareQueryException {

		List toReturn = new ArrayList();
		try {
			java.lang.reflect.Method method = dao.getClass().getMethod(methodName, parameterTypes);
			dao.setSession(this.getActiveSession());
			toReturn.addAll((List) method.invoke(dao, parameters));
		} catch (Exception e) {
			this.logAndThrowException("Error in calling " + methodName + "(): " + e.getMessage(), e);
		}
		return toReturn;
	}

	/**
	 * A generic implementation of the getXXXByXXXX(Database instance) method that calls a specific get method from a DAO. <br/>
	 * Calls the corresponding method that returns list type as specified in the parameter methodName. <br/>
	 * <br/>
	 * Sample usage: <br/>
	 *
	 * <pre>
	 * <code>
	 *      public List<Germplasm> getGermplasmByPrefName(String name, int start, int numOfRows, Database instance) throws MiddlewareQueryException {
	 *        return (List<Germplasm>) getFromInstanceByMethod(getGermplasmDao(), instance, "getByPrefName", new Object[]{name, start, numOfRows},
	 *              new Class[]{String.class, Integer.TYPE, Integer.TYPE});
	 *    }
	 * </code>
	 * </pre>
	 *
	 * @param dao The DAO to call the method from
	 * @param methodName The method to call
	 * @param parameters The parameters to be passed to the method. If the referenced DAO method has parameters start and numOfRows, you may
	 *        add them to this
	 * @param parameterTypes The types of the parameters passed to the methods
	 * @return the List result
	 * @throws MiddlewareQueryException
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public List getFromInstanceByMethod(GenericDAO dao, String methodName, Object[] parameters, Class[] parameterTypes)
			throws MiddlewareQueryException {
		List toReturn = new ArrayList();
		try {
			java.lang.reflect.Method method = dao.getClass().getMethod(methodName, parameterTypes);
			dao.setSession(this.getActiveSession());
			toReturn.addAll((List) method.invoke(dao, parameters));
		} catch (Exception e) { // IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException,
			// NoSuchMethodException
			this.logAndThrowException("Error in calling " + methodName + "(): " + e.getMessage(), e);
		}
		return toReturn;
	}

	/**
	 * A generic implementation of the getXXXByXXXX(Database instance) method that calls a specific get method from a DAO. <br/>
	 * Calls the corresponding method that returns list type as specified in the parameter methodName. <br/>
	 * <br/>
	 * Sample usage: <br/>
	 *
	 * <pre>
	 * <code>
	 *      public List<Germplasm> getGermplasmByPrefName(String name, int start, int numOfRows, Database instance) throws MiddlewareQueryException {
	 *        return (List<Germplasm>) getFromInstanceByMethod(getGermplasmDao(), instance, "getByPrefName", new Object[]{name, start, numOfRows},
	 *              new Class[]{String.class, Integer.TYPE, Integer.TYPE});
	 *    }
	 * </code>
	 * </pre>
	 *
	 * @param dao The DAO to call the method from
	 * @param methodName The method to call
	 * @param parameters The parameters to be passed to the method. If the referenced DAO method has parameters start and numOfRows, you may
	 *        add them to this
	 * @param parameterTypes The types of the parameters passed to the methods
	 * @return the List result
	 * @throws MiddlewareQueryException
	 * @deprecated
	 */
	@Deprecated
	@SuppressWarnings({"unchecked", "rawtypes"})
	public List getFromInstanceByMethod(GenericDAO dao, Database instance, String methodName, Object[] parameters, Class[] parameterTypes)
			throws MiddlewareQueryException {
		List toReturn = new ArrayList();
		try {
			java.lang.reflect.Method method = dao.getClass().getMethod(methodName, parameterTypes);
			dao.setSession(this.getActiveSession());
			toReturn.addAll((List) method.invoke(dao, parameters));
		} catch (Exception e) { // IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException,
			// NoSuchMethodException
			this.logAndThrowException("Error in calling " + methodName + "(): " + e.getMessage(), e);
		}
		return toReturn;
	}

	/**
	 * A generic implementation of the getXXXByXXXX(Integer id, ...) method that calls a specific get method from a DAO. <br/>
	 * Calls the corresponding method that returns list type as specified in the parameter methodName. <br/>
	 * <br/>
	 * Sample usage: <br/>
	 *
	 * <pre>
	 * <code>
	 *     public List<Integer> getMarkerIdsByDatasetId(Integer datasetId) throws MiddlewareQueryException {
	 *        return (List<Integer>) super.getFromInstanceByIdAndMethod(getMarkerMetadataSetDao(), datasetId, "getMarkerIdByDatasetId",
	 *                new Object[]{datasetId}, new Class[]{Integer.class});
	 *
	 *    }
	 * <code>
	 * </pre>
	 *
	 * @param dao The DAO to call the method from
	 * @param id The id used to get the instance to connect to
	 * @param methodName The method to call
	 * @param parameters The parameters to be passed to the method. If the referenced DAO method has parameters start and numOfRows, you may
	 *        add them to this
	 * @param parameterTypes The types of the parameters passed to the methods
	 * @return the List result
	 * @throws MiddlewareQueryException
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public List getFromInstanceByIdAndMethod(GenericDAO dao, Integer id, String methodName, Object[] parameters, Class[] parameterTypes)
			throws MiddlewareQueryException {
		List toReturn = new ArrayList();
		try {
			java.lang.reflect.Method method = dao.getClass().getMethod(methodName, parameterTypes);
			dao.setSession(this.getActiveSession());
			toReturn.addAll((List) method.invoke(dao, parameters));
		} catch (Exception e) { // IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException,
			// NoSuchMethodException
			this.logAndThrowException("Error in calling " + methodName + "(): " + e.getMessage(), e);
		}
		return toReturn;
	}

	/**
	 * A generic implementation of the countAllXXX() method that calls countAll() from Generic DAO. <br/>
	 * Returns the count of entities based on the given DAO. <br/>
	 * <br/>
	 * Sample usage: <br/>
	 *
	 * <pre>
	 * <code>
	 *     public long countAllLocations() throws MiddlewareQueryException {
	 *          return countAll(getLocationDao());
	 *     }
	 * <code>
	 * </pre>
	 *
	 * @param dao The DAO to call the method from
	 * @return The number of entities
	 * @throws MiddlewareQueryException
	 */
	@SuppressWarnings("rawtypes")
	public long countAll(GenericDAO dao) throws MiddlewareQueryException {
		long count = 0;
		dao.setSession(this.getActiveSession());
		count = count + dao.countAll();
		return count;
	}

	/**
	 * A generic implementation of the countByXXXX() method that calls a specific count method from a DAO. <br/>
	 * Calls the corresponding count method as specified in the parameter methodName. <br/>
	 * <br/>
	 * Sample usage: <br/>
	 *
	 * <pre>
	 * <code>
	 *  public long countLocationsByCountry(Country country) throws MiddlewareQueryException {
	 *      return countAllByMethod(getLocationDao(), "countByCountry", new Object[]{country}, new Class[]{Country.class});
	 *  }
	 *  </code>
	 * </pre>
	 *
	 * @param dao The DAO to call the method from
	 * @param methodName The method to call
	 * @param parameters The parameters to be passed to the method
	 * @param parameterTypes The types of the parameters to be passed to the method
	 * @return the count
	 * @throws MiddlewareQueryException
	 * @deprecated
	 */
	@SuppressWarnings("rawtypes")
	@Deprecated
	public long countAllByMethod(GenericDAO dao, String methodName, Object[] parameters, Class[] parameterTypes)
			throws MiddlewareQueryException {
		long count = 0;
		try {
			java.lang.reflect.Method countMethod = dao.getClass().getMethod(methodName, parameterTypes);
			dao.setSession(this.getActiveSession());
			count = count + ((Long) countMethod.invoke(dao, parameters)).intValue();
		} catch (Exception e) {
			this.logAndThrowException("Error in counting: " + e.getMessage(), e);
		}
		return count;
	}

	/**
	 * A generic implementation of the countAllXXX(Database instance) method that calls countAll() from Generic DAO. <br/>
	 * Returns the count of entities based on the given DAO. <br/>
	 * <br/>
	 * Sample usage: <br/>
	 *
	 * <pre>
	 * <code>
	 *     public long countAllGermplasm(Database instance) throws MiddlewareQueryException {
	 *        return super.countFromInstance(getGermplasmDao(), instance);
	 *    }
	 * </code>
	 * </pre>
	 *
	 * @param dao The DAO to call the method from
	 * @param instance The database instance to query from
	 * @return The number of entities
	 * @throws MiddlewareQueryException
	 */
	@SuppressWarnings("rawtypes")
	public long countFromInstance(GenericDAO dao, Database instance) throws MiddlewareQueryException {
		long count = 0;
		dao.setSession(this.getActiveSession());
		count = count + dao.countAll();
		return count;
	}

	/**
	 * A generic implementation of the countByXXXX(Integer id, ...) method that calls a specific count method from a DAO. <br/>
	 * Calls the corresponding count method as specified in the parameter methodName. <br/>
	 * <br/>
	 * Sample usage: <br/>
	 *
	 * <pre>
	 * <code>
	 *      public long countMarkerIDsByMapIDAndLinkageBetweenStartPosition(int mapId, String linkageGroup, double startPos, double endPos)
	 *            throws MiddlewareQueryException {
	 *        return super.countFromInstanceByIdAndMethod(getMarkerDao(), mapId, "countMarkerIDsByMapIDAndLinkageBetweenStartPosition",
	 *                new Object[]{mapId, linkageGroup, startPos, endPos}, new Class[]{Integer.TYPE, String.class, Double.TYPE, Double.TYPE});
	 *    }
	 * </code>
	 * </pre>
	 *
	 * @param dao The DAO to call the method from
	 * @param id The entity id
	 * @param methodName The method to call
	 * @param parameters The parameters to be passed to the method
	 * @param parameterTypes The types of the parameters to be passed to the method
	 * @return The count
	 * @throws MiddlewareQueryException
	 */
	@SuppressWarnings("rawtypes")
	public long countFromInstanceByIdAndMethod(GenericDAO dao, Integer id, String methodName, Object[] parameters, Class[] parameterTypes)
			throws MiddlewareQueryException {
		long count = 0;
		try {
			java.lang.reflect.Method countMethod = dao.getClass().getMethod(methodName, parameterTypes);
			dao.setSession(this.getActiveSession());
			count = count + ((Long) countMethod.invoke(dao, parameters)).intValue();
		} catch (Exception e) { // IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException,
			// NoSuchMethodException
			this.logAndThrowException("Error in counting: " + e.getMessage(), e);
		}
		return count;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public Object save(GenericDAO dao, Object entity) throws MiddlewareQueryException {

		try {
			Object recordSaved = dao.save(entity);
			return recordSaved;
		} catch (Exception e) {
			throw new MiddlewareQueryException("Error encountered with saving " + entity.getClass() + "(" + entity.toString() + "): \n"
					+ e.getMessage(), e);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public Object saveOrUpdate(GenericDAO dao, Object entity) throws MiddlewareQueryException {
		try {
			Object recordSaved = dao.saveOrUpdate(entity);
			return recordSaved;
		} catch (Exception e) {
			throw new MiddlewareQueryException("Error encountered with saving " + entity.getClass() + "(" + entity.toString() + "): \n"
					+ e.getMessage(), e);
		}
	}

	/**
	 * Logs an error based on the given message using the given Logger parameter. TODO: Deprecate this method and do not use. It is referred
	 * as anti pattern. Reference: https://today.java.net/article/2006/04/04/exception-handling-antipatterns#logAndThrow
	 *
	 * @param message The message to log and to set on the exception
	 * @param e The origin of the exception
	 * @throws MiddlewareQueryException
	 */
	protected void logAndThrowException(String message, Throwable e) throws MiddlewareQueryException {
		DataManager.LOG.error(e.getMessage(), e);
		throw new MiddlewareQueryException(message, e);
	}

	/**
	 * Retrieves the positive ids from the given list of ids
	 * 
	 *
	 * @param ids The positive list of ids
	 * @return the positive ids from the given list
	 */
	protected List<Integer> getPositiveIds(List<Integer> ids) {
		List<Integer> positiveIds = new ArrayList<>();
		for (Integer id : ids) {
			if (id >= 0) {
				positiveIds.add(id);
			}
		}
		return positiveIds;
	}

	void doInTransaction(Work work) throws MiddlewareQueryException {

		try {
			work.doWork();
		} catch (Exception e) {
			throw new MiddlewareQueryException("Error encountered with " + work.getName() + e.getMessage(), e);
		}
	}

	protected final TermBuilder getTermBuilder() {
		return new TermBuilder(this.sessionProvider);
	}

	protected final MethodBuilder getMethodBuilder() {
		return new MethodBuilder(this.sessionProvider);
	}

	protected final StandardVariableBuilder getStandardVariableBuilder() {
		return new StandardVariableBuilder(this.sessionProvider);
	}

	protected final StudyBuilder getStudyBuilder() {
		return new StudyBuilder(this.sessionProvider);
	}

	protected final DataSetBuilder getDataSetBuilder() {
		return new DataSetBuilder(this.sessionProvider);
	}

	protected final ExperimentBuilder getExperimentBuilder() {
		return new ExperimentBuilder(this.sessionProvider);
	}

	protected final StudyFactorBuilder getStudyFactorBuilder() {
		return new StudyFactorBuilder(this.sessionProvider);
	}

	protected final StudyVariateBuilder getStudyVariateBuilder() {
		return new StudyVariateBuilder(this.sessionProvider);
	}

	protected final StudyReferenceBuilder getStudyNodeBuilder() {
		return new StudyReferenceBuilder(this.sessionProvider);
	}

	protected final StockBuilder getStockBuilder() {
		return new StockBuilder(this.sessionProvider);
	}

	protected final TraitBuilder getTraitBuilder() {
		return new TraitBuilder(this.sessionProvider);
	}

	protected final TrialEnvironmentBuilder getTrialEnvironmentBuilder() {
		return new TrialEnvironmentBuilder(this.sessionProvider);
	}

	protected final VariableInfoBuilder getVariableInfoBuilder() {
		return new VariableInfoBuilder();
	}

	protected final VariableTypeBuilder getVariableTypeBuilder() {
		return new VariableTypeBuilder(this.sessionProvider);
	}

	protected final NameSynonymBuilder getNameSynonymBuilder() { return new NameSynonymBuilder(this.sessionProvider); }

	protected final StudySaver getStudySaver() {
		return new StudySaver(this.sessionProvider);
	}

	protected final DatasetProjectSaver getDatasetProjectSaver() {
		return new DatasetProjectSaver(this.sessionProvider);
	}

	protected final ExperimentModelSaver getExperimentModelSaver() {
		return new ExperimentModelSaver(this.sessionProvider);
	}

	protected final PhenotypeSaver getPhenotypeSaver() {
		return new PhenotypeSaver(this.sessionProvider);
	}

	protected final GeolocationSaver getGeolocationSaver() {
		return new GeolocationSaver(this.sessionProvider);
	}

	protected final ProjectSaver getProjectSaver() {
		return new ProjectSaver(this.sessionProvider);
	}

	protected final ProjectRelationshipSaver getProjectRelationshipSaver() {
		return new ProjectRelationshipSaver(this.sessionProvider);
	}

	protected final ProjectPropertySaver getProjectPropertySaver() {
		return new ProjectPropertySaver(this.sessionProvider);
	}

	protected final StockSaver getStockSaver() {
		return new StockSaver(this.sessionProvider);
	}

	protected final StandardVariableSaver getStandardVariableSaver() {
		return new StandardVariableSaver(this.sessionProvider);
	}

	protected final CvTermSaver getTermSaver() {
		return new CvTermSaver(this.sessionProvider);
	}

	protected final CvTermRelationshipSaver getTermRelationshipSaver() {
		return new CvTermRelationshipSaver(this.sessionProvider);
	}

	protected final DataSetDestroyer getDataSetDestroyer() {
		return new DataSetDestroyer(this.sessionProvider);
	}

	protected final TraitGroupBuilder getTraitGroupBuilder() {
		return new TraitGroupBuilder(this.sessionProvider);
	}

	protected final ExperimentPropertySaver getExperimentPropertySaver() {
		return new ExperimentPropertySaver(this.sessionProvider);
	}

	protected final ListDataPropertySaver getListDataPropertySaver() {
		return new ListDataPropertySaver(this.sessionProvider);
	}

	protected final FolderBuilder getFolderBuilder() {
		return new FolderBuilder(this.sessionProvider);
	}

	protected final LocdesSaver getLocdesSaver() {
		return new LocdesSaver(this.sessionProvider);
	}

	protected final GeolocationPropertySaver getGeolocationPropertySaver() {
		return new GeolocationPropertySaver(this.sessionProvider);
	}

	protected final ListInventoryBuilder getListInventoryBuilder() {
		return new ListInventoryBuilder(this.sessionProvider);
	}

	protected final NameBuilder getNameBuilder() {
		return new NameBuilder(this.sessionProvider);
	}

	protected final StudyDestroyer getStudyDestroyer() {
		return new StudyDestroyer(this.sessionProvider);
	}
}

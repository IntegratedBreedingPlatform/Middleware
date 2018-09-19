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
import org.generationcp.middleware.operation.builder.StudyTypeBuilder;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

	public DataManager(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public DataManager(final HibernateSessionProvider sessionProvider, final String databaseName) {
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
		final Class[] parameterTypes = new Class[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			final Class parameterClass = parameters[i].getClass();
			if (parameterClass.isPrimitive()) {
				final String parameterClassName = parameterClass.getName();
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
	 * A generic implementation of the countAllXXX() method that calls countAll() from Generic DAO. <br/>
	 * Returns the count of entities based on the given DAO. <br/>
	 * <br/>
	 * Sample usage: <br/>
	 *
	 * <pre>
	 * <code>
	 *     public long countAllLocations() {
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
	public long countAll(final GenericDAO dao) {
		long count = 0;
		dao.setSession(this.getActiveSession());
		count = count + dao.countAll();
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
	 *     public long countAllGermplasm(Database instance) {
	 *        return super.countFromInstance(getGermplasmDao(), instance);
	 *    }
	 * </code>
	 * </pre>
	 *
	 * @param dao The DAO to call the method from
	 * @return The number of entities
	 * @throws MiddlewareQueryException
	 */
	@SuppressWarnings("rawtypes")
	public long countFromInstance(final GenericDAO dao) {
		long count = 0;
		dao.setSession(this.getActiveSession());
		count = count + dao.countAll();
		return count;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public Object save(final GenericDAO dao, final Object entity) {

		try {
			final Object recordSaved = dao.save(entity);
			return recordSaved;
		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error encountered with saving " + entity.getClass() + "(" + entity.toString() + "): \n"
					+ e.getMessage(), e);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public Object saveOrUpdate(final GenericDAO dao, final Object entity) {
		try {
			final Object recordSaved = dao.saveOrUpdate(entity);
			return recordSaved;
		} catch (final Exception e) {
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
	protected void logAndThrowException(final String message, final Throwable e) {
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
	protected List<Integer> getPositiveIds(final List<Integer> ids) {
		final List<Integer> positiveIds = new ArrayList<>();
		for (final Integer id : ids) {
			if (id >= 0) {
				positiveIds.add(id);
			}
		}
		return positiveIds;
	}

	void doInTransaction(final Work work) {

		try {
			work.doWork();
		} catch (final Exception e) {
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

	protected final StudyTypeBuilder getStudyTypeBuilder() {
		return new StudyTypeBuilder();
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

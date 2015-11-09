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

import java.io.Serializable;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.MBDTDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.manager.api.PresetDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.generationcp.middleware.manager.ontology.OntologyDaoFactory;
import org.generationcp.middleware.manager.ontology.OntologyMethodDataManagerImpl;
import org.generationcp.middleware.manager.ontology.OntologyPropertyDataManagerImpl;
import org.generationcp.middleware.manager.ontology.OntologyScaleDataManagerImpl;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.TermDataManagerImpl;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.api.TermDataManager;
import org.generationcp.middleware.operation.transformer.etl.StandardVariableTransformer;
import org.generationcp.middleware.service.DataImportServiceImpl;
import org.generationcp.middleware.service.FieldbookServiceImpl;
import org.generationcp.middleware.service.InventoryServiceImpl;
import org.generationcp.middleware.service.OntologyServiceImpl;
import org.generationcp.middleware.service.ReportServiceImpl;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.service.api.InventoryService;
import org.generationcp.middleware.service.api.OntologyService;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.service.api.ReportService;
import org.generationcp.middleware.service.pedigree.PedigreeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * The {@link ManagerFactory} is a convenience class intended to provide methods to get instances of the Manager/Service implementations
 * provided by the Middleware.
 * </p>
 *
 * @author Kevin Manansala
 * @author Glenn Marintes
 */
public class ManagerFactory implements Serializable {

	private static final long serialVersionUID = -2846462010022009403L;

	private static final Logger LOG = LoggerFactory.getLogger(ManagerFactory.class);

	private HibernateSessionProvider sessionProvider;

	private String databaseName;
	private String cropName;
	private String pedigreeProfile;
	private static ThreadLocal<ManagerFactory> currentManagerFactory = new ThreadLocal<ManagerFactory>();

	public ManagerFactory() {
		ManagerFactory.currentManagerFactory.set(this);
	}

	public static ThreadLocal<ManagerFactory> getCurrentManagerFactoryThreadLocal() {
		return ManagerFactory.currentManagerFactory;
	}

	public HibernateSessionProvider getSessionProvider() {
		return this.sessionProvider;
	}

	public void setSessionProvider(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public GermplasmDataManager getGermplasmDataManager() {
		return new GermplasmDataManagerImpl(this.sessionProvider, this.databaseName);
	}

	public PedigreeDataManager getPedigreeDataManager() {
		return new PedigreeDataManagerImpl(this.sessionProvider, this.databaseName);
	}

	public CrossStudyDataManager getCrossStudyDataManager() {
		return new CrossStudyDataManagerImpl(this.sessionProvider);
	}

	public GermplasmListManager getGermplasmListManager() {
		return new GermplasmListManagerImpl(this.sessionProvider, this.databaseName);
	}

	public LocationDataManager getLocationDataManager() {
		return new LocationDataManagerImpl(this.sessionProvider);
	}

	public OntologyDataManager getOntologyDataManager() {
		return new OntologyDataManagerImpl(this.sessionProvider);
	}

	public TermDataManager getTermDataManager() {
		return new TermDataManagerImpl(this.sessionProvider);
	}

	public OntologyMethodDataManager getOntologyMethodDataManager() {
		return new OntologyMethodDataManagerImpl(this.sessionProvider);
	}

	public OntologyPropertyDataManager getOntologyPropertyDataManager() {
		return new OntologyPropertyDataManagerImpl(this.sessionProvider);
	}

	public OntologyScaleDataManager getOntologyScaleDataManager() {
		return new OntologyScaleDataManagerImpl(this.sessionProvider);
	}

	public OntologyVariableDataManager getOntologyVariableDataManager() {
		return new OntologyVariableDataManagerImpl(this.getOntologyMethodDataManager(), this.getOntologyPropertyDataManager(),
				this.getOntologyScaleDataManager(), this.sessionProvider);
	}

	public PresetDataManager getPresetDataManager() {
		return new PresetDataManagerImpl(this.sessionProvider);
	}

	public StudyDataManager getStudyDataManager() {
		return new StudyDataManagerImpl(this.sessionProvider);
	}

	public StudyDataManager getNewStudyDataManager() {
		return new StudyDataManagerImpl(this.sessionProvider, this.databaseName);
	}

	public OntologyDataManager getNewOntologyDataManager() {
		return new OntologyDataManagerImpl(this.sessionProvider);
	}

	public InventoryDataManager getInventoryDataManager() {
		return new InventoryDataManagerImpl(this.sessionProvider, this.databaseName);
	}

	public UserProgramStateDataManager getUserProgramStateDataManager() {
		return new UserProgramStateDataManagerImpl(this.sessionProvider);
	}

	public GenotypicDataManager getGenotypicDataManager() {
		return new GenotypicDataManagerImpl(this.sessionProvider);
	}

	public UserDataManager getUserDataManager() {
		return new UserDataManagerImpl(this.sessionProvider);
	}

	public FieldbookService getFieldbookMiddlewareService() {
		return new FieldbookServiceImpl(this.sessionProvider, this.databaseName);
	}

	public InventoryService getInventoryMiddlewareService() {
		return new InventoryServiceImpl(this.sessionProvider);
	}

	public DataImportService getDataImportService() {
		return new DataImportServiceImpl(this.sessionProvider);
	}

	public OntologyService getOntologyService() {
		return new OntologyServiceImpl(this.sessionProvider);
	}

	public MBDTDataManager getMbdtDataManager() {
		return new MBDTDataManagerImpl(this.sessionProvider);
	}

	public ReportService getReportService() {
		return new ReportServiceImpl(this.sessionProvider, this.databaseName);
	}

	public PedigreeService getPedigreeService() {
		return PedigreeFactory.getPedigreeService(this.sessionProvider, this.pedigreeProfile, this.cropName);
	}

	/*
	 * This was exposed so that it can be access in the jUnit
	 */
	public PedigreeService getPedigreeService(final String profile, final String crop) {
		return PedigreeFactory.getPedigreeService(this.sessionProvider, profile, crop);
	}

	/**
	 * Closes the db connection by shutting down the HibernateUtil object
	 */
	public void close() {
		ManagerFactory.LOG.trace("Closing ManagerFactory...");

		if (this.sessionProvider != null) {
			this.sessionProvider.close();
		}

		ManagerFactory.currentManagerFactory.remove();
		ManagerFactory.LOG.trace("Closing ManagerFactory...Done.");
	}

	public String getDatabaseName() {
		return this.databaseName;
	}

	public void setDatabaseName(final String localDatabaseName) {
		this.databaseName = localDatabaseName;
	}

	public String getCropName() {
		return this.cropName;
	}

	public void setCropName(final String cropName) {
		this.cropName = cropName;
	}

	public String getPedigreeProfile() {
		return this.pedigreeProfile;
	}

	public void setPedigreeProfile(final String pedigreeProfile) {
		this.pedigreeProfile = pedigreeProfile;
	}

	public OntologyDaoFactory getOntologyDaoFactory() {
		return new OntologyDaoFactory(this.sessionProvider);
	}

	public StandardVariableTransformer getStandardVariableTransformer() {
		return new StandardVariableTransformer(this.sessionProvider);
	}

}

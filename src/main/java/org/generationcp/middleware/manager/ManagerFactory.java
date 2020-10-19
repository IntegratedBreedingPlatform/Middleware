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

import org.generationcp.middleware.api.breedingmethod.BreedingMethodService;
import org.generationcp.middleware.api.breedingmethod.BreedingMethodServiceImpl;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.*;
import org.generationcp.middleware.manager.ontology.*;
import org.generationcp.middleware.manager.ontology.api.*;
import org.generationcp.middleware.operation.builder.DataSetBuilder;
import org.generationcp.middleware.operation.builder.StockBuilder;
import org.generationcp.middleware.operation.builder.TrialEnvironmentBuilder;
import org.generationcp.middleware.operation.builder.WorkbookBuilder;
import org.generationcp.middleware.operation.saver.WorkbookSaver;
import org.generationcp.middleware.operation.transformer.etl.StandardVariableTransformer;
import org.generationcp.middleware.service.*;
import org.generationcp.middleware.service.api.*;
import org.generationcp.middleware.service.api.dataset.DatasetTypeService;
import org.generationcp.middleware.service.api.derived_variables.DerivedVariableService;
import org.generationcp.middleware.service.api.derived_variables.FormulaService;
import org.generationcp.middleware.service.api.gdms.DatasetService;
import org.generationcp.middleware.service.api.study.StudyEntryService;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.generation.ExperimentDesignService;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceService;
import org.generationcp.middleware.service.impl.GermplasmGroupingServiceImpl;
import org.generationcp.middleware.service.impl.GermplasmNamingReferenceDataResolverImpl;
import org.generationcp.middleware.service.impl.KeySequenceRegisterServiceImpl;
import org.generationcp.middleware.service.impl.dataset.DatasetTypeServiceImpl;
import org.generationcp.middleware.service.impl.derived_variables.DerivedVariableServiceImpl;
import org.generationcp.middleware.service.impl.derived_variables.FormulaServiceImpl;
import org.generationcp.middleware.service.impl.gdms.DatasetServiceImpl;
import org.generationcp.middleware.service.impl.study.SampleListServiceImpl;
import org.generationcp.middleware.service.impl.study.SampleServiceImpl;
import org.generationcp.middleware.service.impl.study.StudyEntryServiceImpl;
import org.generationcp.middleware.service.impl.study.StudyServiceImpl;
import org.generationcp.middleware.service.impl.study.generation.ExperimentDesignServiceImpl;
import org.generationcp.middleware.service.impl.study.germplasm.source.GermplasmStudySourceServiceImpl;
import org.generationcp.middleware.service.pedigree.PedigreeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

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
		return new PedigreeDataManagerImpl(this.sessionProvider);
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
				this.getOntologyScaleDataManager(), this.getFormulaService(), this.sessionProvider);
	}

	public PresetService getPresetService() {
		return new PresetServiceImpl(this.sessionProvider);
	}

	public StudyDataManager getStudyDataManager() {
		return new StudyDataManagerImpl(this.sessionProvider);
	}

	public StudyDataManager getNewStudyDataManager() {
		return new StudyDataManagerImpl(this.sessionProvider, this.databaseName);
	}

	public org.generationcp.middleware.service.api.dataset.DatasetService getDatasetMiddlewareService() {
		return new org.generationcp.middleware.service.impl.dataset.DatasetServiceImpl(this.sessionProvider);
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

	public DaoFactory getDaoFactory() {
		return new DaoFactory(this.sessionProvider);
	}

	public StandardVariableTransformer getStandardVariableTransformer() {
		return new StandardVariableTransformer(this.sessionProvider);
	}

	public GermplasmGroupingService getGermplasmGroupingService() {
		return new GermplasmGroupingServiceImpl(this.sessionProvider);
	}

	public GermplasmNamingReferenceDataResolver getGermplasmNamingReferenceDataResolver() {
		// In future we can switch implementation based on profile/crop.
		// Currently just construct and return the only (CIMMYT maize) impl we have.
		return new GermplasmNamingReferenceDataResolverImpl(this.sessionProvider);
	}

	public KeySequenceRegisterService getKeySequenceRegisterService() {
		return new KeySequenceRegisterServiceImpl(this.sessionProvider);
	}

	public StudyService getStudyService() {
		return new StudyServiceImpl(this.sessionProvider);
	}

	public SampleListService getSampleListService() {
		return new SampleListServiceImpl(this.sessionProvider);
	}

	public SampleService getSampleService() {
		return new SampleServiceImpl(this.sessionProvider);
	}

	public FormulaService getFormulaService() {
		return new FormulaServiceImpl(this.sessionProvider);
	}

	public DatasetService getDatasetService() {
		return new DatasetServiceImpl(this.sessionProvider);
	}

	public DatasetTypeService getDatasetTypeService() {
		return new DatasetTypeServiceImpl(this.sessionProvider);
	}

	public DerivedVariableService getDerivedVariableService() {
		return new DerivedVariableServiceImpl(this.sessionProvider);
	}

	public ExperimentDesignService getExperimentDesignService() {
		return new ExperimentDesignServiceImpl(this.sessionProvider);
	}

	public TrialEnvironmentBuilder getTrialEnvironmentBuilder() {
		return new TrialEnvironmentBuilder(this.sessionProvider);
	}

	public DataSetBuilder getDataSetBuilder() {
		return new DataSetBuilder(this.sessionProvider);
	}

	public StockBuilder getStockBuilder() {
		return new StockBuilder(this.sessionProvider);
	}

	public WorkbookBuilder getWorkbookBuilder() {
		return new WorkbookBuilder(this.sessionProvider);
	}

	public WorkbookSaver getWorkbookSaver() {
		return new WorkbookSaver(this.sessionProvider);
	}

	public StudyEntryService getStudyEntryService() {
		return new StudyEntryServiceImpl(this.sessionProvider);
	}

	public GermplasmStudySourceService getGermplasmStudySourceService() {
		return new GermplasmStudySourceServiceImpl(this.sessionProvider);
	}

	public InventoryService getInventoryService() {
		return new InventoryServiceImpl(this.sessionProvider);
	}

	public SearchRequestService getSearchRequestService() {
		return new SearchRequestServiceImpl(this.sessionProvider);
	}

	public BreedingMethodService getBreedingMethodService() {
		return new BreedingMethodServiceImpl(this.sessionProvider);
	}
}

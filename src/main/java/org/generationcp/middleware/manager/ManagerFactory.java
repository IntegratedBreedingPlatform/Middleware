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
import org.generationcp.middleware.api.file.FileMetadataService;
import org.generationcp.middleware.api.file.FileMetadataServiceImpl;
import org.generationcp.middleware.api.germplasm.GermplasmAttributeService;
import org.generationcp.middleware.api.germplasm.GermplasmAttributeServiceImpl;
import org.generationcp.middleware.api.germplasm.GermplasmNameService;
import org.generationcp.middleware.api.germplasm.GermplasmNameServiceImpl;
import org.generationcp.middleware.api.germplasm.GermplasmService;
import org.generationcp.middleware.api.germplasm.GermplasmServiceImpl;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchService;
import org.generationcp.middleware.api.germplasm.search.GermplasmSearchServiceImpl;
import org.generationcp.middleware.api.germplasmlist.GermplasmListService;
import org.generationcp.middleware.api.germplasmlist.GermplasmListServiceImpl;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataService;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataServiceImpl;
import org.generationcp.middleware.api.location.LocationService;
import org.generationcp.middleware.api.location.LocationServiceImpl;
import org.generationcp.middleware.api.nametype.GermplasmNameTypeService;
import org.generationcp.middleware.api.nametype.GermplasmNameTypeServiceImpl;
import org.generationcp.middleware.api.ontology.OntologyVariableService;
import org.generationcp.middleware.api.ontology.OntologyVariableServiceImpl;
import org.generationcp.middleware.api.program.ProgramFavoriteService;
import org.generationcp.middleware.api.program.ProgramFavoriteServiceImpl;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.manager.api.PresetService;
import org.generationcp.middleware.manager.api.SearchRequestService;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
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
import org.generationcp.middleware.operation.builder.DataSetBuilder;
import org.generationcp.middleware.operation.builder.StockBuilder;
import org.generationcp.middleware.operation.builder.TrialEnvironmentBuilder;
import org.generationcp.middleware.operation.builder.WorkbookBuilder;
import org.generationcp.middleware.operation.saver.WorkbookSaver;
import org.generationcp.middleware.operation.transformer.etl.StandardVariableTransformer;
import org.generationcp.middleware.service.DataImportServiceImpl;
import org.generationcp.middleware.service.FieldbookServiceImpl;
import org.generationcp.middleware.service.OntologyServiceImpl;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.service.api.FieldbookService;
import org.generationcp.middleware.service.api.GermplasmGroupingService;
import org.generationcp.middleware.service.api.KeySequenceRegisterService;
import org.generationcp.middleware.service.api.OntologyService;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.service.api.SampleListService;
import org.generationcp.middleware.service.api.SampleService;
import org.generationcp.middleware.service.api.analysis.SiteAnalysisService;
import org.generationcp.middleware.service.api.dataset.DatasetTypeService;
import org.generationcp.middleware.service.api.derived_variables.DerivedVariableService;
import org.generationcp.middleware.service.api.derived_variables.FormulaService;
import org.generationcp.middleware.service.api.gdms.DatasetService;
import org.generationcp.middleware.service.api.inventory.LotService;
import org.generationcp.middleware.service.api.ontology.VariableDataValidatorFactory;
import org.generationcp.middleware.service.api.ontology.VariableDataValidatorFactoryImpl;
import org.generationcp.middleware.service.api.study.StudyEntryService;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.generation.ExperimentDesignService;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceService;
import org.generationcp.middleware.service.impl.GermplasmGroupingServiceImpl;
import org.generationcp.middleware.service.impl.KeySequenceRegisterServiceImpl;
import org.generationcp.middleware.service.impl.analysis.SiteAnalysisServiceImpl;
import org.generationcp.middleware.service.impl.dataset.DatasetTypeServiceImpl;
import org.generationcp.middleware.service.impl.derived_variables.DerivedVariableServiceImpl;
import org.generationcp.middleware.service.impl.derived_variables.FormulaServiceImpl;
import org.generationcp.middleware.service.impl.gdms.DatasetServiceImpl;
import org.generationcp.middleware.service.impl.inventory.LotServiceImpl;
import org.generationcp.middleware.service.impl.study.SampleListServiceImpl;
import org.generationcp.middleware.service.impl.study.SampleServiceImpl;
import org.generationcp.middleware.service.impl.study.StudyEntryServiceImpl;
import org.generationcp.middleware.service.impl.study.StudyInstanceServiceImpl;
import org.generationcp.middleware.service.impl.study.StudyServiceImpl;
import org.generationcp.middleware.service.impl.study.generation.ExperimentDesignServiceImpl;
import org.generationcp.middleware.service.impl.study.generation.ExperimentModelGenerator;
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

	private String cropName;
	private String pedigreeProfile;
	private static final ThreadLocal<ManagerFactory> currentManagerFactory = new ThreadLocal<ManagerFactory>();

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
		return new GermplasmDataManagerImpl(this.sessionProvider);
	}

	public PedigreeDataManager getPedigreeDataManager() {
		return new PedigreeDataManagerImpl(this.sessionProvider);
	}

	public CrossStudyDataManager getCrossStudyDataManager() {
		return new CrossStudyDataManagerImpl(this.sessionProvider);
	}

	public GermplasmListManager getGermplasmListManager() {
		return new GermplasmListManagerImpl(this.sessionProvider);
	}

	public LocationDataManager getLocationDataManager() {
		return new LocationDataManagerImpl(this.sessionProvider);
	}

	public LocationService getLocationService() {
		return new LocationServiceImpl(this.sessionProvider);
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
		return new StudyDataManagerImpl(this.sessionProvider);
	}

	public org.generationcp.middleware.service.api.dataset.DatasetService getDatasetMiddlewareService() {
		return new org.generationcp.middleware.service.impl.dataset.DatasetServiceImpl(this.sessionProvider);
	}

	public OntologyDataManager getNewOntologyDataManager() {
		return new OntologyDataManagerImpl(this.sessionProvider);
	}

	public InventoryDataManager getInventoryDataManager() {
		return new InventoryDataManagerImpl(this.sessionProvider);
	}

	public UserProgramStateDataManager getUserProgramStateDataManager() {
		return new UserProgramStateDataManagerImpl(this.sessionProvider);
	}

	public GenotypicDataManager getGenotypicDataManager() {
		return new GenotypicDataManagerImpl(this.sessionProvider);
	}

	public FieldbookService getFieldbookMiddlewareService() {
		return new FieldbookServiceImpl(this.sessionProvider);
	}

	public DataImportService getDataImportService() {
		return new DataImportServiceImpl(this.sessionProvider);
	}

	public OntologyService getOntologyService() {
		return new OntologyServiceImpl(this.sessionProvider);
	}

	public PedigreeService getPedigreeService() {
		return PedigreeFactory.getPedigreeService(this.sessionProvider, this.pedigreeProfile, this.cropName);
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
		return new StandardVariableTransformer();
	}

	public GermplasmGroupingService getGermplasmGroupingService() {
		return new GermplasmGroupingServiceImpl(this.sessionProvider);
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

	public SearchRequestService getSearchRequestService() {
		return new SearchRequestServiceImpl(this.sessionProvider);
	}

	public StudyInstanceService studyInstanceMiddlewareService() {
		return new StudyInstanceServiceImpl(this.sessionProvider);
	}

	public ExperimentModelGenerator getExperimentModelGenerator() {
		return new ExperimentModelGenerator(this.sessionProvider);
	}

	public BreedingMethodService getBreedingMethodService() {
		return new BreedingMethodServiceImpl(this.sessionProvider);
	}

	public GermplasmService getGermplasmService() {
		return new GermplasmServiceImpl(this.sessionProvider);
	}

	public GermplasmNameTypeService getGermplasmNameTypeService() {
		return new GermplasmNameTypeServiceImpl(this.sessionProvider);
	}

	public GermplasmNameService getGermplasmNameService() {
		return new GermplasmNameServiceImpl(this.sessionProvider);
	}

	public GermplasmAttributeService getGermplasmAttributeService() {
		return new GermplasmAttributeServiceImpl(this.sessionProvider);
	}

	public GermplasmListService getGermplasmListService() {
		return new GermplasmListServiceImpl(this.sessionProvider);
	}

	public GermplasmSearchService getGermplasmSearchService() {
		return new GermplasmSearchServiceImpl(this.sessionProvider);
	}

	public VariableDataValidatorFactory getVariableDataValidatorFactory() {
		return new VariableDataValidatorFactoryImpl();
	}

	public ProgramFavoriteService getProgramFavoriteService() {
		return new ProgramFavoriteServiceImpl(this.sessionProvider);
	}

	public GermplasmListDataService getGermplasmListDataService() {
		return new GermplasmListDataServiceImpl(this.sessionProvider);
	}

	public FileMetadataService getFileMetadataService() {
		return new FileMetadataServiceImpl(this.sessionProvider);
	}

	public SiteAnalysisService getSiteAnalysisService() {
		return new SiteAnalysisServiceImpl(this.sessionProvider);
	}

	public OntologyVariableService getOntologyVariableService() {
		return new OntologyVariableServiceImpl(this.sessionProvider);
	}

	public LotService getLotService() {
		return new LotServiceImpl(this.sessionProvider);
	}

}

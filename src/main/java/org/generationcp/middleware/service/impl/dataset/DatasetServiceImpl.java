package org.generationcp.middleware.service.impl.dataset;

import com.google.common.collect.Lists;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.domain.ontology.VariableType;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableService;
import org.generationcp.middleware.service.impl.study.DesignFactors;
import org.generationcp.middleware.service.impl.study.GermplasmDescriptors;
import org.generationcp.middleware.service.impl.study.MeasurementVariableServiceImpl;
import org.hibernate.Session;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by clarysabel on 10/22/18.
 */
@Transactional
public class DatasetServiceImpl implements DatasetService {

	private static final Logger LOG = LoggerFactory.getLogger(DatasetServiceImpl.class);
	public static final String[] FIXED_GERMPLASM_DESCRIPTOR = {"GID", "DESIGNATION", "ENTRY_NO", "ENTRY_TYPE", "ENTRY_CODE", "OBS_UNIT_ID"};
	public static final String[] FIXED_DESIGN_FACTORS =
		{"REP_NO", "PLOT_NO", "BLOCK_NO", "ROW", "COL", "FIELDMAP COLUMN", "FIELDMAP RANGE"};

	private DaoFactory daoFactory;

	private DmsProjectDao dmsProjectDao;

	private ExperimentDao experimentDao;

	private OntologyVariableDataManager ontologyVariableDataManager;

	private MeasurementVariableService measurementVariableService;

	private GermplasmDescriptors germplasmDescriptors;

	private DesignFactors designFactors;

	DatasetServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public DatasetServiceImpl(final HibernateSessionProvider sessionProvider) {
		final Session currentSession = sessionProvider.getSessionFactory().getCurrentSession();
		this.daoFactory = new DaoFactory(sessionProvider);
		this.dmsProjectDao = this.daoFactory.getDmsProjectDao();
		this.experimentDao = this.getExperimentDao(currentSession);
		this.ontologyVariableDataManager = new OntologyVariableDataManagerImpl(sessionProvider);
		this.measurementVariableService = new MeasurementVariableServiceImpl(currentSession);
		this.germplasmDescriptors = new GermplasmDescriptors(currentSession);
		this.designFactors = new DesignFactors(currentSession);
	}

	public ExperimentDao getExperimentDao(final Session currentSession) {
		final ExperimentDao experimentDao = new ExperimentDao();
		experimentDao.setSession(currentSession);
		return experimentDao;
	}

	@Override
	public long countPhenotypes(final Integer datasetId, final List<Integer> traitIds) {
		return this.daoFactory.getPhenotypeDAO().countPhenotypesForDataset(datasetId, traitIds);
	}

	@Override
	public Integer generateSubObservationDataset (final Integer studyId, final String datasetName, final Integer datasetTypeId, final Integer[] instanceIds,
			final Integer observationUnitVariableId, final Integer numberOfSubObservationUnits) {

		return null;
	}

	protected void setDaoFactory(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	@Override
	public List<ObservationUnitRow> getObservationUnitRows(
		final int studyId, final int datasetId, final int instanceId, final int pageNumber, final int pageSize,
		final String sortBy, final String sortOrder) {
		final List<MeasurementVariableDto> selectionMethodsAndTraits = this.measurementVariableService.getVariables(studyId,
			VariableType.TRAIT.getId(), VariableType.SELECTION_METHOD.getId());

		return this.getObservationUnitTable(studyId, selectionMethodsAndTraits,
			this.findGenericGermplasmDescriptors(studyId), this.findAdditionalDesignFactors(studyId), instanceId,
			pageNumber, pageSize, sortBy, sortOrder);
	}

	private List<ObservationUnitRow> getObservationUnitTable(
		final int datasetId,
		final List<MeasurementVariableDto> selectionMethodsAndTraits, final List<String> germplasmDescriptors,
		final List<String> designFactors, final int instanceId, final int pageNumber, final int pageSize,
		final String sortBy, final String sortOrder) {
		final int totalObservationUnits = this.countTotalObservationUnitsForDataset(datasetId, instanceId);
		return this.experimentDao.getObservationUnitTable(datasetId, selectionMethodsAndTraits,
			germplasmDescriptors, designFactors, instanceId, pageNumber, pageSize, sortBy, sortOrder);

	}

	private List<String> findGenericGermplasmDescriptors(final int studyIdentifier) {

		final List<String> allGermplasmDescriptors = this.germplasmDescriptors.find(studyIdentifier);
		/**
		 * Fixed descriptors are the ones that are NOT stored in stockprop or nd_experimentprop. We dont need additional joins to props
		 * table for these as they are available in columns in main entity (e.g. stock or nd_experiment) tables.
		 */
		final List<String> fixedGermplasmDescriptors =
			Lists.newArrayList(FIXED_GERMPLASM_DESCRIPTOR);
		final List<String> genericGermplasmDescriptors = Lists.newArrayList();

		for (final String gpDescriptor : allGermplasmDescriptors) {
			if (!fixedGermplasmDescriptors.contains(gpDescriptor)) {
				genericGermplasmDescriptors.add(gpDescriptor);
			}
		}
		return genericGermplasmDescriptors;
	}

	private List<String> findAdditionalDesignFactors(final int studyIdentifier) {

		final List<String> allDesignFactors = this.designFactors.find(studyIdentifier);
		/**
		 * Fixed design factors are already being retrieved individually in Measurements query. We are only interested in additional
		 * EXPERIMENTAL_DESIGN and TREATMENT FACTOR variables
		 */
		final List<String> fixedDesignFactors =
			Lists.newArrayList(FIXED_DESIGN_FACTORS);
		final List<String> additionalDesignFactors = Lists.newArrayList();

		for (final String designFactor : allDesignFactors) {
			if (!fixedDesignFactors.contains(designFactor)) {
				additionalDesignFactors.add(designFactor);
			}
		}
		return additionalDesignFactors;
	}

	@Override
	public int countTotalObservationUnitsForDataset(final int datasetId, final int instanceId) {
		return this.experimentDao.countTotalObservationUnitsForDataset(datasetId, instanceId);
	}
}

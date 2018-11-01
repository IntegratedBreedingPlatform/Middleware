package org.generationcp.middleware.service.impl.dataset;

import com.google.common.collect.Lists;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableService;
import org.generationcp.middleware.service.impl.study.DesignFactors;
import org.generationcp.middleware.service.impl.study.GermplasmDescriptors;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

	private OntologyVariableDataManager ontologyVariableDataManager;

	@Autowired
	private MeasurementVariableService measurementVariableService;

	@Autowired
	private GermplasmDescriptors germplasmDescriptors;

	@Autowired
	private DesignFactors designFactors;

	DatasetServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public DatasetServiceImpl(final HibernateSessionProvider sessionProvider) {
		final Session currentSession = sessionProvider.getSession();
		this.daoFactory = new DaoFactory(sessionProvider);
		this.ontologyVariableDataManager = new OntologyVariableDataManagerImpl(sessionProvider);
	}



	@Override
	public long countPhenotypes(final Integer datasetId, final List<Integer> traitIds) {
		return this.daoFactory.getPhenotypeDAO().countPhenotypesForDataset(datasetId, traitIds);
	}

	@Override
	public Integer generateSubObservationDataset (final Integer studyId, final String datasetName, final Integer datasetTypeId, final List<Integer> instanceIds,
			final Integer observationUnitVariableId, final Integer numberOfSubObservationUnits) {

		return null;
	}

	@Override
	public List<DatasetDTO> getDatasets(final Integer studyId, final Set<Integer> datasetTypeIds) {
		final List<DatasetDTO> datasetDTOList = new ArrayList<>();
		this.filterDatasets(datasetDTOList, studyId, datasetTypeIds);
		return datasetDTOList;
	}

	private void filterDatasets(final List<DatasetDTO> filtered, final Integer parentId, final Set<Integer> datasetTypeIds) {
		final List<DatasetDTO> datasetDTOs = this.daoFactory.getDmsProjectDAO().getDatasets(parentId);

		for (final DatasetDTO datasetDTO : datasetDTOs) {
			if (datasetTypeIds.isEmpty() || datasetTypeIds.contains(datasetDTO.getDatasetTypeId())) {
				filtered.add(datasetDTO);
			}
			this.filterDatasets(filtered, datasetDTO.getDatasetId(), datasetTypeIds);
		}
	}

	@Override
	public void addVariable(final Integer datasetId, final Integer variableId, final VariableType type, final String alias) {
		final ProjectPropertyDao projectPropertyDAO = this.daoFactory.getProjectPropertyDAO();
		final ProjectProperty projectProperty = new ProjectProperty();
		projectProperty.setAlias(alias);
		projectProperty.setTypeId(type.getId());
		projectProperty.setVariableId(variableId);
		final DmsProject dataset = new DmsProject();
		dataset.setProjectId(datasetId);
		projectProperty.setProject(dataset);
		projectProperty.setRank(projectPropertyDAO.getNextRank(datasetId));
		projectPropertyDAO.save(projectProperty);
	}

	protected void setDaoFactory(final DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	@Override
	public List<ObservationUnitRow> getObservationUnitRows(
		final int studyId, final int datasetId, final int instanceId, final int pageNumber, final int pageSize,
		final String sortBy, final String sortOrder) {
		final List<MeasurementVariableDto> selectionMethodsAndTraits = this.measurementVariableService.getVariablesForDataset(datasetId,
			VariableType.TRAIT.getId(), VariableType.SELECTION_METHOD.getId());

		return this.daoFactory.getExperimentDAO().getObservationUnitTable(datasetId, selectionMethodsAndTraits,
			this.findGenericGermplasmDescriptors(studyId), this.findAdditionalDesignFactors(studyId), instanceId,
			pageNumber, pageSize, sortBy, sortOrder);
	}

	private List<String> findGenericGermplasmDescriptors(final int studyId) {

		final List<String> allGermplasmDescriptors = this.germplasmDescriptors.find(studyId);
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
		return this.daoFactory.getExperimentDAO().countTotalObservationUnitsForDataset(datasetId, instanceId);
	}
}

package org.generationcp.middleware.service.impl.dataset;

import com.google.common.collect.Lists;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Transactional
public class DatasetServiceImpl implements DatasetService {

	private static final Logger LOG = LoggerFactory.getLogger(DatasetServiceImpl.class);

	public static final ArrayList<Integer> SUBOBS_COLUMNS_VARIABLE_TYPES = Lists.newArrayList( //
		VariableType.GERMPLASM_DESCRIPTOR.getId(), //
		VariableType.TRAIT.getId(), //
		VariableType.OBSERVATION_UNIT.getId());

	public static final ArrayList<Integer> PLOT_COLUMNS_VARIABLE_TYPES = Lists.newArrayList( //
		VariableType.GERMPLASM_DESCRIPTOR.getId(), //
		VariableType.OBSERVATION_UNIT.getId());

	private DaoFactory daoFactory;

	private OntologyVariableDataManager ontologyVariableDataManager;

	public DatasetServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public DatasetServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		ontologyVariableDataManager = new OntologyVariableDataManagerImpl(sessionProvider);
	}

	@Override
	public long countPhenotypes(final Integer datasetId, final List<Integer> traitIds) {
		return this.daoFactory.getPhenotypeDAO().countPhenotypesForDataset(datasetId, traitIds);
	}

	@Override
	public List<MeasurementVariable> getSubObservationSetColumns(final Integer subObservationSetId) {
		// TODO get plot dataset even if subobs is not a direct descendant (ie. sub-sub-obs)
		final DmsProject plotDataset = this.daoFactory.getProjectRelationshipDao()
			.getObjectBySubjectIdAndTypeId(subObservationSetId, TermId.BELONGS_TO_STUDY.getId());

		final List<MeasurementVariable> plotDataSetColumns =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(plotDataset.getProjectId(), PLOT_COLUMNS_VARIABLE_TYPES);
		final List<MeasurementVariable> subObservationSetColumns =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(subObservationSetId, SUBOBS_COLUMNS_VARIABLE_TYPES);

		// TODO get immediate parent columns
		// (ie. Plot subdivided into plant and then into fruits, then immediate parent column would be PLANT_NO)

		plotDataSetColumns.addAll(subObservationSetColumns);
		return plotDataSetColumns;
	}

	@Override
	public Integer generateSubObservationDataset (final Integer studyId, final String datasetName, final Integer datasetTypeId, final List<Integer> instanceIds,
			final Integer observationUnitVariableId, final Integer numberOfSubObservationUnits) {

		return null;
	}

	@Override
	public List<DatasetDTO> getDatasets(final Integer studyId, final Set<Integer> datasetTypeIds) {
		final List<DatasetDTO> datasetDTOList = new ArrayList<>();
		filterDatasets(datasetDTOList, studyId, datasetTypeIds);
		return datasetDTOList;
	}

	private void filterDatasets(final List<DatasetDTO> filtered, final Integer parentId, final Set<Integer> datasetTypeIds) {
		final List<DatasetDTO> datasetDTOs = this.daoFactory.getDmsProjectDAO().getDatasets(parentId);

		for (DatasetDTO datasetDTO : datasetDTOs) {
			if (datasetTypeIds.isEmpty() || datasetTypeIds.contains(datasetDTO.getDatasetTypeId())) {
				filtered.add(datasetDTO);
			}
			filterDatasets(filtered, datasetDTO.getDatasetId(), datasetTypeIds);
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

	protected void setDaoFactory(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

}

package org.generationcp.middleware.service.impl.dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.springframework.transaction.annotation.Transactional;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Transactional
public class DatasetServiceImpl implements DatasetService {

	private static final Logger LOG = LoggerFactory.getLogger(DatasetServiceImpl.class);

	private DaoFactory daoFactory;

	private OntologyVariableDataManager ontologyVariableDataManager;

	DatasetServiceImpl() {
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

		for (final DatasetDTO datasetDTO : datasetDTOs) {
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

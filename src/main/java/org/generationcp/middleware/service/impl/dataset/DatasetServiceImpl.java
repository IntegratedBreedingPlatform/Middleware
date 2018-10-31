package org.generationcp.middleware.service.impl.dataset;

import com.google.common.collect.Lists;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
		// TODO get plot dataset even if subobs is not a direct descendant
		final DmsProject plotDataset = this.daoFactory.getProjectRelationshipDao()
			.getObjectBySubjectIdAndTypeId(subObservationSetId, TermId.BELONGS_TO_STUDY.getId());

		final List<MeasurementVariable> plotDataSetColumns =
			this.daoFactory.getDmsProjectDAO().getObservationSetColumns(plotDataset.getProjectId(), PLOT_COLUMNS_VARIABLE_TYPES);
		final List<MeasurementVariable> subObservationSetColumns =
			this.daoFactory.getDmsProjectDAO().getObservationSetColumns(subObservationSetId, SUBOBS_COLUMNS_VARIABLE_TYPES);

		// TODO get immediate parent columns

		plotDataSetColumns.addAll(subObservationSetColumns);
		return plotDataSetColumns;
	}

	@Override
	public Integer generateSubObservationDataset (final Integer studyId, final String datasetName, final Integer datasetTypeId, final List<Integer> instanceIds,
			final Integer observationUnitVariableId, final Integer numberOfSubObservationUnits) {

		return null;
	}

	public void setDaoFactory(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

}

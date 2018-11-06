package org.generationcp.middleware.service.impl.dataset;

import java.util.List;

import com.google.common.base.Optional;
import org.apache.commons.lang3.EnumUtils;
import org.generationcp.middleware.dao.FormulaDAO;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.service.api.dataset.DatasetService;

public class DatasetServiceImpl implements DatasetService {

	private DaoFactory daoFactory;

	public DatasetServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public long countPhenotypes(final Integer datasetId, final List<Integer> traitIds) {
		return this.daoFactory.getPhenotypeDAO().countPhenotypesForDataset(datasetId, traitIds);
	}

	@Override
	public Phenotype updatePhenotype(
		final Integer observationUnitId, final Integer observationId, final Integer categoricalValueId, final String value,
		final String valueStatus) {

		final PhenotypeDao phenotypeDao = this.daoFactory.getPhenotypeDAO();
		final Phenotype phenotype = phenotypeDao.getByObservationUnitIdAndObservableId(observationUnitId, observationId);
		phenotype.setValue(value);
		phenotype.setcValue(categoricalValueId == 0 ? null : categoricalValueId);
		phenotype.setValueStatus(EnumUtils.getEnum(Phenotype.ValueStatus.class, valueStatus));
		phenotypeDao.update(phenotype);
		return phenotype;

	}

	@Override
	public Optional<Phenotype.ValueStatus> resolveObservationStatus(final Integer variableId) {

		final FormulaDAO formulaDAO = this.daoFactory.getFormulaDAO();
		final Formula formula = formulaDAO.getByTargetVariableId(variableId);
		final List<Formula> inputVariables = formulaDAO.getByInputId(variableId);

		final Boolean hasFormula = formula != null;
		final Boolean isInputVariable = !inputVariables.isEmpty();

		if (hasFormula) {
			return Optional.of(Phenotype.ValueStatus.MANUALLY_EDITED);
		} else if (isInputVariable){
			return Optional.of(Phenotype.ValueStatus.OUT_OF_SYNC);
		}

		return Optional.absent();
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
	
	@Override
	public void removeVariables(final Integer datasetId, final List<Integer> variableIds) {
		this.daoFactory.getProjectPropertyDAO().deleteProjectVariables(datasetId, variableIds);
		this.daoFactory.getPhenotypeDAO().deletePhenotypesByProjectIdAndVariableIds(datasetId, variableIds);
	}
	
	@Override
	public boolean isValidObservationUnit(final Integer datasetId, final Integer observationUnitId) {
		return this.daoFactory.getExperimentDAO().isValidExperiment(datasetId, observationUnitId);
	}

	protected void setDaoFactory(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

}

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

package org.generationcp.middleware.operation.saver;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypeExceptionDto;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.PhenotypeException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PhenotypeSaver {

	private static final Logger LOG = LoggerFactory.getLogger(PhenotypeSaver.class);

	private final DaoFactory daoFactory;

	public PhenotypeSaver(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	public void savePhenotypes(final ExperimentModel experimentModel, final VariableList variates, final Integer loggedInUserId)
		throws MiddlewareQueryException {
		Map<Integer, PhenotypeExceptionDto> exceptions = null;
		if (variates != null && variates.getVariables() != null && !variates.getVariables().isEmpty()) {
			for (final Variable variable : variates.getVariables()) {

				try {
					this.save(experimentModel.getNdExperimentId(), variable, loggedInUserId);
				} catch (final PhenotypeException e) {
					PhenotypeSaver.LOG.error(e.getMessage(), e);
					if (exceptions == null) {
						exceptions = new LinkedHashMap<Integer, PhenotypeExceptionDto>();
					}
					exceptions.put(e.getException().getStandardVariableId(), e.getException());
				}
			}
		}

		if (exceptions != null) {
			throw new PhenotypeException(exceptions);
		}
	}

	public void save(final int experimentId, final Variable variable, final Integer loggedInUserId) throws MiddlewareQueryException {
		Phenotype phenotype = this.createPhenotype(variable, experimentId, loggedInUserId);
		if (phenotype != null) {
			phenotype = this.daoFactory.getPhenotypeDAO().save(phenotype);
			variable.setVariableDataId(phenotype.getPhenotypeId());
		}
	}

	public void saveOrUpdate(final int experimentId, final Integer variableId, final String value, final Phenotype oldPhenotype,
		final Integer dataTypeId, final Phenotype.ValueStatus valueStatus, final Integer loggedInUserId) throws MiddlewareQueryException {
		final Phenotype phenotype = this.createPhenotype(variableId, value, oldPhenotype, dataTypeId, valueStatus);
		phenotype.setUpdatedDate(new Date());
		phenotype.setUpdatedBy(loggedInUserId);
		this.saveOrUpdate(experimentId, phenotype);
	}

	private Phenotype createPhenotype(final Variable variable, final int experimentId, final Integer loggedInUserId)
		throws MiddlewareQueryException {
		Phenotype phenotype = null;
		if (variable.getValue() != null && !"".equals(variable.getValue().trim())) {

			final PhenotypicType role = variable.getVariableType().getStandardVariable().getPhenotypicType();
			final Term dataType = variable.getVariableType().getStandardVariable().getDataType();

			if (role == PhenotypicType.VARIATE) {

				phenotype = this.getPhenotypeObject(phenotype);
				if (variable.getValue() != null && !"".equals(variable.getValue())) {
					phenotype.setValue(variable.getValue().trim());
				} else {
					phenotype.setValue(null);
				}
				phenotype.setObservableId(variable.getVariableType().getId());
				phenotype.setName(String.valueOf(variable.getVariableType().getId()));
				phenotype.setExperiment(this.daoFactory.getExperimentDao().getById(experimentId));

				if (dataType != null && dataType.getId() == TermId.CATEGORICAL_VARIABLE.getId()) {

					Enumeration enumeration = variable.getVariableType().getStandardVariable().getEnumerationByName(variable.getValue());
					// in case the value entered is the id and not the enumeration code/name
					if (enumeration == null && NumberUtils.isNumber(variable.getValue())) {
						enumeration = variable.getVariableType().getStandardVariable()
							.getEnumeration(Double.valueOf(variable.getValue()).intValue());
					}
					if (enumeration != null) {
						phenotype.setcValue(enumeration.getId());
						phenotype.setValue(enumeration.getName());
					} else {
						// set it as a custom value of the categorical variate
						phenotype.setValue(variable.getValue());
						phenotype.setcValue(null);
					}
				}
				phenotype.setCreatedDate(new Date());
				phenotype.setCreatedBy(loggedInUserId);
				phenotype.setUpdatedDate(new Date());
				phenotype.setUpdatedBy(loggedInUserId);
			}
		}
		return phenotype;
	}

	public void saveOrUpdate(final int experimentId, final Phenotype phenotype) throws MiddlewareQueryException {
		if (phenotype != null) {
			final ExperimentModel experiment = new ExperimentModel();
			experiment.setNdExperimentId(experimentId);
			phenotype.setExperiment(experiment);
			this.daoFactory.getPhenotypeDAO().merge(phenotype);
		}
	}

	private Phenotype createPhenotype(final Integer variableId, final String value, final Phenotype oldPhenotype,
		final Integer dataTypeId, final Phenotype.ValueStatus valueStatus) throws MiddlewareQueryException {

		if ((value == null || "".equals(value.trim())) && (oldPhenotype == null || oldPhenotype.getPhenotypeId() == null)) {
			return null;
		}

		final Phenotype phenotype = this.getPhenotypeObject(oldPhenotype);

		if (TermId.CATEGORICAL_VARIABLE.getId() == dataTypeId) {
			this.setCategoricalVariatePhenotypeValues(phenotype, value, variableId);
		} else {
			phenotype.setValue(value);
		}
		phenotype.setValueStatus(valueStatus);
		phenotype.setObservableId(variableId);
		phenotype.setName(String.valueOf(variableId));

		return phenotype;
	}

	private void setCategoricalVariatePhenotypeValues(final Phenotype phenotype, final String value, final Integer variableId)
		throws MiddlewareQueryException {
		if (value == null || "".equals(value)) {
			phenotype.setcValue(null);
			phenotype.setValue(null);
		} else if (!NumberUtils.isNumber(value)) {
			phenotype.setcValue(null);
			phenotype.setValue(value);
		} else {
			final Integer phenotypeValue = Double.valueOf(value).intValue();
			final Map<Integer, String> possibleValuesMap = this.getPossibleValuesMap(variableId);
			if (possibleValuesMap.containsKey(phenotypeValue)) {
				phenotype.setcValue(phenotypeValue);
				phenotype.setValue(possibleValuesMap.get(phenotypeValue));
			} else {
				phenotype.setcValue(null);
				phenotype.setValue(value);
			}
		}
	}

	protected Map<Integer, String> getPossibleValuesMap(final int variableId) throws MiddlewareQueryException {
		final Map<Integer, String> possibleValuesMap = new HashMap<Integer, String>();
		final CVTermRelationship scaleRelationship =
			this.daoFactory.getCvTermRelationshipDao().getRelationshipBySubjectIdAndTypeId(variableId, TermId.HAS_SCALE.getId());
		if (scaleRelationship != null) {
			final List<CVTermRelationship> possibleValues =
				this.daoFactory.getCvTermRelationshipDao().getBySubjectIdAndTypeId(scaleRelationship.getObjectId(), TermId.HAS_VALUE.getId());
			if (possibleValues != null) {
				for (final CVTermRelationship cvTermRelationship : possibleValues) {
					possibleValuesMap.put(cvTermRelationship.getObjectId(),
						this.daoFactory.getCvTermDao().getById(cvTermRelationship.getObjectId()).getName());
				}
			}
		}
		return possibleValuesMap;
	}

	private Phenotype getPhenotypeObject(final Phenotype oldPhenotype) throws MiddlewareQueryException {
		Phenotype phenotype = oldPhenotype;
		if (phenotype == null) {
			phenotype = new Phenotype();
		}
		return phenotype;
	}

	public void saveOrUpdatePhenotypeValue(final int projectId, final int variableId, final String value, final int dataTypeId)
		throws MiddlewareQueryException {
		if (value != null) {
			boolean isInsert = false;
			Integer phenotypeId = this.daoFactory.getPhenotypeDAO().getPhenotypeIdByProjectAndType(projectId, variableId);
			final Phenotype phenotype;
			if (phenotypeId == null) {
				phenotype = new Phenotype();
				phenotypeId = phenotype.getPhenotypeId();
				phenotype.setObservableId(variableId);
				phenotype.setName(String.valueOf(variableId));
				isInsert = true;
			} else {
				phenotype = this.daoFactory.getPhenotypeDAO().getById(phenotypeId);
			}
			if (dataTypeId == TermId.CATEGORICAL_VARIABLE.getId() && NumberUtils.isNumber(value)) {
				phenotype.setcValue(Double.valueOf(value).intValue());
			} else {
				phenotype.setValue(value);
			}
			this.daoFactory.getPhenotypeDAO().saveOrUpdate(phenotype);
			if (isInsert) {
				final int experimentId = this.daoFactory.getExperimentDao().getExperimentIdByProjectId(projectId);
				final ExperimentModel experimentModel = this.daoFactory.getExperimentDao().getById(experimentId);
				phenotype.setExperiment(experimentModel);
			}
		}
	}
}

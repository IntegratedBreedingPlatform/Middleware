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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.cache.BreedingMethodCache;
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
import org.generationcp.middleware.manager.ontology.VariableCache;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentPhenotype;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhenotypeSaver extends Saver {

	private static final Logger LOG = LoggerFactory.getLogger(PhenotypeSaver.class);

	public PhenotypeSaver(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public void savePhenotypes(final ExperimentModel experimentModel, final VariableList variates) throws MiddlewareQueryException {
		Map<Integer, PhenotypeExceptionDto> exceptions = null;
		if (variates != null && variates.getVariables() != null && !variates.getVariables().isEmpty()) {
			for (final Variable variable : variates.getVariables()) {

				try {
					this.save(experimentModel.getNdExperimentId(), variable);
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

	public void save(final int experimentId, final Variable variable) throws MiddlewareQueryException {
		Phenotype phenotype = this.createPhenotype(variable);
		if (phenotype != null) {
			phenotype = this.getPhenotypeDao().save(phenotype);
			this.saveExperimentPhenotype(experimentId, phenotype.getPhenotypeId());
			variable.setPhenotypeId(phenotype.getPhenotypeId());
		}
	}

	public void saveOrUpdate(final int experimentId, final Integer variableId, final String value, final Phenotype oldPhenotype,
			final Integer dataTypeId) throws MiddlewareQueryException {
		final Phenotype phenotype = this.createPhenotype(variableId, value, oldPhenotype, dataTypeId);
		this.saveOrUpdate(experimentId, phenotype);
	}

	public void savePhenotype(final int experimentId, final Variable variable) throws MiddlewareQueryException {
		final Phenotype phenotype = this.createPhenotype(variable);
		if (phenotype != null) {
			this.getPhenotypeDao().save(phenotype);
			this.saveExperimentPhenotype(experimentId, phenotype.getPhenotypeId());
		}
	}

	private Phenotype createPhenotype(final Variable variable) throws MiddlewareQueryException {
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

				if (dataType != null && dataType.getId() == TermId.CATEGORICAL_VARIABLE.getId()) {

					Enumeration enumeration = variable.getVariableType().getStandardVariable().getEnumerationByName(variable.getValue());
					// in case the value entered is the id and not the enumeration code/name
					if (enumeration == null && NumberUtils.isNumber(variable.getValue())) {
						enumeration =
								variable.getVariableType().getStandardVariable()
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
			}
		}
		return phenotype;
	}

	private void saveOrUpdate(final int experimentId, final Phenotype phenotype) throws MiddlewareQueryException {
		if (phenotype != null) {
			this.getPhenotypeDao().merge(phenotype);
			this.saveOrUpdateExperimentPhenotype(experimentId, phenotype.getPhenotypeId());
		}
	}

	protected Phenotype createPhenotype(final Integer variableId, final String value, final Phenotype oldPhenotype, final Integer dataTypeId)
			throws MiddlewareQueryException {

		if ((value == null || "".equals(value.trim())) && (oldPhenotype == null || oldPhenotype.getPhenotypeId() == null)) {
			return null;
		}

		final Phenotype phenotype = this.getPhenotypeObject(oldPhenotype);

		if (NumberUtils.isNumber(value) && this.isBreedingMethodVariable(variableId)) {
			phenotype.setcValue(null);
			phenotype.setValue(this.getBreedingMethodCode(Double.valueOf(value).intValue()));
		} else if (TermId.CATEGORICAL_VARIABLE.getId() == dataTypeId) {
			this.setCategoricalVariatePhenotypeValues(phenotype, value, variableId);
		} else {
			phenotype.setValue(value);
		}
		phenotype.setObservableId(variableId);
		phenotype.setName(String.valueOf(variableId));

		return phenotype;
	}

	protected boolean isBreedingMethodVariable(final Integer variableId) {
		Integer propertyId = null;
		final org.generationcp.middleware.domain.ontology.Variable variable = VariableCache.getFromCache(variableId);
		if (variable != null) {
			propertyId = variable.getProperty().getId();
		} else {
			final CVTermRelationship propertyRelationship =
					this.getCvTermRelationshipDao().getRelationshipBySubjectIdAndTypeId(variableId, TermId.HAS_PROPERTY.getId());
			propertyId = propertyRelationship.getObjectId();
		}
		if (TermId.BREEDING_METHOD_PROP.getId() == propertyId) {
			return true;
		}
		return false;
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

	private String getBreedingMethodCode(final Integer methodId) {
		Method breedingMethod = BreedingMethodCache.getFromCache(methodId);
		if (breedingMethod == null) {
			breedingMethod = this.getMethodDao().getById(methodId);
			BreedingMethodCache.addToCache(breedingMethod.getMid(), breedingMethod);
		}
		return breedingMethod.getMcode();
	}

	protected Map<Integer, String> getPossibleValuesMap(final int variableId) throws MiddlewareQueryException {
		final Map<Integer, String> possibleValuesMap = new HashMap<Integer, String>();
		final CVTermRelationship scaleRelationship =
				this.getCvTermRelationshipDao().getRelationshipBySubjectIdAndTypeId(variableId, TermId.HAS_SCALE.getId());
		if (scaleRelationship != null) {
			final List<CVTermRelationship> possibleValues =
					this.getCvTermRelationshipDao().getBySubjectIdAndTypeId(scaleRelationship.getObjectId(), TermId.HAS_VALUE.getId());
			if (possibleValues != null) {
				for (final CVTermRelationship cvTermRelationship : possibleValues) {
					possibleValuesMap.put(cvTermRelationship.getObjectId(), this.getCvTermDao().getById(cvTermRelationship.getObjectId())
							.getName());
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

	private void saveExperimentPhenotype(final int experimentId, final int phenotypeId) throws MiddlewareQueryException {
		this.getExperimentPhenotypeDao().save(this.createExperimentPhenotype(experimentId, phenotypeId));
	}

	private void saveOrUpdateExperimentPhenotype(final int experimentId, final int phenotypeId) throws MiddlewareQueryException {
		this.getExperimentPhenotypeDao().merge(this.createExperimentPhenotype(experimentId, phenotypeId));
	}

	private ExperimentPhenotype createExperimentPhenotype(final int experimentId, final int phenotypeId) throws MiddlewareQueryException {
		ExperimentPhenotype experimentPhenotype = this.getExperimentPhenotypeDao().getbyExperimentAndPhenotype(experimentId, phenotypeId);

		if (experimentPhenotype == null || experimentPhenotype.getExperimentPhenotypeId() == null) {
			experimentPhenotype = new ExperimentPhenotype();
			experimentPhenotype.setExperiment(experimentId);
			experimentPhenotype.setPhenotype(phenotypeId);
		}
		return experimentPhenotype;

	}

	public void saveOrUpdatePhenotypeValue(final int projectId, final int variableId, final String value, final int dataTypeId)
			throws MiddlewareQueryException {
		if (value != null) {
			boolean isInsert = false;
			Integer phenotypeId = this.getPhenotypeDao().getPhenotypeIdByProjectAndType(projectId, variableId);
			Phenotype phenotype = null;
			if (phenotypeId == null) {
				phenotype = new Phenotype();
				phenotypeId = phenotype.getPhenotypeId();
				phenotype.setObservableId(variableId);
				phenotype.setName(String.valueOf(variableId));
				isInsert = true;
			} else {
				phenotype = this.getPhenotypeDao().getById(phenotypeId);
			}
			if (dataTypeId == TermId.CATEGORICAL_VARIABLE.getId() && NumberUtils.isNumber(value)) {
				phenotype.setcValue(Double.valueOf(value).intValue());
			} else {
				phenotype.setValue(value);
			}
			this.getPhenotypeDao().saveOrUpdate(phenotype);
			if (isInsert) {
				final int experimentId = this.getExperimentProjectDao().getExperimentIdByProjectId(projectId);
				this.saveExperimentPhenotype(experimentId, phenotype.getPhenotypeId());
			}
		}
	}
}

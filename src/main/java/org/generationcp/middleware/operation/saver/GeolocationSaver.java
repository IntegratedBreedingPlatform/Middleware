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

import java.util.ArrayList;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.operation.transformer.etl.VariableListTransformer;
import org.generationcp.middleware.operation.transformer.etl.VariableTypeListTransformer;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.util.StringUtil;

public class GeolocationSaver {
	
	private DaoFactory daoFactory;
	private PhenotypeSaver phenotypeSaver;
	private VariableTypeListTransformer variableTypeListTransformer;
	private VariableListTransformer variableListTransformer; 

	public GeolocationSaver(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.phenotypeSaver = new PhenotypeSaver(sessionProvider);
		this.variableTypeListTransformer = new VariableTypeListTransformer(sessionProvider);
		this.variableListTransformer = new VariableListTransformer(sessionProvider);
	}

	public Geolocation saveGeolocation(VariableList variableList, MeasurementRow row) {
		return this.saveGeolocation(variableList, row, true);
	}

	public Geolocation saveGeolocation(VariableList variableList, MeasurementRow row, boolean isCreate) {
		Integer locationId = null;
		if (row != null && !isCreate && row.getLocationId() != 0) {
			locationId = (int) row.getLocationId();
		}
		final Geolocation geolocation = this.createOrUpdate(variableList, row, locationId);
		if (geolocation != null) {
			if (isCreate) {
				this.daoFactory.getGeolocationDao().save(geolocation);
			} else {
				this.daoFactory.getGeolocationDao().saveOrUpdate(geolocation);
			}
			if (null != geolocation.getVariates()) {
				for (final Variable var : geolocation.getVariates().getVariables()) {
					if (null == var.getPhenotypeId()) {
						this.phenotypeSaver.save(row.getExperimentId(), var);
					} else {
						this.phenotypeSaver
							.saveOrUpdate(row.getExperimentId(), var.getVariableType().getStandardVariable().getId(), var.getValue(),
								this.daoFactory.getPhenotypeDAO().getById(var.getPhenotypeId()),
								var.getVariableType().getStandardVariable().getDataType().getId(), null);
					}
				}
			}
			return geolocation;
		}
		return null;
	}

	protected Geolocation createOrUpdate(VariableList factors, MeasurementRow row, Integer locationId) {
		Geolocation geolocation = null;

		if (factors != null && factors.getVariables() != null && !factors.getVariables().isEmpty()) {
			for (final Variable variable : factors.getVariables()) {

				final Integer variableId = variable.getVariableType().getStandardVariable().getId();
				final PhenotypicType role = variable.getVariableType().getRole();
				final String value = variable.getValue();
				geolocation = this.getGeolocationObject(geolocation, locationId);

				if(isInGeolocation(variableId)){
					setGeolocation(geolocation, variableId, value);

				} else if (PhenotypicType.TRIAL_ENVIRONMENT == role) {
					if (TermId.EXPERIMENT_DESIGN_FACTOR.getId() == variableId) {
						// Experiment Design saves the id of the category instead of the name
						variable.setValue(variable.getIdValue());
					}
					this.addProperty(geolocation, this.createOrUpdateProperty(variable, geolocation));

				} else if (PhenotypicType.VARIATE == role) {
					// value is in observation sheet
					if (row != null) {
						variable.setValue(row.getMeasurementDataValue(variable.getVariableType().getLocalName()));
					}
					this.addVariate(geolocation, variable);

				} else {
					throw new MiddlewareQueryException(
						"Non-Trial Environment Variable was used in calling create location: " + variable.getVariableType().getId());
				}
			}
		}
		return geolocation;
	}

	private boolean isInGeolocation(final int termId) {
		return TermId.TRIAL_INSTANCE_FACTOR.getId() == termId || TermId.LATITUDE.getId() == termId || TermId.LONGITUDE.getId() == termId
			|| TermId.GEODETIC_DATUM.getId() == termId || TermId.ALTITUDE.getId() == termId;
	}


	private Geolocation getGeolocationObject(Geolocation geolocation, Integer locationId) {
		Geolocation finalGeolocation = geolocation;
		if (finalGeolocation == null) {
			if (locationId != null) {
				finalGeolocation = this.getGeolocationById(locationId);
			}
			if (finalGeolocation == null) {
				finalGeolocation = new Geolocation();
			}
		}
		return finalGeolocation;
	}

	protected Geolocation getGeolocationById(Integer locationId) {
		return this.daoFactory.getGeolocationDao().getById(locationId);
	}

	private GeolocationProperty createOrUpdateProperty(Variable variable, Geolocation geolocation) {
		GeolocationProperty property = this.getGeolocationProperty(variable.getVariableType().getId(), geolocation);

		if (property == null) {
			property = new GeolocationProperty();
			property.setType(variable.getVariableType().getId());
			property.setRank(variable.getVariableType().getRank());
		}
		property.setValue(variable.getValue());

		return property;
	}

	private GeolocationProperty getGeolocationProperty(Integer typeId, Geolocation geolocation) {
		if (typeId != null && geolocation != null && geolocation.getProperties() != null) {
			for (GeolocationProperty property : geolocation.getProperties()) {
				if (property.getTypeId().equals(typeId)) {
					return property;
				}
			}
		}
		return null;
	}

	private void addProperty(Geolocation geolocation, GeolocationProperty property) {
		if (geolocation.getProperties() == null) {
			geolocation.setProperties(new ArrayList<GeolocationProperty>());
		}
		property.setGeolocation(geolocation);
		geolocation.getProperties().add(property);
	}

	private void addVariate(Geolocation geolocation, Variable variable) {
		if (geolocation.getVariates() == null) {
			geolocation.setVariates(new VariableList());
		}
		geolocation.getVariates().add(variable);
	}

	public Geolocation createMinimumGeolocation() {
		Geolocation geolocation = this.getGeolocationObject(null, null);
		geolocation.setDescription("1");
		this.daoFactory.getGeolocationDao().save(geolocation);

		return geolocation;
	}

	public Geolocation updateGeolocationInformation(MeasurementRow row, String programUUID) {
		VariableTypeList variableTypes = this.variableTypeListTransformer.transform(row.getMeasurementVariables(), programUUID);
		VariableList variableList = this.variableListTransformer.transformTrialEnvironment(row, variableTypes);

		return this.saveGeolocation(variableList, row, false);
	}

	public void setGeolocation(Geolocation geolocation, int termId, String value) {
		if (TermId.TRIAL_INSTANCE_FACTOR.getId() == termId) {
			geolocation.setDescription(value);

		} else if (TermId.LATITUDE.getId() == termId) {
			geolocation.setLatitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));

		} else if (TermId.LONGITUDE.getId() == termId) {
			geolocation.setLongitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));

		} else if (TermId.GEODETIC_DATUM.getId() == termId) {
			geolocation.setGeodeticDatum(value);

		} else if (TermId.ALTITUDE.getId() == termId) {
			geolocation.setAltitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));
		}
	}

	public Geolocation saveGeolocationOrRetrieveIfExisting(String studyName, VariableList variableList, MeasurementRow row, boolean isDeleteTrialObservations, String programUUID) {
		Geolocation geolocation = null;

		if (variableList != null && variableList.getVariables() != null && !variableList.getVariables().isEmpty()) {
			String trialInstanceNumber = null;
			for (Variable variable : variableList.getVariables()) {
				String value = variable.getValue();
				if (TermId.TRIAL_INSTANCE_FACTOR.getId() == variable.getVariableType().getStandardVariable().getId()) {
					trialInstanceNumber = value;
					break;
				}
			}
			// check if existing
			Integer locationId =
					this.daoFactory.getGeolocationDao().getLocationIdByProjectNameAndDescriptionAndProgramUUID(studyName, trialInstanceNumber,
							programUUID);
			if (isDeleteTrialObservations) {
				locationId = null;
			}
			geolocation = this.createOrUpdate(variableList, row, locationId);
			geolocation.setDescription(trialInstanceNumber);
			this.daoFactory.getGeolocationDao().saveOrUpdate(geolocation);
			return geolocation;
		}
		return null;
	}
}

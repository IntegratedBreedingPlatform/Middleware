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

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.util.StringUtil;

import java.util.ArrayList;

public class GeolocationSaver extends Saver {

	public GeolocationSaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public Geolocation saveGeolocation(VariableList variableList, MeasurementRow row, boolean isNursery) throws MiddlewareQueryException {
		return this.saveGeolocation(variableList, row, isNursery, true);
	}

	public Geolocation saveGeolocation(VariableList variableList, MeasurementRow row, boolean isNursery, boolean isCreate)
			throws MiddlewareQueryException {
		Integer locationId = null;
		if (row != null && !isCreate && row.getLocationId() != 0) {
			locationId = (int) row.getLocationId();
		}
		Geolocation geolocation = this.createOrUpdate(variableList, row, locationId);
		if (geolocation != null) {
			if (isNursery && geolocation.getDescription() == null) {
				// OCC should have a default value of 1
				geolocation.setDescription("1");
			}
			if (isCreate) {
				this.getGeolocationDao().save(geolocation);
			} else {
				this.getGeolocationDao().saveOrUpdate(geolocation);
			}
			if (geolocation.getVariates() != null) {
				for (Variable var : geolocation.getVariates().getVariables()) {
					if (var.getPhenotypeId() == null) {
						this.getPhenotypeSaver().save(row.getExperimentId(), var);
					} else {

						this.getPhenotypeSaver().saveOrUpdate(row.getExperimentId(), var.getVariableType().getStandardVariable().getId(),
								var.getValue(), this.getPhenotypeDao().getById(var.getPhenotypeId()),
								var.getVariableType().getStandardVariable().getDataType().getId());
					}
				}
			}
			return geolocation;
		}
		return null;
	}

	protected Geolocation createOrUpdate(VariableList factors, MeasurementRow row, Integer locationId) throws MiddlewareQueryException {
		Geolocation geolocation = null;

		if (factors != null && factors.getVariables() != null && !factors.getVariables().isEmpty()) {
			for (Variable variable : factors.getVariables()) {

				Integer variableId = variable.getVariableType().getStandardVariable().getId();
				PhenotypicType role = variable.getVariableType().getRole();
				String value = variable.getValue();

				if (TermId.TRIAL_INSTANCE_FACTOR.getId() == variableId) {
					geolocation = this.getGeolocationObject(geolocation, locationId);
					geolocation.setDescription(value);

				} else if (TermId.LATITUDE.getId() == variableId) {
					geolocation = this.getGeolocationObject(geolocation, locationId);
					geolocation.setLatitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));

				} else if (TermId.LONGITUDE.getId() == variableId) {
					geolocation = this.getGeolocationObject(geolocation, locationId);
					geolocation.setLongitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));

				} else if (TermId.GEODETIC_DATUM.getId() == variableId) {
					geolocation = this.getGeolocationObject(geolocation, locationId);
					geolocation.setGeodeticDatum(value);

				} else if (TermId.ALTITUDE.getId() == variableId) {
					geolocation = this.getGeolocationObject(geolocation, locationId);
					geolocation.setAltitude(StringUtil.isEmpty(value) ? null : Double.valueOf(value));

				} else if (PhenotypicType.TRIAL_ENVIRONMENT == role) {
					geolocation = this.getGeolocationObject(geolocation, locationId);
					if (TermId.EXPERIMENT_DESIGN_FACTOR.getId() == variableId) {
						// Experiment Design saves the id of the category instead of the name
						variable.setValue(variable.getIdValue());
					}
					this.addProperty(geolocation, this.createOrUpdateProperty(variable, geolocation));

				} else if (PhenotypicType.VARIATE == role) {
					geolocation = this.getGeolocationObject(geolocation, locationId);
					// value is in observation sheet
					if (row != null) {
						variable.setValue(row.getMeasurementDataValue(variable.getVariableType().getLocalName()));
					}
					this.addVariate(geolocation, variable);

				} else {
					throw new MiddlewareQueryException("Non-Trial Environment Variable was used in calling create location: "
							+ variable.getVariableType().getId());
				}
			}
		}

		return geolocation;
	}

	private Geolocation getGeolocationObject(Geolocation geolocation, Integer locationId) throws MiddlewareQueryException {
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

	protected Geolocation getGeolocationById(Integer locationId) throws MiddlewareQueryException {
		return this.getGeolocationDao().getById(locationId);
	}

	private GeolocationProperty createOrUpdateProperty(Variable variable, Geolocation geolocation) throws MiddlewareQueryException {
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

	public Geolocation createMinimumGeolocation() throws MiddlewareQueryException {
		Geolocation geolocation = this.getGeolocationObject(null, null);
		geolocation.setDescription("1");
		this.getGeolocationDao().save(geolocation);

		return geolocation;
	}

	public Geolocation updateGeolocationInformation(MeasurementRow row, boolean isNursery, String programUUID) throws MiddlewareException {
		VariableTypeList variableTypes = this.getVariableTypeListTransformer().transform(row.getMeasurementVariables(), programUUID);
		VariableList variableList = this.getVariableListTransformer().transformTrialEnvironment(row, variableTypes);

		return this.saveGeolocation(variableList, row, isNursery, false);
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

	public Geolocation saveGeolocationOrRetrieveIfExisting(String studyName, VariableList variableList, MeasurementRow row,
			boolean isNursery, boolean isDeleteTrialObservations, String programUUID) throws MiddlewareQueryException {
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
			if (isNursery && trialInstanceNumber == null) {
				trialInstanceNumber = "1";
			}
			// check if existing
			Integer locationId =
					this.getGeolocationDao().getLocationIdByProjectNameAndDescriptionAndProgramUUID(studyName, trialInstanceNumber,
							programUUID);
			if (isDeleteTrialObservations) {
				locationId = null;
			}
			geolocation = this.createOrUpdate(variableList, row, locationId);
			geolocation.setDescription(trialInstanceNumber);
			this.getGeolocationDao().saveOrUpdate(geolocation);
			return geolocation;
		}
		return null;
	}
}

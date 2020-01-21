
package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;

import java.util.List;

// TODO IBP-3389: Check if this can be replaced with ExperimentPropertySaver
public class EnvironmentPropertySaver {

	private DaoFactory daoFactory;
	public EnvironmentPropertySaver(final HibernateSessionProvider sessionProviderForLocal) {
		this.daoFactory = new DaoFactory(sessionProviderForLocal);
	}

	public void saveFieldmapProperties(final List<FieldMapInfo> infos) throws MiddlewareQueryException {
		for (final FieldMapInfo info : infos) {
			for (final FieldMapDatasetInfo dataset : info.getDatasets()) {
				for (final FieldMapTrialInstanceInfo trial : dataset.getTrialInstances()) {
					// GCP-8093 handle old data saved using the default location, default location is no longer used
					int locationId = trial.getGeolocationId();
					if (trial.getLocationId() != null) {
						this.saveOrUpdate(locationId, TermId.LOCATION_ID.getId(), trial.getLocationId().toString());
					}

					if (trial.getBlockId() != null) {
						this.saveOrUpdate(locationId, TermId.BLOCK_ID.getId(), trial.getBlockId().toString());
					}
				}
			}
		}
	}

	public void saveOrUpdate(final int geolocationId, final int typeId, final String value) throws MiddlewareQueryException {
		final ExperimentModel environment = this.daoFactory.getEnvironmentDao().getById(geolocationId);
		ExperimentProperty property = null;
		if (environment.getProperties() != null && !environment.getProperties().isEmpty()) {
			property = this.findProperty(environment.getProperties(), typeId);
		}
		if (property == null) {
			property = new ExperimentProperty();
			property.setRank(this.getMaxRank(environment.getProperties()));
			property.setExperiment(environment);
			property.setTypeId(typeId);
		}
		property.setValue(value);
		this.daoFactory.getEnvironmentPropertyDao().saveOrUpdate(property);
	}

	private int getMaxRank(final List<ExperimentProperty> properties) {
		int maxRank = 1;
		if(properties != null){
			for (final ExperimentProperty property : properties) {
				if (property.getRank() >= maxRank) {
					maxRank = property.getRank() + 1;
				}
			}
		}
		return maxRank;
	}

	private ExperimentProperty findProperty(final List<ExperimentProperty> properties, final int typeId) {
		for (final ExperimentProperty property : properties) {
			if (property.getTypeId() == typeId) {
				return property;
			}
		}
		return null;
	}

	public void saveOrUpdate(final ExperimentModel environment, final int typeId, final String value) throws MiddlewareQueryException {
		ExperimentProperty property = null;
		if (environment.getProperties() != null && !environment.getProperties().isEmpty()) {
			property = this.findProperty(environment.getProperties(), typeId);
		}
		if (property == null) {
			property = new ExperimentProperty();
			property.setRank(this.getMaxRank(environment.getProperties()));
			property.setExperiment(environment);
			property.setTypeId(typeId);
			environment.getProperties().add(property);
		}
		property.setValue(value);
		this.daoFactory.getEnvironmentPropertyDao().saveOrUpdate(property);
	}
}

/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.operation.saver;

import java.util.List;

import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapLabel;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;


public class ExperimentPropertySaver extends Saver {

    public ExperimentPropertySaver(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public void saveOrUpdateProperty(ExperimentModel experiment, TermId propertyType, String value) throws MiddlewareQueryException {
        int storedIn = TermId.TRIAL_DESIGN_INFO_STORAGE.getId();
        ExperimentProperty experimentProperty = getExperimentProperty(experiment, propertyType.getId());
        if (experimentProperty == null) {
            getProjectPropertySaver().createProjectPropertyIfNecessary(experiment.getProject(), propertyType, storedIn);
            
            setWorkingDatabase(Database.LOCAL);
            experimentProperty = new ExperimentProperty();
            experimentProperty.setNdExperimentpropId(getExperimentPropertyDao().getNegativeId("ndExperimentpropId"));
            experimentProperty.setTypeId(propertyType.getId());
            experimentProperty.setRank(0);
            experimentProperty.setExperiment(experiment);
        }
        
        setWorkingDatabase(Database.LOCAL);
        experimentProperty.setValue(value);
        getExperimentPropertyDao().saveOrUpdate(experimentProperty);
    }
    
    public void saveOrUpdateProperty(ExperimentModel experiment, int propertyType, String value) throws MiddlewareQueryException {
        ExperimentProperty experimentProperty = getExperimentProperty(experiment, propertyType);
        if (experimentProperty == null) {
            
            setWorkingDatabase(Database.LOCAL);
            experimentProperty = new ExperimentProperty();
            experimentProperty.setNdExperimentpropId(getExperimentPropertyDao().getNegativeId("ndExperimentpropId"));
            experimentProperty.setTypeId(propertyType);
            experimentProperty.setRank(0);
            experimentProperty.setExperiment(experiment);
        }
        
        setWorkingDatabase(Database.LOCAL);
        experimentProperty.setValue(value);
        getExperimentPropertyDao().saveOrUpdate(experimentProperty);
    }
    
    private ExperimentProperty getExperimentProperty(ExperimentModel experiment, int typeId) {
        if (experiment != null && experiment.getProperties() != null) {
            for (ExperimentProperty property : experiment.getProperties()) {
                if (property.getTypeId().equals(typeId)) {
                    return property;
                }
            }
        }
        return null;
    }
    
    public void saveFieldmapProperties(List<FieldMapInfo> infos) throws MiddlewareQueryException {
        for (FieldMapInfo info : infos) {
            for (FieldMapDatasetInfo dataset : info.getDatasets()) {
                for (FieldMapTrialInstanceInfo tInfo : dataset.getTrialInstances()) {
                    if (tInfo.getFieldMapLabels() != null) {
                        for (FieldMapLabel label : tInfo.getFieldMapLabels()) {
                            //only save if entry was assigned a plot
                            if (label.getColumn() != null && label.getRange() != null) {
                                ExperimentModel experiment = getExperimentBuilder().getExperimentModel(label.getExperimentId());
                                saveOrUpdateProperty(experiment, TermId.COLUMN_NO, String.valueOf(label.getColumn()));
                                saveOrUpdateProperty(experiment, TermId.RANGE_NO, String.valueOf(label.getRange()));
//                                saveOrUpdateProperty(experiment, TermId.BLOCK_NAME, tInfo.getBlockName());
//                                saveOrUpdateProperty(experiment, TermId.COLUMNS_IN_BLOCK, String.valueOf(tInfo.getColumnsInBlock()));
//                                saveOrUpdateProperty(experiment, TermId.RANGES_IN_BLOCK, String.valueOf(tInfo.getRangesInBlock()));
//                                int plantingOrder = tInfo.getPlantingOrder() != null && tInfo.getPlantingOrder().equals(2) 
//                                        ? TermId.SERPENTINE.getId() : TermId.ROW_COLUMN.getId();
//                                saveOrUpdateProperty(experiment, TermId.PLANTING_ORDER, String.valueOf(plantingOrder));
//                                saveOrUpdateProperty(experiment, TermId.ROWS_PER_PLOT, String.valueOf(tInfo.getRowsPerPlot()));
//                                saveOrUpdateProperty(experiment, TermId.SITE_NAME, tInfo.getLocationName());
//                                saveOrUpdateProperty(experiment, TermId.FIELD_NAME, tInfo.getFieldName());
//                                saveOrUpdateProperty(experiment, TermId.FIELDMAP_UUID, fieldmapUUID);
//                                if (tInfo.getMachineRowCapacity() != null) {
//                                    saveOrUpdateProperty(experiment, TermId.MACHINE_ROW_CAPACITY, String.valueOf(tInfo.getMachineRowCapacity()));
//                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

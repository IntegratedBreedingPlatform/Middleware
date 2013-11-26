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
    
    public void saveFieldmapProperties(FieldMapInfo info) throws MiddlewareQueryException {
        FieldMapTrialInstanceInfo tInfo = info.getDatasets().get(0).getTrialInstances().get(0);
        List<FieldMapLabel> labels = tInfo.getFieldMapLabels();
        
        for (FieldMapLabel label : labels) {
            ExperimentModel experiment = getExperimentBuilder().getExperimentModel(label.getExperimentId());
            getExperimentPropertySaver().saveOrUpdateProperty(experiment, TermId.COLUMN_NO, String.valueOf(label.getColumn()));
            getExperimentPropertySaver().saveOrUpdateProperty(experiment, TermId.RANGE_NO, String.valueOf(label.getRange()));
            getExperimentPropertySaver().saveOrUpdateProperty(experiment, TermId.BLOCK_NAME, tInfo.getBlockName());
            getExperimentPropertySaver().saveOrUpdateProperty(experiment, TermId.COLUMNS_IN_BLOCK, String.valueOf(tInfo.getColumnsInBlock()));
            getExperimentPropertySaver().saveOrUpdateProperty(experiment, TermId.RANGES_IN_BLOCK, String.valueOf(tInfo.getRangesInBlock()));
            int plantingOrder = tInfo.getPlantingOrder() != null && tInfo.getPlantingOrder().equals(2) 
                    ? TermId.SERPENTINE.getId() : TermId.ROW_COLUMN.getId();
            getExperimentPropertySaver().saveOrUpdateProperty(experiment, TermId.PLANTING_ORDER, String.valueOf(plantingOrder));
            getExperimentPropertySaver().saveOrUpdateProperty(experiment, TermId.ROWS_PER_PLOT, String.valueOf(tInfo.getRowsPerPlot()));
        }
    }
}

package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.mbdt.MBDTProjectData;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte

 */
public interface MBDTDataManager {
    public Integer setProjectData(MBDTProjectData projectData) throws MiddlewareQueryException;

    public MBDTProjectData getProjectData(Integer projectID) throws MiddlewareQueryException;
}

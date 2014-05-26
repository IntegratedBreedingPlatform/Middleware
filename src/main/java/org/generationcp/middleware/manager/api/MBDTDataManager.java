package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.domain.mbdt.SelectedGenotypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.mbdt.MBDTGeneration;
import org.generationcp.middleware.pojos.mbdt.MBDTProjectData;
import org.generationcp.middleware.pojos.mbdt.SelectedGenotype;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte

 */
public interface MBDTDataManager {
    public Integer setProjectData(MBDTProjectData projectData) throws MiddlewareQueryException;

    public MBDTProjectData getProjectData(Integer projectID) throws MiddlewareQueryException;

    public MBDTGeneration setGeneration(Integer projectID, MBDTGeneration generation) throws MiddlewareQueryException;

    public MBDTGeneration getGeneration(Integer generationID) throws MiddlewareQueryException;

    public List<MBDTGeneration> getGenerations(Integer projectID) throws MiddlewareQueryException;

    public void setMarkerStatus(Integer generationID, List<Integer> markerIDs) throws MiddlewareQueryException;

    public List<Integer> getMarkerStatus(Integer generationID) throws MiddlewareQueryException;

    public List<SelectedGenotype> getSelectedAccession(Integer generationID) throws MiddlewareQueryException;

    public List<SelectedGenotype> getParentData(Integer generationID) throws MiddlewareQueryException;

    public void setSelectedAccessions(Integer generationID, List<Integer> gids) throws MiddlewareQueryException;

    public void setParentData(Integer generationID, SelectedGenotypeEnum genotypeEnum, List<Integer> gids) throws MiddlewareQueryException;

    // for test purposes
    public void clear();
}

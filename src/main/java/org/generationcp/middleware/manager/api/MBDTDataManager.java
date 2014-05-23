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

    public MBDTGeneration getGeneration(Integer projectID, Integer datasetID) throws MiddlewareQueryException;

    public void setSelectedMarkers(Integer projectID, Integer datasetID, List<Integer> markerIDs) throws MiddlewareQueryException;

    public List<Integer> getSelectedMarkers(Integer projectID, Integer datasetID) throws MiddlewareQueryException;

    public List<SelectedGenotype> getSelectedAccession(Integer projectID, Integer datasetID) throws MiddlewareQueryException;

    public List<SelectedGenotype> getParent(Integer projectID, Integer datasetID) throws MiddlewareQueryException;

    public void setSelectedAccessions(Integer projectID, Integer datasetID, List<Integer> gids) throws MiddlewareQueryException;

    public void setParent(Integer projectID, Integer datasetID, SelectedGenotypeEnum genotypeEnum, List<Integer> gids) throws MiddlewareQueryException;

    // for test purposes
    public void clear();
}

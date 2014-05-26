package org.generationcp.middleware.manager;

import org.generationcp.middleware.dao.mbdt.MBDTGenerationDAO;
import org.generationcp.middleware.dao.mbdt.MBDTProjectDAO;
import org.generationcp.middleware.dao.mbdt.SelectedGenotypeDAO;
import org.generationcp.middleware.dao.mbdt.SelectedMarkerDAO;
import org.generationcp.middleware.domain.mbdt.SelectedGenotypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.MBDTDataManager;
import org.generationcp.middleware.pojos.mbdt.MBDTGeneration;
import org.generationcp.middleware.pojos.mbdt.MBDTProjectData;
import org.generationcp.middleware.pojos.mbdt.SelectedGenotype;
import org.generationcp.middleware.pojos.mbdt.SelectedMarker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */
public class MBDTDataManagerImpl extends DataManager implements MBDTDataManager {

    private MBDTProjectDAO projectDAO;
    private MBDTGenerationDAO generationDAO;
    private SelectedMarkerDAO selectedMarkerDAO;
    private SelectedGenotypeDAO selectedGenotypeDAO;

    public MBDTDataManagerImpl(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    @Override
    public Integer setProjectData(MBDTProjectData projectData) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        projectDAO = prepareProjectDAO();


        // TODO ask if its required to check for duplicate project name
        if (projectData.getProjectID() == null || projectData.getProjectID() == 0) {
            projectData.setProjectID(projectDAO.getNegativeId());
        }

        projectData = projectDAO.save(projectData);
        return projectData.getProjectID();
    }


    // this method is nullable
    @Override
    public MBDTProjectData getProjectData(Integer projectID) throws MiddlewareQueryException {

        if (projectID < 0) {
            requireLocalDatabaseInstance();
        } else {
            requireCentralDatabaseInstance();
        }

        projectDAO = prepareProjectDAO();
        MBDTProjectData data = projectDAO.getById(projectID);
        return data;
    }

    @Override
    public MBDTGeneration setGeneration(Integer projectID, MBDTGeneration generation) throws MiddlewareQueryException {

        requireLocalDatabaseInstance();
        if (generation.getProject() == null) {
            MBDTProjectData project = getProjectData(projectID);
            generation.setProject(project);
        }

        prepareGenerationDAO();

        if (generation.getGenerationID() == null) {
            Integer newId = generationDAO.getNegativeId("generationID");
            generation.setGenerationID(newId);
        }


        generationDAO.saveOrUpdate(generation);

        return generation;
    }

    @Override
    public MBDTGeneration getGeneration(Integer generationID) throws MiddlewareQueryException {
        prepareGenerationDAO();
        return generationDAO.getById(generationID);
    }

    @Override
    public void setMarkerStatus(Integer generatonID, List<Integer> markerIDs) throws MiddlewareQueryException {

        requireLocalDatabaseInstance();

        prepareGenerationDAO();
        prepareSelectedMarkerDAO();

        List<SelectedMarker> markers = selectedMarkerDAO.getMarkersByGenerationID(generatonID);

        if (markers != null && markers.size() > 0) {
            for (SelectedMarker marker : markers) {
                markerIDs.remove(marker.getMarkerID());
            }
        }

        MBDTGeneration generation = generationDAO.getById(generatonID);

        for (Integer markerID : markerIDs) {
            SelectedMarker sm = new SelectedMarker(generation, markerID);
            Integer newId = selectedMarkerDAO.getNegativeId("id");
            sm.setId(newId);

            selectedMarkerDAO.saveOrUpdate(sm);
        }
    }

    @Override
    public List<Integer> getMarkerStatus(Integer generationID) throws MiddlewareQueryException {
        prepareGenerationDAO();
        MBDTGeneration generation = generationDAO.getById(generationID);

        List<SelectedMarker> markers = generation.getSelectedMarkers();

        List<Integer> returnValues = new ArrayList<Integer>();

        for (SelectedMarker marker : markers) {
            returnValues.add(marker.getMarkerID());
        }

        return returnValues;
    }

    @Override
    public List<SelectedGenotype> getSelectedAccession(Integer generationID) throws MiddlewareQueryException {
        prepareSelectedGenotypeDAO();

        try {
            return selectedGenotypeDAO.getAccessions(generationID);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MiddlewareQueryException(e.getMessage());
        }
    }

    @Override
    public List<SelectedGenotype> getParentData(Integer generationID) throws MiddlewareQueryException {
        prepareSelectedGenotypeDAO();

        try {
            return selectedGenotypeDAO.getParentData(generationID);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MiddlewareQueryException(e.getMessage());
        }
    }

    @Override
    public void setSelectedAccessions(Integer generationID, List<Integer> gids) throws MiddlewareQueryException {

        requireLocalDatabaseInstance();

        prepareGenerationDAO();
        prepareSelectedGenotypeDAO();
        MBDTGeneration generation = getGeneration(generationID);

        for (Integer gid : gids) {
            SelectedGenotype genotype = new SelectedGenotype(generation, SelectedGenotypeEnum.R, gid);
            Integer newId = selectedGenotypeDAO.getNegativeId("id");
            genotype.setId(newId);

            selectedGenotypeDAO.saveOrUpdate(genotype);

        }
    }

    @Override
    public void setParentData(Integer generationID, SelectedGenotypeEnum genotypeEnum, List<Integer> gids) throws MiddlewareQueryException {

        requireLocalDatabaseInstance();

        prepareGenerationDAO();
        prepareSelectedGenotypeDAO();
        MBDTGeneration generation = getGeneration(generationID);

        List<SelectedGenotype> existingAccession = selectedGenotypeDAO.getAccessionsByIds(gids);

        if (existingAccession != null && existingAccession.size() > 0) {
            for (SelectedGenotype genotype : existingAccession) {
                gids.remove(genotype.getGid());
                genotype.setType(genotypeEnum);
                selectedGenotypeDAO.saveOrUpdate(genotype);
            }
        }

        for (Integer gid : gids) {
            SelectedGenotype genotype = new SelectedGenotype(generation, genotypeEnum, gid);
            Integer newId = selectedGenotypeDAO.getNegativeId("id");
            genotype.setId(newId);

            selectedGenotypeDAO.saveOrUpdate(genotype);

        }
    }

    protected MBDTProjectDAO prepareProjectDAO() {
        if (projectDAO == null) {
            projectDAO = new MBDTProjectDAO();
        }

        projectDAO.setSession(getActiveSession());

        return projectDAO;
    }

    protected MBDTGenerationDAO prepareGenerationDAO() {
        if (generationDAO == null) {
            generationDAO = new MBDTGenerationDAO();
        }

        generationDAO.setSession(getActiveSession());

        return generationDAO;
    }

    protected SelectedMarkerDAO prepareSelectedMarkerDAO() {
        if (selectedMarkerDAO == null) {
            selectedMarkerDAO = new SelectedMarkerDAO();
        }

        selectedMarkerDAO.setSession(getActiveSession());

        return selectedMarkerDAO;
    }

    protected SelectedGenotypeDAO prepareSelectedGenotypeDAO() {
        if (selectedGenotypeDAO == null) {
            selectedGenotypeDAO = new SelectedGenotypeDAO();
        }

        selectedGenotypeDAO.setSession(getActiveSession());

        return selectedGenotypeDAO;
    }

    @Override
    public void clear() {
        getActiveSession().clear();
    }
}

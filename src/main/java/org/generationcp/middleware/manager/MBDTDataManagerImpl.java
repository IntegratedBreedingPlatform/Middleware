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
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */
public class MBDTDataManagerImpl extends DataManager implements MBDTDataManager {

    private MBDTProjectDAO projectDAO;
    private MBDTGenerationDAO generationDAO;
    private SelectedMarkerDAO selectedMarkerDAO;
    private SelectedGenotypeDAO selectedGenotypeDAO;

    public MBDTDataManagerImpl(HibernateSessionProvider sessionProviderForLocal) {
        super(sessionProviderForLocal);
    }

    @Override
    public Integer setProjectData(MBDTProjectData projectData) throws MiddlewareQueryException {
        projectDAO = prepareProjectDAO();

        if (projectData.getProjectID() == null || projectData.getProjectID() == 0) {
            MBDTProjectData existingData = projectDAO.getByName(projectData.getProjectName());

            if (existingData != null) {
                throw new MiddlewareQueryException("A project with the given name already exists");
            }

            projectData.setProjectID(projectDAO.getNegativeId());
        }

        projectData = projectDAO.save(projectData);
        return projectData.getProjectID();
    }


    // this method is nullable
    @Override
    public MBDTProjectData getProjectData(Integer projectID) throws MiddlewareQueryException {

        projectDAO = prepareProjectDAO();
        MBDTProjectData data = projectDAO.getById(projectID);
        return data;
    }

    @Override
    public List<MBDTProjectData> getAllProjects() throws MiddlewareQueryException {
        projectDAO = prepareProjectDAO();
        return projectDAO.getAll();
    }

    @Override
    public Integer getProjectIDByProjectName(String projectName) throws MiddlewareQueryException {
        projectDAO = prepareProjectDAO();

        MBDTProjectData projectData = projectDAO.getByName(projectName);

        if (projectData == null) {
            return null;
        } else {
            return projectData.getProjectID();
        }
    }

    @Override
    public MBDTGeneration setGeneration(Integer projectID, MBDTGeneration generation) throws MiddlewareQueryException {

        if (generation.getProject() == null) {
            MBDTProjectData project = getProjectData(projectID);

            if (project == null) {
                throw new MiddlewareQueryException("Project with given ID does not exist");
            }
            generation.setProject(project);
        }

        prepareGenerationDAO();

        if (generation.getGenerationID() == null) {

            MBDTGeneration existing = generationDAO.getByNameAndProjectID(generation.getGenerationName(), projectID);

            if (existing != null) {
                throw new MiddlewareQueryException("A generation with the given name within the project already exists");
            }

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
    public List<MBDTGeneration> getAllGenerations(Integer projectID) throws MiddlewareQueryException {
        prepareGenerationDAO();
        return generationDAO.getByProjectID(projectID);
    }

    @Override
    public Integer getGenerationIDByGenerationName(String name, Integer projectID) throws MiddlewareQueryException {
        prepareGenerationDAO();

        MBDTGeneration generation = generationDAO.getByNameAndProjectID(name, projectID);

        if (generation == null) {
            return null;
        } else {
            return generation.getGenerationID();
        }
    }

    @Override
    public void setMarkerStatus(Integer generatonID, List<Integer> markerIDs) throws MiddlewareQueryException {

        if (markerIDs == null || markerIDs.size() == 0 ) {
            return;
        }

        prepareGenerationDAO();
        prepareSelectedMarkerDAO();

        List<SelectedMarker> markers = selectedMarkerDAO.getMarkersByGenerationID(generatonID);

        if (markers != null && markers.size() > 0) {
            for (SelectedMarker marker : markers) {
                markerIDs.remove(marker.getMarkerID());
            }
        }

        MBDTGeneration generation = generationDAO.getById(generatonID);

        if (generation == null) {
            throw new MiddlewareQueryException("Generation with given ID does not exist");
        }

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

        if (generation == null) {
            throw new MiddlewareQueryException("Generation with given ID does not exist");
        }

        List<SelectedMarker> markers = generation.getSelectedMarkers();

        List<Integer> returnValues = new ArrayList<Integer>();

        for (SelectedMarker marker : markers) {
            returnValues.add(marker.getMarkerID());
        }

        return returnValues;
    }

    @Override
    public List<SelectedGenotype> getSelectedAccession(Integer generationID) throws MiddlewareQueryException {

        prepareGenerationDAO();
        prepareSelectedGenotypeDAO();

        MBDTGeneration generation = generationDAO.getById(generationID);
        if (generation == null) {
            throw new MiddlewareQueryException("Generation with given ID does not exist");
        }

        try {
            return selectedGenotypeDAO.getSelectedAccessions(generationID);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MiddlewareQueryException(e.getMessage());
        }
    }

    @Override
    public List<SelectedGenotype> getParentData(Integer generationID) throws MiddlewareQueryException {

        prepareSelectedGenotypeDAO();
        prepareGenerationDAO();

        MBDTGeneration generation = generationDAO.getById(generationID);
        if (generation == null) {
            throw new MiddlewareQueryException("Generation with given ID does not exist");
        }

        try {
            return selectedGenotypeDAO.getParentData(generationID);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MiddlewareQueryException(e.getMessage());
        }
    }

    @Override
    public void setSelectedAccessions(Integer generationID, List<Integer> gids) throws MiddlewareQueryException {

        if (gids == null || gids.size() == 0) {
            return;
        }

        Set<Integer> gidSet = new HashSet<Integer>(gids);

        prepareGenerationDAO();
        prepareSelectedGenotypeDAO();
        MBDTGeneration generation = getGeneration(generationID);

        if (generation == null) {
            throw new MiddlewareQueryException("Generation with given ID does not exist");
        }

        Session session = getActiveSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<SelectedGenotype> existing = selectedGenotypeDAO.getSelectedGenotypeByIds(gidSet);


            // find existing instances and toggle their selected status appropriately
            if (existing != null) {
                for (SelectedGenotype genotype : existing) {
                    switch (genotype.getType()) {
                        case SR:
                            genotype.setType(SelectedGenotypeEnum.R);
                            break;
                        case SD:
                            genotype.setType(SelectedGenotypeEnum.D);
                            break;
                        case R:
                            genotype.setType(SelectedGenotypeEnum.SR);
                            break;
                        case D:
                            genotype.setType(SelectedGenotypeEnum.SD);
                            break;
                    }

                    selectedGenotypeDAO.saveOrUpdate(genotype);

                    gidSet.remove(genotype.getGid());
                }

                // perform batch operation on update commands first
                session.flush();
                session.clear();
            }


            // create new entries with the default type
            for (Integer gid : gidSet) {
                SelectedGenotype genotype = new SelectedGenotype(generation, SelectedGenotypeEnum.SR, gid);
                Integer newId = selectedGenotypeDAO.getNegativeId("id");
                genotype.setId(newId);

                selectedGenotypeDAO.saveOrUpdate(genotype);

            }

            // perform batch update on creation of new entries
            session.flush();
            session.clear();
            transaction.commit();
        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
            transaction.rollback();
        }
    }

    @Override
    public void setParentData(Integer generationID, SelectedGenotypeEnum genotypeEnum, List<Integer> gids) throws MiddlewareQueryException {

        if (gids == null || gids.size() == 0) {
            return;
        }

        Set<Integer> gidSet = new HashSet<Integer>(gids);

        if (genotypeEnum.equals(SelectedGenotypeEnum.SD) || genotypeEnum.equals(SelectedGenotypeEnum.SR)) {
            throw new MiddlewareQueryException("Set Parent Data only takes in Recurrent or Donor as possible types. Use setSelectedAccession to mark / create entries as Selected Recurrent / Selected Donor");
        }

        prepareGenerationDAO();
        prepareSelectedGenotypeDAO();
        MBDTGeneration generation = getGeneration(generationID);

        if (generation == null) {
            throw new MiddlewareQueryException("Generation with given ID does not exist");
        }

        List<SelectedGenotype> existingAccession = selectedGenotypeDAO.getSelectedGenotypeByIds(gidSet);

        Session session = getActiveSession();
        Transaction transaction = session.beginTransaction();

        try {
            if (existingAccession != null && existingAccession.size() > 0) {
                for (SelectedGenotype genotype : existingAccession) {

                    switch (genotype.getType()) {
                        case SR:
                            if (genotypeEnum.equals(SelectedGenotypeEnum.D)) {
                                genotype.setType(SelectedGenotypeEnum.SD);
                            }

                            break;
                        case SD:
                            if (genotypeEnum.equals(SelectedGenotypeEnum.R)) {
                                genotype.setType(SelectedGenotypeEnum.SR);
                            }

                            break;
                        case R:
                            if (genotypeEnum.equals(SelectedGenotypeEnum.D)) {
                                genotype.setType(SelectedGenotypeEnum.D);
                            }
                            break;
                        case D:
                            if (genotypeEnum.equals(SelectedGenotypeEnum.R)) {
                                genotype.setType(SelectedGenotypeEnum.R);
                            }
                            break;
                    }

                    gidSet.remove(genotype.getGid());

                    selectedGenotypeDAO.saveOrUpdate(genotype);
                }

                session.flush();
                session.clear();
            }

            for (Integer gid : gidSet) {
                SelectedGenotype genotype = new SelectedGenotype(generation, genotypeEnum, gid);
                Integer newId = selectedGenotypeDAO.getNegativeId("id");
                genotype.setId(newId);

                selectedGenotypeDAO.saveOrUpdate(genotype);

            }

            session.flush();
            session.clear();
            transaction.commit();
        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
            transaction.rollback();
            throw e;
        } catch (HibernateException e) {
            e.printStackTrace();
            transaction.rollback();
            throw e;
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
        if (getActiveSession() != null) {
            getActiveSession().clear();
        }
    }
}
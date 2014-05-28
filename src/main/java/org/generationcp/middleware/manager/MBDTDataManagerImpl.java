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
        if (generationID < 0) {
            requireLocalDatabaseInstance();
        } else {
            requireCentralDatabaseInstance();
        }
        return generationDAO.getById(generationID);
    }

    @Override
    public List<MBDTGeneration> getGenerations(Integer projectID) throws MiddlewareQueryException {
        prepareGenerationDAO();

        if (projectID < 0) {
            requireLocalDatabaseInstance();
        } else {
            requireCentralDatabaseInstance();
        }

        return generationDAO.getByProjectID(projectID);
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

        if (generationID < 0) {
            requireLocalDatabaseInstance();
        } else {
            requireCentralDatabaseInstance();
        }

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

        if (generationID < 0) {
            requireLocalDatabaseInstance();
        } else {
            requireCentralDatabaseInstance();
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
        Session session = getActiveSession();
        Transaction transaction = session.beginTransaction();
        try {
            List<SelectedGenotype> existing = selectedGenotypeDAO.getSelectedGenotypeByIds(gids);


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

                    gids.remove(genotype.getGid());
                }

                // perform batch operation on update commands first
                session.flush();
                session.clear();
            }


            // create new entries with the default type
            for (Integer gid : gids) {
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

        if (genotypeEnum.equals(SelectedGenotypeEnum.SD) || genotypeEnum.equals(SelectedGenotypeEnum.SR)) {
            throw new MiddlewareQueryException("Set Parent Data only takes in Recurrent or Donor as possible types. Use setSelectedAccession to mark / create entries as Selected Recurrent / Selected Donor");
        }

        requireLocalDatabaseInstance();

        prepareGenerationDAO();
        prepareSelectedGenotypeDAO();
        MBDTGeneration generation = getGeneration(generationID);

        List<SelectedGenotype> existingAccession = selectedGenotypeDAO.getSelectedGenotypeByIds(gids);

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

                    gids.remove(genotype.getGid());

                    selectedGenotypeDAO.saveOrUpdate(genotype);
                }

                session.flush();
                session.clear();
            }

            for (Integer gid : gids) {
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
        getActiveSession().clear();
    }
}

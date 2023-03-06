
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
 */
@Transactional
public class MBDTDataManagerImpl extends DataManager implements MBDTDataManager {

	private MBDTProjectDAO projectDAO;
	private MBDTGenerationDAO generationDAO;
	private SelectedMarkerDAO selectedMarkerDAO;
	private SelectedGenotypeDAO selectedGenotypeDAO;

	private static final Logger LOG = LoggerFactory.getLogger(MBDTDataManagerImpl.class);

	public MBDTDataManagerImpl() {

	}

	public MBDTDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	@Override
	public Integer setProjectData(MBDTProjectData projectData) throws MiddlewareQueryException {
		this.projectDAO = this.prepareProjectDAO();

		if (projectData.getProjectID() == null || projectData.getProjectID() == 0) {
			MBDTProjectData existingData = this.projectDAO.getByName(projectData.getProjectName());

			if (existingData != null) {
				throw new MiddlewareQueryException("A project with the given name already exists");
			}
		}

		projectData = this.projectDAO.save(projectData);
		return projectData.getProjectID();
	}

	// this method is nullable
	@Override
	public MBDTProjectData getProjectData(Integer projectID) throws MiddlewareQueryException {

		this.projectDAO = this.prepareProjectDAO();
		MBDTProjectData data = this.projectDAO.getById(projectID);
		return data;
	}

	@Override
	public List<MBDTProjectData> getAllProjects() throws MiddlewareQueryException {
		this.projectDAO = this.prepareProjectDAO();
		return this.projectDAO.getAll();
	}

	@Override
	public Integer getProjectIDByProjectName(String projectName) throws MiddlewareQueryException {
		this.projectDAO = this.prepareProjectDAO();

		MBDTProjectData projectData = this.projectDAO.getByName(projectName);

		if (projectData == null) {
			return null;
		} else {
			return projectData.getProjectID();
		}
	}

	@Override
	public MBDTGeneration setGeneration(Integer projectID, MBDTGeneration generation) throws MiddlewareQueryException {

		if (generation.getProject() == null) {
			MBDTProjectData project = this.getProjectData(projectID);

			if (project == null) {
				throw new MiddlewareQueryException("Project with given ID does not exist");
			}
			generation.setProject(project);
		}

		this.prepareGenerationDAO();

		if (generation.getGenerationID() == null) {

			MBDTGeneration existing = this.generationDAO.getByNameAndProjectID(generation.getGenerationName(), projectID);

			if (existing != null) {
				throw new MiddlewareQueryException("A generation with the given name within the project already exists");
			}
		}

		this.generationDAO.saveOrUpdate(generation);

		return generation;
	}

	@Override
	public MBDTGeneration getGeneration(Integer generationID) throws MiddlewareQueryException {

		this.prepareGenerationDAO();
		return this.generationDAO.getById(generationID);
	}

	@Override
	public List<MBDTGeneration> getAllGenerations(Integer projectID) throws MiddlewareQueryException {
		this.prepareGenerationDAO();
		return this.generationDAO.getByProjectID(projectID);
	}

	@Override
	public Integer getGenerationIDByGenerationName(String name, Integer projectID) throws MiddlewareQueryException {
		this.prepareGenerationDAO();

		MBDTGeneration generation = this.generationDAO.getByNameAndProjectID(name, projectID);

		if (generation == null) {
			return null;
		} else {
			return generation.getGenerationID();
		}
	}

	@Override
	public void setMarkerStatus(Integer generatonID, List<Integer> markerIDs) throws MiddlewareQueryException {

		if (markerIDs == null || markerIDs.isEmpty()) {
			return;
		}

		this.prepareGenerationDAO();
		this.prepareSelectedMarkerDAO();

		List<SelectedMarker> markers = this.selectedMarkerDAO.getMarkersByGenerationID(generatonID);

		if (markers != null && !markers.isEmpty()) {
			for (SelectedMarker marker : markers) {
				markerIDs.remove(marker.getMarkerID());
			}
		}

		MBDTGeneration generation = this.generationDAO.getById(generatonID);

		if (generation == null) {
			throw new MiddlewareQueryException("Generation with given ID does not exist");
		}

		for (Integer markerID : markerIDs) {
			SelectedMarker sm = new SelectedMarker(generation, markerID);
			this.selectedMarkerDAO.saveOrUpdate(sm);
		}
	}

	@Override
	public List<Integer> getMarkerStatus(Integer generationID) throws MiddlewareQueryException {
		this.prepareGenerationDAO();

		MBDTGeneration generation = this.generationDAO.getById(generationID);

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

		this.prepareGenerationDAO();
		this.prepareSelectedGenotypeDAO();

		MBDTGeneration generation = this.generationDAO.getById(generationID);
		if (generation == null) {
			throw new MiddlewareQueryException("Generation with given ID does not exist");
		}

		try {
			return this.selectedGenotypeDAO.getSelectedAccessions(generationID);
		} catch (Exception e) {
			MBDTDataManagerImpl.LOG.error("Error accessing selected accessions", e);
			throw new MiddlewareQueryException(e.getMessage());
		}
	}

	@Override
	public List<SelectedGenotype> getParentData(Integer generationID) throws MiddlewareQueryException {

		this.prepareSelectedGenotypeDAO();
		this.prepareGenerationDAO();

		MBDTGeneration generation = this.generationDAO.getById(generationID);
		if (generation == null) {
			throw new MiddlewareQueryException("Generation with given ID does not exist");
		}

		try {
			return this.selectedGenotypeDAO.getParentData(generationID);
		} catch (Exception e) {
			MBDTDataManagerImpl.LOG.error("Error getting parent data with ID " + generationID, e);
			throw new MiddlewareQueryException(e.getMessage());
		}
	}

	@Override
	public void setSelectedAccessions(MBDTGeneration generation, List<Integer> gids) throws MiddlewareQueryException {

		if (gids == null || gids.isEmpty()) {
			return;
		}

		Set<Integer> gidSet = new HashSet<Integer>(gids);

		this.prepareGenerationDAO();
		this.prepareSelectedGenotypeDAO();

		if (generation == null) {
			throw new MiddlewareQueryException("Given Generation does not exist");
		}

		try {
			List<SelectedGenotype> existing = this.selectedGenotypeDAO.getSelectedGenotypeByIds(gidSet);

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

					this.selectedGenotypeDAO.saveOrUpdate(genotype);

					gidSet.remove(genotype.getGid());
				}

			}

			// create new entries with the default type
			for (Integer gid : gidSet) {
				SelectedGenotype genotype = new SelectedGenotype(generation, SelectedGenotypeEnum.SR, gid);
				this.selectedGenotypeDAO.saveOrUpdate(genotype);

			}

		} catch (MiddlewareQueryException e) {
			MBDTDataManagerImpl.LOG.error("Setting selected accessions was not successful", e);

		}
	}

	@Override
	public void setParentData(MBDTGeneration generation, SelectedGenotypeEnum genotypeEnum, List<Integer> gids)
		throws MiddlewareQueryException {

		if (gids == null || gids.isEmpty()) {
			return;
		}

		Set<Integer> gidSet = new HashSet<Integer>(gids);

		if (genotypeEnum.equals(SelectedGenotypeEnum.SD) || genotypeEnum.equals(SelectedGenotypeEnum.SR)) {
			throw new MiddlewareQueryException(
				"Set Parent Data only takes in Recurrent or Donor as possible types. Use setSelectedAccession to mark / create entries as Selected Recurrent / Selected Donor");
		}

		this.prepareGenerationDAO();
		this.prepareSelectedGenotypeDAO();

		if (generation == null) {
			throw new MiddlewareQueryException("Generation with given ID does not exist");
		}

		List<SelectedGenotype> existingAccession = this.selectedGenotypeDAO.getSelectedGenotypeByIds(gidSet);

		try {
			if (existingAccession != null && !existingAccession.isEmpty()) {
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

					this.selectedGenotypeDAO.saveOrUpdate(genotype);
				}
			}

			for (Integer gid : gidSet) {
				SelectedGenotype genotype = new SelectedGenotype(generation, genotypeEnum, gid);
				this.selectedGenotypeDAO.saveOrUpdate(genotype);

			}

		} catch (MiddlewareQueryException e) {
			MBDTDataManagerImpl.LOG.error("Setting parent data was not successful", e);
			throw e;
		} catch (HibernateException e) {
			MBDTDataManagerImpl.LOG.error("Setting parent data was not successful", e);
			throw e;
		}
	}

	protected MBDTProjectDAO prepareProjectDAO() {
		if (this.projectDAO == null) {
			this.projectDAO = new MBDTProjectDAO(this.getActiveSession());
		}
		return this.projectDAO;
	}

	protected MBDTGenerationDAO prepareGenerationDAO() {
		if (this.generationDAO == null) {
			this.generationDAO = new MBDTGenerationDAO(this.getActiveSession());
		}
		return this.generationDAO;
	}

	protected SelectedMarkerDAO prepareSelectedMarkerDAO() {
		if (this.selectedMarkerDAO == null) {
			this.selectedMarkerDAO = new SelectedMarkerDAO(this.getActiveSession());
		}
		return this.selectedMarkerDAO;
	}

	protected SelectedGenotypeDAO prepareSelectedGenotypeDAO() {
		if (this.selectedGenotypeDAO == null) {
			this.selectedGenotypeDAO = new SelectedGenotypeDAO(this.getActiveSession());
		}
		return this.selectedGenotypeDAO;
	}

	@Override
	public void clear() {
		if (this.getActiveSession() != null) {
			this.getActiveSession().clear();
		}
	}
}

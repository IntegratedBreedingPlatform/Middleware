package org.generationcp.middleware.manager;

import org.generationcp.middleware.dao.CountryDAO;
import org.generationcp.middleware.dao.FormulaDAO;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.dao.KeySequenceRegisterDAO;
import org.generationcp.middleware.dao.ListDataProjectDAO;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.dao.ProgenitorDAO;
import org.generationcp.middleware.dao.ProgramPresetDAO;
import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.SampleListDao;
import org.generationcp.middleware.dao.SearchRequestDAO;
import org.generationcp.middleware.dao.dms.DatasetTypeDAO;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.dao.dms.GeolocationPropertyDao;
import org.generationcp.middleware.dao.dms.LocationSearchDao;
import org.generationcp.middleware.dao.dms.ObservationUnitsSearchDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProgramFavoriteDAO;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.dao.dms.StockDao;
import org.generationcp.middleware.dao.dms.StudySearchDao;
import org.generationcp.middleware.dao.ims.ExperimentTransactionDAO;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.dao.ims.StockTransactionDAO;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class DaoFactory {

	private HibernateSessionProvider sessionProvider;

	public DaoFactory() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public DaoFactory(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public FormulaDAO getFormulaDAO() {
		final FormulaDAO formulaDAO = new FormulaDAO();
		formulaDAO.setSession(this.sessionProvider.getSession());
		return formulaDAO;
	}

	public SampleListDao getSampleListDao() {
		final SampleListDao sampleListDao = new SampleListDao();
		sampleListDao.setSession(this.sessionProvider.getSession());
		return sampleListDao;
	}

	public SampleDao getSampleDao() {
		final SampleDao sampleDao = new SampleDao();
		sampleDao.setSession(this.sessionProvider.getSession());
		return sampleDao;
	}

	public CVTermDao getCvTermDao() {
		final CVTermDao cvTermDao = new CVTermDao();
		cvTermDao.setSession(this.sessionProvider.getSession());
		return cvTermDao;
	}

	public CvTermPropertyDao getCvTermPropertyDao() {
		final CvTermPropertyDao cvTermPropertyDao = new CvTermPropertyDao();
		cvTermPropertyDao.setSession(this.sessionProvider.getSession());
		return cvTermPropertyDao;
	}

	public CVTermRelationshipDao getCvTermRelationshipDao() {
		final CVTermRelationshipDao cvTermRelationshipDao = new CVTermRelationshipDao();
		cvTermRelationshipDao.setSession(this.sessionProvider.getSession());
		return cvTermRelationshipDao;
	}

	public LotDAO getLotDao() {
		final LotDAO lotDao = new LotDAO();
		lotDao.setSession(this.sessionProvider.getSession());
		return lotDao;
	}

	public TransactionDAO getTransactionDAO() {
		final TransactionDAO transactionDao = new TransactionDAO();
		transactionDao.setSession(this.sessionProvider.getSession());
		return transactionDao;
	}

	public StockDao getStockDao() {
		final StockDao stockDao = new StockDao();
		stockDao.setSession(this.sessionProvider.getSession());
		return stockDao;
	}

	public StockTransactionDAO getStockTransactionDAO() {
		final StockTransactionDAO stockTransactionDao = new StockTransactionDAO();
		stockTransactionDao.setSession(this.sessionProvider.getSession());
		return stockTransactionDao;
	}

	public GermplasmDAO getGermplasmDao() {
		final GermplasmDAO germplasmDao = new GermplasmDAO();
		germplasmDao.setSession(this.sessionProvider.getSession());
		return germplasmDao;
	}

	public GermplasmListDAO getGermplasmListDAO() {
		final GermplasmListDAO germplasmListDao = new GermplasmListDAO();
		germplasmListDao.setSession(this.sessionProvider.getSession());
		return germplasmListDao;
	}

	public GermplasmListDataDAO getGermplasmListDataDAO() {
		final GermplasmListDataDAO germplasmListDataDao = new GermplasmListDataDAO();
		germplasmListDataDao.setSession(this.sessionProvider.getSession());
		return germplasmListDataDao;
	}

	public LocationDAO getLocationDAO() {
		final LocationDAO locationDao = new LocationDAO();
		locationDao.setSession(this.sessionProvider.getSession());
		return locationDao;
	}

	public PhenotypeDao getPhenotypeDAO() {
		final PhenotypeDao phenotypeDao = new PhenotypeDao();
		phenotypeDao.setSession(this.sessionProvider.getSession());
		return phenotypeDao;

	}

	public DmsProjectDao getDmsProjectDAO() {
		final DmsProjectDao dmsProjectDao = new DmsProjectDao();
		dmsProjectDao.setSession(this.sessionProvider.getSession());
		return dmsProjectDao;

	}

	public KeySequenceRegisterDAO getKeySequenceRegisterDAO() {
		final KeySequenceRegisterDAO keySequenceRegisterDAO = new KeySequenceRegisterDAO();
		keySequenceRegisterDAO.setSession(this.sessionProvider.getSession());
		return keySequenceRegisterDAO;

	}

	public ProjectPropertyDao getProjectPropertyDAO() {
		final ProjectPropertyDao projectPropDao = new ProjectPropertyDao();
		projectPropDao.setSession(this.sessionProvider.getSession());
		return projectPropDao;
	}

	public ExperimentDao getExperimentDao() {
		final ExperimentDao experimentDao = new ExperimentDao();
		experimentDao.setSession(this.sessionProvider.getSession());
		return experimentDao;

	}

	public GeolocationDao getGeolocationDao() {
		final GeolocationDao geolocationDao = new GeolocationDao();
		geolocationDao.setSession(this.sessionProvider.getSession());
		return geolocationDao;
	}

	public GeolocationPropertyDao getGeolocationPropertyDao() {
		final GeolocationPropertyDao geolocationPropertyDao = new GeolocationPropertyDao();
		geolocationPropertyDao.setSession(this.sessionProvider.getSession());
		return geolocationPropertyDao;
	}

	public ProgramPresetDAO getProgramPresetDAO() {
		final ProgramPresetDAO programPresetDAO = new ProgramPresetDAO();
		programPresetDAO.setSession(this.sessionProvider.getSession());
		return programPresetDAO;
	}

	public NameDAO getNameDao() {
		final NameDAO nameDAO = new NameDAO();
		nameDAO.setSession(this.sessionProvider.getSession());
		return nameDAO;
	}

	public ProgenitorDAO getProgenitorDao() {
		final ProgenitorDAO progenitorDao = new ProgenitorDAO();
		progenitorDao.setSession(this.sessionProvider.getSession());
		return progenitorDao;
	}

	public DatasetTypeDAO getDatasetTypeDao() {
		final DatasetTypeDAO datasetTypeDao = new DatasetTypeDAO();
		datasetTypeDao.setSession(this.sessionProvider.getSession());
		return datasetTypeDao;
	}

	public SearchRequestDAO getSearchRequestDAO() {
		final SearchRequestDAO brapiSearchDAO = new SearchRequestDAO();
		brapiSearchDAO.setSession(this.sessionProvider.getSession());
		return brapiSearchDAO;
	}

	public ObservationUnitsSearchDao getObservationUnitsSearchDAO() {
		final ObservationUnitsSearchDao obsUnitsSearchDao = new ObservationUnitsSearchDao();
		obsUnitsSearchDao.setSession(this.sessionProvider.getSession());
		return obsUnitsSearchDao;
	}

	public StudySearchDao getStudySearchDao() {
		final StudySearchDao studySearchDao = new StudySearchDao();
		studySearchDao.setSession(this.sessionProvider.getSession());
		return studySearchDao;
	}

	public LocationSearchDao getLocationSearchDao() {
		final LocationSearchDao locationSearchDao = new LocationSearchDao();
		locationSearchDao.setSession(this.sessionProvider.getSession());
		return locationSearchDao;
	}

	public CountryDAO getCountryDao() {
		final CountryDAO countryDAO = new CountryDAO();
		countryDAO.setSession(this.sessionProvider.getSession());
		return countryDAO;
	}

	public ListDataProjectDAO getListDataProjectDAO() {
		final ListDataProjectDAO listDataProjectDAO = new ListDataProjectDAO();
		listDataProjectDAO.setSession(this.sessionProvider.getSession());
		return listDataProjectDAO;
	}

	public ProgramFavoriteDAO getProgramFavoriteDao() {
		final ProgramFavoriteDAO programFavoriteDao = new ProgramFavoriteDAO();
		programFavoriteDao.setSession(this.sessionProvider.getSession());
		return programFavoriteDao;
	}

	public ExperimentTransactionDAO getExperimentTransactionDao() {
		final ExperimentTransactionDAO experimentTransactionDAO = new ExperimentTransactionDAO();
		experimentTransactionDAO.setSession(this.sessionProvider.getSession());
		return experimentTransactionDAO;
	}

}

package org.generationcp.middleware.manager;

import org.generationcp.middleware.dao.AttributeDAO;
import org.generationcp.middleware.dao.AttributeExternalReferenceDAO;
import org.generationcp.middleware.dao.BibrefDAO;
import org.generationcp.middleware.dao.BreedersQueryDao;
import org.generationcp.middleware.dao.CopMatrixDao;
import org.generationcp.middleware.dao.CountryDAO;
import org.generationcp.middleware.dao.CropTypeDAO;
import org.generationcp.middleware.dao.CvTermExternalReferenceDAO;
import org.generationcp.middleware.dao.ExperimentExternalReferenceDao;
import org.generationcp.middleware.dao.FileMetadataDAO;
import org.generationcp.middleware.dao.FormulaDAO;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.GermplasmExternalReferenceDAO;
import org.generationcp.middleware.dao.GermplasmListExternalReferenceDAO;
import org.generationcp.middleware.dao.GermplasmSearchDAO;
import org.generationcp.middleware.dao.GermplasmStudySourceDAO;
import org.generationcp.middleware.dao.KeySequenceRegisterDAO;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.LocdesDAO;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.dao.NamingConfigurationDAO;
import org.generationcp.middleware.dao.PhenotypeExternalReferenceDAO;
import org.generationcp.middleware.dao.ProgenitorDAO;
import org.generationcp.middleware.dao.ProgramPresetDAO;
import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.SampleExternalReferenceDAO;
import org.generationcp.middleware.dao.SampleListDao;
import org.generationcp.middleware.dao.SearchRequestDAO;
import org.generationcp.middleware.dao.StudyExternalReferenceDao;
import org.generationcp.middleware.dao.StudyInstanceExternalReferenceDao;
import org.generationcp.middleware.dao.StudyTypeDAO;
import org.generationcp.middleware.dao.UserDefinedFieldDAO;
import org.generationcp.middleware.dao.UserProgramTreeStateDAO;
import org.generationcp.middleware.dao.audit.germplasm.GermplasmAuditDAO;
import org.generationcp.middleware.dao.dms.DatasetTypeDAO;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.ExperimentPropertyDao;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.dao.dms.GeolocationPropertyDao;
import org.generationcp.middleware.dao.dms.LocationSearchDao;
import org.generationcp.middleware.dao.dms.ObservationUnitsSearchDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.PhenotypeOutlierDao;
import org.generationcp.middleware.dao.dms.ProgramFavoriteDAO;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.dao.dms.StockDao;
import org.generationcp.middleware.dao.dms.StockPropertyDao;
import org.generationcp.middleware.dao.dms.StudySearchDao;
import org.generationcp.middleware.dao.gdms.AccMetadataSetDAO;
import org.generationcp.middleware.dao.gdms.AlleleValuesDAO;
import org.generationcp.middleware.dao.gdms.CharValuesDAO;
import org.generationcp.middleware.dao.gdms.DartValuesDAO;
import org.generationcp.middleware.dao.gdms.DatasetDAO;
import org.generationcp.middleware.dao.gdms.DatasetUsersDAO;
import org.generationcp.middleware.dao.gdms.ExtendedMarkerInfoDAO;
import org.generationcp.middleware.dao.gdms.MapDAO;
import org.generationcp.middleware.dao.gdms.MappingDataDAO;
import org.generationcp.middleware.dao.gdms.MappingPopDAO;
import org.generationcp.middleware.dao.gdms.MappingPopValuesDAO;
import org.generationcp.middleware.dao.gdms.MarkerAliasDAO;
import org.generationcp.middleware.dao.gdms.MarkerDAO;
import org.generationcp.middleware.dao.gdms.MarkerDetailsDAO;
import org.generationcp.middleware.dao.gdms.MarkerInfoDAO;
import org.generationcp.middleware.dao.gdms.MarkerMetadataSetDAO;
import org.generationcp.middleware.dao.gdms.MarkerOnMapDAO;
import org.generationcp.middleware.dao.gdms.MarkerUserInfoDAO;
import org.generationcp.middleware.dao.gdms.MtaDAO;
import org.generationcp.middleware.dao.gdms.MtaMetadataDAO;
import org.generationcp.middleware.dao.gdms.QtlDAO;
import org.generationcp.middleware.dao.gdms.QtlDetailsDAO;
import org.generationcp.middleware.dao.gdms.TrackDataDAO;
import org.generationcp.middleware.dao.gdms.TrackMarkerDAO;
import org.generationcp.middleware.dao.germplasmlist.GermplasmListDAO;
import org.generationcp.middleware.dao.germplasmlist.GermplasmListDataDAO;
import org.generationcp.middleware.dao.germplasmlist.GermplasmListDataDefaultViewDAO;
import org.generationcp.middleware.dao.germplasmlist.GermplasmListDataDetailDAO;
import org.generationcp.middleware.dao.germplasmlist.GermplasmListDataSearchDAO;
import org.generationcp.middleware.dao.germplasmlist.GermplasmListDataViewDAO;
import org.generationcp.middleware.dao.ims.ExperimentTransactionDAO;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.dao.oms.CVDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.dao.oms.CvTermSynonymDao;
import org.generationcp.middleware.dao.oms.StandardVariableDao;
import org.generationcp.middleware.dao.oms.VariableOverridesDao;
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

	public CopMatrixDao getCopMatrixDao() {
		return new CopMatrixDao(this.sessionProvider.getSession());
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

	public StockPropertyDao getStockPropertyDao() {
		final StockPropertyDao stockPropertyDao = new StockPropertyDao();
		stockPropertyDao.setSession(this.sessionProvider.getSession());
		return stockPropertyDao;
	}

	public GermplasmDAO getGermplasmDao() {
		return new GermplasmDAO(this.sessionProvider.getSession());
	}

	public GermplasmSearchDAO getGermplasmSearchDAO() {
		return new GermplasmSearchDAO(this.sessionProvider.getSession());
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

	public AttributeDAO getAttributeDAO() {
		final AttributeDAO attributeDAO = new AttributeDAO();
		attributeDAO.setSession(this.sessionProvider.getSession());
		return attributeDAO;
	}

	public LocationDAO getLocationDAO() {
		return  new LocationDAO(this.sessionProvider.getSession());
	}

	public MethodDAO getMethodDAO() {
		return new MethodDAO(this.sessionProvider.getSession());
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
		return new NameDAO(this.sessionProvider.getSession());
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

	public UserDefinedFieldDAO getUserDefinedFieldDAO() {
		return new UserDefinedFieldDAO(this.sessionProvider.getSession());
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

	public ExperimentPropertyDao getExperimentPropertyDao() {
		final ExperimentPropertyDao experimentPropertyDao = new ExperimentPropertyDao();
		experimentPropertyDao.setSession(this.sessionProvider.getSession());
		return experimentPropertyDao;
	}

	public GermplasmStudySourceDAO getGermplasmStudySourceDAO() {
		final GermplasmStudySourceDAO germplasmStudySourceDAO = new GermplasmStudySourceDAO();
		germplasmStudySourceDAO.setSession(this.sessionProvider.getSession());
		return germplasmStudySourceDAO;
	}

	public BibrefDAO getBibrefDAO() {
		final BibrefDAO bibrefDAO = new BibrefDAO();
		bibrefDAO.setSession(this.sessionProvider.getSession());
		return bibrefDAO;
	}

	public StudyTypeDAO getStudyTypeDao() {
		final StudyTypeDAO studyTypeDAO = new StudyTypeDAO();
		studyTypeDAO.setSession(this.sessionProvider.getSession());
		return studyTypeDAO;
	}

	public LocdesDAO getLocDesDao() {
		final LocdesDAO locdesDao = new LocdesDAO();
		locdesDao.setSession(this.sessionProvider.getSession());
		return locdesDao;
	}

	public NamingConfigurationDAO getNamingConfigurationDAO() {
		final NamingConfigurationDAO namingConfigurationDAO = new NamingConfigurationDAO();
		namingConfigurationDAO.setSession(this.sessionProvider.getSession());
		return namingConfigurationDAO;
	}

	public UserProgramTreeStateDAO getUserProgramTreeStateDAO() {
		final UserProgramTreeStateDAO userProgramTreeStateDAO = new UserProgramTreeStateDAO();
		userProgramTreeStateDAO.setSession(this.sessionProvider.getSession());
		return userProgramTreeStateDAO;
	}

	public CropTypeDAO getCropTypeDAO() {
		final CropTypeDAO cropTypeDAO = new CropTypeDAO();
		cropTypeDAO.setSession(this.sessionProvider.getSession());
		return cropTypeDAO;
	}

	public BreedersQueryDao getBreedersQueryDao() {
		return new BreedersQueryDao(this.sessionProvider.getSession());
	}

	public VariableOverridesDao getVariableProgramOverridesDao() {
		final VariableOverridesDao variableOverridesDao = new VariableOverridesDao();
		variableOverridesDao.setSession(this.sessionProvider.getSession());
		return variableOverridesDao;
	}

	public StandardVariableDao getStandardVariableDao() {
		return new StandardVariableDao(this.sessionProvider.getSession());
	}

	public CVDao getCvDao() {
		final CVDao cvDao = new CVDao();
		cvDao.setSession(this.sessionProvider.getSession());
		return cvDao;
	}

	public TrackMarkerDAO getTrackMarkerDao() {
		final TrackMarkerDAO trackMarkerDao = new TrackMarkerDAO();
		trackMarkerDao.setSession(this.sessionProvider.getSession());
		return trackMarkerDao;
	}

	public MapDAO getMapDao() {
		final MapDAO mapDao = new MapDAO();
		mapDao.setSession(this.sessionProvider.getSession());
		return mapDao;
	}

	public MappingDataDAO getMappingDataDao() {
		final MappingDataDAO mappingDataDao = new MappingDataDAO();
		mappingDataDao.setSession(this.sessionProvider.getSession());
		return mappingDataDao;
	}

	public MappingPopDAO getMappingPopDao() {
		final MappingPopDAO mappingPopDao = new MappingPopDAO();
		mappingPopDao.setSession(this.sessionProvider.getSession());
		return mappingPopDao;
	}

	public MappingPopValuesDAO getMappingPopValuesDao() {
		final MappingPopValuesDAO mappingPopValuesDao = new MappingPopValuesDAO();
		mappingPopValuesDao.setSession(this.sessionProvider.getSession());
		return mappingPopValuesDao;
	}

	public MarkerAliasDAO getMarkerAliasDao() {
		final MarkerAliasDAO markerAliasDao = new MarkerAliasDAO();
		markerAliasDao.setSession(this.sessionProvider.getSession());
		return markerAliasDao;
	}

	public MarkerDAO getMarkerDao() {
		final MarkerDAO markerDao = new MarkerDAO();
		markerDao.setSession(this.sessionProvider.getSession());
		return markerDao;
	}

	public MarkerDetailsDAO getMarkerDetailsDao() {
		final MarkerDetailsDAO markerDetailsDao = new MarkerDetailsDAO();
		markerDetailsDao.setSession(this.sessionProvider.getSession());
		return markerDetailsDao;
	}

	public MarkerInfoDAO getMarkerInfoDao() {
		final MarkerInfoDAO markerInfoDao = new MarkerInfoDAO();
		markerInfoDao.setSession(this.sessionProvider.getSession());
		return markerInfoDao;
	}

	public ExtendedMarkerInfoDAO getExtendedMarkerInfoDao() {
		final ExtendedMarkerInfoDAO extendedMarkerInfoDAO = new ExtendedMarkerInfoDAO();
		extendedMarkerInfoDAO.setSession(this.sessionProvider.getSession());
		return extendedMarkerInfoDAO;
	}

	public MarkerMetadataSetDAO getMarkerMetadataSetDao() {
		final MarkerMetadataSetDAO markerMetadataSetDao = new MarkerMetadataSetDAO();
		markerMetadataSetDao.setSession(this.sessionProvider.getSession());
		return markerMetadataSetDao;
	}

	public MarkerOnMapDAO getMarkerOnMapDao() {
		final MarkerOnMapDAO markerOnMapDao = new MarkerOnMapDAO();
		markerOnMapDao.setSession(this.sessionProvider.getSession());
		return markerOnMapDao;
	}

	public MarkerUserInfoDAO getMarkerUserInfoDao() {
		final MarkerUserInfoDAO markerUserInfoDao = new MarkerUserInfoDAO();
		markerUserInfoDao.setSession(this.sessionProvider.getSession());
		return markerUserInfoDao;
	}

	public QtlDAO getQtlDao() {
		final QtlDAO qtlDao = new QtlDAO();
		qtlDao.setSession(this.sessionProvider.getSession());
		return qtlDao;
	}

	public QtlDetailsDAO getQtlDetailsDao() {
		final QtlDetailsDAO qtlDetailsDao = new QtlDetailsDAO();
		qtlDetailsDao.setSession(this.sessionProvider.getSession());
		return qtlDetailsDao;
	}

	public MtaDAO getMtaDao() {
		final MtaDAO mtaDao = new MtaDAO();
		mtaDao.setSession(this.sessionProvider.getSession());
		return mtaDao;
	}

	public MtaMetadataDAO getMtaMetadataDao() {
		final MtaMetadataDAO mtaMetadataDao = new MtaMetadataDAO();
		mtaMetadataDao.setSession(this.sessionProvider.getSession());
		return mtaMetadataDao;
	}

	public TrackDataDAO getTrackDataDao() {
		final TrackDataDAO trackDataDao = new TrackDataDAO();
		trackDataDao.setSession(this.sessionProvider.getSession());
		return trackDataDao;
	}

	public AccMetadataSetDAO getAccMetadataSetDao() {
		final AccMetadataSetDAO accMetadataSetDao = new AccMetadataSetDAO();
		accMetadataSetDao.setSession(this.sessionProvider.getSession());
		return accMetadataSetDao;
	}

	public AlleleValuesDAO getAlleleValuesDao() {
		final AlleleValuesDAO alleleValuesDao = new AlleleValuesDAO();
		alleleValuesDao.setSession(this.sessionProvider.getSession());
		return alleleValuesDao;
	}

	public CharValuesDAO getCharValuesDao() {
		final CharValuesDAO charValuesDao = new CharValuesDAO();
		charValuesDao.setSession(this.sessionProvider.getSession());
		return charValuesDao;
	}

	public DartValuesDAO getDartValuesDao() {
		final DartValuesDAO dartValuesDao = new DartValuesDAO();
		dartValuesDao.setSession(this.sessionProvider.getSession());
		return dartValuesDao;
	}

	public DatasetDAO getDatasetDao() {
		final DatasetDAO datasetDao = new DatasetDAO();
		datasetDao.setSession(this.sessionProvider.getSession());
		return datasetDao;
	}

	public DatasetUsersDAO getDatasetUsersDao() {
		final DatasetUsersDAO datasetUsersDao = new DatasetUsersDAO();
		datasetUsersDao.setSession(this.sessionProvider.getSession());
		return datasetUsersDao;
	}

	public CvTermSynonymDao getCvTermSynonymDao() {
		final CvTermSynonymDao cvTermSynonymDao = new CvTermSynonymDao();
		cvTermSynonymDao.setSession(this.sessionProvider.getSession());
		return cvTermSynonymDao;
	}

	public PhenotypeOutlierDao getPhenotypeOutlierDao() {
		final PhenotypeOutlierDao phenotypeOutlierDao = new PhenotypeOutlierDao();
		phenotypeOutlierDao.setSession(this.sessionProvider.getSession());
		return phenotypeOutlierDao;
	}

	public GermplasmExternalReferenceDAO getGermplasmExternalReferenceDAO() {
		final GermplasmExternalReferenceDAO germplasmExternalReferenceDAO = new GermplasmExternalReferenceDAO();
		germplasmExternalReferenceDAO.setSession(this.sessionProvider.getSession());
		return germplasmExternalReferenceDAO;
	}

	public GermplasmListExternalReferenceDAO getGermplasmListExternalReferenceDAO() {
		final GermplasmListExternalReferenceDAO germplasmListExternalReferenceDAO = new GermplasmListExternalReferenceDAO();
		germplasmListExternalReferenceDAO.setSession(this.sessionProvider.getSession());
		return germplasmListExternalReferenceDAO;
	}

	public StudyExternalReferenceDao getStudyExternalReferenceDAO() {
		final StudyExternalReferenceDao studyExternalReferenceDAO = new StudyExternalReferenceDao();
		studyExternalReferenceDAO.setSession(this.sessionProvider.getSession());
		return studyExternalReferenceDAO;
	}

	public CvTermExternalReferenceDAO getCvTermExternalReferenceDAO() {
		final CvTermExternalReferenceDAO cvTermExternalReferenceDAO = new CvTermExternalReferenceDAO();
		cvTermExternalReferenceDAO.setSession(this.sessionProvider.getSession());
		return cvTermExternalReferenceDAO;
	}

	public PhenotypeExternalReferenceDAO getPhenotypeExternalReferenceDAO() {
		final PhenotypeExternalReferenceDAO phenotypeExternalReferenceDAO = new PhenotypeExternalReferenceDAO();
		phenotypeExternalReferenceDAO.setSession(this.sessionProvider.getSession());
		return phenotypeExternalReferenceDAO;
	}

	public StudyInstanceExternalReferenceDao getStudyInstanceExternalReferenceDao() {
		final StudyInstanceExternalReferenceDao studyInstanceExternalReferenceDao = new StudyInstanceExternalReferenceDao();
		studyInstanceExternalReferenceDao.setSession(this.sessionProvider.getSession());
		return studyInstanceExternalReferenceDao;
	}

	public SampleExternalReferenceDAO getSampleExternalReferenceDAO() {
		final SampleExternalReferenceDAO sampleExternalReferenceDAO = new SampleExternalReferenceDAO();
		sampleExternalReferenceDAO.setSession(this.sessionProvider.getSession());
		return sampleExternalReferenceDAO;
	}

	public ExperimentExternalReferenceDao getExperimentExternalReferenceDao() {
		final ExperimentExternalReferenceDao experimentExternalReferenceDao = new ExperimentExternalReferenceDao();
		experimentExternalReferenceDao.setSession(this.sessionProvider.getSession());
		return experimentExternalReferenceDao;
	}

	public AttributeExternalReferenceDAO getAttributeExternalReferenceDao() {
		final AttributeExternalReferenceDAO attributeExternalReferenceDao = new AttributeExternalReferenceDAO();
		attributeExternalReferenceDao.setSession(this.sessionProvider.getSession());
		return attributeExternalReferenceDao;
	}

	public GermplasmAuditDAO getGermplasmAuditDAO() {
		return new GermplasmAuditDAO(this.sessionProvider.getSession());
	}

	public FileMetadataDAO getFileMetadataDAO() {
		return new FileMetadataDAO(this.sessionProvider.getSession());
	}

	public GermplasmListDataViewDAO getGermplasmListDataViewDAO() {
		return new GermplasmListDataViewDAO(this.sessionProvider.getSession());
	}

	public GermplasmListDataSearchDAO getGermplasmListDataSearchDAO() {
		return new GermplasmListDataSearchDAO(this.sessionProvider.getSession());
	}

	public GermplasmListDataDetailDAO getGermplasmListDataDetailDAO() {
		return new GermplasmListDataDetailDAO(this.sessionProvider.getSession());
	}

	public GermplasmListDataDefaultViewDAO getGermplasmListDataDefaultViewDAO() {
		return new GermplasmListDataDefaultViewDAO(this.sessionProvider.getSession());
	}

}

package org.generationcp.middleware.manager;

import org.generationcp.middleware.dao.AttributeDAO;
import org.generationcp.middleware.dao.AttributeExternalReferenceDAO;
import org.generationcp.middleware.dao.BibrefDAO;
import org.generationcp.middleware.dao.CopMatrixDao;
import org.generationcp.middleware.dao.CountryDAO;
import org.generationcp.middleware.dao.CropParameterDAO;
import org.generationcp.middleware.dao.CropTypeDAO;
import org.generationcp.middleware.dao.CvTermExternalReferenceDAO;
import org.generationcp.middleware.dao.ExperimentExternalReferenceDao;
import org.generationcp.middleware.dao.FileMetadataDAO;
import org.generationcp.middleware.dao.FileMetadataExternalReferenceDAO;
import org.generationcp.middleware.dao.FormulaDAO;
import org.generationcp.middleware.dao.GenotypeDao;
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
import org.generationcp.middleware.dao.ProgramLocationDefaultDAO;
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
import org.generationcp.middleware.dao.dms.PhenotypeAuditDao;
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
import org.generationcp.middleware.dao.ims.LotAttributeDAO;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.dao.oms.CVDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.dao.oms.CvTermSynonymDao;
import org.generationcp.middleware.dao.oms.StandardVariableDao;
import org.generationcp.middleware.dao.oms.VariableOverridesDao;
import org.generationcp.middleware.dao.study.StudyEntrySearchDAO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class DaoFactory {

	private HibernateSessionProvider sessionProvider;

	public DaoFactory() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public DaoFactory(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public CropParameterDAO getCropParameterDAO() {
		return new CropParameterDAO(this.sessionProvider.getSession());
	}

	public FormulaDAO getFormulaDAO() {
		return new FormulaDAO(this.sessionProvider.getSession());
	}

	public SampleListDao getSampleListDao() {
		return new SampleListDao(this.sessionProvider.getSession());
	}

	public SampleDao getSampleDao() {
		return new SampleDao(this.sessionProvider.getSession());
	}

	public CVTermDao getCvTermDao() {
		return new CVTermDao(this.sessionProvider.getSession());
	}

	public CvTermPropertyDao getCvTermPropertyDao() {
		return new CvTermPropertyDao(this.sessionProvider.getSession());
	}

	public CVTermRelationshipDao getCvTermRelationshipDao() {
		return new CVTermRelationshipDao(this.sessionProvider.getSession());
	}

	public CopMatrixDao getCopMatrixDao() {
		return new CopMatrixDao(this.sessionProvider.getSession());
	}

	public LotDAO getLotDao() {
		return new LotDAO(this.sessionProvider.getSession());
	}

	public TransactionDAO getTransactionDAO() {
		return new TransactionDAO(this.sessionProvider.getSession());
	}

	public StockDao getStockDao() {
		return new StockDao(this.sessionProvider.getSession());
	}

	public StockPropertyDao getStockPropertyDao() {
		return new StockPropertyDao(this.sessionProvider.getSession());
	}

	public GermplasmDAO getGermplasmDao() {
		return new GermplasmDAO(this.sessionProvider.getSession());
	}

	public GermplasmSearchDAO getGermplasmSearchDAO() {
		return new GermplasmSearchDAO(this.sessionProvider.getSession());
	}

	public GermplasmListDAO getGermplasmListDAO() {
		return new GermplasmListDAO(this.sessionProvider.getSession());
	}

	public GermplasmListDataDAO getGermplasmListDataDAO() {
		return new GermplasmListDataDAO(this.sessionProvider.getSession());
	}

	public AttributeDAO getAttributeDAO() {
		return new AttributeDAO(this.sessionProvider.getSession());
	}

	public LotAttributeDAO getLotAttributeDAO() {
		return new LotAttributeDAO(this.sessionProvider.getSession());
	}

	public LocationDAO getLocationDAO() {
		return new LocationDAO(this.sessionProvider.getSession());
	}

	public MethodDAO getMethodDAO() {
		return new MethodDAO(this.sessionProvider.getSession());
	}

	public PhenotypeDao getPhenotypeDAO() {
		return new PhenotypeDao(this.sessionProvider.getSession());

	}

	public DmsProjectDao getDmsProjectDAO() {
		return new DmsProjectDao(this.sessionProvider.getSession());

	}

	public KeySequenceRegisterDAO getKeySequenceRegisterDAO() {
		return new KeySequenceRegisterDAO(this.sessionProvider.getSession());

	}

	public ProjectPropertyDao getProjectPropertyDAO() {
		return new ProjectPropertyDao(this.sessionProvider.getSession());
	}

	public ExperimentDao getExperimentDao() {
		return new ExperimentDao(this.sessionProvider.getSession());

	}

	public GeolocationDao getGeolocationDao() {
		return new GeolocationDao(this.sessionProvider.getSession());
	}

	public GeolocationPropertyDao getGeolocationPropertyDao() {
		return new GeolocationPropertyDao(this.sessionProvider.getSession());
	}

	public ProgramPresetDAO getProgramPresetDAO() {
		return new ProgramPresetDAO(this.sessionProvider.getSession());
	}

	public NameDAO getNameDao() {
		return new NameDAO(this.sessionProvider.getSession());
	}

	public ProgenitorDAO getProgenitorDao() {
		return new ProgenitorDAO(this.sessionProvider.getSession());
	}

	public DatasetTypeDAO getDatasetTypeDao() {
		return new DatasetTypeDAO(this.sessionProvider.getSession());
	}

	public SearchRequestDAO getSearchRequestDAO() {
		return new SearchRequestDAO(this.sessionProvider.getSession());
	}

	public ObservationUnitsSearchDao getObservationUnitsSearchDAO() {
		return new ObservationUnitsSearchDao(this.sessionProvider.getSession());
	}

	public StudySearchDao getStudySearchDao() {
		return new StudySearchDao(this.sessionProvider.getSession());
	}

	public LocationSearchDao getLocationSearchDao() {
		return new LocationSearchDao(this.sessionProvider.getSession());
	}

	public CountryDAO getCountryDao() {
		return new CountryDAO(this.sessionProvider.getSession());
	}

	public UserDefinedFieldDAO getUserDefinedFieldDAO() {
		return new UserDefinedFieldDAO(this.sessionProvider.getSession());
	}

	public ProgramFavoriteDAO getProgramFavoriteDao() {
		return new ProgramFavoriteDAO(this.sessionProvider.getSession());
	}

	public ExperimentTransactionDAO getExperimentTransactionDao() {
		return new ExperimentTransactionDAO(this.sessionProvider.getSession());
	}

	public ExperimentPropertyDao getExperimentPropertyDao() {
		return new ExperimentPropertyDao(this.sessionProvider.getSession());
	}

	public GermplasmStudySourceDAO getGermplasmStudySourceDAO() {
		return new GermplasmStudySourceDAO(this.sessionProvider.getSession());
	}

	public BibrefDAO getBibrefDAO() {
		return new BibrefDAO(this.sessionProvider.getSession());
	}

	public StudyTypeDAO getStudyTypeDao() {
		return new StudyTypeDAO(this.sessionProvider.getSession());
	}

	public LocdesDAO getLocDesDao() {
		return new LocdesDAO(this.sessionProvider.getSession());
	}

	public NamingConfigurationDAO getNamingConfigurationDAO() {
		return new NamingConfigurationDAO(this.sessionProvider.getSession());
	}

	public UserProgramTreeStateDAO getUserProgramTreeStateDAO() {
		return new UserProgramTreeStateDAO(this.sessionProvider.getSession());
	}

	public CropTypeDAO getCropTypeDAO() {
		return new CropTypeDAO(this.sessionProvider.getSession());
	}

	public VariableOverridesDao getVariableProgramOverridesDao() {
		return new VariableOverridesDao(this.sessionProvider.getSession());
	}

	public StandardVariableDao getStandardVariableDao() {
		return new StandardVariableDao(this.sessionProvider.getSession());
	}

	public CVDao getCvDao() {
		return new CVDao(this.sessionProvider.getSession());
	}

	public TrackMarkerDAO getTrackMarkerDao() {
		return new TrackMarkerDAO(this.sessionProvider.getSession());
	}

	public MapDAO getMapDao() {
		return new MapDAO(this.sessionProvider.getSession());
	}

	public MappingDataDAO getMappingDataDao() {
		return new MappingDataDAO(this.sessionProvider.getSession());
	}

	public MappingPopDAO getMappingPopDao() {
		return new MappingPopDAO(this.sessionProvider.getSession());
	}

	public MappingPopValuesDAO getMappingPopValuesDao() {
		return new MappingPopValuesDAO(this.sessionProvider.getSession());
	}

	public MarkerAliasDAO getMarkerAliasDao() {
		return new MarkerAliasDAO(this.sessionProvider.getSession());
	}

	public MarkerDAO getMarkerDao() {
		return new MarkerDAO(this.sessionProvider.getSession());
	}

	public MarkerDetailsDAO getMarkerDetailsDao() {
		return new MarkerDetailsDAO(this.sessionProvider.getSession());
	}

	public MarkerInfoDAO getMarkerInfoDao() {
		return new MarkerInfoDAO(this.sessionProvider.getSession());
	}

	public ExtendedMarkerInfoDAO getExtendedMarkerInfoDao() {
		return new ExtendedMarkerInfoDAO(this.sessionProvider.getSession());
	}

	public MarkerMetadataSetDAO getMarkerMetadataSetDao() {
		return new MarkerMetadataSetDAO(this.sessionProvider.getSession());
	}

	public MarkerOnMapDAO getMarkerOnMapDao() {
		return new MarkerOnMapDAO(this.sessionProvider.getSession());
	}

	public MarkerUserInfoDAO getMarkerUserInfoDao() {
		return new MarkerUserInfoDAO(this.sessionProvider.getSession());
	}

	public QtlDAO getQtlDao() {
		return new QtlDAO(this.sessionProvider.getSession());
	}

	public QtlDetailsDAO getQtlDetailsDao() {
		return new QtlDetailsDAO(this.sessionProvider.getSession());
	}

	public MtaDAO getMtaDao() {
		return new MtaDAO(this.sessionProvider.getSession());
	}

	public MtaMetadataDAO getMtaMetadataDao() {
		return new MtaMetadataDAO(this.sessionProvider.getSession());
	}

	public TrackDataDAO getTrackDataDao() {
		return new TrackDataDAO(this.sessionProvider.getSession());
	}

	public AccMetadataSetDAO getAccMetadataSetDao() {
		return new AccMetadataSetDAO(this.sessionProvider.getSession());
	}

	public AlleleValuesDAO getAlleleValuesDao() {
		return new AlleleValuesDAO(this.sessionProvider.getSession());
	}

	public CharValuesDAO getCharValuesDao() {
		return new CharValuesDAO(this.sessionProvider.getSession());
	}

	public DartValuesDAO getDartValuesDao() {
		return new DartValuesDAO(this.sessionProvider.getSession());
	}

	public DatasetDAO getDatasetDao() {
		return new DatasetDAO(this.sessionProvider.getSession());
	}

	public DatasetUsersDAO getDatasetUsersDao() {
		return new DatasetUsersDAO(this.sessionProvider.getSession());
	}

	public CvTermSynonymDao getCvTermSynonymDao() {
		return new CvTermSynonymDao(this.sessionProvider.getSession());
	}

	public PhenotypeOutlierDao getPhenotypeOutlierDao() {
		return new PhenotypeOutlierDao(this.sessionProvider.getSession());
	}

	public GermplasmExternalReferenceDAO getGermplasmExternalReferenceDAO() {
		return new GermplasmExternalReferenceDAO(this.sessionProvider.getSession());
	}

	public GermplasmListExternalReferenceDAO getGermplasmListExternalReferenceDAO() {
		return new GermplasmListExternalReferenceDAO(this.sessionProvider.getSession());
	}

	public StudyExternalReferenceDao getStudyExternalReferenceDAO() {
		return new StudyExternalReferenceDao(this.sessionProvider.getSession());
	}

	public CvTermExternalReferenceDAO getCvTermExternalReferenceDAO() {
		return new CvTermExternalReferenceDAO(this.sessionProvider.getSession());
	}

	public PhenotypeExternalReferenceDAO getPhenotypeExternalReferenceDAO() {
		return new PhenotypeExternalReferenceDAO(this.sessionProvider.getSession());
	}

	public StudyInstanceExternalReferenceDao getStudyInstanceExternalReferenceDao() {
		return new StudyInstanceExternalReferenceDao(this.sessionProvider.getSession());
	}

	public SampleExternalReferenceDAO getSampleExternalReferenceDAO() {
		return new SampleExternalReferenceDAO(this.sessionProvider.getSession());
	}

	public ExperimentExternalReferenceDao getExperimentExternalReferenceDao() {
		return new ExperimentExternalReferenceDao(this.sessionProvider.getSession());
	}

	public AttributeExternalReferenceDAO getAttributeExternalReferenceDao() {
		return new AttributeExternalReferenceDAO(this.sessionProvider.getSession());
	}

	public FileMetadataExternalReferenceDAO getFileMetadataExternalReferenceDAO() {
		return new FileMetadataExternalReferenceDAO(this.sessionProvider.getSession());
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

	public ProgramLocationDefaultDAO getProgramLocationDefaultDAO() {
		return new ProgramLocationDefaultDAO(this.sessionProvider.getSession());
	}

	public StudyEntrySearchDAO getStudyEntrySearchDAO() {
		return new StudyEntrySearchDAO(this.sessionProvider.getSession());
	}

	public PhenotypeAuditDao getPhenotypeAuditDao() {
		return new PhenotypeAuditDao(this.sessionProvider.getSession());
	}

	public GenotypeDao getGenotypeDao() {
		return new GenotypeDao(this.sessionProvider.getSession());
	}
}

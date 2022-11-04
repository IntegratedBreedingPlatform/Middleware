
package org.generationcp.middleware.service.impl.study;

import com.google.common.collect.Maps;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.dao.GermplasmStudySourceDAO;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceSearchRequest;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

/**
 * The class <code>StudyServiceImplTest</code> contains tests for the class <code>{@link StudyServiceImpl}</code>.
 *
 * @author Akhil
 */
public class StudyServiceImplTest {

	private static final String FACT1 = "FACT1";

	private static final String STOCK_ID = "STOCK_ID";

	private static final int STUDY_ID = 1234;

	@Mock
	private Session mockSession;

	@Mock
	private SQLQuery mockSqlQuery;

	@Mock
	private HibernateSessionProvider mockSessionProvider;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private StudyMeasurements studyMeasurements;

	@Mock
	private DaoFactory daoFactory;

	@Mock
	private ProjectPropertyDao projectPropertyDao;

	@Mock
	private DmsProjectDao dmsProjectDao;

	@Mock
	private ExperimentDao experimentDao;

	@Mock
	private PhenotypeDao phenotypeDao;

	private StudyServiceImpl studyServiceImpl;

	final Map<Integer, String> additionalGermplasmDescriptors = Collections.singletonMap(TermId.STOCK_ID.getId(), StudyServiceImplTest.STOCK_ID);

	final Map<Integer, String> additionalDesignFactors = Collections.singletonMap(TermId.BLOCK_ID.getId(), StudyServiceImplTest.FACT1);

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.studyServiceImpl = new StudyServiceImpl(this.mockSessionProvider);
		this.studyServiceImpl.setStudyDataManager(this.studyDataManager);
		this.studyServiceImpl.setDaoFactory(this.daoFactory);
		Mockito.when(this.daoFactory.getProjectPropertyDAO()).thenReturn(this.projectPropertyDao);
		Mockito.when(this.daoFactory.getExperimentDao()).thenReturn(this.experimentDao);
		Mockito.when(this.daoFactory.getPhenotypeDAO()).thenReturn(this.phenotypeDao);

		Mockito.when(this.daoFactory.getDmsProjectDAO()).thenReturn(this.dmsProjectDao);
		Mockito.when(this.mockSessionProvider.getSession()).thenReturn(this.mockSession);
		Mockito.when(this.mockSession.createSQLQuery(ArgumentMatchers.anyString())).thenReturn(this.mockSqlQuery);
		Mockito.when(this.mockSqlQuery.addScalar(ArgumentMatchers.anyString())).thenReturn(this.mockSqlQuery);
		final Map<Integer, String> germplasmDescriptorsMap = Maps.newHashMap();
		germplasmDescriptorsMap.put(TermId.GID.getId(), TermId.GID.name());
		germplasmDescriptorsMap.put(TermId.DESIG.getId(),ColumnLabels.DESIGNATION.name());
		germplasmDescriptorsMap.put(TermId.ENTRY_NO.getId(), TermId.ENTRY_NO.name());
		germplasmDescriptorsMap.put(TermId.ENTRY_TYPE.getId(),TermId.ENTRY_TYPE.name());
		germplasmDescriptorsMap.put(TermId.ENTRY_CODE.getId(),TermId.ENTRY_CODE.name());
		germplasmDescriptorsMap.put(TermId.STOCK_ID.getId(), StudyServiceImplTest.STOCK_ID);
		Mockito.when(this.studyServiceImpl.getGenericGermplasmDescriptors(StudyServiceImplTest.STUDY_ID)).thenReturn(germplasmDescriptorsMap);

		final Map<Integer, String> designFactorsMap = Maps.newHashMap();
		designFactorsMap.put(TermId.REP_NO.getId(), TermId.REP_NO.name());
		designFactorsMap.put(TermId.PLOT_NO.getId(), TermId.PLOT_NO.name());
		designFactorsMap.put(TermId.BLOCK_ID.getId(), StudyServiceImplTest.FACT1);
		Mockito.when(this.studyServiceImpl.getAdditionalDesignFactors(StudyServiceImplTest.STUDY_ID)).thenReturn(designFactorsMap);

	}

	@Test
	public void testHasMeasurementDataEnteredAssertTrue() {
		Mockito.when(this.phenotypeDao.hasMeasurementDataEntered(Mockito.anyList(), Mockito.anyInt())).thenReturn(true);

		final List<Integer> ids = Arrays.asList(1000, 1002);
		assertThat(true, is(equalTo(this.studyServiceImpl.hasMeasurementDataEntered(ids, 4))));
	}

	@Test
	public void testHasMeasurementDataEnteredAssertFalse() {
		Mockito.when(this.phenotypeDao.hasMeasurementDataEntered(Mockito.anyList(), Mockito.anyInt())).thenReturn(false);

		final List<Integer> ids = Arrays.asList(1000, 1002);
		assertThat(false, is(equalTo(this.studyServiceImpl.hasMeasurementDataEntered(ids, 4))));
	}

	@Test
	public void testHasCrossesOrSelections() {
		final GermplasmStudySourceDAO sourceDao = Mockito.mock(GermplasmStudySourceDAO.class);
		Mockito.doReturn(sourceDao).when(this.daoFactory).getGermplasmStudySourceDAO();
		final int studyId = new Random().nextInt();
		this.studyServiceImpl.hasCrossesOrSelections(studyId);
		final ArgumentCaptor<GermplasmStudySourceSearchRequest> captor = ArgumentCaptor.forClass(GermplasmStudySourceSearchRequest.class);
		Mockito.verify(sourceDao).countGermplasmStudySourceList(captor.capture());
		Assert.assertEquals(studyId, captor.getValue().getStudyId());
	}

	@Test
	public void testFindGenericGermplasmDescriptors() {
		Assert.assertEquals(this.additionalGermplasmDescriptors, this.studyServiceImpl.getGenericGermplasmDescriptors(StudyServiceImplTest.STUDY_ID));
	}

	@Test
	public void testFindAdditionalDesignFactors() {
		Assert.assertEquals(this.additionalDesignFactors, this.studyServiceImpl.getAdditionalDesignFactors(StudyServiceImplTest.STUDY_ID));
	}

	@Test
	public void testGetPlotDatasetId() {
		final Integer plotDatasetId = new Random().nextInt();
		final Integer studyId = new Random().nextInt();
		Mockito.doReturn(Collections.singletonList(new DmsProject(plotDatasetId))).when(this.dmsProjectDao).getDatasetsByTypeForStudy(
			studyId, DatasetTypeEnum.PLOT_DATA.getId());
		Assert.assertEquals(plotDatasetId, this.studyServiceImpl.getPlotDatasetId(studyId));
	}

	@Test
	public void testEnvironmentDatasetId() {
		final Integer envDatasetId = new Random().nextInt();
		final Integer studyId = new Random().nextInt();
		Mockito.doReturn(Collections.singletonList(new DmsProject(envDatasetId))).when(this.dmsProjectDao).getDatasetsByTypeForStudy(
			studyId, DatasetTypeEnum.SUMMARY_DATA.getId());
		Assert.assertEquals(envDatasetId, this.studyServiceImpl.getEnvironmentDatasetId(studyId));
	}
}

package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.api.germplasmlist.GermplasmListService;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.StockDao;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.hibernate.annotations.common.reflection.ReflectionUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class StudyEntryServiceImplTest {

	private static final Integer STUDY_ID = new Random().nextInt(Integer.MAX_VALUE);
	private static final Integer LIST_ID = new Random().nextInt(Integer.MAX_VALUE);

	@InjectMocks
	private StudyEntryServiceImpl studyEntryService;

	@Mock
	private DaoFactory daoFactory;

	@Mock
	private DmsProjectDao dmsProjectDao;

	@Mock
	private StockDao stockDao;

	@Mock
	private GermplasmListService germplasmListService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		Mockito.when(this.daoFactory.getDmsProjectDAO()).thenReturn(this.dmsProjectDao);
		Mockito.when(this.daoFactory.getStockDao()).thenReturn(this.stockDao);

		ReflectionTestUtils.setField(this.studyEntryService, "daoFactory", this.daoFactory);
	}

	@Test
	public void saveStudyEntries_shouldAddGermplasmListEntryDetailsVariables() {
		final List<ProjectProperty> projectProperties = Arrays.asList(
			this.createMockProjectProperty(VariableType.GERMPLASM_DESCRIPTOR.getId(), TermId.GID.getId()),
			this.createMockProjectProperty(VariableType.ENTRY_DETAIL.getId(), TermId.ENTRY_TYPE.getId()),
			this.createMockProjectProperty(VariableType.ENTRY_DETAIL.getId(), TermId.ENTRY_NO.getId()),
			this.createMockProjectProperty(VariableType.ENTRY_DETAIL.getId(), new Random().nextInt(Integer.MAX_VALUE)));

		final DmsProject plotDataDataset = new DmsProject();
		plotDataDataset.setProperties(projectProperties);

		Mockito.when(this.dmsProjectDao.getDatasetsByTypeForStudy(STUDY_ID, DatasetTypeEnum.PLOT_DATA.getId()))
			.thenReturn(Arrays.asList(plotDataDataset));

		final Integer germplasmListEntryDetailVariableId1 = new Random().nextInt(Integer.MAX_VALUE);
		final Integer germplasmListEntryDetailVariableId2 = new Random().nextInt(Integer.MAX_VALUE);
		final List<Variable> germplasmListVariables = Arrays.asList(
			this.createMockVariable(TermId.ENTRY_TYPE.getId()),
			this.createMockVariable(germplasmListEntryDetailVariableId1),
			this.createMockVariable(germplasmListEntryDetailVariableId2)
		);
		Mockito.when(this.germplasmListService.getGermplasmListVariables(null, LIST_ID, null)).thenReturn(germplasmListVariables);

		this.studyEntryService.saveStudyEntries(STUDY_ID, LIST_ID);

		final ArgumentCaptor<DmsProject> dmsProjectArgumentCaptor = ArgumentCaptor.forClass(DmsProject.class);
		Mockito.verify(this.dmsProjectDao).save(dmsProjectArgumentCaptor.capture());

		final DmsProject actualPlotDataSet = dmsProjectArgumentCaptor.getValue();
		assertNotNull(actualPlotDataSet);
		assertThat(actualPlotDataSet.getProperties(), hasSize(5));
		this.assertProjectProperty(actualPlotDataSet.getProperties().get(0), TermId.GID.getId(), VariableType.GERMPLASM_DESCRIPTOR.getId());
		this.assertProjectProperty(actualPlotDataSet.getProperties().get(1), TermId.ENTRY_TYPE.getId(), VariableType.ENTRY_DETAIL.getId());
		this.assertProjectProperty(actualPlotDataSet.getProperties().get(2), TermId.ENTRY_NO.getId(), VariableType.ENTRY_DETAIL.getId());
		this.assertProjectProperty(actualPlotDataSet.getProperties().get(3), germplasmListEntryDetailVariableId1, VariableType.ENTRY_DETAIL.getId());
		this.assertProjectProperty(actualPlotDataSet.getProperties().get(4), germplasmListEntryDetailVariableId2, VariableType.ENTRY_DETAIL.getId());

		Mockito.verify(this.stockDao).createStudyEntries(STUDY_ID, LIST_ID);
	}

	private ProjectProperty createMockProjectProperty(final Integer typeId, final Integer variableId) {
		final ProjectProperty projectProperty = Mockito.mock(ProjectProperty.class);
		Mockito.when(projectProperty.getTypeId()).thenReturn(typeId);
		Mockito.when(projectProperty.getVariableId()).thenReturn(variableId);
		Mockito.when(projectProperty.getRank()).thenReturn(new Random().nextInt(Integer.MAX_VALUE));
		return projectProperty;
	}

	private Variable createMockVariable(final Integer variableId) {
		final Variable variable = Mockito.mock(Variable.class);
		Mockito.when(variable.getId()).thenReturn(variableId);
		return variable;
	}

	private void assertProjectProperty(final ProjectProperty projectProperty, final Integer variableId, final Integer typeId) {
		assertNotNull(projectProperty);
		assertThat(projectProperty.getVariableId(), is(variableId));
		assertThat(projectProperty.getTypeId(), is(typeId));
	}

}

package org.generationcp.middleware.api.study;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.pojos.dms.StudyType;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.dataset.StockPropertyData;
import org.generationcp.middleware.service.api.study.StudyEntryDto;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class StudyEntryObservationServiceImplIntegrationTest extends IntegrationTestBase {

	@Resource
	private StudyEntryObservationService studyEntryObservationService;

	private DaoFactory daoFactory;
	private GermplasmTestDataGenerator germplasmTestDataGenerator;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.daoFactory);
	}

	@Test
	public void shouldCreateAndUpdateAndDeleteObservation_OK() {
		final DmsProject study = new DmsProject(
			"TEST STUDY " + RandomStringUtils.randomAlphanumeric(10), "TEST DESCRIPTION", null, Collections.emptyList(),
			false,
			false, new StudyType(6), "20200606", null, null,
			null, "1");
		this.daoFactory.getDmsProjectDAO().save(study);
		final DmsProject plotDataset = new DmsProject(
			"TEST DATASET", "TEST DATASET DESC", null, Collections.emptyList(),
			false,
			false, new StudyType(6), "20200606", null, null,
			null, "1");
		plotDataset.setDatasetType(new DatasetType(DatasetTypeEnum.PLOT_DATA.getId()));
		plotDataset.setStudy(study);
		this.daoFactory.getDmsProjectDAO().save(plotDataset);

		final ProjectProperty entryTypeProp =
			new ProjectProperty(plotDataset, VariableType.ENTRY_DETAIL.getId(), "", 1, TermId.ENTRY_TYPE.getId(),
				"ENTRY_TYPE");
		this.daoFactory.getProjectPropertyDAO().save(entryTypeProp);

		final Germplasm germplasm = this.germplasmTestDataGenerator.createGermplasm("BLE");
		final StockModel stockModel =
			new StockModel(plotDataset.getProjectId(), new StudyEntryDto(1, 1, germplasm.getGid(), germplasm.getGermplasmPreferredName(), null,null,null,"-",null,null));
		this.daoFactory.getStockDao().save(stockModel);

		final CVTerm variable =
			new CVTerm(null, CvId.VARIABLES.getId(), RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), null,
				0, 0, false);
		this.daoFactory.getCvTermDao().save(variable);

		// Create observation
		final String newValue = "newValue";
		final StockPropertyData newStockPropertyData = new StockPropertyData(stockModel.getStockId(), variable.getCvTermId(), newValue, null);
		this.studyEntryObservationService.createObservation(newStockPropertyData);

		// Assert recently created observation
		final StockProperty newObservation =
			this.daoFactory.getStockPropertyDao().getByStockIdAndTypeId(stockModel.getStockId(), variable.getCvTermId()).get();
		this.assertObservation(newObservation, stockModel.getStockId(), variable.getCvTermId(), newValue);

		// Update observation
		final String updatedValue = "updatedValue";
		final StockPropertyData updatedStockPropertyData = new StockPropertyData(stockModel.getStockId(), variable.getCvTermId(), updatedValue, null);
		this.studyEntryObservationService.updateObservation(updatedStockPropertyData, false);

		// Assert recently updated observation
		final StockProperty updatedObservation =
			this.daoFactory.getStockPropertyDao().getByStockIdAndTypeId(stockModel.getStockId(), variable.getCvTermId()).get();
		this.assertObservation(updatedObservation, stockModel.getStockId(), variable.getCvTermId(), updatedValue);

		// Delete observation
		this.studyEntryObservationService.deleteObservation(newObservation.getStockPropId());
		assertFalse(this.daoFactory.getStockPropertyDao().getByStockIdAndTypeId(stockModel.getStockId(), variable.getCvTermId()).isPresent());

		// recreate observation using update observation where allowCreate is true
		this.studyEntryObservationService.updateObservation(updatedStockPropertyData, true);
		this.assertObservation(updatedObservation, stockModel.getStockId(), variable.getCvTermId(), updatedValue);

	}

	private void assertObservation(final StockProperty observation, final Integer stockId, final Integer typeId, final String value) {
		assertNotNull(observation);
		assertThat(observation.getStock().getStockId(), is(stockId));
		assertThat(observation.getTypeId(), is(typeId));
		assertThat(observation.getValue(), is(value));
	}

}

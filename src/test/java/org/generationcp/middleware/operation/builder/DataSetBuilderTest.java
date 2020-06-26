
package org.generationcp.middleware.operation.builder;

import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.data.initializer.StandardVariableTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DMSVariableTypeTestDataInitializer;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.yecht.IoStrRead;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataSetBuilderTest {

	private static final int STUDY_ID_WITH_STUDY = 1;
	private static final int STUDY_ID_NO_STUDY = 2;
	private static final int STUDY_ID_NODATASETS = 3;
	private static final int RANK = 0;
	private static final String DATASET_NAME = "DATASET_NAME";
	private static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	private static final String LOCATION_NAME = "LOCATION_NAME";
	private static final String NFERT_NO = "NFert_NO";
	private static final int TRIAL_DATASET_ID = 1;

	private final List<DMSVariableType> factorVariableTypes = new ArrayList<>();
	private final List<DMSVariableType> variateVariableTypes = new ArrayList<>();

	private static final String EMPTY_VALUE = "";
	private static final String[] TRAITS = {
		DataSetBuilderTest.TRAIT_ASI, DataSetBuilderTest.TRAIT_APHID,
		DataSetBuilderTest.TRAIT_EPH, DataSetBuilderTest.TRAIT_FMSROT};
	private static final String ACDTOL_E_1TO5 = "AcdTol_E_1to5";
	private static final String TRAIT_ASI = "ASI";
	private static final String TRAIT_APHID = "Aphid1_5";
	private static final String TRAIT_EPH = "EPH";
	private static final String TRAIT_FMSROT = "FMSROT";

	@Mock
	private HibernateSessionProvider hibernateSessionProvider;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private WorkbookBuilder workbookBuilder;

	@Mock
	private DmsProjectDao dmsProjectDao;

	@Mock
	private DaoFactory daoFactory;

	@InjectMocks
	private final DataSetBuilder dataSetBuilder = new DataSetBuilder();

	public static final List<Integer> DEFAULT_DATASET_COLUMNS = Arrays.asList(
		TermId.TRIAL_INSTANCE_FACTOR.getId(),
		TermId.ENTRY_TYPE.getId(),
		TermId.ENTRY_NO.getId(),
		TermId.GID.getId(),
		TermId.DESIG.getId(),
		TermId.REP_NO.getId(),
		TermId.PLOT_NO.getId(),
		TermId.OBS_UNIT_ID.getId());


	@Before
	public void setUp() {

		when((this.studyDataManager).findOneDataSetReferenceByType(STUDY_ID_NO_STUDY, DatasetTypeEnum.SUMMARY_DATA.getId()))
			.thenReturn(this.generateDatasetReference(3));
		when(this.daoFactory.getDmsProjectDAO()).thenReturn(this.dmsProjectDao);
		when(this.dmsProjectDao.getById(3)).thenReturn(this.generateDmsProject(3));

		this.factorVariableTypes.add(this.createGermplasmFactorVariableType("TRIAL_INSTANCE", TermId.TRIAL_INSTANCE_FACTOR.getId()));
		this.factorVariableTypes.add(this.createGermplasmFactorVariableType("ENTRY_TYPE", TermId.ENTRY_TYPE.getId()));
		this.factorVariableTypes.add(this.createGermplasmFactorVariableType("ENTRY_NO", TermId.ENTRY_NO.getId()));
		this.factorVariableTypes.add(this.createGermplasmFactorVariableType("GID", TermId.GID.getId()));
		this.factorVariableTypes.add(this.createGermplasmFactorVariableType("DESIG", TermId.DESIG.getId()));
		this.factorVariableTypes.add(this.createGermplasmFactorVariableType("REP_NO", TermId.REP_NO.getId()));
		this.factorVariableTypes.add(this.createGermplasmFactorVariableType("PLOT_NO", TermId.PLOT_NO.getId()));
		this.factorVariableTypes.add(this.createGermplasmFactorVariableType("OBS_UNIT_ID", TermId.OBS_UNIT_ID.getId()));

		int ctr = 1;
		for (final String traitName : DataSetBuilderTest.TRAITS) {
			this.variateVariableTypes.add(this.createVariateVariableType(traitName,ctr));
			ctr++;
		}

	}

	@Test
	public void testGetStudyDataSetByUsingDatasetType() {
		final DmsProject dataset = this.dataSetBuilder.getTrialDataset(DataSetBuilderTest.STUDY_ID_NO_STUDY);
		Assert.assertEquals("The Study Dataset's project id should be 3", "3", dataset.getProjectId().toString());
	}

	@Test
	public void filterVariables_partOfHiddenDatasetColumns_RemoveVariable() {
		final VariableTypeList variables = new VariableTypeList();
		variables.add(new DMSVariableType(DataSetBuilderTest.DATASET_NAME, DataSetBuilderTest.DATASET_NAME,
			StandardVariableTestDataInitializer.createStandardVariable(TermId.DATASET_NAME.getId(), DataSetBuilderTest.DATASET_NAME),
			DataSetBuilderTest.RANK));
		final VariableTypeList newVariables = this.dataSetBuilder.filterDatasetVariables(variables, true);
		Assert.assertTrue("Dataset column removed.", newVariables.getVariableTypes().size() == 0);
	}

	@Test
	public void filterVariables_isMeasurementDatasetAndIsTrialFactors_RemoveVariable() {
		final VariableTypeList variables = new VariableTypeList();

		variables
			.add(new DMSVariableType(DataSetBuilderTest.LOCATION_NAME,
				DataSetBuilderTest.LOCATION_NAME,
				StandardVariableTestDataInitializer.createStandardVariableTestData(
					DataSetBuilderTest.LOCATION_NAME, PhenotypicType.TRIAL_ENVIRONMENT), DataSetBuilderTest.RANK));
		final VariableTypeList newVariables = this.dataSetBuilder.filterDatasetVariables(variables, true);

		Assert.assertTrue("Trial factor removed.", newVariables.getVariableTypes().size() == 0);
	}

	@Test
	public void filterVariables_isTrialAndOcc_AddVariable() {
		final VariableTypeList variables = new VariableTypeList();

		variables.add(new DMSVariableType(DataSetBuilderTest.TRIAL_INSTANCE, DataSetBuilderTest.TRIAL_INSTANCE,
			StandardVariableTestDataInitializer
				.createStandardVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), DataSetBuilderTest.TRIAL_INSTANCE), DataSetBuilderTest.RANK));
		final VariableTypeList newVariables = this.dataSetBuilder.filterDatasetVariables(variables, false);

		Assert.assertTrue("Trial instance added.", newVariables.size() == variables.size());
		Assert.assertEquals(DataSetBuilderTest.TRIAL_INSTANCE, newVariables.getVariableTypes().get(0).getLocalName());
	}

	@Test
	public void filterVariables_isTreatmentFactorDuplicate_RemoveVariable() {
		// Create treatment factor variable
		final VariableTypeList variables = new VariableTypeList();
		variables.add(DMSVariableTypeTestDataInitializer.createDmsVariableType(DataSetBuilderTest.NFERT_NO, DataSetBuilderTest.NFERT_NO));

		// Create additional prop added to projectprop when adding treatment factors
		final DMSVariableType variableType = DMSVariableTypeTestDataInitializer
			.createDmsVariableType(DataSetBuilderTest.NFERT_NO, DataSetBuilderTest.NFERT_NO, DataSetBuilderTest.RANK);
		variableType.setVariableType(VariableType.TREATMENT_FACTOR);
		variableType.setStandardVariable(StandardVariableTestDataInitializer.createStandardVariable(8241, variableType.getLocalName()));
		variables.add(variableType);

		final VariableTypeList newVariables = this.dataSetBuilder.filterDatasetVariables(variables, true);

		// Assert that duplicate was removed
		Assert.assertTrue("Duplicate treatment factor variable was removed.",
			newVariables.getVariableTypes().size() < variables.getVariableTypes().size());
		Assert.assertEquals(DataSetBuilderTest.NFERT_NO, variables.getVariableTypes().get(0).getLocalName());
		Assert.assertNotNull(newVariables.getVariableTypes().get(0).getVariableType());
	}

	@Test(expected = MiddlewareQueryException.class)
	public void testGetStudyDataSetExceptionCase() {
		final DmsProject dataset = this.dataSetBuilder.getTrialDataset(DataSetBuilderTest.STUDY_ID_NODATASETS);
	}

	private List<DatasetReference> generateDatasetReferences(final boolean includeStudyDataset) {

		final List<DatasetReference> dsRefs = new ArrayList<DatasetReference>();

		final DatasetReference ref1 = new DatasetReference(1, "TestStudy");
		final DatasetReference ref3 = new DatasetReference(3, "TestStudy-DUMMY");
		final DatasetReference ref4 = new DatasetReference(2, "TestStudy-PLOTDATA");

		dsRefs.add(ref1);
		dsRefs.add(ref3);
		dsRefs.add(ref4);

		// this allows us to run tests with and without a Study dataset present
		if (includeStudyDataset) {
			final DatasetReference ref2 = new DatasetReference(4, "TestStudy-ENVIRONMENT");
			dsRefs.add(ref2);
		}

		return dsRefs;
	}

	private DmsProject generateDmsProject(final int id) {
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(id);
		return dmsProject;
	}

	private DatasetReference generateDatasetReference(final int id) {
		final DatasetReference dsr = new DatasetReference(id, "Name");
		return dsr;
	}

	@Test
	public void testFilterVariables() {
		final List<Integer> siblingsList = Arrays.asList(TermId.DATASET_NAME.getId(), TermId.DATASET_TITLE.getId(), TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
			TermId.TRIAL_INSTANCE_FACTOR.getId(), TermId.LOCATION_ID.getId(), TermId.GID.getId(), TermId.DESIG.getId(),
			TermId.ENTRY_NO.getId(), TermId.OBS_UNIT_ID.getId(), TermId.REP_NO.getId(), TermId.PLOT_NO.getId(), 1, 2, 3, 4);

		final DataSet dataSet = this.createDataSet();
		final int totalVariables = DataSetBuilderTest.DEFAULT_DATASET_COLUMNS.size() + 4;//Traits

		Assert.assertEquals("Default Column with Traits", totalVariables, this.dataSetBuilder.filterVariables(dataSet.getVariableTypes(),siblingsList).size());
	}
	private DataSet createDataSet() {

		final DataSet dataSet = new DataSet();
		dataSet.setId(DataSetBuilderTest.TRIAL_DATASET_ID);

		final VariableTypeList variableTypes = new VariableTypeList();

		dataSet.setVariableTypes(variableTypes);
		for (final DMSVariableType factor : this.factorVariableTypes) {
			factor.setVariableType(VariableType.GERMPLASM_DESCRIPTOR);
			dataSet.getVariableTypes().add(factor);
		}
		for (final DMSVariableType variate : this.variateVariableTypes) {
			variate.setVariableType(VariableType.TRAIT);
			dataSet.getVariableTypes().add(variate);
		}

		return dataSet;
	}

	private DMSVariableType createEnvironmentVariableType(final String localName) {

		final DMSVariableType factor = new DMSVariableType();
		final StandardVariable factorStandardVar = new StandardVariable();
		factorStandardVar.setId(TermId.LOCATION_ID.getId());
		factorStandardVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		factorStandardVar.setName(localName);
		factor.setLocalName(localName);
		factor.setStandardVariable(factorStandardVar);
		factor.setRole(factorStandardVar.getPhenotypicType());
		return factor;
	}

	private DMSVariableType createGermplasmFactorVariableType(final String localName, final int termId) {
		final DMSVariableType factor = new DMSVariableType();
		final StandardVariable factorStandardVar = new StandardVariable();
		factorStandardVar.setPhenotypicType(PhenotypicType.GERMPLASM);

		final Term dataType = new Term();
		dataType.setId(TermId.NUMERIC_DBID_VARIABLE.getId());

		final Term method = new Term();
		method.setId(1111);
		method.setDefinition(DataSetBuilderTest.EMPTY_VALUE);

		final Term scale = new Term();
		scale.setDefinition(DataSetBuilderTest.EMPTY_VALUE);
		scale.setId(22222);

		final Term property = new Term();
		scale.setDefinition(DataSetBuilderTest.EMPTY_VALUE);
		scale.setId(33333);

		factorStandardVar.setId(termId);
		factorStandardVar.setProperty(property);
		factorStandardVar.setScale(scale);
		factorStandardVar.setMethod(method);
		factorStandardVar.setDataType(dataType);
		factor.setLocalName(localName);
		factor.setStandardVariable(factorStandardVar);
		factor.setRole(factorStandardVar.getPhenotypicType());

		return factor;
	}

	private DMSVariableType createVariateVariableType(final String localName, final int termId) {
		final DMSVariableType variate = new DMSVariableType();
		final StandardVariable variateStandardVar = new StandardVariable();
		variateStandardVar.setPhenotypicType(PhenotypicType.VARIATE);

		final Term storedIn = new Term();
		storedIn.setId(TermId.OBSERVATION_VARIATE.getId());

		final Term dataType = new Term();
		dataType.setId(TermId.NUMERIC_VARIABLE.getId());

		final Term method = new Term();
		method.setId(1111);
		method.setDefinition(DataSetBuilderTest.EMPTY_VALUE);

		final Term scale = new Term();
		scale.setDefinition(DataSetBuilderTest.EMPTY_VALUE);
		scale.setId(22222);

		final Term property = new Term();
		scale.setDefinition(DataSetBuilderTest.EMPTY_VALUE);
		scale.setId(33333);

		variateStandardVar.setId(termId);
		variateStandardVar.setProperty(property);
		variateStandardVar.setScale(scale);
		variateStandardVar.setMethod(method);
		variateStandardVar.setDataType(dataType);
		variateStandardVar.setName(localName);
		variate.setLocalName(localName);
		variate.setStandardVariable(variateStandardVar);
		variate.setRole(variateStandardVar.getPhenotypicType());

		return variate;
	}

	private DMSVariableType createVariateVariableType(
		final String localName, final String propertyName, final String scaleName,
		final String methodName) {
		final DMSVariableType variate = new DMSVariableType();
		final StandardVariable variateStandardVar = new StandardVariable();
		variateStandardVar.setPhenotypicType(PhenotypicType.VARIATE);

		final Term storedIn = new Term();
		storedIn.setId(TermId.OBSERVATION_VARIATE.getId());

		final Term dataType = new Term();
		dataType.setId(TermId.NUMERIC_VARIABLE.getId());

		final Term method = new Term();
		method.setId(1111);
		method.setDefinition(DataSetBuilderTest.EMPTY_VALUE);
		method.setName(methodName);

		final Term scale = new Term();
		scale.setDefinition(DataSetBuilderTest.EMPTY_VALUE);
		scale.setId(22222);
		scale.setName(scaleName);

		final Term property = new Term();
		property.setDefinition(DataSetBuilderTest.EMPTY_VALUE);
		property.setId(33333);
		property.setName(propertyName);

		variateStandardVar.setId(1234);
		variateStandardVar.setProperty(property);
		variateStandardVar.setScale(scale);
		variateStandardVar.setMethod(method);
		variateStandardVar.setDataType(dataType);
		variate.setLocalName(localName);
		variate.setStandardVariable(variateStandardVar);
		variate.setRole(variateStandardVar.getPhenotypicType());

		return variate;
	}
}

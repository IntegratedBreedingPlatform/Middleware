
package org.generationcp.middleware.data.initializer;

import java.util.Random;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.workbench.Project;

/*
 * Contains Study related test data initializers
 */
public class StudyTestDataInitializer {

	public static final String STUDY_NAME = "STUDY NAME";
	public static final String STUDY_DESCRIPTION = "STUDY DESCRIPTION";
	public static final Integer STUDY_ID = 10010;
	public static final int PARENT_FOLDER_ID = 1;
	private static final String TEST_FOLDER_NAME = "TEST_FOLDER_NAME";
	private static final String TEST_FOLDER_DESC = "TEST_FOLDER_DESC";
	public static final String DATASET_NAME = "DATA SET NAME";
	public static int DATASET_ID = 255;

	private final StudyDataManagerImpl studyDataManager;
	private final OntologyDataManager ontologyManager;
	private final Project commonTestProject;
	private final GermplasmDataManager germplasmDataDM;
	private Integer gid;

	public StudyTestDataInitializer(final StudyDataManagerImpl studyDataManagerImpl, final OntologyDataManager ontologyDataManager,
			final Project testProject, final GermplasmDataManager germplasmDataDM) {
		this.studyDataManager = studyDataManagerImpl;
		this.ontologyManager = ontologyDataManager;
		this.commonTestProject = testProject;
		this.germplasmDataDM = germplasmDataDM;
	}

	public StudyReference addTestStudy() throws Exception {
		return this.addTestStudy(this.commonTestProject.getUniqueID());
	}

	public StudyReference addTestStudy(final String uniqueId) throws Exception {
		final VariableTypeList typeList = new VariableTypeList();
		final VariableList variableList = new VariableList();

		Variable variable = this.createVariable(TermId.STUDY_NAME.getId(), StudyTestDataInitializer.STUDY_NAME, 1);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		variable = this.createVariable(TermId.STUDY_TITLE.getId(), StudyTestDataInitializer.STUDY_DESCRIPTION, 2);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		variable = this.createVariable(TermId.STUDY_TYPE.getId(), String.valueOf(StudyType.T.getId()), 3, PhenotypicType.STUDY);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		variable = this.createVariable(TermId.STUDY_STATUS.getId(), String.valueOf(TermId.ACTIVE_STUDY.getId()), 4);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		final StudyValues studyValues = new StudyValues();
		studyValues.setVariableList(variableList);

		final VariableList locationVariableList = this.createTrialEnvironment("Description", "1.0", "2.0", "data", "3.0", "prop1", "prop2");
		studyValues.setLocationId(this.studyDataManager.addTrialEnvironment(locationVariableList));

		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		this.gid = this.germplasmDataDM.addGermplasm(germplasm, germplasm.getPreferredName());
		final VariableList germplasmVariableList =
				this.createGermplasm("unique name", String.valueOf(this.gid), "name", "2000", "prop1", "prop2");
		studyValues.setGermplasmId(this.studyDataManager.addStock(germplasmVariableList));

		return this.studyDataManager.addStudy(StudyTestDataInitializer.PARENT_FOLDER_ID, typeList, studyValues, uniqueId);
	}

	private Variable createVariable(final int termId, final String value, final int rank) throws Exception {
		return this.createVariable(termId, value, rank, PhenotypicType.VARIATE);
	}

	private Variable createVariable(final int termId, final String value, final int rank, final PhenotypicType type) throws Exception {
		final StandardVariable stVar = this.ontologyManager.getStandardVariable(termId, this.commonTestProject.getUniqueID());

		final DMSVariableType vtype = new DMSVariableType();
		vtype.setStandardVariable(stVar);
		vtype.setRank(rank);
		final Variable var = new Variable();
		var.setValue(value);
		var.setVariableType(vtype);
		vtype.setLocalName(value);
		vtype.setRole(type);
		return var;
	}

	private VariableList createTrialEnvironment(final String name, final String latitude, final String longitude, final String data,
			final String altitude, final String property1, final String property2) throws Exception {
		final VariableList variableList = new VariableList();
		variableList.add(this.createVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), name, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.LATITUDE.getId(), latitude, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.LONGITUDE.getId(), longitude, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.GEODETIC_DATUM.getId(), data, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.ALTITUDE.getId(), altitude, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), property1, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.TRIAL_LOCATION.getId(), property2, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(TermId.SITE_NAME.getId(), "999", 0, PhenotypicType.TRIAL_ENVIRONMENT));
		return variableList;
	}

	private VariableList createGermplasm(final String name, final String gid, final String designation, final String code,
			final String property1, final String property2) throws Exception {
		final VariableList variableList = new VariableList();
		variableList.add(this.createVariable(TermId.ENTRY_NO.getId(), name, 1, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(TermId.GID.getId(), gid, 2, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(TermId.DESIG.getId(), designation, 3, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(TermId.ENTRY_CODE.getId(), code, 4, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(TermId.CHECK.getId(), property1, 5, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(TermId.CROSS.getId(), property2, 6, PhenotypicType.GERMPLASM));
		return variableList;
	}

	public DmsProject createFolderTestData(final String uniqueId) throws MiddlewareQueryException {
		final int randomInt = new Random().nextInt(10000);
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setName(StudyTestDataInitializer.TEST_FOLDER_NAME + randomInt);
		dmsProject.setDescription(StudyTestDataInitializer.TEST_FOLDER_DESC + randomInt);
		dmsProject.setProgramUUID(uniqueId);
		final int folderId = this.studyDataManager.addSubFolder(DmsProject.SYSTEM_FOLDER_ID, dmsProject.getName(),
				dmsProject.getDescription(), dmsProject.getProgramUUID());
		dmsProject.setProjectId(folderId);
		return dmsProject;
	}

	public DatasetReference addTestDataset(final int studyId) throws Exception {
		final VariableTypeList typeList = new VariableTypeList();

		final DatasetValues datasetValues = new DatasetValues();
		datasetValues.setName(StudyTestDataInitializer.DATASET_NAME);
		datasetValues.setDescription("My Dataset Description");
		datasetValues.setType(DataSetType.MEANS_DATA);

		DMSVariableType variableType = this.createVariableType(18000, "Grain Yield", "Grain Yield", 4);
		variableType.setLocalName("Grain Yield");
		typeList.add(variableType);

		variableType = this.createVariableType(18050, "Disease Pressure", "Disease Pressure", 5);
		variableType.setLocalName("Disease Pressure");
		typeList.add(variableType);

		variableType = this.createVariableType(TermId.PLOT_NO.getId(), "Plot No", "Plot No", 6);
		variableType.setLocalName("Plot No");
		typeList.add(variableType);

		return this.studyDataManager.addDataSet(studyId, typeList, datasetValues, null);
	}

	private DMSVariableType createVariableType(final int termId, final String name, final String description, final int rank)
			throws Exception {
		final StandardVariable stdVar = this.ontologyManager.getStandardVariable(termId, this.commonTestProject.getUniqueID());
		final DMSVariableType vtype = new DMSVariableType();
		vtype.setLocalName(name);
		vtype.setLocalDescription(description);
		vtype.setRank(rank);
		vtype.setStandardVariable(stdVar);
		vtype.setRole(PhenotypicType.TRIAL_ENVIRONMENT);

		return vtype;
	}

	public Integer getGid() {
		return this.gid;
	}
}

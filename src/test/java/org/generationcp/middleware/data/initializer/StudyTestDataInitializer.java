package org.generationcp.middleware.data.initializer;

import java.util.Random;

import org.generationcp.middleware.domain.dms.DMSVariableType;
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
	
	private StudyDataManagerImpl studyDataManager;
	private OntologyDataManager ontologyManager;
	private Project commonTestProject;
	private GermplasmDataManager germplasmDataDM;
	private Integer gid;
	
	public StudyTestDataInitializer(final StudyDataManagerImpl studyDataManagerImpl, final OntologyDataManager ontologyDataManager, final Project testProject, final GermplasmDataManager germplasmDataDM) {
		this.studyDataManager = studyDataManagerImpl;
		this.ontologyManager = ontologyDataManager;
		this.commonTestProject = testProject;
		this.germplasmDataDM = germplasmDataDM;
	}
	
	public StudyReference addTestStudy() throws Exception {
		return this.addTestStudy(this.commonTestProject.getUniqueID());
	}
	
	public StudyReference addTestStudy(String uniqueId) throws Exception {
		VariableTypeList typeList = new VariableTypeList();
		VariableList variableList = new VariableList();

		Variable variable = this.createVariable(TermId.STUDY_NAME.getId(), STUDY_NAME, 1);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		variable = this.createVariable(TermId.STUDY_TITLE.getId(), STUDY_DESCRIPTION, 2);
		typeList.add(variable.getVariableType());
		variableList.add(variable);
		
		variable = this.createVariable(TermId.STUDY_TYPE.getId(), String.valueOf(StudyType.T.getId()), 3, PhenotypicType.STUDY);
		typeList.add(variable.getVariableType());
		variableList.add(variable);
		
		variable = this.createVariable(TermId.STUDY_STATUS.getId(), String.valueOf(TermId.ACTIVE_STUDY.getId()), 4);
		typeList.add(variable.getVariableType());
		variableList.add(variable);
		
		StudyValues studyValues = new StudyValues();
		studyValues.setVariableList(variableList);

		VariableList locationVariableList = this.createTrialEnvironment("Description", "1.0", "2.0", "data", "3.0", "prop1", "prop2");
		studyValues.setLocationId(this.studyDataManager.addTrialEnvironment(locationVariableList));
		
		Germplasm germplasm  = GermplasmTestDataInitializer.createGermplasm(1);
		gid = this.germplasmDataDM.addGermplasm(germplasm, germplasm.getPreferredName());
		VariableList germplasmVariableList = this.createGermplasm("unique name", String.valueOf(gid), "name", "2000", "prop1", "prop2");
		studyValues.setGermplasmId(this.studyDataManager.addStock(germplasmVariableList));

		return this.studyDataManager.addStudy(PARENT_FOLDER_ID, typeList, studyValues, uniqueId);
	}
		
	private Variable createVariable(int termId, String value, int rank) throws Exception {
		return this.createVariable(termId, value, rank, PhenotypicType.VARIATE);
	}
	
	private Variable createVariable(int termId, String value, int rank, PhenotypicType type) throws Exception {
		StandardVariable stVar = this.ontologyManager.getStandardVariable(termId, commonTestProject.getUniqueID());

		DMSVariableType vtype = new DMSVariableType();
		vtype.setStandardVariable(stVar);
		vtype.setRank(rank);
		Variable var = new Variable();
		var.setValue(value);
		var.setVariableType(vtype);
		vtype.setLocalName(value);
		vtype.setRole(type);
		return var;
	}
	
	private VariableList createTrialEnvironment(String name, String latitude, String longitude, String data, String altitude,
			String property1, String property2) throws Exception {
		VariableList variableList = new VariableList();
		variableList.add(this.createVariable(8170, name, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(8191, latitude, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(8192, longitude, 0,PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(8193, data, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(8194, altitude, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(8135, property1, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(8180, property2, 0, PhenotypicType.TRIAL_ENVIRONMENT));
		variableList.add(this.createVariable(8195, "999", 0, PhenotypicType.TRIAL_ENVIRONMENT));
		return variableList;
	}
	
	private VariableList createGermplasm(String name, String gid, String designation, String code, String property1, String property2)
			throws Exception {
		VariableList variableList = new VariableList();
		variableList.add(this.createVariable(8230, name, 1, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(8240, gid, 2, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(8250, designation, 3, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(8300, code, 4, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(8255, property1, 5, PhenotypicType.GERMPLASM));
		variableList.add(this.createVariable(8377, property2, 6, PhenotypicType.GERMPLASM));
		return variableList;
	}
	
	public DmsProject createFolderTestData(String uniqueId) throws MiddlewareQueryException {
		int randomInt = new Random().nextInt(10000);
		DmsProject dmsProject = new DmsProject();
		dmsProject.setName(TEST_FOLDER_NAME + randomInt);
		dmsProject.setDescription(TEST_FOLDER_DESC + randomInt);
		dmsProject.setProgramUUID(uniqueId);
		int folderId =
				this.studyDataManager.addSubFolder(DmsProject.SYSTEM_FOLDER_ID, dmsProject.getName(), dmsProject.getDescription(),
						dmsProject.getProgramUUID());
		dmsProject.setProjectId(folderId);
		return dmsProject;
	}
	
	public Integer getGid() {
		return this.gid;
	}
}

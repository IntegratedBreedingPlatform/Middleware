
package org.generationcp.middleware;

import java.util.List;
import java.util.Random;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;

public class StudyTestDataUtil {

	private StudyDataManager studyDataManager;

	private OntologyDataManager ontologyManager;

	private static final String TEST_FOLDER_NAME = "TEST_FOLDER_NAME";
	private static final String TEST_FOLDER_DESC = "TEST_FOLDER_DESC";

	public StudyTestDataUtil(StudyDataManager studyDataManager, OntologyDataManager ontologyManager) {
		this.studyDataManager = studyDataManager;
		this.ontologyManager = ontologyManager;
	}

	public DmsProject createFolderTestData(String uniqueId) throws MiddlewareQueryException {
		int randomInt = new Random().nextInt(10000);
		DmsProject dmsProject = new DmsProject();
		dmsProject.setName(StudyTestDataUtil.TEST_FOLDER_NAME + randomInt);
		dmsProject.setDescription(StudyTestDataUtil.TEST_FOLDER_DESC + randomInt);
		dmsProject.setProgramUUID(uniqueId);
		int folderId =
				this.studyDataManager.addSubFolder(DmsProject.SYSTEM_FOLDER_ID, dmsProject.getName(), dmsProject.getDescription(),
						dmsProject.getProgramUUID());
		dmsProject.setProjectId(folderId);
		return dmsProject;
	}

	public DmsProject createStudyTestData(String uniqueId) {
		String name = "Study Name " + new Random().nextInt(10000);
		String description = "Study Description";

		VariableTypeList typeList = new VariableTypeList();
		VariableList variableList = new VariableList();

		Variable variable = this.createVariable(TermId.STUDY_NAME.getId(), name, 1, uniqueId);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		variable = this.createVariable(TermId.STUDY_TITLE.getId(), description, 2, uniqueId);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		variable = this.createVariable(TermId.STUDY_TYPE.getId(), String.valueOf(StudyType.T.getId()), 3, uniqueId);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		StudyValues studyValues = new StudyValues();
		studyValues.setVariableList(variableList);

		DmsProject dmsProject = new DmsProject();
		dmsProject.setName(name);
		dmsProject.setDescription(description);
		dmsProject.setProgramUUID(uniqueId);

		StudyReference studyReference = this.studyDataManager.addStudy(DmsProject.SYSTEM_FOLDER_ID, typeList, studyValues, uniqueId);
		dmsProject.setProjectId(studyReference.getId());
		return dmsProject;
	}

	public DmsProject createStudyTestDataWithActiveStatus(String uniqueId) {
		String name = "Study Name " + new Random().nextInt(10000);
		String description = "Study Description";

		VariableTypeList typeList = new VariableTypeList();
		VariableList variableList = new VariableList();

		Variable variable = this.createVariable(TermId.STUDY_NAME.getId(), name, 1, uniqueId);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		variable = this.createVariable(TermId.STUDY_TITLE.getId(), description, 2, uniqueId);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		variable = this.createVariable(TermId.STUDY_TYPE.getId(), String.valueOf(StudyType.T.getId()), 3, uniqueId);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		variable = this.createVariable(TermId.STUDY_STATUS.getId(), String.valueOf(TermId.ACTIVE_STUDY.getId()), 4, uniqueId);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		StudyValues studyValues = new StudyValues();
		studyValues.setVariableList(variableList);

		DmsProject dmsProject = new DmsProject();
		dmsProject.setName(name);
		dmsProject.setDescription(description);
		dmsProject.setProgramUUID(uniqueId);

		StudyReference studyReference = this.studyDataManager.addStudy(DmsProject.SYSTEM_FOLDER_ID, typeList, studyValues, uniqueId);
		dmsProject.setProjectId(studyReference.getId());
		return dmsProject;
	}

	public Variable createVariable(int termId, String value, int rank, String uniqueId) {
		StandardVariable stVar = this.ontologyManager.getStandardVariable(termId,uniqueId);

		DMSVariableType vtype = new DMSVariableType();
		vtype.setStandardVariable(stVar);
		vtype.setRank(rank);
		Variable var = new Variable();
		var.setValue(value);
		var.setVariableType(vtype);
		return var;
	}

	public void deleteTestData(int projectId) {
		this.studyDataManager.deleteEmptyFolder(projectId, null);
	}

	public List<Reference> getRootFolders(String uniqueId) {
		return this.studyDataManager.getRootFolders(uniqueId, StudyType.nurseriesAndTrials());
	}

}

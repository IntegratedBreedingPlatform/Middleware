
package org.generationcp.middleware;

import java.util.List;
import java.util.Random;

import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;

public class StudyTestDataUtil extends DataManagerIntegrationTest {

	private static StudyTestDataUtil instance;
	private final StudyDataManager studyDataManager;
	private final OntologyDataManager ontologyManager;
	private static final String TEST_FOLDER_NAME = "TEST_FOLDER_NAME";
	private static final String TEST_FOLDER_DESC = "TEST_FOLDER_DESC";

	private StudyTestDataUtil() {
		this.studyDataManager = DataManagerIntegrationTest.managerFactory.getStudyDataManager();
		this.ontologyManager = DataManagerIntegrationTest.managerFactory.getOntologyDataManager();
	}

	public static StudyTestDataUtil getInstance() {
		if (StudyTestDataUtil.instance == null) {
			StudyTestDataUtil.instance = new StudyTestDataUtil();
		}
		return StudyTestDataUtil.instance;
	}

	public DmsProject createFolderTestData(String uniqueId) throws MiddlewareQueryException {
		int randomInt = new Random().nextInt(10000);
		DmsProject dmsProject = new DmsProject();
		dmsProject.setName(StudyTestDataUtil.TEST_FOLDER_NAME + randomInt);
		dmsProject.setDescription(StudyTestDataUtil.TEST_FOLDER_DESC + randomInt);
		dmsProject.setProgramUUID(uniqueId);
		this.studyDataManager.addSubFolder(DmsProject.SYSTEM_FOLDER_ID, dmsProject.getName(), dmsProject.getDescription(),
						dmsProject.getProgramUUID());
		return dmsProject;
	}

	public DmsProject createStudyTestData(String uniqueId) throws MiddlewareQueryException {
		String name = "Study Name " + new Random().nextInt(10000);
		String description = "Study Description";

		VariableTypeList typeList = new VariableTypeList();
		VariableList variableList = new VariableList();

		Variable variable = this.createVariable(TermId.STUDY_NAME.getId(), name, 1);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		variable = this.createVariable(TermId.STUDY_TITLE.getId(), description, 2);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		variable = this.createVariable(TermId.STUDY_TYPE.getId(), String.valueOf(StudyType.T.getId()), 3);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		StudyValues studyValues = new StudyValues();
		studyValues.setVariableList(variableList);

		DmsProject dmsProject = new DmsProject();
		dmsProject.setName(name);
		dmsProject.setDescription(description);
		dmsProject.setProgramUUID(uniqueId);

		this.studyDataManager.addStudy(DmsProject.SYSTEM_FOLDER_ID, typeList, studyValues, uniqueId);
		return dmsProject;
	}

	public DmsProject createStudyTestDataWithActiveStatus(String uniqueId) throws MiddlewareQueryException {
		String name = "Study Name " + new Random().nextInt(10000);
		String description = "Study Description";

		VariableTypeList typeList = new VariableTypeList();
		VariableList variableList = new VariableList();

		Variable variable = this.createVariable(TermId.STUDY_NAME.getId(), name, 1);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		variable = this.createVariable(TermId.STUDY_TITLE.getId(), description, 2);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		variable = this.createVariable(TermId.STUDY_TYPE.getId(), String.valueOf(StudyType.T.getId()), 3);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		variable = this.createVariable(TermId.STUDY_STATUS.getId(), String.valueOf(TermId.ACTIVE_STUDY.getId()), 4);
		typeList.add(variable.getVariableType());
		variableList.add(variable);

		StudyValues studyValues = new StudyValues();
		studyValues.setVariableList(variableList);

		DmsProject dmsProject = new DmsProject();
		dmsProject.setName(name);
		dmsProject.setDescription(description);
		dmsProject.setProgramUUID(uniqueId);

		StudyReference studyReference = this.studyDataManager.addStudy(DmsProject.SYSTEM_FOLDER_ID, typeList, studyValues, uniqueId);
		return dmsProject;
	}

	public Variable createVariable(int termId, String value, int rank) throws MiddlewareQueryException {
		StandardVariable stVar = this.ontologyManager.getStandardVariable(termId);

		VariableType vtype = new VariableType();
		vtype.setStandardVariable(stVar);
		vtype.setRank(rank);
		Variable var = new Variable();
		var.setValue(value);
		var.setVariableType(vtype);
		return var;
	}

	public void deleteTestData(int projectId) throws MiddlewareQueryException {
		this.studyDataManager.deleteEmptyFolder(projectId, null);
	}

	public List<FolderReference> getLocalRootFolders(String uniqueId) throws MiddlewareQueryException {
		return this.studyDataManager.getRootFolders(uniqueId);
	}

}

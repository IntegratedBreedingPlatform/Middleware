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
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;

public class StudyTestDataUtil extends DataManagerIntegrationTest {
	
	private static StudyTestDataUtil instance;
	private StudyDataManager studyDataManager;
	private OntologyDataManager ontologyManager;
	private static final String TEST_FOLDER_NAME = "TEST_FOLDER_NAME";
	private static final String TEST_FOLDER_DESC = "TEST_FOLDER_DESC";
	
	private StudyTestDataUtil() {
		studyDataManager = managerFactory.getStudyDataManager();
		ontologyManager = managerFactory.getOntologyDataManager();
	}
	
	public static StudyTestDataUtil getInstance() {
		if(instance==null) {
			instance = new StudyTestDataUtil();
		}
		return instance;
	}
	
	public DmsProject createFolderTestData() throws MiddlewareQueryException {
		int randomInt = new Random().nextInt(10000);
		//TODO set test data for programUUID
		int folderId = studyDataManager.addSubFolder(DmsProject.SYSTEM_FOLDER_ID, 
				TEST_FOLDER_NAME+randomInt, TEST_FOLDER_DESC+randomInt, null);
		DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(folderId);
		return dmsProject;
	}
	
	public DmsProject createStudyTestData() throws MiddlewareQueryException {
		VariableTypeList typeList = new VariableTypeList();
        VariableList variableList = new VariableList();

        Variable variable = createVariable(TermId.STUDY_NAME.getId(), "Study Name " + 
        		new Random().nextInt(10000), 1);
        typeList.add(variable.getVariableType());
        variableList.add(variable);

        variable = createVariable(TermId.STUDY_TITLE.getId(), "Study Description", 2);
        typeList.add(variable.getVariableType());
        variableList.add(variable);

        StudyValues studyValues = new StudyValues();
        studyValues.setVariableList(variableList);

        StudyReference studyReference = studyDataManager.addStudy(
        		DmsProject.SYSTEM_FOLDER_ID, typeList, studyValues, null);
        DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(studyReference.getId());
		return dmsProject;
	}
	
	public DmsProject createStudyTestDataWithActiveStatus() throws MiddlewareQueryException {
		VariableTypeList typeList = new VariableTypeList();
        VariableList variableList = new VariableList();

        Variable variable = createVariable(TermId.STUDY_NAME.getId(), "Study Name " + 
        		new Random().nextInt(10000), 1);
        typeList.add(variable.getVariableType());
        variableList.add(variable);

        variable = createVariable(TermId.STUDY_TITLE.getId(), "Study Description", 2);
        typeList.add(variable.getVariableType());
        variableList.add(variable);
        
        variable = createVariable(TermId.STUDY_STATUS.getId(), 
        		String.valueOf(TermId.ACTIVE_STUDY.getId()), 3);
        typeList.add(variable.getVariableType());
        variableList.add(variable);

        StudyValues studyValues = new StudyValues();
        studyValues.setVariableList(variableList);

        StudyReference studyReference = studyDataManager.addStudy(
        		DmsProject.SYSTEM_FOLDER_ID, typeList, studyValues, null);
        DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(studyReference.getId());
		return dmsProject;
	}
	
	public Variable createVariable(int termId, String value, int rank) throws MiddlewareQueryException {
		StandardVariable stVar = ontologyManager.getStandardVariable(termId);

        VariableType vtype = new VariableType();
        vtype.setStandardVariable(stVar);
        vtype.setRank(rank);
        Variable var = new Variable();
        var.setValue(value);
        var.setVariableType(vtype);
        return var;
    }
	
	public void deleteTestData(int projectId) throws MiddlewareQueryException {
		studyDataManager.deleteEmptyFolder(projectId, null);
	}
	
	public List<FolderReference> getLocalRootFolders() throws MiddlewareQueryException {
		return studyDataManager.getRootFolders(Database.LOCAL, null);
    }
	
}

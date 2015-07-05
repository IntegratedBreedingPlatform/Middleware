
package org.generationcp.middleware.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.pojos.Method;
import org.junit.Test;

public class FieldbookListUtilTest {

	@Test
	public void removeStudyDetailsWithEmptyRowsTest() throws Exception {

		List<StudyDetails> studyDetailsList = new ArrayList<StudyDetails>();
		StudyDetails studyDetails = new StudyDetails();

		studyDetails.setRowCount(5);
		this.setAndAddDetails(studyDetails, studyDetailsList);

		List<StudyDetails> actual = FieldbookListUtil.removeStudyDetailsWithEmptyRows(studyDetailsList);

		assertEquals(5, actual.get(0).getId().intValue());
	}

	public void setAndAddDetails(StudyDetails studyDetails, List<StudyDetails> studyDetailsList) {
		studyDetails.setId(5);
		studyDetails.setStudyName("newStudy");
		studyDetailsList.add(studyDetails);
	}

	@Test
	public void removeStudyDetailsWithEmptyRowsWithoutRowsTest() throws Exception {

		List<StudyDetails> studyDetailsList = new ArrayList<StudyDetails>();
		StudyDetails studyDetails = new StudyDetails();

		this.setAndAddDetails(studyDetails, studyDetailsList);

		List<StudyDetails> actual = FieldbookListUtil.removeStudyDetailsWithEmptyRows(studyDetailsList);

		assertEquals(true, actual.isEmpty());
	}

	@Test
	public void sortMethodNamesInAscendingOrderTest() throws Exception {
		List<Method> newMethodList = new ArrayList<Method>();
		Method methodOne = this.createMethod("UUID", 10, "AName");
		Method methodTwo = this.createMethod("UUID2", 12, "SecondName");
		Method methodThree = this.createMethod("UUID3", 14, "ThirdName");

		newMethodList.add(methodTwo);
		newMethodList.add(methodOne);
		newMethodList.add(methodThree);

		FieldbookListUtil.sortMethodNamesInAscendingOrder(newMethodList);

		assertEquals("AName", newMethodList.get(0).getMname());
		assertEquals("SecondName", newMethodList.get(1).getMname());
		assertEquals("ThirdName", newMethodList.get(2).getMname());
	}

	public Method createMethod(String uniqueId, Integer id, String name) {
		Method methodName = new Method();
		methodName.setUniqueID(uniqueId);
		methodName.setMid(id);
		methodName.setMname(name);
		return methodName;
	}

}

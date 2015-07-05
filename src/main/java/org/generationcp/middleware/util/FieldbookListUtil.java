
package org.generationcp.middleware.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.pojos.Method;

public class FieldbookListUtil {

	private FieldbookListUtil() {

	}

	public static List<StudyDetails> removeStudyDetailsWithEmptyRows(List<StudyDetails> studyDetailList) {
		List<StudyDetails> newList = new ArrayList<StudyDetails>();
		for (StudyDetails detail : studyDetailList) {
			if (detail.hasRows()) {
				newList.add(detail);
			}
		}
		return newList;
	}

	public static void sortMethodNamesInAscendingOrder(List<Method> methodList) {
		Collections.sort(methodList, new Comparator<Method>() {

			@Override
			public int compare(Method o1, Method o2) {
				String methodName1 = o1.getMname().toUpperCase();
				String methodName2 = o2.getMname().toUpperCase();

				// ascending order
				return methodName1.compareTo(methodName2);
			}
		});
	}
}

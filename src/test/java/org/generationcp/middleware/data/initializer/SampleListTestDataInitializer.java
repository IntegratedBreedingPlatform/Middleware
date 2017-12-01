package org.generationcp.middleware.data.initializer;

import java.util.Date;

import org.generationcp.middleware.enumeration.SampleListType;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.User;

public class SampleListTestDataInitializer {

	public static SampleList createSampleList(final User user) {
		final SampleList sampleList = new SampleList();
		sampleList.setCreatedDate(new Date());
		sampleList.setCreatedBy(user);
		sampleList.setDescription("description");
		sampleList.setListName("Sample list");
		sampleList.setNotes("Notes");
		sampleList.setType(SampleListType.SAMPLE_LIST);
		sampleList.setProgramUUID("c35c7769-bdad-4c70-a6c4-78c0dbf784e5");
		return sampleList;
	}
	
	public static SampleList createSampleListFolder(final User user) {
		final SampleList sampleList = new SampleList();
		sampleList.setCreatedDate(new Date());
		sampleList.setCreatedBy(user);
		sampleList.setDescription("description");
		sampleList.setListName("Sample Folder");
		sampleList.setNotes("Notes");
		sampleList.setType(SampleListType.FOLDER);
		sampleList.setProgramUUID("c35c7769-bdad-4c70-a6c4-78c0dbf784e5");
		return sampleList;
	}
}

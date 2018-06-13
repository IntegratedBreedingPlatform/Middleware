
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;

public class FieldMapInfoTestDataInitializer {

	public FieldMapInfo createFieldMapInfo() {
		final FieldMapInfo fieldMapInfo = new FieldMapInfo();
		fieldMapInfo.setDatasets(new ArrayList<FieldMapDatasetInfo>());
		return fieldMapInfo;
	}

	public List<FieldMapInfo> createFieldMapInfoList(final int size) {
		final List<FieldMapInfo> fieldMapInfoList = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			fieldMapInfoList.add(this.createFieldMapInfo());
		}
		return fieldMapInfoList;
	}
}

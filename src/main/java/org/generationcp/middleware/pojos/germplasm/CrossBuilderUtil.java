
package org.generationcp.middleware.pojos.germplasm;

import java.util.List;

import org.generationcp.middleware.pojos.Name;

import com.google.common.base.Preconditions;

public class CrossBuilderUtil {

	static boolean nameTypeBasedResolution(StringBuilder toReturn, List<Integer> nameTypeOrder, List<Name> namesByGID) {
		Preconditions.checkNotNull(toReturn);
		Preconditions.checkNotNull(nameTypeOrder);
		Preconditions.checkNotNull(namesByGID);

		for (Integer integer : nameTypeOrder) {
			if (namesByGID != null) {
				for (Name name : namesByGID) {
					if (integer.equals(name.getTypeId())) {
						toReturn.append(name.getNval());
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean nameTypeBasedResolution() {
		// TODO Auto-generated method stub
		return false;
	}
}


package org.generationcp.middleware.pojos.germplasm;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.service.pedigree.PedigreeDataManagerFactory;

import com.google.common.base.Preconditions;

public class CrossBuilderUtil {

	static boolean nameTypeBasedResolution(final StringBuilder toReturn, final PedigreeDataManagerFactory pedigreeDataManagerFactory,
			final Integer gid, final List<String> nameTypeOrders) {

		Preconditions.checkNotNull(toReturn);
		Preconditions.checkNotNull(pedigreeDataManagerFactory);
		Preconditions.checkNotNull(nameTypeOrders);


		if (gid != null && gid > 0) {

			final List<Integer> nameTypeOrderId = new ArrayList<>();
			for (String  nameType: nameTypeOrders) {
				UserDefinedField byTableTypeAndCode = pedigreeDataManagerFactory.getGermplasmDataManager().getUserDefinedFieldByTableTypeAndCode("NAMES", "NAME", nameType);
				nameTypeOrderId.add(byTableTypeAndCode.getFldno());
			}

			final List<Name> namesByGID =
					pedigreeDataManagerFactory.getGermplasmDataManager().getByGIDWithListTypeFilters(gid, null,
							new ArrayList<Integer>(nameTypeOrderId));
			for (final Integer nameTypeId : nameTypeOrderId) {
				if (namesByGID != null) {
					for (final Name name : namesByGID) {
						if (name.getTypeId().equals(nameTypeId)) {
							toReturn.append(name.getNval());
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}

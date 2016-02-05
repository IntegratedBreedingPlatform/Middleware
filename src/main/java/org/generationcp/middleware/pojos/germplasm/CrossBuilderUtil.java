
package org.generationcp.middleware.pojos.germplasm;

import java.util.List;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.pedigree.PedigreeDataManagerFactory;

import com.google.common.base.Preconditions;

public class CrossBuilderUtil {

	static boolean nameTypeBasedResolution(final StringBuilder toReturn, final PedigreeDataManagerFactory pedigreeDataManagerFactory,
			final Germplasm germplasm, final List<Integer> nameTypeOrder) {

		Preconditions.checkNotNull(toReturn);
		Preconditions.checkNotNull(pedigreeDataManagerFactory);
		Preconditions.checkNotNull(germplasm);
		Preconditions.checkNotNull(nameTypeOrder);

		if (germplasm != null) {
			final List<Name> namesByGID =
					pedigreeDataManagerFactory.getGermplasmDataManager().getByGIDWithListTypeFilters(germplasm.getGid(), null,
							nameTypeOrder);
			for (final Integer integer : nameTypeOrder) {
				if (namesByGID != null) {
					for (final Name name : namesByGID) {
						if (integer.equals(name.getTypeId())) {
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

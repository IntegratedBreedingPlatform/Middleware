
package org.generationcp.middleware.pojos.germplasm;

import java.util.List;

import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.pedigree.PedigreeDataManagerFactory;

import com.google.common.base.Preconditions;

public class CrossBuilderUtil {

	static boolean nameTypeBasedResolution(final StringBuilder toReturn, final PedigreeDataManagerFactory pedigreeDataManagerFactory,
			final Integer gid, final List<Integer> nameTypeOrder) {

		Preconditions.checkNotNull(toReturn);
		Preconditions.checkNotNull(pedigreeDataManagerFactory);
		Preconditions.checkNotNull(nameTypeOrder);

		if (gid != null && gid > 0) {
			final List<Name> namesByGID =
					pedigreeDataManagerFactory.getGermplasmDataManager().getByGIDWithListTypeFilters(gid, null,
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

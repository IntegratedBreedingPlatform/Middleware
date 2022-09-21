package org.generationcp.middleware.dao.util;

import org.generationcp.middleware.domain.oms.TermId;

public class CommonQueryConstants {

	public static String HAS_FIELD_LAYOUT_EXPRESSION = "	case when (max(if(ndep.type_id = " + TermId.FIELDMAP_COLUMN.getId() + ", ndep.value, null)) is null) \n "
		+ "		and (max(if(ndep.type_id = " + TermId.FIELDMAP_RANGE.getId() + ", ndep.value, null))) is null \n"
		+ " 	then 0 else 1 end as hasFieldLayout \n";
}

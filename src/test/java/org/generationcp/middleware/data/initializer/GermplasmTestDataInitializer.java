
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.util.Util;

public class GermplasmTestDataInitializer {

	public Germplasm createGermplasmWithPreferredName(){
		Name name = new Name(null, null, 1, 1, 1, "Name", 0, 0, 0);
		Germplasm germplasm = new Germplasm(null, 0, 0, 0, 0, 1, 0, 0, Util.getCurrentDateAsIntegerValue(), name);
		return germplasm;
	}
	
	public static Germplasm createGermplasm(final int id) {
		final Germplasm germplasm = new Germplasm();
		germplasm.setGid(id);
		germplasm.setGdate(20150101);
		germplasm.setGpid1(1);
		germplasm.setGpid2(2);
		germplasm.setPreferredName(createGermplasmName(id));
		return germplasm;
	}

	public static Name createGermplasmName(final int id) {
		final Name name = new Name();
		name.setGermplasmId(id);
		name.setNval("Name" + id);

		return name;
	}

	public static List<Name> createNameList(final int noOfEntries) {
		final List<Name> names = new ArrayList<Name>();

		for (int i = 1; i <= noOfEntries; i++) {
			names.add(createGermplasmName(i));
		}

		return names;
	}
}


package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.util.Util;

public class GermplasmTestDataInitializer {

	public Germplasm createGermplasmWithPreferredName() {
		final Name name = new Name(null, null, 1, 1, 1, "Name", 0, 0, 0);
		final Germplasm germplasm = new Germplasm(null, 0, 0, 0, 0, 1, 0, 0, Util.getCurrentDateAsIntegerValue(), name);
		return germplasm;
	}

	public static Germplasm createGermplasm(final Integer id) {
		final Germplasm germplasm = new Germplasm();
		germplasm.setGid(id);
		germplasm.setGdate(20150101);
		germplasm.setGpid1(1);
		germplasm.setGpid2(2);
		germplasm.setGnpgs(2);
		germplasm.setLgid(Integer.valueOf(0));
		germplasm.setGrplce(Integer.valueOf(0));
		germplasm.setLocationId(Integer.valueOf(1));
		germplasm.setMethodId(Integer.valueOf(1));
		germplasm.setMgid(Integer.valueOf(0));
		germplasm.setUserId(Integer.valueOf(1));
		germplasm.setReferenceId(Integer.valueOf(1));
		germplasm.setLgid(Integer.valueOf(0));
		germplasm.setMethodName("MethodName");
		germplasm.setLocationName("LocationName");
		germplasm.setPreferredName(createGermplasmName(id));
		return germplasm;
	}

	public static Name createGermplasmName(final int gid) {
		return createGermplasmName(gid, "Name " + gid);
	}

	public static Name createGermplasmName(final int gid, final String germplasmName) {
		final Name name = new Name();
		name.setGermplasmId(gid);
		name.setNval(germplasmName);
		name.setLocationId(Integer.valueOf(1));
		name.setNdate(Integer.valueOf(20160101));
		name.setReferenceId(Integer.valueOf(1));
		name.setTypeId(Integer.valueOf(1));
		name.setUserId(Integer.valueOf(1));

		return name;
	}

	public static List<Name> createNameList(final int noOfEntries) {
		final List<Name> names = new ArrayList<Name>();

		for (int i = 1; i <= noOfEntries; i++) {
			names.add(GermplasmTestDataInitializer.createGermplasmName(i));
		}

		return names;
	}
}


package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;

public class GermplasmTestDataInitializer {

	public static Germplasm createGermplasm(final int id) {
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
			names.add(createGermplasmName(i));
		}

		return names;
	}
}


package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class GermplasmTestDataInitializer {

	public static final String PREFERRED_NAME = "IBP-VARIETY";

	public static Germplasm createGermplasmWithPreferredName() {
		final Name name = new Name(null, null, 1, 1, GermplasmTestDataInitializer.PREFERRED_NAME, 0, 0, 0);
		final Germplasm germplasm = new Germplasm(null, 0, 0, 0, 0, 0, Util.getCurrentDateAsIntegerValue(),
			0, 0, 0, name, null, new Method(1));
		germplasm.setGermplasmUUID(UUID.randomUUID().toString());
		return germplasm;
	}

	public static Germplasm createGermplasmWithPreferredName(final String preferredName) {
		final Name name = new Name(null, null, 1, 1, preferredName, 0, 0, 0);
		final Germplasm germplasm = new Germplasm(null, 0, 0, 0, 0, 0, Util.getCurrentDateAsIntegerValue(),
			0, 0, 0, name, null, new Method(1));
		germplasm.setGermplasmUUID(UUID.randomUUID().toString());
		return germplasm;
	}

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
		germplasm.setMethod(new Method(1));
		germplasm.setMgid(Integer.valueOf(0));
		germplasm.setReferenceId(Integer.valueOf(1));
		germplasm.setLgid(Integer.valueOf(0));
		germplasm.setLocationName("LocationName");
		germplasm.setPreferredName(GermplasmTestDataInitializer.createGermplasmName(id));
		germplasm.setGermplasmUUID(UUID.randomUUID().toString());
		return germplasm;
	}

	public static Germplasm createGermplasm(final Integer gDate, final int gpId1, final int gpId2, final int gnpgs, final int lgId,
			final int grplace, final int locationId, final int methodId , final int mgId, final int userId, final int referenceId,
			final String methodName, final String locationName) {
		final Germplasm germplasm = new Germplasm();
		germplasm.setGdate(gDate);
		germplasm.setGpid1(gpId1);
		germplasm.setGpid2(gpId2);
		germplasm.setGnpgs(gnpgs);
		germplasm.setLgid(lgId);
		germplasm.setGrplce(grplace);
		germplasm.setLocationId(locationId);
		germplasm.setMethod(new Method(methodId));
		germplasm.setMgid(mgId);
		germplasm.setReferenceId(referenceId);
		germplasm.setLocationName(locationName);
		final Name name = GermplasmTestDataInitializer.createGermplasmName(ThreadLocalRandom.current().nextInt(1, Integer
			.MAX_VALUE));
		name.setNstat(1);
		germplasm.setPreferredName(name);
		return germplasm;
	}

	public static Name createGermplasmName(final int gid) {
		return GermplasmTestDataInitializer.createGermplasmName(gid, "Name " + gid);
	}

	public static Name createGermplasmName(final int gid, final String germplasmName) {
		final Name name = new Name();
		name.setGermplasm(new Germplasm(gid));
		name.setNval(germplasmName);
		name.setLocationId(Integer.valueOf(1));
		name.setNdate(Integer.valueOf(20160101));
		name.setReferenceId(Integer.valueOf(1));
		name.setTypeId(Integer.valueOf(1));
		name.setNstat(Integer.valueOf(1));

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

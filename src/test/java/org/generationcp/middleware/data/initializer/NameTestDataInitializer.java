
package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;

import java.util.ArrayList;
import java.util.List;

public class NameTestDataInitializer {

	public static Name createName(final Integer typeId, final Integer gid, final String nVal) {
		final Name name = new Name();
		name.setTypeId(typeId);
		name.setNval(nVal);
		name.setGermplasm(new Germplasm(gid));
		name.setLocationId(0);
		name.setNstat(1);
		name.setNdate(20150707);
		name.setReferenceId(0);
		return name;
	}

	public static Name createName(final Integer typeId, final String nVal) {
		final Name name = new Name();
		name.setTypeId(typeId);
		name.setNval(nVal);
		return name;
	}

	public static List<Name> createNameList(final int count) {
		final List<Name> names = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			names.add(NameTestDataInitializer.createName(0, "DRVNM " + i));
		}
		return names;
	}

	public static Name createName(final Integer nid, final Integer nstat, final String nval, final Integer typeId) {
		final Name name = new Name();
		name.setNid(nid);
		name.setNstat(nstat);
		name.setNval(nval);
		name.setTypeId(typeId);
		return name;
	}
}

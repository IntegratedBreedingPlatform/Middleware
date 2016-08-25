
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.Name;

public class NameTestDataInitializer {

	public Name createName(final Integer typeId, final Integer gid, final String nVal) {
		final Name name = new Name();
		name.setTypeId(typeId);
		name.setNval(nVal);
		name.setGermplasmId(gid);
		name.setLocationId(0);
		name.setNstat(1);
		name.setNdate(20150707);
		name.setReferenceId(0);
		name.setUserId(1);
		return name;
	}
	
	public Name createName(final Integer typeId, final String nVal) {
		final Name name = new Name();
		name.setTypeId(typeId);
		name.setNval(nVal);
		return name;
	}

	public List<Name> createNameList(final int count) {
		final List<Name> names = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			names.add(this.createName(0, "DRVNM " + i));
		}
		return names;
	}
}

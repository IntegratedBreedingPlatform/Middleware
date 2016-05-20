
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.api.GermplasmGroup;

public class GermplasmGroupTestDataInitializer {

	private final GermplasmTestDataInitializer germplasmInitializer = new GermplasmTestDataInitializer();

	public GermplasmGroupTestDataInitializer() {
		// do nothing
	}

	public GermplasmGroup createGermplasmGroup(final Integer groupId, final Integer noOfEntries) {
		final GermplasmGroup germplasmGroup = new GermplasmGroup();

		// the founder germplasm gid is the mgid of a germplasm group
		final Germplasm founder = this.germplasmInitializer.createGermplasm(groupId);
		germplasmGroup.setFounder(founder);

		final List<Germplasm> groupMembers = new ArrayList<Germplasm>();
		for (int i = 1; i <= noOfEntries; i++) {
			final Germplasm groupMember = this.germplasmInitializer.createGermplasm(i);
			groupMember.setMgid(groupId);
			groupMembers.add(groupMember);
		}

		return germplasmGroup;
	}

	public List<GermplasmGroup> createGermplasmGroupList(final List<Integer> groupIds, final Integer noOfEntriesPerGroup) {
		final List<GermplasmGroup> germplasmGroupList = new ArrayList<GermplasmGroup>();

		for (final Integer groupId : groupIds) {
			germplasmGroupList.add(this.createGermplasmGroup(groupId, noOfEntriesPerGroup));
		}

		return germplasmGroupList;
	}

	public Map<Integer, GermplasmGroup> createGermplasmGroupMap(final Map<Integer, Integer> groupIdNoOfEntriesPerGroupMap) {
		final Map<Integer, GermplasmGroup> germplasmGroupList = new HashMap<Integer, GermplasmGroup>();

		for (final Map.Entry<Integer, Integer> entry : groupIdNoOfEntriesPerGroupMap.entrySet()) {
			final Integer groupId = entry.getKey();
			final Integer noOfEntriesPerGroup = entry.getValue();
			germplasmGroupList.put(groupId, this.createGermplasmGroup(groupId, noOfEntriesPerGroup));
		}

		return germplasmGroupList;
	}
}

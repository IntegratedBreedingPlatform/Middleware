
package org.generationcp.middleware.data.initializer;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.service.api.GermplasmGroup;
import org.generationcp.middleware.service.api.GermplasmGroupMember;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GermplasmGroupTestDataInitializer {

	public GermplasmGroupTestDataInitializer() {
		// do nothing
	}

	public GermplasmGroup createGermplasmGroup(final Integer groupId, final Integer noOfEntries) {
		final GermplasmGroup germplasmGroup = new GermplasmGroup();

		// the founder germplasm gid is the mgid of a germplasm group
		final GermplasmGroupMember founder = new GermplasmGroupMember(groupId, RandomStringUtils.randomAlphabetic(10));
		germplasmGroup.setFounder(founder);

		final List<GermplasmGroupMember> groupMembers = new ArrayList<>();
		for (int i = 1; i <= noOfEntries; i++) {
			final GermplasmGroupMember groupMember = new GermplasmGroupMember(groupId, RandomStringUtils.randomAlphabetic(10));
			groupMembers.add(groupMember);
		}
		germplasmGroup.setGroupMembers(groupMembers);

		return germplasmGroup;
	}

	public List<GermplasmGroup> createGermplasmGroupList(final List<Integer> groupIds, final Integer noOfEntriesPerGroup) {
		final List<GermplasmGroup> germplasmGroupList = new ArrayList<>();

		for (final Integer groupId : groupIds) {
			germplasmGroupList.add(this.createGermplasmGroup(groupId, noOfEntriesPerGroup));
		}

		return germplasmGroupList;
	}

	public Map<Integer, GermplasmGroup> createGermplasmGroupMap(final Map<Integer, Integer> groupIdNoOfEntriesPerGroupMap) {
		final Map<Integer, GermplasmGroup> germplasmGroupList = new HashMap<>();

		for (final Map.Entry<Integer, Integer> entry : groupIdNoOfEntriesPerGroupMap.entrySet()) {
			final Integer groupId = entry.getKey();
			final Integer noOfEntriesPerGroup = entry.getValue();
			germplasmGroupList.put(groupId, this.createGermplasmGroup(groupId, noOfEntriesPerGroup));
		}

		return germplasmGroupList;
	}
}


package org.generationcp.middleware.service.impl;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.service.api.GermplasmNamingService;
import org.generationcp.middleware.service.api.GermplasmType;
import org.generationcp.middleware.util.Util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class GermplasmNamingServiceImpl implements GermplasmNamingService {

	private GermplasmDAO germplasmDAO;
	private NameDAO nameDAO;

	public GermplasmNamingServiceImpl() {

	}

	public GermplasmNamingServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.germplasmDAO = new GermplasmDAO();
		this.germplasmDAO.setSession(sessionProvider.getSession());

		this.nameDAO = new NameDAO();
		this.nameDAO.setSession(sessionProvider.getSession());
	}

	@Override
	public void applyGroupName(final Integer gid, final String groupName, final UserDefinedField nameType, final Integer userId,
			final Integer locationId) {

		final Germplasm germplasm = this.germplasmDAO.getById(gid);

		if (germplasm.getMgid() == null || germplasm.getMgid() == 0) {
			throw new RuntimeException("Germplasm must be part of a management group for group name assignment.");
		}

		final List<Germplasm> groupMembers = this.germplasmDAO.getManagementGroupMembers(germplasm.getMgid());
		for (final Germplasm member : groupMembers) {
			this.addName(member, groupName, nameType, userId, locationId);
		}
	}

	private void addName(final Germplasm germplasm, final String groupName, final UserDefinedField nameType, final Integer userId,
			final Integer locationId) {

		final List<Name> currentNames = germplasm.getNames();

		Name existingNameOfGivenType = null;
		if (!currentNames.isEmpty() && nameType != null) {
			for (final Name name : currentNames) {
				if (nameType.getFldno().equals(name.getTypeId())) {
					existingNameOfGivenType = name;
					break;
				}
			}
		}

		if (existingNameOfGivenType == null) {
			// Make the current preferred name as non-preferred by setting nstat = 0
			final Name currentPreferredName = germplasm.findPreferredName();
			if (currentPreferredName != null) {
				currentPreferredName.setNstat(0);
			}

			final Name name = new Name();
			name.setGermplasmId(germplasm.getGid());
			name.setTypeId(nameType.getFldno());
			name.setNval(groupName);
			name.setNstat(1); // nstat = 1 means it is preferred name.
			name.setUserId(userId);
			name.setLocationId(locationId);
			name.setNdate(Util.getCurrentDateAsIntegerValue());
			name.setReferenceId(0);

			germplasm.getNames().add(name);
			this.germplasmDAO.save(germplasm);
		} else {
			throw new RuntimeException(String.format("Germplasm already has a name: %s (type: %s).", existingNameOfGivenType, nameType.getFcode()));
		}
	}

	@Override
	public int getNextSequence(final String prefix) {
		return new Random().nextInt(200);
	}

	@Override
	public List<String> getProgramIdentifiers(final Integer levelCode) {
		if (levelCode == 1) {
			return Lists.newArrayList("AB", "BC", "DJ", "EA");
		} else if (levelCode == 2) {
			return Lists.newArrayList("CA", "CB", "CC", "CZ");
		}
		return Lists.newArrayList();
	}

	@Override
	public Set<GermplasmType> getGermplasmTypes() {
		return Sets.newHashSet(GermplasmType.values());
	}

}

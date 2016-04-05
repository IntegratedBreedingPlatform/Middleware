
package org.generationcp.middleware.service.impl;

import java.util.List;
import java.util.Set;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.service.api.GermplasmGroupNamingResult;
import org.generationcp.middleware.service.api.GermplasmNamingService;
import org.generationcp.middleware.service.api.GermplasmType;
import org.generationcp.middleware.service.api.KeySequenceRegisterService;
import org.generationcp.middleware.util.Util;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class GermplasmNamingServiceImpl implements GermplasmNamingService {

	private GermplasmDAO germplasmDAO;
	private NameDAO nameDAO;

	private KeySequenceRegisterService keySequenceRegisterService;

	public GermplasmNamingServiceImpl() {

	}

	public GermplasmNamingServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.germplasmDAO = new GermplasmDAO();
		this.germplasmDAO.setSession(sessionProvider.getSession());

		this.nameDAO = new NameDAO();
		this.nameDAO.setSession(sessionProvider.getSession());

		this.keySequenceRegisterService = new KeySequenceRegisterServiceImpl(sessionProvider);
	}

	public GermplasmNamingServiceImpl(GermplasmDAO germplasmDAO, NameDAO nameDAO, KeySequenceRegisterService keySequenceRegisterService) {
		this.germplasmDAO = germplasmDAO;
		this.nameDAO = nameDAO;
		this.keySequenceRegisterService = keySequenceRegisterService;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public GermplasmGroupNamingResult applyGroupName(final Integer gid, final String groupName, final UserDefinedField nameType,
			final Integer userId,
			final Integer locationId) {

		GermplasmGroupNamingResult result = new GermplasmGroupNamingResult();

		final Germplasm germplasm = this.germplasmDAO.getById(gid);

		if (germplasm.getMgid() == null || germplasm.getMgid() == 0) {
			result.addMessage(
					String.format("Germplasm (gid: %s) is not part of a management group. Can not assign group name.",
							germplasm.getGid()));
			return result;
		}

		final List<Germplasm> groupMembers = this.germplasmDAO.getManagementGroupMembers(germplasm.getMgid());
		final String nameWithSequence = groupName + this.keySequenceRegisterService.incrementAndGetNextSequence(groupName);
		for (final Germplasm member : groupMembers) {
			this.addName(member, nameWithSequence, nameType, userId, locationId, result);
		}
		
		return result;
	}

	private void addName(final Germplasm germplasm, final String groupName, final UserDefinedField nameType, final Integer userId,
			final Integer locationId, final GermplasmGroupNamingResult result) {

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
			result.addMessage(String.format("Germplasm (gid: %s) successfully assigned name %s of type %s as a preferred name.",
					germplasm.getGid(), groupName, nameType.getFcode()));
		} else {
			result.addMessage(
					String.format("Germplasm (gid: %s) already has existing name %s of type %s. Supplied name %s was not added.",
							germplasm.getGid(), existingNameOfGivenType.getNval(), nameType.getFcode(), groupName));
		}
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
	public List<String> getLocationIdentifiers() {
		return Lists.newArrayList("CZ", "CA", "CB");
	}

	@Override
	public Set<GermplasmType> getGermplasmTypes() {
		return Sets.newHashSet(GermplasmType.values());
	}

}

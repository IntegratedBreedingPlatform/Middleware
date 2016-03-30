package org.generationcp.middleware.service.impl;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.service.api.GermplasmCodingService;
import org.generationcp.middleware.service.api.GermplasmType;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


public class GermplasmCodingServiceImpl implements GermplasmCodingService {

	public GermplasmCodingServiceImpl() {

	}

	public GermplasmCodingServiceImpl(HibernateSessionProvider sessionProvider) {

	}

	@Override
	public void applyCodedName(Integer gid, String codedName, Integer nameType) {

	}

	@Override
	public int getNextSequence(String prefix) {
		return new Random().nextInt(200);
	}

	@Override
	public List<String> getProgramIdentifiers(Integer levelCode) {
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

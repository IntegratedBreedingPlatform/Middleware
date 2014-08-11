package org.generationcp.middleware.operation.builder;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.GermplasmDataManagerUtil;

public class NameBuilder extends Builder {

	public NameBuilder(HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public int getMaximumSequence(boolean isBulk, String prefix, String suffix, int count) throws MiddlewareQueryException {
		Set<String> names = new HashSet<String>();
		setWorkingDatabase(Database.LOCAL);
		names.addAll(getNameDao().getAllMatchingNames(prefix, suffix));
		setWorkingDatabase(Database.CENTRAL);
		names.addAll(getNameDao().getAllMatchingNames(prefix, suffix));
		
		return getMaximumSequenceInNames(isBulk, names, prefix, suffix, count);
	}
	
	private int getMaximumSequenceInNames(boolean isBulk, Set<String> names, String prefix, String suffix, int plantsSelected) {
		String standardizedPrefix = GermplasmDataManagerUtil.standardizeName(prefix);
		String standardizedSuffix = GermplasmDataManagerUtil.standardizeName(suffix);
		String regex = null;
		if (isBulk) {
			regex = "^[\\Q" + prefix + "\\E|\\Q" + standardizedPrefix + "\\E]" 
					+ "[" + plantsSelected + "]" 
					+ "[\\Q" + suffix + "\\E|\\Q" + standardizedSuffix + "\\E]"
					+ "[\\(]?([\\d\\s]*)[\\)]?$";
		}
		else {
			regex = "^[\\Q" + prefix + "\\E|\\Q" + standardizedPrefix + "\\E]([\\d\\s]*)[\\Q" + suffix + "\\E|\\Q" + standardizedSuffix + "\\E]$";
		}
		Pattern pattern = Pattern.compile(regex);
		
		int maxSequence = -1;
		for (String name : names) {
			Matcher matcher = pattern.matcher(name);
			boolean found = matcher.find();
			int groupCount = matcher.groupCount();
			if (found && groupCount > 0) {
				String countString = matcher.group(1).trim();
				int count = 0;
				if (countString != null) {
					if ("".equals(countString)) {
						count = 0;
					}
					else {
						count = Integer.valueOf(countString);
					}
				}
				if (count > maxSequence) {
					maxSequence = count;
				}
			}
		}
		return maxSequence;
	}
}

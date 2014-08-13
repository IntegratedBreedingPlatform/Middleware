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
		prefix = prefix.toUpperCase();
		suffix = suffix.toUpperCase();
		String standardizedPrefix = GermplasmDataManagerUtil.standardizeName(prefix);
		String standardizedSuffix = GermplasmDataManagerUtil.standardizeName(suffix);
		String regex1, regex2, regex3 = null;
		if (isBulk) {
			regex1 = "^\\Q" + prefix + "\\E" 
					+ (plantsSelected > 1 ? "[" + plantsSelected + "]" : "")
					+ "\\Q" + suffix + "\\E"
					+ "[\\(]?([\\d\\s]*)[\\)]?$";
			regex2 = "^\\Q" + standardizedPrefix + "\\E" 
					+ (plantsSelected > 1 ? "[" + plantsSelected + "]" : "") 
					+ "\\Q" + standardizedSuffix + "\\E"
					+ "[\\(]?([\\d\\s]*)[\\)]?$";
			regex3 = "^\\Q" + prefix.replaceAll("\\s", "") + "\\E" 
					+ (plantsSelected > 1 ? "[" + plantsSelected + "]" : "")
					+ "\\Q" + suffix.replaceAll("\\s", "") + "\\E"
					+ "[\\(]?([\\d\\s]*)[\\)]?$";
		}
		else {
			regex1 = "^\\Q" + prefix + "\\E([\\d\\s]*)\\Q" + suffix + "\\E$";
			regex2 = "^\\Q" + standardizedPrefix + "\\E([\\d\\s]*)\\Q" + standardizedSuffix + "\\E$";
			regex3 = "^\\Q" + prefix.replaceAll("\\s", "") + "\\E([\\d\\s]*)\\Q" + suffix.replaceAll("\\s", "") + "\\E$";
		}
		Pattern pattern1 = Pattern.compile(regex1); //original version (upper case) may or may not be standardized
		Pattern pattern2 = Pattern.compile(regex2); //standardized upper case version
		Pattern pattern3 = Pattern.compile(regex3); //without spaces in uppder case version
		
		int maxSequence = -1;
		for (String name : names) {
			Matcher matcher1 = pattern1.matcher(name.toUpperCase());
			Matcher matcher2 = pattern2.matcher(name.toUpperCase());
			Matcher matcher3 = pattern3.matcher(name.toUpperCase().replaceAll("\\s", ""));
			boolean found1 = matcher1.find();
			boolean found2 = matcher2.find();
			boolean found3 = matcher3.find();
			Matcher matcher = found1 ? matcher1 : (found2 ? matcher2 : (found3 ? matcher3 : null));
			if (matcher != null) {
				int groupCount = matcher.groupCount();
				if (groupCount > 0) {
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
		}
		return maxSequence;
	}
}

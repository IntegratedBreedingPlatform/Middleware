/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Utility methods for the GermplasmDataManager class
 *
 * @author Kevin Manansala, Joyce Avestro
 *
 */
public class GermplasmDataManagerUtil {

	/**
	 * 
	 * From ICIS Wiki documentation:
	 * 
	 * <p>
	 * A major problem with identifying germplasm is the detection of minor variants of a name. For example some users hyphenate foreign
	 * language names while others use spaces or capital letters within a name string of small case letters, some separate a character
	 * prefix from a number by a space, others do not. Another problem is that different ODBC drivers implement the same SQL search
	 * differently with respect to case sensitivity. For these reasons, a set of name standardization has been devised with the objective of
	 * producing the same standardized name from as wide a range of variants as possible.
	 * 
	 * <p>
	 * When a name string is supplied to the ICIS DLL for searching, it is searched as supplied and after standardization. Routines, which
	 * write names to the database, do not apply the standardization and users must specifically call a standardization routine if they wish
	 * to standardize names before storing them. Names in the database are not required to be standardized. It is hoped that the resulting
	 * standardized name is acceptable for presentation, but there will be cases where users insist on having a name, which violates the
	 * rules. For example CIMMYT prefers that abbreviations contain no spaces. This is allowed but the rule violation should not be used to
	 * distinguish genotypes, and the standardized name should be given as a synonym. Users will always be able to search for names that
	 * violate the rules and the correct germplasm should be found. Given a germplasm name, apply the standardization procedure to it.
	 * 
	 * <p>
	 * These standardization rules are applied in order as follows:
	 * 
	 * <p>
	 * (L= any letter; ^= space; N= any numeral, S= any of {-,',[,],+,.})
	 * 
	 * <li>Capitalize all letters Khao-Dawk-Mali105 becomes KHAO-DAWK-MALI105
	 * 
	 * <li>L( becomes L^( and )L becomes )^L IR64(BPH) becomes IR64 (BPH)
	 * 
	 * <li>N( becomes N^( and )N becomes )^N IR64(5A) becomes IR64 (5A)
	 * 
	 * <li>L. becomes L^ IR 63 SEL. becomes IR 64 SEL
	 * 
	 * <li>LN becomes L^N EXCEPT SLN MALI105 becomes MALI 105 but MALI-F4 IS unchanged
	 * 
	 * <li>NL becomes N^L EXCEPT SNL B 533A-1 becomes B 533 A-1 but B 533 A-4B is unchanged
	 * 
	 * <li>LL-LL becomes LL^LL KHAO-DAWK-MALI 105 becomes KHAO DAWK MALI 105
	 * 
	 * <li>^0N becomes ^N IRTP 00123 becomes IRTP 123
	 * 
	 * <li>^^ becomes ^
	 * 
	 * <li>REMOVE LEADING OR TRAILING ^
	 * 
	 * <li>^) becomes ) and (^ becomes (
	 * 
	 * <li>L-N becomes L^N when there is only one l) in the name and L is not preceded by a space
	 * 
	 * <li>^/ becomes / and /^ becomes /
	 * 
	 * @param name
	 * @return the standardized germplasm name
	 */
	public static String standardizeName(String name) {

		String toreturn;

		if (name != null) {
			toreturn = name.trim();
		} else {
			toreturn = "";
		}

		// a) Capitalize all letters
		toreturn = toreturn.toUpperCase();

		int stringLength = toreturn.length();
		for (int ctr = 0; ctr < stringLength; ctr++) {
			char currentChar = toreturn.charAt(ctr);
			if (currentChar == '(') {
				if (ctr - 1 >= 0) {
					char previousChar = toreturn.charAt(ctr - 1);
					// L( becomes L^( or N( becomes N^(
					if (Character.isLetterOrDigit(previousChar)) {
						String firstHalf = toreturn.substring(0, ctr);
						String secondHalf = toreturn.substring(ctr);
						toreturn = firstHalf + " " + secondHalf;
						stringLength++;
						continue;
					}
				}

				if (ctr + 1 < stringLength) {
					char nextChar = toreturn.charAt(ctr + 1);
					// (^ becomes (
					if (Character.isWhitespace(nextChar)) {
						String firstHalf = toreturn.substring(0, ctr + 1);
						String secondHalf = toreturn.substring(ctr + 2);
						toreturn = firstHalf + secondHalf;
						stringLength--;
						continue;
					}
				}
			} else if (currentChar == ')') {
				if (ctr - 1 >= 0) {
					char previousChar = toreturn.charAt(ctr - 1);
					// ^) becomes )
					if (Character.isWhitespace(previousChar)) {
						String firstHalf = toreturn.substring(0, ctr - 1);
						String secondHalf = toreturn.substring(ctr);
						toreturn = firstHalf + secondHalf;
						stringLength--;
						ctr--;
						continue;
					}
				}

				if (ctr + 1 < stringLength) {
					char nextChar = toreturn.charAt(ctr + 1);
					// )L becomes )^L or )N becomes )^N
					if (Character.isLetterOrDigit(nextChar)) {
						String firstHalf = toreturn.substring(0, ctr + 1);
						String secondHalf = toreturn.substring(ctr + 1);
						toreturn = firstHalf + " " + secondHalf;
						stringLength++;
						continue;
					}
				}
			} else if (currentChar == '.') {
				if (ctr - 1 >= 0) {
					char previousChar = toreturn.charAt(ctr - 1);
					// L. becomes L^
					if (Character.isLetter(previousChar)) {
						if (ctr + 1 < stringLength) {
							String firstHalf = toreturn.substring(0, ctr);
							String secondHalf = toreturn.substring(ctr + 1);
							toreturn = firstHalf + " " + secondHalf;
							continue;
						} else {
							toreturn = toreturn.substring(0, ctr);
							break;
						}
					}
				}
			} else if (Character.isLetter(currentChar)) {
				if (ctr + 1 < stringLength) {
					char nextChar = toreturn.charAt(ctr + 1);
					if (Character.isDigit(nextChar)) {
						// LN becomes L^N EXCEPT SLN
						// check if there is a special character before the
						// letter
						if (ctr - 1 >= 0) {
							char previousChar = toreturn.charAt(ctr - 1);
							if (GermplasmDataManagerUtil.isGermplasmNameSpecialChar(previousChar)) {
								continue;
							}
						}

						String firstHalf = toreturn.substring(0, ctr + 1);
						String secondHalf = toreturn.substring(ctr + 1);
						toreturn = firstHalf + " " + secondHalf;
						stringLength++;
						continue;
					}
				}
			} else if (currentChar == '0') {
				// ^0N becomes ^N
				if (ctr - 1 >= 0 && ctr + 1 < stringLength) {
					char nextChar = toreturn.charAt(ctr + 1);
					char previousChar = toreturn.charAt(ctr - 1);

					if (Character.isDigit(nextChar) && Character.isWhitespace(previousChar)) {
						String firstHalf = toreturn.substring(0, ctr);
						String secondHalf = toreturn.substring(ctr + 1);
						toreturn = firstHalf + secondHalf;
						stringLength--;
						ctr--;
						continue;
					}
				}
			} else if (Character.isDigit(currentChar)) {
				if (ctr + 1 < stringLength) {
					char nextChar = toreturn.charAt(ctr + 1);
					if (Character.isLetter(nextChar)) {
						// NL becomes N^L EXCEPT SNL
						// check if there is a special character before the
						// number
						if (ctr - 1 >= 0) {
							char previousChar = toreturn.charAt(ctr - 1);
							if (GermplasmDataManagerUtil.isGermplasmNameSpecialChar(previousChar)) {
								continue;
							}
						}

						String firstHalf = toreturn.substring(0, ctr + 1);
						String secondHalf = toreturn.substring(ctr + 1);
						toreturn = firstHalf + " " + secondHalf;
						stringLength++;
						continue;
					}
				}
			} else if (currentChar == '-') {
				if (ctr - 1 >= 0 && ctr + 1 < stringLength) {
					// L-N becomes L^N when there is only one in the name
					// and L is not preceded by a space
					char nextChar = toreturn.charAt(ctr + 1);
					char previousChar = toreturn.charAt(ctr - 1);

					if (Character.isLetter(previousChar) && Character.isDigit(nextChar)
					// if there is only one '-' in the string then the
					// last occurrence of that char is the only
					// occurrence
							&& toreturn.lastIndexOf(currentChar) == ctr) {
						// check if the letter is preceded by a space or not
						if (ctr - 2 >= 0) {
							char prevPrevChar = toreturn.charAt(ctr - 2);
							if (Character.isWhitespace(prevPrevChar)) {
								continue;
							}
						}

						String firstHalf = toreturn.substring(0, ctr);
						String secondHalf = toreturn.substring(ctr + 1);
						toreturn = firstHalf + " " + secondHalf;
						continue;
					}
				}

				if (ctr - 2 >= 0 && ctr + 2 < stringLength) {
					// LL-LL becomes LL^LL
					char nextChar = toreturn.charAt(ctr + 1);
					char nextNextChar = toreturn.charAt(ctr + 2);
					char previousChar = toreturn.charAt(ctr - 1);
					char prevPrevChar = toreturn.charAt(ctr - 2);

					if (Character.isLetter(prevPrevChar) && Character.isLetter(previousChar) && Character.isLetter(nextChar)
							&& Character.isLetter(nextNextChar)) {
						String firstHalf = toreturn.substring(0, ctr);
						String secondHalf = toreturn.substring(ctr + 1);
						toreturn = firstHalf + " " + secondHalf;
						continue;
					}
				}
			} else if (currentChar == ' ') {
				if (ctr + 1 < stringLength) {
					char nextChar = toreturn.charAt(ctr + 1);
					// ^^ becomes ^
					if (nextChar == ' ') {
						String firstHalf = toreturn.substring(0, ctr);
						String secondHalf = toreturn.substring(ctr + 1);
						toreturn = firstHalf + secondHalf;
						stringLength--;
						ctr--;
						continue;
					}
				}
			} else if (currentChar == '/') {
				// ^/ becomes / and /^ becomes /
				if (ctr - 1 >= 0) {
					char previousChar = toreturn.charAt(ctr - 1);
					if (Character.isWhitespace(previousChar)) {
						String firstHalf = toreturn.substring(0, ctr - 1);
						String secondHalf = toreturn.substring(ctr);
						toreturn = firstHalf + secondHalf;
						stringLength--;
						ctr = ctr - 2;
						continue;
					}
				}

				if (ctr + 1 < stringLength) {
					char nextChar = toreturn.charAt(ctr + 1);
					if (Character.isWhitespace(nextChar)) {
						String firstHalf = toreturn.substring(0, ctr + 1);
						String secondHalf = toreturn.substring(ctr + 2);
						toreturn = firstHalf + secondHalf;
						stringLength--;
						ctr--;
						continue;
					}
				}
			}

		}

		// REMOVE LEADING OR TRAILING ^
		toreturn = toreturn.trim();

		return toreturn;
	}

	public static String removeSpaces(String string) {
		if (string == null) {
			return "";
		}
		StringTokenizer tokenizer = new StringTokenizer(string);
		StringBuffer withSpacesRemoved = new StringBuffer();
		while (tokenizer.hasMoreTokens()) {
			withSpacesRemoved.append(tokenizer.nextToken());
		}
		return withSpacesRemoved.toString();
	}

	/**
	 * Returns true if the given char is considered a special character based on ICIS Germplasm Name standardization rules. Returns false
	 * otherwise.
	 * 
	 * @param c
	 * @return true if c is a special name character
	 */
	public static boolean isGermplasmNameSpecialChar(char c) {
		char specialCharacters[] = {'-', '\'', '[', ']', '+', '.'};
		for (char sp : specialCharacters) {
			if (c == sp) {
				return true;
			}
		}

		return false;
	}

	public static String getNameToUseByMode(String name, GetGermplasmByNameModes mode) {
		// Do string manipulation on name parameter depending on GetGermplasmByNameModes parameter
		String nameToUse = "";
		if (mode == GetGermplasmByNameModes.NORMAL) {
			nameToUse = name;
		} else if (mode == GetGermplasmByNameModes.SPACES_REMOVED) {
			String nameWithSpacesRemoved = GermplasmDataManagerUtil.removeSpaces(name);
			nameToUse = nameWithSpacesRemoved.toString();
		} else if (mode == GetGermplasmByNameModes.STANDARDIZED) {
			String standardizedName = GermplasmDataManagerUtil.standardizeName(name);
			nameToUse = standardizedName;
		}
		return nameToUse;
	}

	public static List<String> getNamesToUseByMode(List<String> names, GetGermplasmByNameModes mode) {
		List<String> namesToUse = new ArrayList<String>();

		for (String name : names) {
			String nameToUse = "";
			if (mode == GetGermplasmByNameModes.NORMAL) {
				nameToUse = name;
			} else if (mode == GetGermplasmByNameModes.SPACES_REMOVED || mode == GetGermplasmByNameModes.SPACES_REMOVED_BOTH_SIDES) {
				String nameWithSpacesRemoved = GermplasmDataManagerUtil.removeSpaces(name);
				nameToUse = nameWithSpacesRemoved.toString();
			} else if (mode == GetGermplasmByNameModes.STANDARDIZED) {
				String standardizedName = GermplasmDataManagerUtil.standardizeName(name);
				nameToUse = standardizedName;
			}
			namesToUse.add(nameToUse);
		}
		return namesToUse;
	}

	public static List<String> createNamePermutations(String name) {
		List<String> names = new ArrayList<>();
		names.add(name);
		names.add(GermplasmDataManagerUtil.standardizeName(name));
		names.add(GermplasmDataManagerUtil.removeSpaces(name));
		return names;
	}

}

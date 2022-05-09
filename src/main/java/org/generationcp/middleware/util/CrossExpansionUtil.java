
package org.generationcp.middleware.util;

import org.apache.commons.lang.math.NumberUtils;

public class CrossExpansionUtil {

	public static final int MAX_CROSS_NAME_SIZE = 240;
	public static final String CROSS_NAME_TRUNCATED_SUFFIX = "(truncated)";

	public static String getNewDelimiter(String xPed) {
		String getNewDelimiter = "";
		int x = 0;
		int maxLevel = 0;
		String cad = "";
		for (x = 32; x >= 1; x--) {
			cad = "/" + x + "/";
			if (x == 2) {
				cad = "//";
			}
			if (x == 1) {
				cad = "/";
			}
			if (xPed.contains(cad)) {
				maxLevel = x;
				x = 0;
			}
		}
		// New level must be one higher than current one found
		maxLevel = maxLevel + 1;
		getNewDelimiter = "/" + maxLevel + "/";
		if (maxLevel == 1) {
			getNewDelimiter = "/";
		}
		if (maxLevel == 2) {
			getNewDelimiter = "//";
		}
		return getNewDelimiter;
	}

	public static void getParentsDoubleRetroCrossNew(String[] mxp1, String[] mxp2) {
		// 24 agosto 2012, Medificaciones por Jesper
		// Resolving situation of GID=29367 CID=22793 and GID=29456 CID=22881
		// Female : CMH75A.66/2*CNO79 Male CMH75A.66/3*CNO79 Result: CMH75A.66/2*CNO79*2//CNO79
		// Solution:
		// p1 : CMH75A.66/2*CNO79 p2: CMH75A.66/2*CNO79//CNO79
		// JEN 2012-07-16
		String xp1 = mxp1[0];
		String xp2 = mxp2[0];
		String beforeStr = "";
		String afterStr = "";
		String delimiter = "";
		String cutStr = "";
		String cleanBefore = "";
		String cleanAfter = "";
		int x = 0;
		int y = 0;
		int xx = 0;
		int lev1 = 0;
		int lev2 = 0;
		String xDel = "";
		String a = "";
		String b = "";
		// Default is true - 50% chance it is right
		boolean slashLeft = true;
		// /2*A is SlashLeft, and A*2/ is not SlashLeft
		boolean changed = false;
		for (x = 1; x <= xp1.length(); x++) {
			if (xp1.substring(x - 1, x).equals(xp2.substring(x - 1, x))) {
				beforeStr = beforeStr + xp1.substring(x - 1, x);
			} else {
				lev1 = 0;
				xx = beforeStr.length() + 1;
				xDel = beforeStr.substring(beforeStr.length() - 1, beforeStr.length());
				if (beforeStr.length() > 1 && NumberUtils.isNumber(beforeStr.substring(beforeStr.length() - 1, beforeStr.length()))
						&& beforeStr.endsWith("*")) {
					lev1 = Integer.valueOf(beforeStr.substring(beforeStr.length() - 1, beforeStr.length()));
					xDel = "*";
				}
				if ("*".equals(xp1.substring(xx - 1, xx))) {
					xDel = "*";
					xx = xx + 1;
				}
				if ("*".equals(xDel) || "/".equals(xDel)) {
					for (y = xx; y < xp1.length(); y++) {
						if (!NumberUtils.isNumber(xp1.substring(y - 1, y))) {
							// Exit for
							break;
						}
						lev1 = lev1 * 10 + Integer.valueOf(xp1.substring(y - 1, y));
						// Take off leading offset in AfterStr if that has now been put in Lev2
						if (xp1.length() - y < afterStr.length()) {
							afterStr = afterStr.substring(1, afterStr.length());
						}
					}
					if (lev1 == 0) {
						lev1 = 1;
					}
				}
				// Exit for
				break;
			}
		}
		afterStr = "";
		if (xp1.length() < xp2.length()) {
			cutStr = xp2.substring(xp2.length() - xp1.length());
		} else {
			cutStr = String.format("%" + xp1.length() + "s", xp2);
		}
		for (x = xp1.length(); x > 0; x--) {
			if (xp1.substring(x - 1, x).equals(cutStr.substring(x - 1, x))) {
				afterStr = xp1.substring(x - 1, x) + afterStr;
			} else {
				lev2 = 0;
				xx = beforeStr.length() + 1;
				if (beforeStr.length() > 1 && NumberUtils.isNumber(beforeStr.substring(beforeStr.length() - 1, beforeStr.length()))
						&& beforeStr.endsWith("*")) {
					// This criteria has problems 2012-07-17
					lev2 = Integer.valueOf(beforeStr.substring(beforeStr.length() - 1, beforeStr.length()));
					xDel = "*";
				}
				if ("*".equals(xp2.substring(beforeStr.length(), beforeStr.length() + 1))) {
					xDel = "*";
					xx = beforeStr.length() + 2;
				}
				if ("*".equals(xDel) || "/".equals(xDel)) {
					for (y = xx; y < xp2.length(); y++) {
						// This criteria has problems 2012-07-17
						if (!NumberUtils.isNumber(xp2.substring(y - 1, y))) {
							// Exit for
							break;
						}
						lev2 = lev2 * 10 + Integer.valueOf(xp2.substring(y - 1, y));
						// Take off leading offset in AfterStr if that has now been put in Lev2
						if (xp2.length() - y < afterStr.length()) {
							afterStr = afterStr.substring(1, afterStr.length());
						}
					}
				}
				if (lev2 == 0) {
					lev2 = 1;
					changed = true;
					while (afterStr.length() > 2 && changed) {
						changed = false;
						a = afterStr.substring(0, 1);
						b = afterStr.substring(1, 2);
						if (("/".equals(a) || NumberUtils.isNumber(a)) && ("/".equals(b) || NumberUtils.isNumber(b))) {
							afterStr = afterStr.substring(1, afterStr.length());
							changed = true;
						}
					}
				}
				// Exit for
				break;
			}
		}
		
		if (afterStr.startsWith("/")) {
			slashLeft = false;
		}
		cleanAfter = afterStr;
		cleanBefore = beforeStr;
		// Fixing CleanAfter
		if (cleanAfter.startsWith("*") || cleanAfter.startsWith("/")) {
			cleanAfter = cleanAfter.substring(1, cleanAfter.length());
		}
		// Fixing CleanBefore
		if (cleanBefore.length() > 1
				&& NumberUtils.isNumber(cleanBefore.substring(cleanBefore.length() - 1, cleanBefore.length()))
				&& ("/".equals(cleanBefore.substring(cleanBefore.length() - 2, cleanBefore.length() - 1)) || "*".equals(cleanBefore
						.substring(cleanBefore.length() - 1, cleanBefore.length())))) {
			// This criteria has problems 2012-07-19
			cleanBefore = cleanBefore.substring(0, cleanBefore.length() - 2);
		}
		try {
			delimiter = CrossExpansionUtil.getNewDelimiter(xp1 + xp2);
		} catch (Exception e) {
			System.out.println("Error en GetNewDelimiter " + e);
		}
		if (lev1 > lev2) {
			if (slashLeft) {
				xp1 = xp2 + delimiter + cleanAfter;
			} else {
				xp1 = cleanBefore + delimiter + xp2;
			}
		}
		if (lev2 > lev1) {
			if (slashLeft) {
				xp2 = xp1 + delimiter + cleanAfter;
			} else {
				xp2 = cleanBefore + delimiter + xp1;
			}
		}
		mxp1[0] = xp1;
		mxp2[0] = xp2;
	}

	public static int giveNameValue(int pNtypeX, int pNstatX, CimmytWheatNameUtil cimmyWheatUtil) {
		int x = 0;
		int pr = 0;
		boolean useFullNames = cimmyWheatUtil.isUseFullNameInPedigree();
		int tNstatVal1 = 0, tNstatIndx1 = 0;
		int tNstatVal2 = 0, tNstatIndx2 = 0;
		for (x = 1; x < cimmyWheatUtil.getNstatArray().length; x++) {
			if (cimmyWheatUtil.getNstatArray()[x] == 1) {
				tNstatIndx1 = x;
				tNstatVal1 = cimmyWheatUtil.getNstatWeightArray()[tNstatIndx1];
			}
			if (cimmyWheatUtil.getNstatArray()[x] == 2) {
				tNstatIndx2 = x;
				tNstatVal2 = cimmyWheatUtil.getNstatWeightArray()[tNstatIndx2];
			}
		}
		if (useFullNames) {
			if (tNstatIndx1 > tNstatIndx2) {
				cimmyWheatUtil.getNstatWeightArray()[tNstatIndx1] = tNstatVal2;
				cimmyWheatUtil.getNstatWeightArray()[tNstatIndx2] = tNstatVal1;
			}
		} else {
			if (tNstatIndx2 > tNstatIndx1) {
				cimmyWheatUtil.getNstatWeightArray()[tNstatIndx2] = tNstatVal1;
				cimmyWheatUtil.getNstatWeightArray()[tNstatIndx1] = tNstatVal2;
			}
		}

		for (x = 1; x < cimmyWheatUtil.getNtypeArray().length; x++) {
			if (cimmyWheatUtil.getNtypeArray()[x] == pNtypeX) {
				pr = cimmyWheatUtil.getNtypeWeightArray()[x];
				x = cimmyWheatUtil.getNtypeArray().length + 1;
			}
		}
		for (int y = 1; y < cimmyWheatUtil.getNstatArray().length; y++) {
			if (cimmyWheatUtil.getNstatArray()[y] == pNstatX) {
				pr += cimmyWheatUtil.getNstatWeightArray()[y];
				y = cimmyWheatUtil.getNtypeArray().length + 1;
			}
		}
		if (tNstatIndx1 != 0) {
			cimmyWheatUtil.getNstatWeightArray()[tNstatIndx1] = tNstatVal1;
		}
		if (tNstatIndx2 != 0) {
			cimmyWheatUtil.getNstatWeightArray()[tNstatIndx2] = tNstatVal2;
		}
		return pr;
	}

	public static String truncateCrossValueIfNeeded(final String crossValue, final int maxLength) {
		if (crossValue.length() > maxLength) {
			final StringBuilder truncatedValue = new StringBuilder(crossValue.substring(0, maxLength - 1));
			truncatedValue.append(CROSS_NAME_TRUNCATED_SUFFIX);
			return truncatedValue.toString();
		}
		return crossValue;
	}

}


package org.generationcp.middleware.util;

public class CimmytWheatNameUtil {

	private final String nstatOrderedList;
	private final String ntypeOrderedList;
	private final String nuidExcludeList;

	private int[] nstatWeightArray;
	private int[] ntypeWeightArray;
	private Integer[] ntypeArray;
	private Integer[] nstatArray;
	private Integer[] nuidArray;

	// base on propertie file
	private boolean levelZeroFullName = true;
	private boolean useFullNameInPedigree;

	// Some sort of weight calculations
	public CimmytWheatNameUtil() {
		this.nstatOrderedList = "2,1,0";
		this.ntypeOrderedList = "7,6,17,4,1200,13";
		this.nuidExcludeList = "20,47,66,76,84,90";
		this.levelZeroFullName = true;
		this.initializePreferredNameRules();
	}

	private void initializePreferredNameRules() {

		int iTemp = 0;
		// nstatOrderList
		String[] nstatArrayString = this.nstatOrderedList.split(",");
		this.nstatWeightArray = new int[nstatArrayString.length + 1];
		this.nstatArray = new Integer[nstatArrayString.length + 1];
		int calculito = -1;
		int concecutiv = 0;
		for (iTemp = nstatArrayString.length; iTemp > 0; iTemp--) {
			try {
				concecutiv += 1;
				this.nstatWeightArray[iTemp] = concecutiv * 100;
				this.nstatArray[iTemp] = Integer.valueOf(nstatArrayString[iTemp - 1]);
				if (this.nstatArray[iTemp] == 2 && calculito == -1) {
					this.useFullNameInPedigree = true;
					calculito = 0;
				}
				if (this.nstatArray[iTemp] == 1 && calculito == -1) {
					this.useFullNameInPedigree = false;
					calculito = 0;
				}
			} catch (Exception e) {
			}
		}
		// ntypeOrderList
		String[] ntypeArrayString = this.ntypeOrderedList.split(",");
		this.ntypeWeightArray = new int[ntypeArrayString.length + 1];
		this.ntypeArray = new Integer[ntypeArrayString.length + 1];
		int iTemp1 = 0;
		for (iTemp = ntypeArrayString.length; iTemp > 0; iTemp--) {
			try {
				iTemp1 += 1;
				this.ntypeWeightArray[iTemp] = iTemp1;
				this.ntypeArray[iTemp] = Integer.valueOf(ntypeArrayString[iTemp - 1]);
			} catch (Exception e) {
			}
		}
		// ntypeOrderList
		String[] nuidArrayString = this.nuidExcludeList.split(",");
		this.nuidArray = new Integer[nuidArrayString.length + 1];
		for (iTemp = nuidArrayString.length; iTemp > 0; iTemp--) {
			try {
				this.nuidArray[iTemp] = Integer.valueOf(nuidArrayString[iTemp - 1]);
			} catch (Exception e) {
			}
		}
		// levelZeroFullName
	}

	public Integer[] getNstatArray() {
		return this.nstatArray;
	}

	public void setNstatArray(Integer[] nstatArray) {
		this.nstatArray = nstatArray;
	}

	public int[] getNstatWeightArray() {
		return this.nstatWeightArray;
	}

	public void setNstatWeightArray(int[] nstatWeightArray) {
		this.nstatWeightArray = nstatWeightArray;
	}

	public int[] getNtypeWeightArray() {
		return this.ntypeWeightArray;
	}

	public void setNtypeWeightArray(int[] ntypeWeightArray) {
		this.ntypeWeightArray = ntypeWeightArray;
	}

	public Integer[] getNtypeArray() {
		return this.ntypeArray;
	}

	public void setNtypeArray(Integer[] ntypeArray) {
		this.ntypeArray = ntypeArray;
	}

	public boolean isUseFullNameInPedigree() {
		return this.useFullNameInPedigree;
	}

	public void setUseFullNameInPedigree(boolean useFullNameInPedigree) {
		this.useFullNameInPedigree = useFullNameInPedigree;
	}

	public boolean isLevelZeroFullName() {
		return this.levelZeroFullName;
	}

	public void setLevelZeroFullName(boolean levelZeroFullName) {
		this.levelZeroFullName = levelZeroFullName;
	}

	public Integer[] getNuidArray() {
		return this.nuidArray;
	}

	public void setNuidArray(Integer[] nuidArray) {
		this.nuidArray = nuidArray;
	}

}

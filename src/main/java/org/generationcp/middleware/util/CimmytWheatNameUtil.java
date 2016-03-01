
package org.generationcp.middleware.util;

/**
 * Utility class for helping determine the names used in wheat cross expansions for CIMMYT
 *
 *
 * It is set up such that some properties could be configurable (possibly via Spring), but it is not currently set up that way
 */
public class CimmytWheatNameUtil {

    public static final String DEFAULT_NSTAT_ORDER_PRIORITY = "2,1,0";
    public static final String DEFAULT_NTYPE_ORDER_PRIORITY = "7,6,17,4,1200,13";
    public static final String DEFAULT_PROVIDED_NAME_UID_RESTRICTION_LIST = "20,47,66,76,84,90";
    private final String nstatOrderedList;
	private final String ntypeOrderedList;
	private String nuidExcludeList;

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
		this.nstatOrderedList = DEFAULT_NSTAT_ORDER_PRIORITY;
		this.ntypeOrderedList = DEFAULT_NTYPE_ORDER_PRIORITY;
		this.levelZeroFullName = true;
		this.initializePreferredNameRules();
	}

	private void initializePreferredNameRules() {

		int iTemp = 0;
		// nstatOrderList
		final String[] nstatArrayString = this.nstatOrderedList.split(",");
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
			} catch (final Exception e) {
			}
		}
		// ntypeOrderList
		final String[] ntypeArrayString = this.ntypeOrderedList.split(",");
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


	}

    /**
     * Basically initializes a 1-indexed array based on the provided comma delimited name exclusion list string
     */
    public void initializeRestrictedNameUIDArray() {
        // ntypeOrderList
        String[] nuidArrayString = this.nuidExcludeList.split(",");
        this.nuidArray = new Integer[nuidArrayString.length + 1];
        for (int iTemp = nuidArrayString.length; iTemp > 0; iTemp--) {
            try {
                this.nuidArray[iTemp] = Integer.valueOf(nuidArrayString[iTemp - 1]);
            } catch (Exception e) {
            }
        }
    }

    public Integer[] getNstatArray() {
		return this.nstatArray;
	}

	public void setNstatArray(final Integer[] nstatArray) {
		this.nstatArray = nstatArray;
	}

	public int[] getNstatWeightArray() {
		return this.nstatWeightArray;
	}

	public void setNstatWeightArray(final int[] nstatWeightArray) {
		this.nstatWeightArray = nstatWeightArray;
	}

	public int[] getNtypeWeightArray() {
		return this.ntypeWeightArray;
	}

	public void setNtypeWeightArray(final int[] ntypeWeightArray) {
		this.ntypeWeightArray = ntypeWeightArray;
	}

	public Integer[] getNtypeArray() {
		return this.ntypeArray;
	}

	public void setNtypeArray(final Integer[] ntypeArray) {
		this.ntypeArray = ntypeArray;
	}

	public boolean isUseFullNameInPedigree() {
		return this.useFullNameInPedigree;
	}

	public void setUseFullNameInPedigree(final boolean useFullNameInPedigree) {
		this.useFullNameInPedigree = useFullNameInPedigree;
	}

	public boolean isLevelZeroFullName() {
		return this.levelZeroFullName;
	}

	public void setLevelZeroFullName(final boolean levelZeroFullName) {
		this.levelZeroFullName = levelZeroFullName;
	}

	public Integer[] getNuidArray() {
		return this.nuidArray;
	}

	public void setNuidArray(final Integer[] nuidArray) {
		this.nuidArray = nuidArray;
	}

    public void setNuidExcludeList(String nuidExcludeList) {
        this.nuidExcludeList = nuidExcludeList;
    }
}

package org.generationcp.middleware.util;

public class CimmytWheatNameUtil {
	
	private String nstatOrderedList;
	private String ntypeOrderedList;
	private String nuidExcludeList;
	    
	private int[] nstatWeightArray;
	private int[] ntypeWeightArray;
	private Integer[] ntypeArray;
	private Integer[] nstatArray;
	private Integer[] nuidArray;
	
	//base on propertie file
	private boolean levelZeroFullName = true; 
	private boolean useFullNameInPedigree;
	
	public CimmytWheatNameUtil(){
		 nstatOrderedList = "2,1,0";
	     ntypeOrderedList = "2,5,7,6,17,4,1200,13";
	     nuidExcludeList = "20,47,66,76,84,90";
	     levelZeroFullName = true;
	     initializePreferredNameRules();
	}
    
	public void initializePreferredNameRules() {
    	
        
        int iTemp = 0;
        //nstatOrderList
        String[] nstatArrayString = nstatOrderedList.split(",");
        nstatWeightArray = new int[nstatArrayString.length + 1];
        nstatArray = new Integer[nstatArrayString.length + 1];
        int calculito = -1;
        int concecutiv = 0;
        for (iTemp = nstatArrayString.length; iTemp > 0; iTemp--) {
            try {
                concecutiv += 1;
                nstatWeightArray[iTemp] = concecutiv * 100;
                nstatArray[iTemp] = Integer.valueOf(nstatArrayString[iTemp - 1]);
                if (nstatArray[iTemp] == 2 && calculito == -1) {
                	useFullNameInPedigree = (true);
                    calculito = 0;
                }
                if (nstatArray[iTemp] == 1 && calculito == -1) {
                	useFullNameInPedigree = (false);
                    calculito = 0;
                }
            } catch (Exception e) {
            }
        }
        //ntypeOrderList
        String[] ntypeArrayString = ntypeOrderedList.split(",");
        ntypeWeightArray = new int[ntypeArrayString.length + 1];
        ntypeArray = new Integer[ntypeArrayString.length + 1];
        int iTemp1 = 0;
        for (iTemp = ntypeArrayString.length; iTemp > 0; iTemp--) {
            try {
                iTemp1 += 1;
                ntypeWeightArray[iTemp] = iTemp1;
                ntypeArray[iTemp] = Integer.valueOf(ntypeArrayString[iTemp - 1]);
            } catch (Exception e) {
            }
        }
        //ntypeOrderList
        String[] nuidArrayString = nuidExcludeList.split(",");
        nuidArray = new Integer[nuidArrayString.length + 1];
        for (iTemp = nuidArrayString.length; iTemp > 0; iTemp--) {
            try {
                nuidArray[iTemp] = Integer.valueOf(nuidArrayString[iTemp - 1]);
            } catch (Exception e) {
            }
        }
        //levelZeroFullName
    }

	public Integer[] getNstatArray() {
		return nstatArray;
	}

	public void setNstatArray(Integer[] nstatArray) {
		this.nstatArray = nstatArray;
	}

	public int[] getNstatWeightArray() {
		return nstatWeightArray;
	}

	public void setNstatWeightArray(int[] nstatWeightArray) {
		this.nstatWeightArray = nstatWeightArray;
	}

	public int[] getNtypeWeightArray() {
		return ntypeWeightArray;
	}

	public void setNtypeWeightArray(int[] ntypeWeightArray) {
		this.ntypeWeightArray = ntypeWeightArray;
	}

	public Integer[] getNtypeArray() {
		return ntypeArray;
	}

	public void setNtypeArray(Integer[] ntypeArray) {
		this.ntypeArray = ntypeArray;
	}

	public boolean isUseFullNameInPedigree() {
		return useFullNameInPedigree;
	}

	public void setUseFullNameInPedigree(boolean useFullNameInPedigree) {
		this.useFullNameInPedigree = useFullNameInPedigree;
	}

	public boolean isLevelZeroFullName() {
		return levelZeroFullName;
	}

	public void setLevelZeroFullName(boolean levelZeroFullName) {
		this.levelZeroFullName = levelZeroFullName;
	}

	public Integer[] getNuidArray() {
		return nuidArray;
	}

	public void setNuidArray(Integer[] nuidArray) {
		this.nuidArray = nuidArray;
	}	
	
}

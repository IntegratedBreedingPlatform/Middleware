package org.generationcp.middleware.util;

import org.apache.commons.lang.math.NumberUtils;

public class CrossExpansionUtil {
	public static void genCrossStr(StringBuilder crossStr,String str1, String str2,int level)
    {
       int s1,s2,s3,s4;
       s1 = maxSepStr(str1);
       s2 = maxSepStr(str2);
       s3 = Math.max(s1,s2);
       s4 = s3+1;
       if (str1.length()==0)
          crossStr = new StringBuilder(str2);
       else if (str2.length()==0)
          crossStr = new StringBuilder(str1);
       else
       {
          String sep = sepStr(s4);
          crossStr = new StringBuilder(str1+sep+str2);
       }
    }
    
    public static String sepStr(int level)
    {
       String crossStr = "";
       if (level==0){
          crossStr= ("");
       }
       else if(level==1){
          crossStr= ("/");
       }
       else if(level==2){
          crossStr= ("//");
       }
       else if(level==3){
          crossStr= ("///");
       }
       else{
          crossStr = ("/"+level+"/");
       }
       return crossStr;
    }
    
    public static int maxSepStr(String str)
    {
       int i=0,j,n,imax=0;

       i=0;
       imax = 0;
       n = str.length();//str.GetLength();
       while (i<n) {
          while (i<n && str.charAt(i)!='/') {
        	  i++;
          }
          if (i==n) {
        	  break;
          }
          if (!"///".equalsIgnoreCase(str.substring(i,3))) {
             imax = Math.max(imax,3);
             i +=3;
          } else if (!"//".equalsIgnoreCase(str.substring(i,2))) {
             imax = Math.max(imax,2);
             i +=2;
          } else if (str.charAt(i)=='/') {
             imax=Math.max(imax,1);
             if (i+1<n && NumberUtils.isDigits(String.valueOf(str.charAt(i+1))))
             {
                j = i+2;
                while (j<n &&  NumberUtils.isDigits(String.valueOf(str.charAt(j)))){
                	j++;
                }
                if (j==n){
                	break;
                }
                if (str.charAt(j)=='/'){
                   imax = Math.max(imax,Integer.parseInt(str.substring(i+1,j-i-1)));
                }
                i = j+1;
             } else {
                i +=1;
             }
          }
       }
       return imax;
    }
    
    public static String GetNewDelimiter(String xPed) throws Exception {
        String GetNewDelimiter = "";
        int x = 0;
        int MaxLevel = 0;
        String cad = "";
        for (x = 32; x >= 1; x--) {
            cad = "/" + String.valueOf(x) + "/";
            if (x == 2) {
                cad = "//";
            }
            if (x == 1) {
                cad = "/";
            }
            if (xPed.contains(cad)) {
                MaxLevel = x;
                x = 0;
            }
        }
        // New level must be one higher than current one found
        MaxLevel = MaxLevel + 1;
        GetNewDelimiter = "/" + String.valueOf(MaxLevel) + "/";
        if (MaxLevel == 1) {
            GetNewDelimiter = "/";
        }
        if (MaxLevel == 2) {
            GetNewDelimiter = "//";
        }
        return GetNewDelimiter;
    }
    
    public static void GetParentsDoubleRetroCrossNew(String[] Mxp1, String[] Mxp2) { //24 agosto 2012, Medificaciones por Jesper
        // Resolving situation of GID=29367 CID=22793 and GID=29456 CID=22881
        // Female : CMH75A.66/2*CNO79  Male CMH75A.66/3*CNO79  Result: CMH75A.66/2*CNO79*2//CNO79
        // Solution:
        // p1 : CMH75A.66/2*CNO79  p2: CMH75A.66/2*CNO79//CNO79
        // JEN  2012-07-16
        String xp1 = Mxp1[0];
        String xp2 = Mxp2[0];
        String BeforeStr = "";
        String AfterStr = "";
        String Delimiter = "";
        String CutStr = "";
        String CleanBefore = "";
        String CleanAfter = "";
        int x = 0;
        int y = 0;
        int xx = 0;
        int Lev1 = 0;
        int Lev2 = 0;
        String xDel = "";
        String A = "";
        String B = "";
        // Default is true - 50% chance it is right
        boolean SlashLeft = true;  //  /2*A is SlashLeft, and A*2/ is not SlashLeft
        boolean Changed = false;   //
        for (x = 1; x <= xp1.length(); x++) {
            if (xp1.substring(x - 1, x).equals(xp2.substring(x - 1, x))) {
                BeforeStr = BeforeStr + xp1.substring(x - 1, x);
            } else {
                Lev1 = 0;
                xx = BeforeStr.length() + 1;
                xDel = BeforeStr.substring(BeforeStr.length() - 1, BeforeStr.length());
                if (BeforeStr.length() > 1) {
                    if (NumberUtils.isNumber(BeforeStr.substring(BeforeStr.length() - 1, BeforeStr.length())) && BeforeStr.endsWith("*")) {
                        Lev1 = Integer.valueOf(BeforeStr.substring(BeforeStr.length() - 1, BeforeStr.length()));
                        xDel = "*";
                    }
                }
                if (xp1.substring(xx - 1, xx).equals("*")) {
                    xDel = "*";
                    xx = xx + 1;
                }
                if (xDel.equals("*") || xDel.equals("/")) {
                    for (y = xx; y < xp1.length(); y++) {
                        if (!NumberUtils.isNumber(xp1.substring(y - 1, y))) {
                            // Exit for
                            break;
                        }
                        Lev1 = (Lev1 * 10) + Integer.valueOf(xp1.substring(y - 1, y));
                        //  Take off leading offset in AfterStr if that has now been put in Lev2
                        if ((xp1.length() - y) < AfterStr.length()) {
                                AfterStr = AfterStr.substring(1, AfterStr.length());
                        }
                    }
                    if (Lev1 == 0) {
                        Lev1 = 1;
                    }
                }
                // Exit for
                break;
            }
        }
        AfterStr = "";
        if (xp1.length() < xp2.length()) {
            CutStr = xp2.substring(xp2.length() - xp1.length());
        } else {
            CutStr = String.format("%" + xp1.length() + "s", xp2);
        }
        for (x = xp1.length(); x > 0; x--) {
            if (xp1.substring(x - 1, x).equals(CutStr.substring(x - 1, x))) {
                AfterStr = xp1.substring(x - 1, x) + AfterStr;
            } else {
                Lev2 = 0;
                xx = BeforeStr.length() + 1;
                if (BeforeStr.length() > 1) {
                    // This criteria has problems 2012-07-17
                    if (NumberUtils.isNumber(BeforeStr.substring(BeforeStr.length() - 1, BeforeStr.length())) && BeforeStr.endsWith("*")) {
                        Lev2 = Integer.valueOf(BeforeStr.substring(BeforeStr.length() - 1, BeforeStr.length()));
                        xDel = "*";
                    }
                }
                if (xp2.substring(BeforeStr.length(), BeforeStr.length() + 1).equals("*")) {
                    xDel = "*";
                    xx = BeforeStr.length() + 2;
                }
                if (xDel.equals("*") || xDel.equals("/")) {
                    for (y = xx; y < xp2.length(); y++) {
                        // This criteria has problems 2012-07-17
                        if (!NumberUtils.isNumber(xp2.substring(y - 1, y))) {
                            // Exit for
                            break;
                        }
                        Lev2 = (Lev2 * 10) + Integer.valueOf(xp2.substring(y - 1, y));
                        //  Take off leading offset in AfterStr if that has now been put in Lev2
                        if ((xp2.length() - y) < AfterStr.length()) {
                            AfterStr = AfterStr.substring(1, AfterStr.length());
                        }
                    }
                }
                if (Lev2 == 0) {
                    Lev2 = 1;
                    Changed = true;
                    while ((AfterStr.length() > 2) && Changed) {
                        Changed = false;
                        A = AfterStr.substring(0, 1);
                        B = AfterStr.substring(1, 2);
                        if ((A.equals("/") || (NumberUtils.isNumber(A))) && (B.equals("/") || (NumberUtils.isNumber(B)))) {
                            AfterStr = AfterStr.substring(1, AfterStr.length());
                            Changed = true;
                        }
                    }
                }
                // Exit for
                break;
            }
        }
        //if (AfterStr.substring(0,1).equals("/")) {
        if (AfterStr.startsWith("/")) {
            SlashLeft = false;
        }
        CleanAfter = AfterStr;
        CleanBefore = BeforeStr;
        // Fixing CleanAfter
        // if (CleanAfter.substring(0,1).equals("*") || CleanAfter.substring(0,1).equals("/")) {
        if (CleanAfter.startsWith("*") || CleanAfter.startsWith("/")) {
            CleanAfter = CleanAfter.substring(1, CleanAfter.length());
        }
        // Fixing CleanBefore
        if (CleanBefore.length() > 1) {
            // This criteria has problems 2012-07-19
            if (NumberUtils.isNumber(CleanBefore.substring(CleanBefore.length() - 1, CleanBefore.length())) && ((CleanBefore.substring(CleanBefore.length() - 2, CleanBefore.length() - 1).equals("/")) || (CleanBefore.substring(CleanBefore.length() - 1, CleanBefore.length()).equals("*")))) {
                CleanBefore = CleanBefore.substring(0, CleanBefore.length() - 2);
            }
        }
        try {
            Delimiter = CrossExpansionUtil.GetNewDelimiter(xp1 + xp2);
        } catch (Exception e) {
            System.out.println("Error en GetNewDelimiter " + e);
        }
        if (Lev1 > Lev2) {
            if (SlashLeft) {
                xp1 = xp2 + Delimiter + CleanAfter;
            } else {
                xp1 = CleanBefore + Delimiter + xp2;
            }
        }
        if (Lev2 > Lev1) {
            if (SlashLeft) {
                xp2 = xp1 + Delimiter + CleanAfter;
            } else {
                xp2 = CleanBefore + Delimiter + xp1;
            }
        }
        Mxp1[0] = xp1;
        Mxp2[0] = xp2;
    }
    
    
    public static int giveNameValue(int p_ntypeX, int p_nstatX, CimmytWheatNameUtil cimmyWheatUtil) {
        int x = 0;
        int pr = 0;
        boolean useFullNames = cimmyWheatUtil.isUseFullNameInPedigree();
        int t_nstatVal1 = 0, t_nstatIndx1 = 0;
        int t_nstatVal2 = 0, t_nstatIndx2 = 0;
        for (x = 1; x < cimmyWheatUtil.getNstatArray().length; x++) {
            if (cimmyWheatUtil.getNstatArray()[x] == 1) {
                t_nstatIndx1 = x;
                t_nstatVal1 = cimmyWheatUtil.getNstatWeightArray()[t_nstatIndx1];
            }
            if (cimmyWheatUtil.getNstatArray()[x] == 2) {
                t_nstatIndx2 = x;
                t_nstatVal2 =  cimmyWheatUtil.getNstatWeightArray()[t_nstatIndx2];
            }
        }
        if (useFullNames) {
            if (t_nstatIndx1 > t_nstatIndx2) {
            	cimmyWheatUtil.getNstatWeightArray()[t_nstatIndx1] = t_nstatVal2;
            	cimmyWheatUtil.getNstatWeightArray()[t_nstatIndx2] = t_nstatVal1;
            }
        } else {
            if (t_nstatIndx2 > t_nstatIndx1) {
            	cimmyWheatUtil.getNstatWeightArray()[t_nstatIndx2] = t_nstatVal1;
            	cimmyWheatUtil.getNstatWeightArray()[t_nstatIndx1] = t_nstatVal2;
            }
        }/*
         * if (useFullNames) { if (t_nstatIndx1 >t_nstatIndx2) {
         * nstatArray[t_nstatIndx1] = t_nstatIndx2; nstatArray[t_nstatIndx2] =
         * t_nstatIndx1; nstatWeightArray[t_nstatIndx1] = t_nstatVal2;
         * nstatWeightArray[t_nstatIndx2] = t_nstatVal1; } } else { if
         * (t_nstatIndx2 >t_nstatIndx1) { nstatArray[t_nstatIndx1] =
         * t_nstatIndx2; nstatArray[t_nstatIndx2] = t_nstatIndx1;
         * nstatWeightArray[t_nstatIndx2] = t_nstatVal1;
         * nstatWeightArray[t_nstatIndx1] = t_nstatVal2; } }
         */
        for (x = 1; x < cimmyWheatUtil.getNtypeArray().length; x++) {
            if (cimmyWheatUtil.getNtypeArray()[x] == p_ntypeX) {
                pr = cimmyWheatUtil.getNtypeWeightArray()[x];
                x = cimmyWheatUtil.getNtypeArray().length + 1;
            }
        }
        for (int y = 1; y < cimmyWheatUtil.getNstatArray().length; y++) {
            if ( cimmyWheatUtil.getNstatArray()[y] == p_nstatX) {
                pr += cimmyWheatUtil.getNstatWeightArray()[y];
                y = cimmyWheatUtil.getNtypeArray().length + 1;
            }
        }
        if (t_nstatIndx1 != 0) {
        	 cimmyWheatUtil.getNstatWeightArray()[t_nstatIndx1] = t_nstatVal1;
        }
        if (t_nstatIndx2 != 0) {
        	cimmyWheatUtil.getNstatWeightArray()[t_nstatIndx2] = t_nstatVal2;
        }
        return pr;
    }
}

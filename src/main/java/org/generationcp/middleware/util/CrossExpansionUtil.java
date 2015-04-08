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
}

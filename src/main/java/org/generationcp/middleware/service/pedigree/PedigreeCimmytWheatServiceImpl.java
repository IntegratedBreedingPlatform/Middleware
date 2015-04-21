package org.generationcp.middleware.service.pedigree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.FieldbookServiceImpl;
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CimmytWheatNameUtil;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.CrossExpansionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PedigreeCimmytWheatServiceImpl extends Service implements PedigreeService{
	private static final Logger LOG = LoggerFactory.getLogger(FieldbookServiceImpl.class);
	private CimmytWheatNameUtil cimmytWheatNameUtil;
	private static final int NSTAT_DELETED = 9;
	
    public PedigreeCimmytWheatServiceImpl() {
        super();
        cimmytWheatNameUtil = new CimmytWheatNameUtil();
    }
    public PedigreeCimmytWheatServiceImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
        cimmytWheatNameUtil = new CimmytWheatNameUtil();
    }
    public PedigreeCimmytWheatServiceImpl(HibernateSessionProvider sessionProvider, String localDatabaseName) {
        super(sessionProvider, localDatabaseName);
        cimmytWheatNameUtil = new CimmytWheatNameUtil();
    }
	
    @Override
    public String getCrossExpansion(Integer gid, CrossExpansionProperties crossExpansionProperties) throws MiddlewareQueryException {
        Germplasm germplasm =  getGermplasmDataManager().getGermplasmWithPrefName(gid);
        if (germplasm != null) {
    		try {
				return getCimmytWheatPedigree(gid, crossExpansionProperties.getWheatLevel(), new Germplasm(), 0, 0, 0, 0, 0);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				throw new MiddlewareQueryException(e.getMessage(), e);
			}
        } else {
            return "";
        }
    }      
    
	private List<Name> getCimmytWheatWayNamesList(int gid,List<Integer> ntypeArray, List<Integer> nstatArray, List<Integer> nuiArray) throws MiddlewareQueryException{		
		
		List<Name> nameList  = getNameDao().getByGIDWithFilters(gid, null, null);
		List<Name> returnNameList = new ArrayList<Name>();
		for(Name name : nameList){						
			if (name.getNstat() != NSTAT_DELETED && 
					ntypeArray.contains(name.getTypeId()) 
            		&& nstatArray.contains(name.getNstat()) 
            		&& !nuiArray.contains(name.getUserId())) {                
                	returnNameList.add(name);                
            }
		}
		return returnNameList;
	}

	/**
     * Recursive procedure to generate the pedigree
     *
     * @param pGid Input GID
	 * @param level default zero
	 * @param parentGermplasmClass empty class
	 * @param femaleBackcrossGid default zero
	 * @param maleBackcrossGid default zero
	 * @param Resp1 default zero
	 * @param Resp2 default zero
     * @return
     * @throws Exception
     */
    public String getCimmytWheatPedigree(int pGid, int level, Germplasm parentGermplasmClass, int femaleBackcrossGid, int maleBackcrossGid, int Resp1, int Resp2, int ntype) throws Exception {
        
        LOG.debug("Armando pedigree con [p_gid] " + pGid + " [nivel] " + level + " [fback] " + femaleBackcrossGid + " [mback] " + maleBackcrossGid + " [Resp1] " + Resp1 + " [Resp2] " + Resp2);
        int xCurrent = 0;
        int xMax     = 0;        
        String armaPedigree = "";
        String ped           = "";
        String xPrefName     = "";
        String delimiter     = "";
        String cut           = "";
        String p1            = "";
        String p2            = "";
        String[] oldp1 = new String[1];
        String[] oldp2 = new String[1];
        Germplasm fGpidInfClass = new Germplasm();
        Germplasm mGpidInfClass = new Germplasm();
        fGpidInfClass.setGpid1(0);
        fGpidInfClass.setGpid2(0);
        mGpidInfClass.setGpid1(0);
        mGpidInfClass.setGpid2(0);
        
        String longStr;
        String shortStr;
        boolean backCrossFound = false;
        Germplasm grTemp = new Germplasm();
        grTemp.setGid(0);
        grTemp.setGpid1(0);
        grTemp.setGpid2(0);
        List<Name> listNL = null;
        int vecesRep = 0;
        
        
        try {
            Germplasm temp = getGermplasmDataManager().getGermplasmByGID(pGid);
            if(temp != null){
            	grTemp = temp;
            }
            if (grTemp.getGid() == 0) {
                return "";
            }
            listNL = getCimmytWheatWayNamesList(grTemp.getGid(), Arrays.asList(cimmytWheatNameUtil.getNtypeArray()), Arrays.asList(cimmytWheatNameUtil.getNstatArray()), Arrays.asList(cimmytWheatNameUtil.getNuidArray()));
            //since it should not be dependent on the status anymore
            // Determine if there was a Female or Male Backcross, that should be used in the pedigree, then the proper name of the
            // line can't be used. For instance if the name is 27217-A-4-8 and the pedigree is also MT/BRT and we are
            // called with mback representing BRT, then we must retrieve MT/BRT instead of 27217-A-4-8 to be able to reproduce
            // the name MT/2*BRT as the final pedigree. See example with GID=1935, before it generated 27217-A-4-8/BRT, now it
            // generates MT/2*BRT as expected.
            backCrossFound = false;
            if ((grTemp.getGnpgs() == 2) && (grTemp.getGpid1() == femaleBackcrossGid) && (femaleBackcrossGid != 0)) {
                backCrossFound = true;
            }
            if ((grTemp.getGnpgs() == 2) && (grTemp.getGpid2() == maleBackcrossGid) && (maleBackcrossGid != 0)) {
                backCrossFound = true;
            }
            //' Also handle the CIMMYT retrocrosses A/B//A is A*2/B and B//A/B is A/2*B  JEN 2012-02-17
            if (grTemp.getGnpgs() == 2 && grTemp.getGpid2() == femaleBackcrossGid && femaleBackcrossGid != 0) {
                backCrossFound = true;
            }
            if (grTemp.getGnpgs() == 2 && grTemp.getGpid1() == maleBackcrossGid && maleBackcrossGid != 0) {
                backCrossFound = true;
            }
        } catch (Exception e) {
        	LOG.error(e.getMessage(), e);
        }
        
        if ((!listNL.isEmpty()) && (grTemp.getGnpgs() == -1)) {
            // Name found, but we can only use it if not a backcross
        	Germplasm rsBack = new Germplasm();
            if (grTemp.getGpid1() != null && grTemp.getGpid1() != 0) {
                Germplasm temp1 = getGermplasmDataManager().getGermplasmByGID(grTemp.getGpid1());
                if(temp1 != null){
                	rsBack = temp1;
                }
                
                if (rsBack.getGid() == null) {
                    if (femaleBackcrossGid != 0 && (femaleBackcrossGid == rsBack.getGpid1())) {
                        backCrossFound = true;
                    }
                    if (maleBackcrossGid != 0 && (maleBackcrossGid == rsBack.getGpid2())) {
                        backCrossFound = true;
                    }
                    // New artificial backcrosses implemented 2 If's  JEN - 2012-02-13
                    if (femaleBackcrossGid != 0 && femaleBackcrossGid == rsBack.getGpid2()) {
                        backCrossFound = true;
                    }
                    if (maleBackcrossGid != 0 && maleBackcrossGid == rsBack.getGpid1()) {
                        backCrossFound = true;
                    }
                }
            }
        }
        // If the current GID is identical to one of the backcross GIDs that must be respected,
        // cancel it as a backross and find a proper name
        if (Resp1 == pGid || Resp2 == pGid) {
            backCrossFound = false;
        }
        if ((!listNL.isEmpty()) && !(backCrossFound)) {        	
            for (Name namesrecord : listNL) {
                xCurrent = CrossExpansionUtil.giveNameValue(namesrecord.getTypeId(), namesrecord.getNstat(), cimmytWheatNameUtil);
                //Apply check if the LevelZeroFullName is true or not
                //If that is the case and we are at level zero, add 200 to xCurrent if NSTAT=1
                if ((level == 0) && cimmytWheatNameUtil.isLevelZeroFullName() && (namesrecord.getNstat() == 1)) {
                    xCurrent = xCurrent + 200;
                }
                if (xCurrent > xMax) {
                    xPrefName = namesrecord.getNval();
                    xMax = xCurrent;
                }
            }
            ped = xPrefName;
            
        } else {
            if ((grTemp.getGpid1() == 0) && (grTemp.getGpid2() == 0)) {
                ped = "Unknown";
            } else {
                if ((grTemp.getGnpgs() == -1) && (grTemp.getGpid2() != 0)) {
                    ped = getCimmytWheatPedigree(grTemp.getGpid2(), level, parentGermplasmClass, femaleBackcrossGid, maleBackcrossGid, Resp1, Resp2, ntype);
                } else if ((grTemp.getGnpgs() == -1) && (grTemp.getGpid1() != 0)) {
                    ped = getCimmytWheatPedigree(grTemp.getGpid1(), level, parentGermplasmClass, femaleBackcrossGid, maleBackcrossGid, Resp1, Resp2, ntype);
                } else {
                    parentGermplasmClass.setGpid1(grTemp.getGpid1());
                    parentGermplasmClass.setGpid2(grTemp.getGpid2());
                    if (grTemp.getGpid1() == femaleBackcrossGid) {
                        p1 = getCimmytWheatPedigree(grTemp.getGpid1(), level + 1, fGpidInfClass, 0, 0, Resp1, Resp2, ntype);
                    } else {
                        p1 = getCimmytWheatPedigree(grTemp.getGpid1(), level + 1, fGpidInfClass, 0, grTemp.getGpid2(), Resp1, Resp2, ntype);
                    }
                    if (grTemp.getGpid2() == maleBackcrossGid) {
                        p2 = getCimmytWheatPedigree(grTemp.getGpid2(), level + 1, mGpidInfClass, 0, 0, Resp1, Resp2, ntype);
                    } else {
                        p2 = getCimmytWheatPedigree(grTemp.getGpid2(), level + 1, mGpidInfClass, grTemp.getGpid1(), 0, Resp1, Resp2, ntype);
                    }
                }
//         ' Since female/male backcross is a bit meaningless when IWIS2 false backrosses are handled, then
//         ' we just detect which part could be handled by length of p1 and p2, and if they are contained in
//         ' first part or last part of the other
                if (grTemp.getGpid1().intValue() == mGpidInfClass.getGpid1().intValue() || grTemp.getGpid2().intValue() == fGpidInfClass.getGpid1().intValue()  ||
                		grTemp.getGpid2().intValue() == fGpidInfClass.getGpid2().intValue()  || grTemp.getGpid1().intValue() == mGpidInfClass.getGpid2().intValue() ){
                	//Handle Backcross                
                    vecesRep = 2;
                    if ((!"".equals(p1)) && (!"".equals(p2))) {
                        if ((!p1.contains(p2)) && (!p2.contains(p1))) {
                            ped = "Houston we have a problem";
                            oldp1[0] = p1;
                            oldp2[0] = p2;
                            fGpidInfClass.setGpid1(0);
                            fGpidInfClass.setGpid2(0);
                            mGpidInfClass.setGpid1(0);
                            mGpidInfClass.setGpid2(0);
                            p1 = getCimmytWheatPedigree(grTemp.getGpid1(), level + 1, fGpidInfClass, 0, 0, grTemp.getGpid1(), grTemp.getGpid2(), ntype);
                            p2 = getCimmytWheatPedigree(grTemp.getGpid2(), level + 1, mGpidInfClass, 0, 0, grTemp.getGpid1(), grTemp.getGpid2(), ntype);
                            if ((!p1.contains(p2)) && (!p2.contains(p1))) {
                                ped = "Houston we have a BIG problem";
                                // Resolving situation of GID=29367 CID=22793 and GID=29456 CID=22881
                                // Female : CMH75A.66/2*CNO79  Male CMH75A.66/3*CNO79  Result: CMH75A.66/2*CNO79*2//CNO79
                                // Solution: convert p1 and p2 to the following
                                // p1 : CMH75A.66/2*CNO79  p2: CMH75A.66/2*CNO79//CNO79
                                //
                                // A valid way to pass parameters by reference according to http://www.cs.utoronto.ca/~dianeh/tutorials/params/swap.html
                               
                                CrossExpansionUtil.getParentsDoubleRetroCrossNew(oldp1, oldp2);
                                p1 = oldp1[0];
                                p2 = oldp2[0];
                            }
                        }
                        if (p1.length() > p2.length()) {
                            longStr = p1;
                            shortStr = p2;
                        } else {
                            longStr = p2;
                            shortStr = p1;
                        }
                        if (longStr.substring(0, shortStr.length()).equals(shortStr)) {
                            //' Handle female type of backcross
                            cut = longStr.substring(shortStr.length());
                            if (cut.startsWith("/")) {
                            	//does not do anything for now
                            } else if (cut.startsWith("*")) {
                                if ("/".equals(cut.substring(2, 3))) {
                                    vecesRep = Integer.valueOf(cut.substring(1, 2));
                                    cut = cut.substring(2);
                                } else {
                                    vecesRep = Integer.valueOf(cut.substring(1, 3));
                                    cut = cut.substring(3);
                                }
                                vecesRep = vecesRep + 1;
                            }
                            ped = shortStr + "*" + vecesRep + cut;
                        }
                        if (longStr.substring(longStr.length() - shortStr.length()).equals(shortStr)) {
                            //' Handle male type of backcross
                            cut = longStr.substring(0, longStr.length() - shortStr.length());
                            if (cut.endsWith("/")) {
                            	//does not do anything for now
                            } else if (cut.endsWith("*")) {
                                if (cut.substring(cut.length() - 3, cut.length() - 2).equals("/")) {
                                    vecesRep = Integer.valueOf(cut.substring(cut.length() - 2, cut.length() - 1));
                                    cut = cut.substring(0, cut.length() - 2);
                                } else {
                                    vecesRep = Integer.valueOf(cut.substring(cut.length() - 3, cut.length() - 1));
                                    cut = cut.substring(0, cut.length() - 3);
                                }
                                vecesRep = vecesRep + 1;
                            }
                            ped = cut + vecesRep + "*" + shortStr;
                        }
                    }
                }
                if ((grTemp.getGpid1().intValue() != mGpidInfClass.getGpid1().intValue()) && (grTemp.getGpid1().intValue() != fGpidInfClass.getGpid1().intValue()) 
                		&& (grTemp.getGpid2().intValue() != fGpidInfClass.getGpid2().intValue()) && (grTemp.getGpid2().intValue() != fGpidInfClass.getGpid2().intValue()) && (grTemp.getGpid2().intValue() != mGpidInfClass.getGpid2().intValue())
            			&& (((!"".equals(p1)) || (!"".equals(p2))) && ("".equals(ped))) ) {                    
                        if ("".equals(p1)) {
                            p1 = "Missing";
                        }
                        if ("".equals(p2)) {
                            p2 = "Missing";
                        }
                        delimiter = CrossExpansionUtil.getNewDelimiter(p1 + p2).toString();
                        ped = p1 + delimiter + p2;                    
                }
            }
        }
        armaPedigree = ped;
        return armaPedigree;
    }       
}

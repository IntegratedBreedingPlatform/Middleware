package org.generationcp.middleware.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.germplasm.BackcrossElement;
import org.generationcp.middleware.pojos.germplasm.GermplasmCross;
import org.generationcp.middleware.pojos.germplasm.GermplasmCrossElement;
import org.generationcp.middleware.pojos.germplasm.SingleGermplasmCrossElement;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CimmytWheatNameUtil;
import org.generationcp.middleware.util.CrossExpansionRule;
import org.generationcp.middleware.util.CrossExpansionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PedigreeServiceImpl extends Service implements PedigreeService{
	private static final Logger LOG = LoggerFactory.getLogger(FieldbookServiceImpl.class);
	private CimmytWheatNameUtil cimmytWheatNameUtil;
	private static final int NSTAT_DELETED = 9;
	
    public PedigreeServiceImpl() {
        super();
        cimmytWheatNameUtil = new CimmytWheatNameUtil();
    }
    public PedigreeServiceImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
        cimmytWheatNameUtil = new CimmytWheatNameUtil();
    }
    public PedigreeServiceImpl(HibernateSessionProvider sessionProvider, String localDatabaseName) {
        super(sessionProvider, localDatabaseName);
        cimmytWheatNameUtil = new CimmytWheatNameUtil();
    }

	
	@Deprecated
    @Override
    public String getCrossExpansion(Integer gid, int level) throws MiddlewareQueryException {
        Germplasm germplasm = getGermplasmDataManager().getGermplasmWithPrefName(gid);
        if (germplasm != null) {
            SingleGermplasmCrossElement startElement = new SingleGermplasmCrossElement();
            startElement.setGermplasm(germplasm);
            GermplasmCrossElement cross = expandGermplasmCross(startElement, 1, false);
            return cross.toString();
        } else {
            return null;
        }
    }
    
    @Override
    public String getCrossExpansion(Integer gid, CrossExpansionRule crossExpansionRule) throws MiddlewareQueryException {
        Germplasm germplasm =  getGermplasmDataManager().getGermplasmWithPrefName(gid);
        if (germplasm != null) {
        	if(crossExpansionRule.isCimmytWheat()){
        		return getCrossExpansionCimmytWheat(gid, 0, 0);
        	}else{
	            SingleGermplasmCrossElement startElement = new SingleGermplasmCrossElement();
	            startElement.setGermplasm(germplasm);
	            GermplasmCrossElement cross = expandGermplasmCross(startElement, crossExpansionRule.getStopLevel(), false);
	            return cross.toString();
        	}
        } else {
            return null;
        }
    }
    
    
    private GermplasmCrossElement expandGermplasmCross(GermplasmCrossElement element, int level, boolean forComplexCross) throws MiddlewareQueryException {
        if (level == 0) {
            //if the level is zero then there is no need to expand and the element
            //should be returned as is
            return element;
        }else {
            if (element instanceof SingleGermplasmCrossElement) {
                SingleGermplasmCrossElement singleGermplasm = (SingleGermplasmCrossElement) element;
                Germplasm germplasmToExpand = singleGermplasm.getGermplasm();
                
                if(germplasmToExpand == null){
                    return singleGermplasm;
                }

                if (germplasmToExpand.getGnpgs() < 0) {
                    //for germplasms created via a derivative or maintenance method
                    //skip and then expand on the gpid1 parent
                    if (germplasmToExpand.getGpid1() != null && germplasmToExpand.getGpid1() != 0 && !forComplexCross) {
                        SingleGermplasmCrossElement nextElement = new SingleGermplasmCrossElement();
                        Germplasm gpid1Germplasm =  getGermplasmDataManager().getGermplasmWithPrefName(germplasmToExpand.getGpid1());
                        if(gpid1Germplasm != null){
                            nextElement.setGermplasm(gpid1Germplasm);
                            return expandGermplasmCross(nextElement, level, forComplexCross);
                        } else{
                            return element;
                        }
                    } else {
                        return element;
                    }
                } else {
                    GermplasmCross cross = new GermplasmCross();

                    Method method =  getGermplasmDataManager().getMethodByID(germplasmToExpand.getMethodId());
                    if (method != null) {
                        String methodName = method.getMname();
                        if (methodName != null) {
                            methodName = methodName.toLowerCase();
                        } else {
                            methodName = "";
                        }

                        if (methodName.contains("single cross")) {
                            //get the immediate parents
                            Germplasm firstParent =  getGermplasmDataManager().getGermplasmWithPrefName(germplasmToExpand.getGpid1());
                            SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
                            firstParentElem.setGermplasm(firstParent);
                            
                            Germplasm secondParent =  getGermplasmDataManager().getGermplasmWithPrefName(germplasmToExpand.getGpid2());
                            SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
                            secondParentElem.setGermplasm(secondParent);

                            //expand the parents as needed, depends on the level
                            GermplasmCrossElement expandedFirstParent = expandGermplasmCross(firstParentElem, level - 1, forComplexCross);
                            GermplasmCrossElement expandedSecondParent = expandGermplasmCross(secondParentElem, level - 1, forComplexCross);

                            //get the number of crosses in the first parent
                            int numOfCrosses = 0;
                            if (expandedFirstParent instanceof GermplasmCross) {
                                numOfCrosses = ((GermplasmCross) expandedFirstParent).getNumberOfCrossesBefore() + 1;
                            }

                            cross.setFirstParent(expandedFirstParent);
                            cross.setSecondParent(expandedSecondParent);
                            cross.setNumberOfCrossesBefore(numOfCrosses);

                        } else if (methodName.contains("double cross")) {
                            //get the grandparents on both sides
                            Germplasm firstParent =  getGermplasmDataManager().getGermplasmByGID(germplasmToExpand.getGpid1());
                            Germplasm secondParent =  getGermplasmDataManager().getGermplasmByGID(germplasmToExpand.getGpid2());
                            
                            Germplasm firstGrandParent = null;
                            SingleGermplasmCrossElement firstGrandParentElem = new SingleGermplasmCrossElement();
                            Germplasm secondGrandParent = null;
                            SingleGermplasmCrossElement secondGrandParentElem = new SingleGermplasmCrossElement();
                            if(firstParent != null){
                                firstGrandParent =  getGermplasmDataManager().getGermplasmWithPrefName(firstParent.getGpid1());
                                firstGrandParentElem.setGermplasm(firstGrandParent);
                                
                                secondGrandParent =  getGermplasmDataManager().getGermplasmWithPrefName(firstParent.getGpid2());
                                secondGrandParentElem.setGermplasm(secondGrandParent);
                            }

                            Germplasm thirdGrandParent = null;
                            SingleGermplasmCrossElement thirdGrandParentElem = new SingleGermplasmCrossElement();
                            Germplasm fourthGrandParent = null;
                            SingleGermplasmCrossElement fourthGrandParentElem = new SingleGermplasmCrossElement();
                            if(secondParent != null){
                                thirdGrandParent =  getGermplasmDataManager().getGermplasmWithPrefName(secondParent.getGpid1());
                                thirdGrandParentElem.setGermplasm(thirdGrandParent);
                                fourthGrandParent =  getGermplasmDataManager().getGermplasmWithPrefName(secondParent.getGpid2());
                                fourthGrandParentElem.setGermplasm(fourthGrandParent);
                            }

                            //expand the grand parents as needed, depends on the level
                            GermplasmCrossElement expandedFirstGrandParent = null;
                            GermplasmCrossElement expandedSecondGrandParent = null;
                            GermplasmCrossElement expandedThirdGrandParent = null;
                            GermplasmCrossElement expandedFourthGrandParent =  null;
                            
                            if(firstParent != null){
                                expandedFirstGrandParent = expandGermplasmCross(firstGrandParentElem, level - 1, forComplexCross);
                                expandedSecondGrandParent = expandGermplasmCross(secondGrandParentElem, level - 1, forComplexCross);
                            }
                            
                            if(secondParent != null){
                                expandedThirdGrandParent = expandGermplasmCross(thirdGrandParentElem, level - 1, forComplexCross);
                                expandedFourthGrandParent = expandGermplasmCross(fourthGrandParentElem, level - 1, forComplexCross);
                            }

                            //create the cross object for the first pair of grand parents
                            GermplasmCross firstCross = new GermplasmCross();
                            int numOfCrossesForFirst = 0;
                            if(firstParent != null){
                                firstCross.setFirstParent(expandedFirstGrandParent);
                                firstCross.setSecondParent(expandedSecondGrandParent);
                                //compute the number of crosses before this cross
                                if (expandedFirstGrandParent instanceof GermplasmCross) {
                                    numOfCrossesForFirst = ((GermplasmCross) expandedFirstGrandParent).getNumberOfCrossesBefore() + 1;
                                }
                                firstCross.setNumberOfCrossesBefore(numOfCrossesForFirst);
                            }

                            //create the cross object for the second pair of grand parents
                            GermplasmCross secondCross = new GermplasmCross();
                            if(secondParent != null){
                                secondCross.setFirstParent(expandedThirdGrandParent);
                                secondCross.setSecondParent(expandedFourthGrandParent);
                                //compute the number of crosses before this cross
                                int numOfCrossesForSecond = 0;
                                if (expandedThirdGrandParent instanceof GermplasmCross) {
                                    numOfCrossesForSecond = ((GermplasmCross) expandedThirdGrandParent).getNumberOfCrossesBefore() + 1;
                                }
                                secondCross.setNumberOfCrossesBefore(numOfCrossesForSecond);
                            } 

                            //create the cross of the two sets of grandparents, this will be returned
                            if(firstParent != null){
                                cross.setFirstParent(firstCross);
                            } else{
                                SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
                                firstParentElem.setGermplasm(firstParent);
                                cross.setFirstParent(firstParentElem);
                            }
                            
                            if(secondParent != null){
                                cross.setSecondParent(secondCross);
                            } else{
                                SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
                                secondParentElem.setGermplasm(secondParent);
                                cross.setSecondParent(secondParentElem);
                            }
                            
                            //compute the number of crosses before the cross to be returned
                            int numOfCrosses = 0;
                            if(firstParent != null){
                                numOfCrosses = numOfCrossesForFirst + 1;
                                if (expandedSecondGrandParent instanceof GermplasmCross) {
                                    numOfCrosses = numOfCrosses + ((GermplasmCross) expandedSecondGrandParent).getNumberOfCrossesBefore() + 1;
                                }
                            }
                            cross.setNumberOfCrossesBefore(numOfCrosses);

                        } else if (methodName.contains("three-way cross")) {
                            //get the two parents first
                            Germplasm firstParent =  getGermplasmDataManager().getGermplasmByGID(germplasmToExpand.getGpid1());
                            Germplasm secondParent =  getGermplasmDataManager().getGermplasmByGID(germplasmToExpand.getGpid2());
                            
                            //check for the parent generated by a cross, the other one should be a derived germplasm
                            if (firstParent != null && firstParent.getGnpgs() > 0) {
                                // the first parent is the one created by a cross
                                Germplasm firstGrandParent =  getGermplasmDataManager().getGermplasmWithPrefName(firstParent.getGpid1());
                                SingleGermplasmCrossElement firstGrandParentElem = new SingleGermplasmCrossElement();
                                firstGrandParentElem.setGermplasm(firstGrandParent);

                                Germplasm secondGrandParent =  getGermplasmDataManager().getGermplasmWithPrefName(firstParent.getGpid2());
                                SingleGermplasmCrossElement secondGrandParentElem = new SingleGermplasmCrossElement();
                                secondGrandParentElem.setGermplasm(secondGrandParent);

                                //expand the grand parents as needed, depends on the level
                                GermplasmCrossElement expandedFirstGrandParent = expandGermplasmCross(firstGrandParentElem, level - 1, forComplexCross);
                                GermplasmCrossElement expandedSecondGrandParent = expandGermplasmCross(secondGrandParentElem, level - 1, forComplexCross);

                                //make the cross object for the grand parents
                                GermplasmCross crossForGrandParents = new GermplasmCross();
                                crossForGrandParents.setFirstParent(expandedFirstGrandParent);
                                crossForGrandParents.setSecondParent(expandedSecondGrandParent);
                                //compute the number of crosses before this one
                                int numOfCrossesForGrandParents = 0;
                                if (expandedFirstGrandParent instanceof GermplasmCross) {
                                    numOfCrossesForGrandParents = ((GermplasmCross) expandedFirstGrandParent).getNumberOfCrossesBefore() + 1;
                                }
                                crossForGrandParents.setNumberOfCrossesBefore(numOfCrossesForGrandParents);

                                //make the element for the second parent
                                secondParent =  getGermplasmDataManager().getGermplasmWithPrefName(germplasmToExpand.getGpid2());
                                SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
                                secondParentElem.setGermplasm(secondParent);

                                // create the cross to return
                                cross.setFirstParent(crossForGrandParents);
                                cross.setSecondParent(secondParentElem);
                                //compute the number of crosses before this cross
                                cross.setNumberOfCrossesBefore(numOfCrossesForGrandParents + 1);
                            } else if(secondParent != null){
                                // the second parent is the one created by a cross
                                Germplasm firstGrandParent =  getGermplasmDataManager().getGermplasmWithPrefName(secondParent.getGpid1());
                                SingleGermplasmCrossElement firstGrandParentElem = new SingleGermplasmCrossElement();
                                firstGrandParentElem.setGermplasm(firstGrandParent);

                                Germplasm secondGrandParent =  getGermplasmDataManager().getGermplasmWithPrefName(secondParent.getGpid2());
                                SingleGermplasmCrossElement secondGrandParentElem = new SingleGermplasmCrossElement();
                                secondGrandParentElem.setGermplasm(secondGrandParent);

                                //expand the grand parents as needed, depends on the level
                                GermplasmCrossElement expandedFirstGrandParent = expandGermplasmCross(firstGrandParentElem, level - 1, forComplexCross);
                                GermplasmCrossElement expandedSecondGrandParent = expandGermplasmCross(secondGrandParentElem, level - 1, forComplexCross);

                                //make the cross object for the grand parents
                                GermplasmCross crossForGrandParents = new GermplasmCross();
                                crossForGrandParents.setFirstParent(expandedFirstGrandParent);
                                crossForGrandParents.setSecondParent(expandedSecondGrandParent);
                                //compute the number of crosses before this one
                                int numOfCrossesForGrandParents = 0;
                                if (expandedFirstGrandParent instanceof GermplasmCross) {
                                    numOfCrossesForGrandParents = ((GermplasmCross) expandedFirstGrandParent).getNumberOfCrossesBefore() + 1;
                                }
                                crossForGrandParents.setNumberOfCrossesBefore(numOfCrossesForGrandParents);

                                //make the element for the first parent
                                firstParent =  getGermplasmDataManager().getGermplasmWithPrefName(germplasmToExpand.getGpid1());
                                SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
                                firstParentElem.setGermplasm(firstParent);

                                //create the cross to return
                                cross.setFirstParent(crossForGrandParents);
                                cross.setSecondParent(firstParentElem);
                                cross.setNumberOfCrossesBefore(numOfCrossesForGrandParents + 1);
                            } else{
                                SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
                                firstParentElem.setGermplasm(firstParent);
                                SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
                                secondParentElem.setGermplasm(secondParent);
                                
                                cross.setFirstParent(firstParentElem);
                                cross.setSecondParent(secondParentElem);
                                cross.setNumberOfCrossesBefore(0);
                            }

                        } else if(methodName.contains("backcross")){
                            BackcrossElement backcross = new BackcrossElement();
                            
                            Germplasm firstParent =  getGermplasmDataManager().getGermplasmWithPrefName(germplasmToExpand.getGpid1());
                            Germplasm secondParent =  getGermplasmDataManager().getGermplasmWithPrefName(germplasmToExpand.getGpid2());
                            
                            boolean itsABackCross = false;
                            
                            //determine which is the recurrent parent
                            if(firstParent != null && secondParent != null){
                                SingleGermplasmCrossElement recurringParentElem = new SingleGermplasmCrossElement();
                                SingleGermplasmCrossElement parentElem = new SingleGermplasmCrossElement();
                                if(secondParent.getGnpgs() >= 2){
                                    if(firstParent.getGid().equals(secondParent.getGpid1())
                                            || firstParent.getGid().equals(secondParent.getGpid2())){
                                        itsABackCross = true;
                                        backcross.setRecurringParentOnTheRight(false);
                                        
                                        recurringParentElem.setGermplasm(firstParent);
                                        
                                        Germplasm toCheck = null;
                                        if(firstParent.getGid().equals(secondParent.getGpid1()) && secondParent.getGpid2() != null){
                                            toCheck =  getGermplasmDataManager().getGermplasmWithPrefName(secondParent.getGpid2());
                                        } else if(firstParent.getGid().equals(secondParent.getGpid2()) && secondParent.getGpid1() != null){
                                            toCheck =  getGermplasmDataManager().getGermplasmWithPrefName(secondParent.getGpid1());
                                        }
                                        Object[] numOfDosesAndOtherParent = determineNumberOfRecurringParent(firstParent.getGid(), toCheck);
                                        parentElem.setGermplasm((Germplasm) numOfDosesAndOtherParent[1]);
                                        
                                        backcross.setNumberOfDosesOfRecurringParent(((Integer) numOfDosesAndOtherParent[0]).intValue() + 2);
                                    }
                                } else if(firstParent.getGnpgs() >= 2){
                                    if(secondParent.getGid().equals(firstParent.getGpid1())
                                            || secondParent.getGid().equals(firstParent.getGpid2())){
                                        itsABackCross = true;
                                        backcross.setRecurringParentOnTheRight(true);
                                        
                                        recurringParentElem.setGermplasm(secondParent);
                                        
                                        Germplasm toCheck = null;
                                        if(secondParent.getGid().equals(firstParent.getGpid1()) && firstParent.getGpid2() != null){
                                            toCheck =  getGermplasmDataManager().getGermplasmWithPrefName(firstParent.getGpid2());
                                        } else if(secondParent.getGid().equals(firstParent.getGpid2()) && firstParent.getGpid1() != null){
                                            toCheck =  getGermplasmDataManager().getGermplasmWithPrefName(firstParent.getGpid1());
                                        }
                                        Object[] numOfDosesAndOtherParent = determineNumberOfRecurringParent(secondParent.getGid(), toCheck);
                                        parentElem.setGermplasm((Germplasm) numOfDosesAndOtherParent[1]);
                                        
                                        backcross.setNumberOfDosesOfRecurringParent(((Integer) numOfDosesAndOtherParent[0]).intValue() + 2);
                                    }
                                } else{
                                    itsABackCross = false;
                                }
                                
                                if(itsABackCross){
                                    GermplasmCrossElement expandedRecurringParent = expandGermplasmCross(recurringParentElem, level - 1, forComplexCross);
                                    backcross.setRecurringParent(expandedRecurringParent);
                                    
                                    GermplasmCrossElement expandedParent = expandGermplasmCross(parentElem, level -1, forComplexCross);
                                    backcross.setParent(expandedParent);
                                    
                                    return backcross;
                                }
                            }
                            
                            if(!itsABackCross){
                                SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
                                firstParentElem.setGermplasm(firstParent);
                                SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
                                secondParentElem.setGermplasm(secondParent);
                                
                                cross.setFirstParent(firstParentElem);
                                cross.setSecondParent(secondParentElem);
                                cross.setNumberOfCrossesBefore(0);
                            }
                        } else if(methodName.contains("cross") && methodName.contains("complex")){
                            //get the immediate parents
                            Germplasm firstParent =  getGermplasmDataManager().getGermplasmWithPrefName(germplasmToExpand.getGpid1());
                            SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
                            firstParentElem.setGermplasm(firstParent);
                            
                            Germplasm secondParent =  getGermplasmDataManager().getGermplasmWithPrefName(germplasmToExpand.getGpid2());
                            SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
                            secondParentElem.setGermplasm(secondParent);

                            //expand the parents as needed, depends on the level
                            GermplasmCrossElement expandedFirstParent = expandGermplasmCross(firstParentElem, level, true);
                            GermplasmCrossElement expandedSecondParent = expandGermplasmCross(secondParentElem, level, true);

                            //get the number of crosses in the first parent
                            int numOfCrosses = 0;
                            if (expandedFirstParent instanceof GermplasmCross) {
                                numOfCrosses = ((GermplasmCross) expandedFirstParent).getNumberOfCrossesBefore() + 1;
                            }

                            cross.setFirstParent(expandedFirstParent);
                            cross.setSecondParent(expandedSecondParent);
                            cross.setNumberOfCrossesBefore(numOfCrosses);
                        } else if (methodName.contains("cross")){
                            Germplasm firstParent =  getGermplasmDataManager().getGermplasmWithPrefName(germplasmToExpand.getGpid1());
                            Germplasm secondParent =  getGermplasmDataManager().getGermplasmWithPrefName(germplasmToExpand.getGpid2());
                            
                            SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
                            firstParentElem.setGermplasm(firstParent);
                            SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
                            secondParentElem.setGermplasm(secondParent);
                            
                            cross.setFirstParent(firstParentElem);
                            cross.setSecondParent(secondParentElem);
                            cross.setNumberOfCrossesBefore(0);
                        } else{
                            SingleGermplasmCrossElement crossElement = new SingleGermplasmCrossElement();
                            crossElement.setGermplasm(germplasmToExpand);
                            return crossElement;
                        }

                        return cross;
                    } else {
                        logAndThrowException("Error with expanding cross, can not find method with id: " + germplasmToExpand.getMethodId(), 
                                new Throwable(), LOG);
                    }
                }
            } else {
                logAndThrowException("expandGermplasmCross was incorrectly called", new Throwable(), LOG);
            }
        }
        return element;
    }
	
    /**
     * 
     * @param recurringParentGid
     * @param toCheck
     * @return an array of 2 Objects, first is an Integer which is the number of doses of the recurring parent, and the other is a Germplasm object
     * representing the parent crossed with the recurring parent.
     */
    private Object[] determineNumberOfRecurringParent(Integer recurringParentGid, Germplasm toCheck) throws MiddlewareQueryException{
        Object[] toreturn = new Object[2];
        if(toCheck == null){
            toreturn[0] = Integer.valueOf(0);
            toreturn[1] = null;
        } else if(toCheck.getGpid1() != null && !toCheck.getGpid1().equals(recurringParentGid)
                && toCheck.getGpid2() != null && !toCheck.getGpid2().equals(recurringParentGid)){
            toreturn[0] = Integer.valueOf(0);
            toreturn[1] = toCheck;
        } else if(toCheck.getGpid1() != null && toCheck.getGpid1().equals(recurringParentGid)){
            Germplasm nextToCheck = null;
            if(toCheck.getGpid2() != null){
                nextToCheck = getGermplasmDataManager().getGermplasmWithPrefName(toCheck.getGpid2());
            }
            Object[] returned = determineNumberOfRecurringParent(recurringParentGid, nextToCheck);
            toreturn[0] = ((Integer) returned[0]) + 1;
            toreturn[1] = returned[1];
        } else if(toCheck.getGpid2() != null && toCheck.getGpid2().equals(recurringParentGid)){
            Germplasm nextToCheck = null;
            if(toCheck.getGpid1() != null){
                nextToCheck = getGermplasmDataManager().getGermplasmWithPrefName(toCheck.getGpid1());
            }
            Object[] returned = determineNumberOfRecurringParent(recurringParentGid, nextToCheck);
            toreturn[0] = ((Integer) returned[0]) + 1;
            toreturn[1] = returned[1];
        } else{
            toreturn[0] = Integer.valueOf(0);
            toreturn[1] = toCheck;
        }
        
        return toreturn;
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
    
    
    @Override
	public String getCrossExpansionCimmytWheat(int gid, int level, int type) throws MiddlewareQueryException {
		try{			
			return getCimmytWheatPedigree(gid, level, new Germplasm(), 0, 0, 0, 0, type);
		}catch(Exception e){
			throw new MiddlewareQueryException(e.getMessage(), e);
		}
	}

}

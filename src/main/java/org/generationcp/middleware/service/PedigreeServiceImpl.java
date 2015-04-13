package org.generationcp.middleware.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.GermplasmNameType;
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

    public PedigreeServiceImpl() {
        super();
    }
    public PedigreeServiceImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
    }
    public PedigreeServiceImpl(HibernateSessionProvider sessionProvider, String localDatabaseName) {
        super(sessionProvider, localDatabaseName);
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
			if (name.getNstat() != 9) {
                if (ntypeArray.contains(name.getTypeId()) 
                		&& nstatArray.contains(name.getNstat()) 
                		&& !nuiArray.contains(name.getUserId())) {
                	returnNameList.add(name);
                }
            }
		}
		return returnNameList;
	}
	private GermplasmNameType getGermplasmType(int ntype){
		if(ntype == 17){
			return GermplasmNameType.ALTERNATIVE_ABBREVIATION;
		}
		return GermplasmNameType.ABBREVIATED_CULTIVAR_NAME;
	}

	/**
     * Recursive procedure to generate the pedigree
     *
     * @param p_gid Input GID
     * @param nivel default zero
     * @param gpidinfClass empty class
     * @param fback default zero
     * @param mback default zero
     * @param Resp1 default zero
     * @param Resp2 default zero
     * @return
     * @throws Exception
     */
    public String arma_pedigree(int p_gid, int nivel, Germplasm gpidinfClass, int fback, int mback, int Resp1, int Resp2, int ntype, CimmytWheatNameUtil cimmytWheatNameUtil) throws Exception {
        
        System.out.println("Armando pedigree con [p_gid] " + p_gid + " [nivel] " + nivel + " [fback] " + fback + " [mback] " + mback + " [Resp1] " + Resp1 + " [Resp2] " + Resp2);
        int xCurrent = 0;
        int xMax     = 0;        
        String arma_pedigree = "";
        String ped           = "";
        String xPrefName     = "";
        String Delimiter     = "";
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
        
        String LongStr;
        String ShortStr;
        boolean BackCrossFound = false;
        //GermplsmRecord grTemp = new GermplsmRecord();
        Germplasm grTemp = new Germplasm();
        grTemp.setGid(0);
        grTemp.setGpid1(0);
        grTemp.setGpid2(0);
        //boolean levelZeroFullName = true; 
        List<Name> listNL = null;
        int veces_rep = 0;
        
        
        try {
            Germplasm temp = getGermplasmDataManager().getGermplasmByGID(p_gid);
            if(temp != null){
            	grTemp = temp;
            }
            if (grTemp.getGid() == 0) {
                return "";
            }
            listNL = getCimmytWheatWayNamesList(grTemp.getGid(), Arrays.asList(cimmytWheatNameUtil.getNtypeArray()), Arrays.asList(cimmytWheatNameUtil.getNstatArray()), Arrays.asList(cimmytWheatNameUtil.getNuidArray()));
            //since it should not be dependent on the status anymore
            //listNL = getNamesByGID(grTemp.getGid(), null, getGermplasmType(ntype)); 
            // Determine if there was a Female or Male Backcross, that should be used in the pedigree, then the proper name of the
            // line can't be used. For instance if the name is 27217-A-4-8 and the pedigree is also MT/BRT and we are
            // called with mback representing BRT, then we must retrieve MT/BRT instead of 27217-A-4-8 to be able to reproduce
            // the name MT/2*BRT as the final pedigree. See example with GID=1935, before it generated 27217-A-4-8/BRT, now it
            // generates MT/2*BRT as expected.
            BackCrossFound = false;
            if ((grTemp.getGnpgs() == 2) && (grTemp.getGpid1() == fback) && (fback != 0)) {
                BackCrossFound = true;
            }
            if ((grTemp.getGnpgs() == 2) && (grTemp.getGpid2() == mback) && (mback != 0)) {
                BackCrossFound = true;
            }
            //' Also handle the CIMMYT retrocrosses A/B//A is A*2/B and B//A/B is A/2*B  JEN 2012-02-17
            if (grTemp.getGnpgs() == 2 && grTemp.getGpid2() == fback && fback != 0) {
                BackCrossFound = true;
            }
            if (grTemp.getGnpgs() == 2 && grTemp.getGpid1() == mback && mback != 0) {
                BackCrossFound = true;
            }
        } catch (Exception e) {
        }
        
        if ((!listNL.isEmpty()) && (grTemp.getGnpgs() == -1)) {
            // Name found, but we can only use it if not a backcross
            //GermplsmRecord rs_back = new GermplsmRecord();
        	Germplasm rs_back = new Germplasm();
            if (grTemp.getGpid1() != null && grTemp.getGpid1() != 0) {
                //int ret = armainfoGID(grTemp.getGpid1(), rs_back);
                Germplasm temp1 = getGermplasmDataManager().getGermplasmByGID(grTemp.getGpid1());
                if(temp1 != null){
                	rs_back = temp1;
                }
                
                if (rs_back.getGid() == null) {
                    if (fback != 0 && (fback == rs_back.getGpid1())) {
                        BackCrossFound = true;
                    }
                    if (mback != 0 && (mback == rs_back.getGpid2())) {
                        BackCrossFound = true;
                    }
                    // New artificial backcrosses implemented 2 If's  JEN - 2012-02-13
                    if (fback != 0 && fback == rs_back.getGpid2()) {
                        BackCrossFound = true;
                    }
                    if (mback != 0 && mback == rs_back.getGpid1()) {
                        BackCrossFound = true;
                    }
                }
            }
        }
        // If the current GID is identical to one of the backcross GIDs that must be respected,
        // cancel it as a backross and find a proper name
        if ((Resp1 == p_gid) || (Resp2 == p_gid)) {
            BackCrossFound = false;
        }
        if ((!listNL.isEmpty()) && !(BackCrossFound)) {
            //xMax = 0;
            
            for (Name namesrecord : listNL) {
                xCurrent = giveNameValue(namesrecord.getTypeId(), namesrecord.getNstat(), cimmytWheatNameUtil);
                //Apply check if the LevelZeroFullName is true or not
                //If that is the case and we are at level zero, add 200 to xCurrent if NSTAT=1
                if ((nivel == 0) && cimmytWheatNameUtil.isLevelZeroFullName() && (namesrecord.getNstat() == 1)) {
                    xCurrent = xCurrent + 200;
                }
                if (xCurrent > xMax) {
                    xPrefName = namesrecord.getNval();
                    xMax = xCurrent;
                }
            }
            /*
            xPrefName = listNL.get(0).getNval();
            */
            ped = xPrefName;
            
        } else {
            if ((grTemp.getGpid1() == 0) && (grTemp.getGpid2() == 0)) {
                ped = "Unknown";
            } else {
                if ((grTemp.getGnpgs() == -1) && (grTemp.getGpid2() != 0)) {
                    ped = arma_pedigree(grTemp.getGpid2(), nivel, gpidinfClass, fback, mback, Resp1, Resp2, ntype, cimmytWheatNameUtil);
                } else if ((grTemp.getGnpgs() == -1) && (grTemp.getGpid1() != 0)) {
                    ped = arma_pedigree(grTemp.getGpid1(), nivel, gpidinfClass, fback, mback, Resp1, Resp2, ntype, cimmytWheatNameUtil);
                } else {
                    gpidinfClass.setGpid1(grTemp.getGpid1());
                    gpidinfClass.setGpid2(grTemp.getGpid2());
                    if (grTemp.getGpid1() == fback) {
                        p1 = arma_pedigree(grTemp.getGpid1(), nivel + 1, fGpidInfClass, 0, 0, Resp1, Resp2, ntype, cimmytWheatNameUtil);
                    } else {
                        p1 = arma_pedigree(grTemp.getGpid1(), nivel + 1, fGpidInfClass, 0, grTemp.getGpid2(), Resp1, Resp2, ntype, cimmytWheatNameUtil);
                    }
                    if (grTemp.getGpid2() == mback) {
                        p2 = arma_pedigree(grTemp.getGpid2(), nivel + 1, mGpidInfClass, 0, 0, Resp1, Resp2, ntype, cimmytWheatNameUtil);
                    } else {
                        p2 = arma_pedigree(grTemp.getGpid2(), nivel + 1, mGpidInfClass, grTemp.getGpid1(), 0, Resp1, Resp2, ntype, cimmytWheatNameUtil);
                    }
                }
//         ' Since female/male backcross is a bit meaningless when IWIS2 false backrosses are handled, then
//         ' we just detect which part could be handled by length of p1 and p2, and if they are contained in
//         ' first part or last part of the other
                if (grTemp.getGpid1().intValue() == mGpidInfClass.getGpid1().intValue() || grTemp.getGpid2().intValue() == fGpidInfClass.getGpid1().intValue()  ||
                		grTemp.getGpid2().intValue() == fGpidInfClass.getGpid2().intValue()  || grTemp.getGpid1().intValue() == mGpidInfClass.getGpid2().intValue() )//Handle Backcross
                {
                    veces_rep = 2;
                    if ((!p1.equals("")) && (!p2.equals(""))) {
                        if ((!p1.contains(p2)) && (!p2.contains(p1))) {
                            ped = "Houston we have a problem";
                            oldp1[0] = p1;
                            oldp2[0] = p2;
                            fGpidInfClass.setGpid1(0);
                            fGpidInfClass.setGpid2(0);
                            mGpidInfClass.setGpid1(0);
                            mGpidInfClass.setGpid2(0);
                            p1 = arma_pedigree(grTemp.getGpid1(), nivel + 1, fGpidInfClass, 0, 0, grTemp.getGpid1(), grTemp.getGpid2(), ntype, cimmytWheatNameUtil);
                            p2 = arma_pedigree(grTemp.getGpid2(), nivel + 1, mGpidInfClass, 0, 0, grTemp.getGpid1(), grTemp.getGpid2(), ntype, cimmytWheatNameUtil);
                            if ((!p1.contains(p2)) && (!p2.contains(p1))) {
                                ped = "Houston we have a BIG problem";
                                // Resolving situation of GID=29367 CID=22793 and GID=29456 CID=22881
                                // Female : CMH75A.66/2*CNO79  Male CMH75A.66/3*CNO79  Result: CMH75A.66/2*CNO79*2//CNO79
                                // Solution: convert p1 and p2 to the following
                                // p1 : CMH75A.66/2*CNO79  p2: CMH75A.66/2*CNO79//CNO79
                                //
                                // A valid way to pass parameters by reference according to http://www.cs.utoronto.ca/~dianeh/tutorials/params/swap.html
                               
                                GetParentsDoubleRetroCrossNew(oldp1, oldp2);
                                p1 = oldp1[0];
                                p2 = oldp2[0];
                            }
                        }
                        if (p1.length() > p2.length()) {
                            LongStr = p1;
                            ShortStr = p2;
                        } else {
                            LongStr = p2;
                            ShortStr = p1;
                        }
                        if (LongStr.substring(0, ShortStr.length()).equals(ShortStr)) {
                            //' Handle female type of backcross
                            cut = LongStr.substring(ShortStr.length());
                            if (cut.startsWith("/")) {
                            } else if (cut.startsWith("*")) {
                                if (cut.substring(2, 3).equals("/")) {
                                    veces_rep = Integer.valueOf(cut.substring(1, 2)); //
                                    cut = cut.substring(2);
                                } else {
                                    veces_rep = Integer.valueOf(cut.substring(1, 3));
                                    cut = cut.substring(3);
                                }
                                veces_rep = veces_rep + 1;
                            }
                            ped = ShortStr + "*" + veces_rep + cut;
                        }
                        if (LongStr.substring(LongStr.length() - ShortStr.length()).equals(ShortStr)) {
                            //' Handle male type of backcross
                            cut = LongStr.substring(0, LongStr.length() - ShortStr.length());
                            if (cut.endsWith("/")) {
                            } else if (cut.endsWith("*")) {
                                if (cut.substring(cut.length() - 3, cut.length() - 2).equals("/")) {
                                    veces_rep = Integer.valueOf(cut.substring(cut.length() - 2, cut.length() - 1));
                                    cut = cut.substring(0, (cut.length() - 2));
                                } else {
                                    veces_rep = Integer.valueOf(cut.substring(cut.length() - 3, cut.length() - 1));
                                    cut = cut.substring(0, cut.length() - 3);
                                }
                                veces_rep = veces_rep + 1;
                            }
                            ped = cut + veces_rep + "*" + ShortStr;
                        }
                    }
                }
                if ((grTemp.getGpid1().intValue() != mGpidInfClass.getGpid1().intValue()) && (grTemp.getGpid1().intValue() != fGpidInfClass.getGpid1().intValue()) 
                		&& (grTemp.getGpid2().intValue() != fGpidInfClass.getGpid2().intValue()) && (grTemp.getGpid2().intValue() != fGpidInfClass.getGpid2().intValue()) && (grTemp.getGpid2().intValue() != mGpidInfClass.getGpid2().intValue())) {
                    if (((!p1.equals("")) || (!p2.equals(""))) && (ped.equals(""))) {
                        if (p1.equals("")) {
                            p1 = "Missing";
                        }
                        if (p2.equals("")) {
                            p2 = "Missing";
                        }
                        Delimiter = CrossExpansionUtil.GetNewDelimiter(p1 + p2).toString();
                        ped = p1 + Delimiter + p2;
                    }
                }
            }
        }
//        System.out.println( nivel + " : " + p_gid + ":" + ped );
        arma_pedigree = ped;
        return arma_pedigree;
    }
    
    private void GetParentsDoubleRetroCrossNew(String[] Mxp1, String[] Mxp2) { //24 agosto 2012, Medificaciones por Jesper
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
    
    
    private int giveNameValue(int p_ntypeX, int p_nstatX, CimmytWheatNameUtil cimmyWheatUtil) {
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
    @Override
	public String getCrossExpansionCimmytWheat(int gid, int level, int type) throws MiddlewareQueryException {
		try{
			CimmytWheatNameUtil cimmytWheatNameUtil = new CimmytWheatNameUtil();
			return arma_pedigree(gid, level, new Germplasm(), 0, 0, 0, 0, type, cimmytWheatNameUtil);
		}catch(Exception e){
			throw new MiddlewareQueryException(e.getMessage(), e);
		}
	}

}

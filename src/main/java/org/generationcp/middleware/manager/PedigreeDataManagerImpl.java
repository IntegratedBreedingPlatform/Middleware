/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.util.MaxPedigreeLevelReachedException;

/**
 * Implementation of the PedigreeDataManager interface. To instantiate this
 * class, a Hibernate Session must be passed to its constructor.
 * 
 */
public class PedigreeDataManagerImpl extends DataManager implements PedigreeDataManager{

    private GermplasmDataManagerImpl germplasmDataManager;
    private final static ThreadLocal<Integer> PEDIGREE_COUNTER = new ThreadLocal<>();
    private final static ThreadLocal<Boolean> CALCULATE_FULL = new ThreadLocal<>();

    public PedigreeDataManagerImpl() {
    }

    public PedigreeDataManagerImpl(HibernateSessionProvider sessionProvider, String databaseName) {
        super(sessionProvider, databaseName);
        germplasmDataManager = new GermplasmDataManagerImpl(sessionProvider, databaseName);
    }
        
    @Override
    public GermplasmPedigreeTree generatePedigreeTree(Integer gid, int level) throws MiddlewareQueryException {
        return generatePedigreeTree(gid, level, false);
    }

    @Override
    public Integer countPedigreeLevel(Integer gid, Boolean includeDerivativeLine) throws MiddlewareQueryException,MaxPedigreeLevelReachedException {
        return countPedigreeLevel(gid, includeDerivativeLine, false);
    }

    @Override
    public Integer countPedigreeLevel(Integer gid, Boolean includeDerivativeLine, boolean calculateFullPedigree)
            throws MiddlewareQueryException,MaxPedigreeLevelReachedException {
        try {
            PEDIGREE_COUNTER.set(1);
            CALCULATE_FULL.set(calculateFullPedigree);
            return getPedigreeLevelCount(gid, includeDerivativeLine);
        } finally {
            PEDIGREE_COUNTER.remove();
            CALCULATE_FULL.remove();
        }
    }

    public Integer getPedigreeLevelCount(Integer gid, Boolean includeDerivativeLine) throws MiddlewareQueryException {
    	Integer maxPedigreeLevel = 0;

        if(gid==null || gid==0) {
        	return maxPedigreeLevel;
        }

    	Germplasm germplasm = getGermplasmDao().getById(gid);

    	if(germplasm.getGnpgs()==-1) {
    		if(!includeDerivativeLine) {
    			maxPedigreeLevel = getMaxPedigreeLevelFromParent(gid,1,includeDerivativeLine);
    		} else {
    			maxPedigreeLevel = getMaxPedigreeLevelFromParent(gid,2,includeDerivativeLine);
    		}
    	} else if(germplasm.getGnpgs() >= 2) {
    		maxPedigreeLevel = getMaxPedigreeLevelFromBothParents(gid,includeDerivativeLine);
    		if(germplasm.getGnpgs() > 2) {
    			maxPedigreeLevel = getMaxPedigreeLevelFromProgenitor(gid,germplasm.getGnpgs(),includeDerivativeLine,maxPedigreeLevel);
    		}
    	}

    	return maxPedigreeLevel + 1;
    }
    
    private Integer getMaxPedigreeLevelFromProgenitor(
    		Integer gid, Integer gnpgs, boolean includeDerivativeLine, Integer maxPedigreeLevel) 
    		throws MiddlewareQueryException {
    	Germplasm parentGermplasm = getParentByGIDAndProgenitorNumber(gid, gnpgs);

		if(parentGermplasm!=null) {
            incrementPedigreeLevelCounter();
			Integer numOfPedigree = getPedigreeLevelCount(parentGermplasm.getGid(),includeDerivativeLine);
			if(numOfPedigree>maxPedigreeLevel) {
				return numOfPedigree;
			}
		}

		return maxPedigreeLevel;
	}

    protected void incrementPedigreeLevelCounter() {
        int currentCount = getCurrentCounterCount();
        boolean calculateFull = getCalculateFullFlagValue();
        if ((currentCount + 1) > MAX_PEDIGREE_LEVEL && !calculateFull) {
            throw MaxPedigreeLevelReachedException.getInstance();
        } else {
            PEDIGREE_COUNTER.set(currentCount + 1);
        }
    }

    protected int getCurrentCounterCount() {
        return PEDIGREE_COUNTER.get();
    }

    protected boolean getCalculateFullFlagValue() {
        return CALCULATE_FULL.get();
    }

	private Integer getMaxPedigreeLevelFromParent(
    		Integer gid, Integer parentNo, boolean includeDerivativeLine) throws MiddlewareQueryException {
    	Integer parentId = getGermplasmProgenitorID(gid,parentNo);
    	if(!includeDerivativeLine && parentId!=null) {
    		return getMaxPedigreeLevelFromBothParents(parentId,includeDerivativeLine);
    	} else if(parentId!=null){
            incrementPedigreeLevelCounter();
    		return getPedigreeLevelCount(parentId,includeDerivativeLine);
    	}
		return 0;
	}
    
    private Integer getMaxPedigreeLevelFromBothParents(
    		Integer gid, boolean includeDerivativeLine) throws MiddlewareQueryException {
        int currentPedigreeCount = PEDIGREE_COUNTER.get();

    	Integer numOfPedigreeFromParent1 = getPedigreeLevel(gid,1,includeDerivativeLine);
        PEDIGREE_COUNTER.set(currentPedigreeCount);
		Integer numOfPedigreeFromParent2 = getPedigreeLevel(gid,2,includeDerivativeLine);

		if(numOfPedigreeFromParent2>numOfPedigreeFromParent1) {
			return numOfPedigreeFromParent2;
		}

		return numOfPedigreeFromParent1;
	}

	private Integer getPedigreeLevel(Integer gid, Integer parentNo, boolean includeDerivativeLine) 
			throws MiddlewareQueryException {
		Integer parentId = getGermplasmProgenitorID(gid,parentNo);
		if(parentId!=null) {
            incrementPedigreeLevelCounter();
			return getPedigreeLevelCount(parentId,includeDerivativeLine);
		}
		return 0;
	}

	private Integer getGermplasmProgenitorID(Integer gid, Integer proNo) 
			throws MiddlewareQueryException {
		if(gid==null) {
			return null;
		}

		Germplasm germplasm = getParentByGIDAndProgenitorNumber(gid,proNo);

		if(germplasm!=null) {
			return germplasm.getGid();
		}
		return null;
	}

	@Override
    public GermplasmPedigreeTree generatePedigreeTree(Integer gid, int level, Boolean includeDerivativeLines) throws MiddlewareQueryException {
        GermplasmPedigreeTree tree = new GermplasmPedigreeTree();
        // set root node
        Germplasm root = germplasmDataManager.getGermplasmWithPrefName(gid);

        if (root != null) {
            GermplasmPedigreeTreeNode rootNode = new GermplasmPedigreeTreeNode();
            rootNode.setGermplasm(root);
            if (level > 1) {
                if(includeDerivativeLines) {
                    rootNode = addParents(rootNode, level);
                } else {
                    rootNode = addParentsExcludeDerivativeLines(rootNode, level);
                }
            }
            tree.setRoot(rootNode);
            return tree;
        }
        return null;
    }
    

    
    /**
     * Given a GermplasmPedigreeTreeNode and the level of the desired tree, add parents 
     * to the node recursively until the specified level of the tree is reached.
     * 
     * @param node
     * @param level
     * @return the given GermplasmPedigreeTreeNode with its parents added to it
     * @throws MiddlewareQueryException
     */
    private GermplasmPedigreeTreeNode addParents(GermplasmPedigreeTreeNode node, int level) throws MiddlewareQueryException {
        if (level == 1) {
            return node;
        } else {
            // get parents of node
            Germplasm germplasmOfNode = node.getGermplasm();
            if (germplasmOfNode.getGnpgs() == -1) {
                // get and add the source germplasm
                Germplasm parent = germplasmDataManager.getGermplasmWithPrefName(germplasmOfNode.getGpid2());
                if (parent != null) {
                    GermplasmPedigreeTreeNode nodeForParent = new GermplasmPedigreeTreeNode();
                    nodeForParent.setGermplasm(parent);
                    node.getLinkedNodes().add(addParents(nodeForParent, level - 1));
                }
            } else if (germplasmOfNode.getGnpgs() >= 2) {
                // get and add female parent
                Germplasm femaleParent = germplasmDataManager.getGermplasmWithPrefName(germplasmOfNode.getGpid1());
                if (femaleParent != null) {
                    GermplasmPedigreeTreeNode nodeForFemaleParent = new GermplasmPedigreeTreeNode();
                    nodeForFemaleParent.setGermplasm(femaleParent);
                    node.getLinkedNodes().add(addParents(nodeForFemaleParent, level - 1));
                }

                // get and add male parent
                Germplasm maleParent = germplasmDataManager.getGermplasmWithPrefName(germplasmOfNode.getGpid2());
                if (maleParent != null) {
                    GermplasmPedigreeTreeNode nodeForMaleParent = new GermplasmPedigreeTreeNode();
                    nodeForMaleParent.setGermplasm(maleParent);
                    node.getLinkedNodes().add(addParents(nodeForMaleParent, level - 1));
                }

                if (germplasmOfNode.getGnpgs() > 2) {
                    // if there are more parents, get and add each of them
                	List<Germplasm> otherParents = germplasmDataManager.getProgenitorsByGIDWithPrefName(germplasmOfNode.getGid());
                	if(otherParents!=null) {
	                    for (Germplasm otherParent : otherParents) {
	                        GermplasmPedigreeTreeNode nodeForOtherParent = new GermplasmPedigreeTreeNode();
	                        nodeForOtherParent.setGermplasm(otherParent);
	                        node.getLinkedNodes().add(addParents(nodeForOtherParent, level - 1));
	                    }
                    }
                }
            }
            return node;
        }
    }
    

    /**
     * Given a GermplasmPedigreeTreeNode and the level of the desired tree, add parents 
     * to the node recursively excluding derivative lines until the specified level of 
     * the tree is reached.
     * 
     * @param node
     * @param level
     * @return the given GermplasmPedigreeTreeNode with its parents added to it
     * @throws MiddlewareQueryException
     */
    private GermplasmPedigreeTreeNode addParentsExcludeDerivativeLines(GermplasmPedigreeTreeNode node, int level) throws MiddlewareQueryException {
        if (level == 1) {
            return node;
        } else {
            // get parents of node
            Germplasm germplasmOfNode = node.getGermplasm();
            
            if (germplasmOfNode.getGnpgs() == -1) {
                // get and add the source germplasm
                
                Germplasm parent = germplasmDataManager.getGermplasmWithPrefName(germplasmOfNode.getGpid1());
                
                
                if (parent != null) {

                    Germplasm grandParent1 = germplasmDataManager.getGermplasmWithPrefName(parent.getGpid1());   
                    if(grandParent1 != null){
                    	GermplasmPedigreeTreeNode nodeForGrandParent1 = new GermplasmPedigreeTreeNode();
                    	nodeForGrandParent1.setGermplasm(grandParent1);
                    	node.getLinkedNodes().add(addParentsExcludeDerivativeLines(nodeForGrandParent1, level - 1));
                    }
                    
                    Germplasm grandParent2 = germplasmDataManager.getGermplasmWithPrefName(parent.getGpid2());   
                    if(grandParent2 != null){
                        GermplasmPedigreeTreeNode nodeForGrandParent2 = new GermplasmPedigreeTreeNode();
                        nodeForGrandParent2.setGermplasm(grandParent2);
                        node.getLinkedNodes().add(addParentsExcludeDerivativeLines(nodeForGrandParent2, level - 1));
                    }
                        
                }
            } else if (germplasmOfNode.getGnpgs() >= 2) {
                // get and add female parent
                Germplasm femaleParent = germplasmDataManager.getGermplasmWithPrefName(germplasmOfNode.getGpid1());
                if (femaleParent != null) {
                    GermplasmPedigreeTreeNode nodeForFemaleParent = new GermplasmPedigreeTreeNode();
                    nodeForFemaleParent.setGermplasm(femaleParent);
                    node.getLinkedNodes().add(addParentsExcludeDerivativeLines(nodeForFemaleParent, level - 1));
                }

                // get and add male parent
                Germplasm maleParent = germplasmDataManager.getGermplasmWithPrefName(germplasmOfNode.getGpid2());
                if (maleParent != null) {
                    GermplasmPedigreeTreeNode nodeForMaleParent = new GermplasmPedigreeTreeNode();
                    nodeForMaleParent.setGermplasm(maleParent);
                    node.getLinkedNodes().add(addParentsExcludeDerivativeLines(nodeForMaleParent, level - 1));
                }

                if (germplasmOfNode.getGnpgs() > 2) {
                    // if there are more parents, get and add each of them
                    List<Germplasm> otherParents = germplasmDataManager.getProgenitorsByGIDWithPrefName(germplasmOfNode.getGid());
                    if(otherParents!=null) {
	                    for (Germplasm otherParent : otherParents) {
	                        GermplasmPedigreeTreeNode nodeForOtherParent = new GermplasmPedigreeTreeNode();
	                        nodeForOtherParent.setGermplasm(otherParent);
	                        node.getLinkedNodes().add(addParentsExcludeDerivativeLines(nodeForOtherParent, level - 1));
	                    }
                    }
                }           
            }
            return node;
        }
    }
    
    @Override
    public GermplasmPedigreeTree getMaintenanceNeighborhood(Integer gid, int numberOfStepsBackward, int numberOfStepsForward)
            throws MiddlewareQueryException {
        
        return getNeighborhood(gid, numberOfStepsBackward, numberOfStepsForward, 'M');
    }
    
    @Override
    public GermplasmPedigreeTree getDerivativeNeighborhood(Integer gid, int numberOfStepsBackward, int numberOfStepsForward)
            throws MiddlewareQueryException {
        
        return getNeighborhood(gid, numberOfStepsBackward, numberOfStepsForward, 'D');
    }



    private GermplasmPedigreeTree getNeighborhood(Integer gid, int numberOfStepsBackward, int numberOfStepsForward, char methodType)
            throws MiddlewareQueryException {
        GermplasmPedigreeTree neighborhood = new GermplasmPedigreeTree();

        // get the root of the neighborhood
        Object[] traceResult = traceRoot(gid, numberOfStepsBackward, methodType);

        if (traceResult != null) {
            Germplasm root = (Germplasm) traceResult[0];
            Integer stepsLeft = (Integer) traceResult[1];

            GermplasmPedigreeTreeNode rootNode = new GermplasmPedigreeTreeNode();
            rootNode.setGermplasm(root);

            // get the derived lines from the root until the whole neighborhood is created
            int treeLevel = numberOfStepsBackward - stepsLeft + numberOfStepsForward;
            rootNode = getDerivedLines(rootNode, treeLevel, methodType);

            neighborhood.setRoot(rootNode);

            return neighborhood;
        } else {
            return null;
        }
    }
    


    /**
     * Recursive function which gets the root of a derivative neighborhood by
     * tracing back through the source germplasms. The function stops when the
     * steps are exhausted or a germplasm created by a generative method is
     * encountered, whichever comes first.
     * 
     * @param gid
     * @param steps
     * @return Object[] - first element is the Germplasm POJO, second is an
     *         Integer which is the number of steps left to take
     * @throws MiddlewareQueryException
     */
    private Object[] traceRoot(Integer gid, int steps, char methodType) throws MiddlewareQueryException {
        Germplasm germplasm = germplasmDataManager.getGermplasmWithPrefName(gid);
        
        if (germplasm == null) {
            return new Object[0];
        } else if (steps == 0 || germplasm.getGnpgs() != -1) {
            return new Object[] { germplasm, Integer.valueOf(steps) };
        } else {
            int nextStep = steps;
            
            //for MAN neighborhood, move the step count only if the ancestor is a MAN.
            //otherwise, skip through the ancestor without changing the step count
            if (methodType == 'M') {
                Method method = germplasmDataManager.getMethodByID(germplasm.getMethodId());
                if (method != null && "MAN".equals(method.getMtype())) {
                    nextStep--;
                }
            
            //for DER neighborhood, always move the step count
            } else {
                nextStep--;
            }
            
            Object[] returned = traceRoot(germplasm.getGpid2(), nextStep, methodType);
            if (returned != null) {
                return returned;
            } else {
                return new Object[] { germplasm, Integer.valueOf(steps) };
            }
        }
    }

    /**
     * Recursive function to get the derived lines given a Germplasm. This
     * constructs the derivative neighborhood.
     * 
     * @param node
     * @param steps
     * @return
     * @throws MiddlewareQueryException
     */
    private GermplasmPedigreeTreeNode getDerivedLines(GermplasmPedigreeTreeNode node, int steps, char methodType) throws MiddlewareQueryException {
        if (steps <= 0) {
            return node;
        } else {
            List<Germplasm> derivedGermplasms = new ArrayList<Germplasm>();
            Integer gid = node.getGermplasm().getGid();
            derivedGermplasms = getChildren(gid, methodType);
            for (Germplasm g : derivedGermplasms) {
                GermplasmPedigreeTreeNode derivedNode = new GermplasmPedigreeTreeNode();
                derivedNode.setGermplasm(g);
                node.getLinkedNodes().add(getDerivedLines(derivedNode, steps - 1, methodType));
            }

            return node;
        }
    }


    private List<Germplasm> getChildren(Integer gid, char methodType) throws MiddlewareQueryException {
    	return getGermplasmDao().getChildren(gid, methodType);
	}

	@Override
    public Germplasm getParentByGIDAndProgenitorNumber(Integer gid, Integer progenitorNumber) throws MiddlewareQueryException {
		return getGermplasmDao().getProgenitorByGID(gid, progenitorNumber);
    }

    @Override
    public List<Object[]> getDescendants(Integer gid, int start, int numOfRows) throws MiddlewareQueryException {
        List<Object[]> result = new ArrayList<Object[]>();
        Object[] germplasmList;

        List<Germplasm> germplasmDescendant = getGermplasmDescendantByGID(gid, start, numOfRows);
        for (Germplasm g : germplasmDescendant) {
            germplasmList = new Object[2];
            if (g.getGpid1().equals(gid)) {
                germplasmList[0] = 1;
            } else if (g.getGpid2().equals(gid)) {
                germplasmList[0] = 2;
            } else {
                germplasmList[0] = 
                		getProgenitorDao().getByGIDAndPID(g.getGid(), gid).getProgntrsPK().getPno().intValue();
            }
            germplasmList[1] = g;

            result.add(germplasmList);
        }

        return result;
    }

    private List<Germplasm> getGermplasmDescendantByGID(Integer gid, int start,
			int numOfRows) throws MiddlewareQueryException {
    	return getGermplasmDao().getGermplasmDescendantByGID(gid, start, numOfRows);
	}

	@Override
    public long countDescendants(Integer gid) throws MiddlewareQueryException {
		 return getGermplasmDao().countGermplasmDescendantByGID(gid);
    }

    @Override
    public List<Germplasm> getManagementNeighbors(Integer gid, int start, int numOfRows) throws MiddlewareQueryException {
    	return getGermplasmDao().getManagementNeighbors(gid, start, numOfRows);
    }

    @Override
    public long countManagementNeighbors(Integer gid) throws MiddlewareQueryException {
    	return getGermplasmDao().countManagementNeighbors(gid);
    }

    @Override
    public long countGroupRelatives(Integer gid) throws MiddlewareQueryException {
    	return getGermplasmDao().countGroupRelatives(gid);
    }

    @Override
    public List<Germplasm> getGroupRelatives(Integer gid, int start, int numRows) throws MiddlewareQueryException {
    	return getGermplasmDao().getGroupRelatives(gid, start, numRows);
    }

    @Override
    public List<Germplasm> getGenerationHistory(Integer gid) throws MiddlewareQueryException {
        List<Germplasm> toreturn = new ArrayList<Germplasm>();

        Germplasm currentGermplasm = germplasmDataManager.getGermplasmWithPrefName(gid);
        if (currentGermplasm != null) {
            toreturn.add(currentGermplasm);

            while (currentGermplasm.getGnpgs() == -1) {
                // trace back the sources
                Integer sourceId = currentGermplasm.getGpid2();
                currentGermplasm = getGermplasmDataManager().getGermplasmWithPrefName(sourceId);

                if (currentGermplasm != null) {
                    toreturn.add(currentGermplasm);
                } else {
                    break;
                }
            }
        }
        return toreturn;
    }
    
    @Override
    public List<Germplasm> getPedigreeLine(Integer gid, int locationID) throws MiddlewareQueryException {
    	List<Germplasm> germplasms = new ArrayList<Germplasm>();
	
    	Germplasm currentGermplasm = germplasmDataManager.getGermplasmByGID(gid);
    	
    	if (currentGermplasm != null) {
    		germplasms = addParentsWithDerivativeMethod(germplasms, currentGermplasm, locationID);
    	}
    	
    	return germplasms;
    }

    /**
     * Recursive function to get the list of all ancestor germplasm with DER method
     * type and the given the locationID
     * 
     * @param germplasmsParam
     * @param currentGermplasm
     * @param locationID
     * @return the given Germplasm list with its parents added to it
     * @throws MiddlewareQueryException
     */
    @Deprecated
    private List<Germplasm> addParentsWithDerivativeMethod(List<Germplasm> germplasmsParam, Germplasm currentGermplasm, int locationID) throws MiddlewareQueryException {
    	List<Germplasm> germplasms = germplasmsParam;
    	// get parents of node
        if (currentGermplasm.getGnpgs() == -1) {
            // get the source germplasm
            Germplasm parent = germplasmDataManager.getGermplasmWithMethodType(currentGermplasm.getGpid2());
            if (parent != null) {
            	Method method = parent.getMethod();
            	String mType = "";
            	
            	if (method != null) {
            		mType = method.getMtype();
            	}
            	
            	// add parent only if method = DER and if it matches the given locationID
            	if ("DER".equals(mType) && parent.getLocationId().intValue() == locationID) {
            		germplasms.add(parent);
            	} 
            	germplasms = addParentsWithDerivativeMethod(germplasms, parent, locationID);
            }
        } else if (currentGermplasm.getGnpgs() >= 1) {
            // get female parent
            Germplasm femaleParent = germplasmDataManager.getGermplasmByGID(currentGermplasm.getGpid1());
            if (femaleParent != null) {
            	germplasms = addParentsWithDerivativeMethod(germplasms, femaleParent, locationID);
            }
        	
            // get male parent
            Germplasm maleParent = germplasmDataManager.getGermplasmByGID(currentGermplasm.getGpid2());
            if (maleParent != null) {
            	germplasms = addParentsWithDerivativeMethod(germplasms, maleParent, locationID);
            }
			
            if (currentGermplasm.getGnpgs() > 2) {
                // if there are more parents, get each of them
                List<Germplasm> otherParents = germplasmDataManager.getProgenitorsByGIDWithPrefName(currentGermplasm.getGid());
                if(otherParents!=null) {
	                for (Germplasm otherParent : otherParents) {
	                	germplasms = addParentsWithDerivativeMethod(germplasms, otherParent, locationID);
	                }
                }
            }
        }
		return germplasms;
    }


    private GermplasmDataManagerImpl getGermplasmDataManager(){
	    return this.germplasmDataManager;
    }
    
    public void setGermplasmDataManager(
	    	GermplasmDataManagerImpl germplasmDataManager) {
	        this.germplasmDataManager = germplasmDataManager;
	    }

    
}

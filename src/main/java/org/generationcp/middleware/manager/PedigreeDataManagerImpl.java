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

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.util.MaxPedigreeLevelReachedException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the PedigreeDataManager interface. To instantiate this
 * class, a Hibernate Session must be passed to its constructor.
 * 
 */
@Transactional
public class PedigreeDataManagerImpl extends DataManager implements PedigreeDataManager{
	
	public static final int MAX_PEDIGREE_LEVEL = 5;
	public static final int NONE = 0;
	public static final int MALE_RECURRENT = 1;
	public static final int FEMALE_RECURRENT = 2;

	private GermplasmDataManager germplasmDataManager;
    private static final ThreadLocal<Integer> PEDIGREE_COUNTER = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> CALCULATE_FULL = new ThreadLocal<>();

    public PedigreeDataManagerImpl() {
    }

    public PedigreeDataManagerImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
        germplasmDataManager = new GermplasmDataManagerImpl(sessionProvider);
    }
        
    @Override
    public GermplasmPedigreeTree generatePedigreeTree(Integer gid, int level) {
        return generatePedigreeTree(gid, level, false);
    }

    @Override
    public Integer countPedigreeLevel(Integer gid, Boolean includeDerivativeLine) throws MaxPedigreeLevelReachedException {
        return countPedigreeLevel(gid, includeDerivativeLine, false);
    }

    @Override
    public Integer countPedigreeLevel(Integer gid, Boolean includeDerivativeLine, boolean calculateFullPedigree)
            throws MaxPedigreeLevelReachedException {
        try {
            PEDIGREE_COUNTER.set(1);
            CALCULATE_FULL.set(calculateFullPedigree);
            return getPedigreeLevelCount(gid, includeDerivativeLine);
        } finally {
            PEDIGREE_COUNTER.remove();
            CALCULATE_FULL.remove();
        }
    }

    public Integer getPedigreeLevelCount(Integer gid, Boolean includeDerivativeLine) {
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
    		Integer gid, Integer gnpgs, boolean includeDerivativeLine, Integer maxPedigreeLevel) {
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
    		Integer gid, Integer parentNo, boolean includeDerivativeLine) {
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
    		Integer gid, boolean includeDerivativeLine) {
        int currentPedigreeCount = PEDIGREE_COUNTER.get();

    	Integer numOfPedigreeFromParent1 = getPedigreeLevel(gid,1,includeDerivativeLine);
        PEDIGREE_COUNTER.set(currentPedigreeCount);
		Integer numOfPedigreeFromParent2 = getPedigreeLevel(gid, 2, includeDerivativeLine);

		if(numOfPedigreeFromParent2>numOfPedigreeFromParent1) {
			return numOfPedigreeFromParent2;
		}

		return numOfPedigreeFromParent1;
	}

	private Integer getPedigreeLevel(Integer gid, Integer parentNo, boolean includeDerivativeLine) {
		Integer parentId = getGermplasmProgenitorID(gid, parentNo);
		if(parentId!=null) {
            incrementPedigreeLevelCounter();
			return getPedigreeLevelCount(parentId,includeDerivativeLine);
		}
		return 0;
	}

	private Integer getGermplasmProgenitorID(Integer gid, Integer proNo) {
		if(gid==null) {
			return null;
		}

		Germplasm germplasm = getParentByGIDAndProgenitorNumber(gid, proNo);

		if(germplasm!=null) {
			return germplasm.getGid();
		}
		return null;
	}

	@Override
    public GermplasmPedigreeTree generatePedigreeTree(Integer gid, int level, Boolean includeDerivativeLines) {
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
     */
    private GermplasmPedigreeTreeNode addParents(GermplasmPedigreeTreeNode node, int level) {
        if (level == 1) {
            return node;
        } else {
            // get parents of node
            Germplasm germplasmOfNode = node.getGermplasm();
            final Integer maleGid = germplasmOfNode.getGpid2();
			final boolean excludeDerivativeLines = false;
			if (germplasmOfNode.getGnpgs() == -1) {
                // Get and add the source germplasm
				this.addNodeForKnownParent(node, level, maleGid, excludeDerivativeLines);
                
            } else if (germplasmOfNode.getGnpgs() >= 2) {
                // Get and add female and male parents
                final Integer femaleGid = germplasmOfNode.getGpid1();
				this.addNodeForParent(node, level, femaleGid, excludeDerivativeLines);
				this.addNodeForParent(node, level, maleGid, excludeDerivativeLines);

				// IF there are more parents, get and add each of them
                if (germplasmOfNode.getGnpgs() > 2) {
                	List<Germplasm> otherParents = germplasmDataManager.getProgenitorsByGIDWithPrefName(germplasmOfNode.getGid());
                    for (Germplasm otherParent : otherParents) {
                        GermplasmPedigreeTreeNode nodeForOtherParent = new GermplasmPedigreeTreeNode();
                        nodeForOtherParent.setGermplasm(otherParent);
                        node.getLinkedNodes().add(addParents(nodeForOtherParent, level - 1));
                    }
                }
            }
            return node;
        }
    }

	void addNodeForParent(GermplasmPedigreeTreeNode node, int level, final Integer parentGid,
			final boolean excludeDerivativeLines) {
		if (parentGid == 0) {
			this.addUnknownParent(node);

		} else {
			this.addNodeForKnownParent(node, level, parentGid, excludeDerivativeLines);
		}
	}

	private void addNodeForKnownParent(GermplasmPedigreeTreeNode node, int level, final Integer parentGid,
			final boolean excludeDerivativeLines) {
		Germplasm parent = germplasmDataManager.getGermplasmWithPrefName(parentGid);
		if (parent != null) {
			GermplasmPedigreeTreeNode nodeForParent = new GermplasmPedigreeTreeNode();
			nodeForParent.setGermplasm(parent);
			if (excludeDerivativeLines) {
				node.getLinkedNodes().add(addParentsExcludeDerivativeLines(nodeForParent, level - 1));
			} else {
				node.getLinkedNodes().add(addParents(nodeForParent, level - 1));
			}
		}
	}

	private void addUnknownParent(GermplasmPedigreeTreeNode node) {
		GermplasmPedigreeTreeNode nodeForParent = new GermplasmPedigreeTreeNode();
		nodeForParent.setGermplasm(this.germplasmDataManager.getUnknownGermplasmWithPreferredName());
		node.getLinkedNodes().add(nodeForParent);
	}
    

    /**
     * Given a GermplasmPedigreeTreeNode and the level of the desired tree, add parents 
     * to the node recursively excluding derivative lines until the specified level of 
     * the tree is reached.
     * 
     * @param node
     * @param level
     * @return the given GermplasmPedigreeTreeNode with its parents added to it
     */
    private GermplasmPedigreeTreeNode addParentsExcludeDerivativeLines(GermplasmPedigreeTreeNode node, int level) {
        if (level == 1) {
            return node;
        } else {
            // get parents of node
            Germplasm germplasmOfNode = node.getGermplasm();
            
            final Integer femaleGid = germplasmOfNode.getGpid1();
			final boolean excludeDerivativeLines = true;
			if (germplasmOfNode.getGnpgs() == -1) {
                // get and add the source germplasm
                
                Germplasm parent = germplasmDataManager.getGermplasmWithPrefName(femaleGid);
                if (parent != null) {
                	this.addNodeForKnownParent(node, level, parent.getGpid1(), excludeDerivativeLines);
                	this.addNodeForKnownParent(node, level, parent.getGpid2(), excludeDerivativeLines);
                }
            } else if (germplasmOfNode.getGnpgs() >= 2) {
                // Get and add female and male parents
            	this.addNodeForParent(node, level, femaleGid, excludeDerivativeLines);
            	this.addNodeForParent(node, level, germplasmOfNode.getGpid2(), excludeDerivativeLines);

                if (germplasmOfNode.getGnpgs() > 2) {
                    // if there are more parents, get and add each of them
                    List<Germplasm> otherParents = germplasmDataManager.getProgenitorsByGIDWithPrefName(germplasmOfNode.getGid());
                    for (Germplasm otherParent : otherParents) {
                        GermplasmPedigreeTreeNode nodeForOtherParent = new GermplasmPedigreeTreeNode();
                        nodeForOtherParent.setGermplasm(otherParent);
                        node.getLinkedNodes().add(addParentsExcludeDerivativeLines(nodeForOtherParent, level - 1));
                    }
                }           
            }
            return node;
        }
    }
    
    @Override
    public GermplasmPedigreeTree getMaintenanceNeighborhood(Integer gid, int numberOfStepsBackward, int numberOfStepsForward) {
        
        return getNeighborhood(gid, numberOfStepsBackward, numberOfStepsForward, 'M');
    }
    
    @Override
    public GermplasmPedigreeTree getDerivativeNeighborhood(Integer gid, int numberOfStepsBackward, int numberOfStepsForward) {
        
        return getNeighborhood(gid, numberOfStepsBackward, numberOfStepsForward, 'D');
    }



    private GermplasmPedigreeTree getNeighborhood(Integer gid, int numberOfStepsBackward, int numberOfStepsForward, char methodType) {
        GermplasmPedigreeTree neighborhood = new GermplasmPedigreeTree();

        // get the root of the neighborhood
        Object[] traceResult = traceRoot(gid, numberOfStepsBackward, methodType);

        if (traceResult != null && traceResult.length >= 2) {
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
     */
    private Object[] traceRoot(Integer gid, int steps, char methodType) {
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
     */
    private GermplasmPedigreeTreeNode getDerivedLines(GermplasmPedigreeTreeNode node, int steps, char methodType) {
        if (steps <= 0) {
            return node;
        } else {
            Integer gid = node.getGermplasm().getGid();
            List<Germplasm> derivedGermplasms = getChildren(gid, methodType);
            for (Germplasm g : derivedGermplasms) {
                GermplasmPedigreeTreeNode derivedNode = new GermplasmPedigreeTreeNode();
                derivedNode.setGermplasm(g);
                node.getLinkedNodes().add(getDerivedLines(derivedNode, steps - 1, methodType));
            }

            return node;
        }
    }


    private List<Germplasm> getChildren(Integer gid, char methodType) {
    	return getGermplasmDao().getChildren(gid, methodType);
	}

	@Override
    public Germplasm getParentByGIDAndProgenitorNumber(Integer gid, Integer progenitorNumber) {
		return getGermplasmDao().getProgenitorByGID(gid, progenitorNumber);
    }

    @Override
    public List<Object[]> getDescendants(Integer gid, int start, int numOfRows) {
        List<Object[]> result = new ArrayList<>();
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
                		getProgenitorDao().getByGIDAndPID(g.getGid(), gid).getProgenitorNumber();
            }
            germplasmList[1] = g;

            result.add(germplasmList);
        }

        return result;
    }

    private List<Germplasm> getGermplasmDescendantByGID(Integer gid, int start,
			int numOfRows) {
    	return getGermplasmDao().getGermplasmDescendantByGID(gid, start, numOfRows);
	}

	@Override
    public long countDescendants(Integer gid) {
		 return getGermplasmDao().countGermplasmDescendantByGID(gid);
    }

    @Override
    public List<Germplasm> getManagementNeighbors(Integer gid, int start, int numOfRows) {
    	return getGermplasmDao().getManagementNeighbors(gid, start, numOfRows);
    }

    @Override
    public long countManagementNeighbors(Integer gid) {
    	return getGermplasmDao().countManagementNeighbors(gid);
    }

    @Override
    public long countGroupRelatives(Integer gid) {
    	return getGermplasmDao().countGroupRelatives(gid);
    }

    @Override
    public List<Germplasm> getGroupRelatives(Integer gid, int start, int numRows) {
    	return getGermplasmDao().getGroupRelatives(gid, start, numRows);
    }

    @Override
    public List<Germplasm> getGenerationHistory(Integer gid) {
        List<Germplasm> toreturn = new ArrayList<>();

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
    
	public GermplasmDataManager getGermplasmDataManager() {
	    return this.germplasmDataManager;
    }
    
	public void setGermplasmDataManager(GermplasmDataManager germplasmDataManager) {
		this.germplasmDataManager = germplasmDataManager;
	}

    public int calculateRecurrentParent(Integer maleParentGID, Integer femaleParentGID) {
        Germplasm maleParent = getGermplasmDataManager().getGermplasmByGID(maleParentGID);
        Germplasm femaleParent = getGermplasmDataManager().getGermplasmByGID(femaleParentGID);

        if (maleParent == null || femaleParent == null) {
            return NONE;
        }

        if (femaleParent.getGnpgs() >= 2 && (maleParentGID.equals(femaleParent.getGpid1())
                    || maleParentGID.equals(femaleParent.getGpid2())) ) {
            return MALE_RECURRENT;
        } else if(maleParent.getGnpgs() >= 2 && (femaleParentGID.equals(maleParent.getGpid1())
            || femaleParentGID.equals(maleParent.getGpid2())) ) {
            return FEMALE_RECURRENT;
        }

        return NONE;
    }

    
}

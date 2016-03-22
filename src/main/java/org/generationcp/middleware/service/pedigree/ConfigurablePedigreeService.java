
package org.generationcp.middleware.service.pedigree;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.germplasm.BackcrossElementNode;
import org.generationcp.middleware.pojos.germplasm.GermplasmCrossElementNode;
import org.generationcp.middleware.pojos.germplasm.GermplasmCrossNode;
import org.generationcp.middleware.pojos.germplasm.SingleGermplasmCrossElementNode;
import org.generationcp.middleware.service.FieldbookServiceImpl;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ConfigurablePedigreeService implements PedigreeService {

	private static final Logger LOG = LoggerFactory.getLogger(FieldbookServiceImpl.class);

	private PedigreeDataManagerFactory pedigreeDataManagerFactory;

	private String cropName;

	public ConfigurablePedigreeService() {

	}

	public ConfigurablePedigreeService(HibernateSessionProvider sessionProvider, final String cropName) {
		this.cropName = cropName;
		this.pedigreeDataManagerFactory = new PedigreeDataManagerFactory(sessionProvider);

	}

	@Override
	public String getCropName() {
		return cropName;
	}

	@Override
	public String getCrossExpansion(Integer gid, CrossExpansionProperties crossExpansionProperties) {
		return this.getCrossExpansion(gid, null, crossExpansionProperties);
	}

	@Override
	public String getCrossExpansion(Integer gid, Integer level, CrossExpansionProperties crossExpansionProperties) {
		System.out.println("Expanding - " + gid);
		Germplasm germplasm = this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(gid);
		if (germplasm != null) {
			int rootLevel = level == null ? crossExpansionProperties.getCropGenerationLevel(this.getCropName()) : level;
			final SingleGermplasmCrossElementNode startElement = new SingleGermplasmCrossElementNode();
			//startElement.setGermplasm(germplasm);
			setMetaData(startElement, germplasm, true);

			GermplasmCrossElementNode cross =
					this.expandGermplasmCross(startElement, rootLevel, rootLevel, false);
			return cross.getCrossExpansionString(getCropName(), crossExpansionProperties, this.pedigreeDataManagerFactory);
		} else {
			return "";
		}
	}

	@Override
	public String getCrossExpansion(final Germplasm germplasm, final Integer level, final CrossExpansionProperties crossExpansionProperties) {

		// We need to clean up our pedigree service
		throw new UnsupportedOperationException("This method is curently not supported and"
				+ " really should not be called from anywhere in the code.");
	}

	private GermplasmCrossElementNode expandGermplasmCross(GermplasmCrossElementNode element, int rootLevel, int currentLevel, boolean forComplexCross) {
		if (currentLevel == 0) {
			// if the level is zero then there is no need to expand and the
			// element
			// should be returned as is
			return element;
		} else {
			if (element instanceof SingleGermplasmCrossElementNode) {
				// No meta data attached to this single class as it is passed in
				SingleGermplasmCrossElementNode singleGermplasm = (SingleGermplasmCrossElementNode) element;
				Germplasm germplasmToExpand = singleGermplasm.getGermplasm();

				if (germplasmToExpand == null) {
					return singleGermplasm;
				}
				// If this is a maintenance or a derivative crossing
				if (germplasmToExpand.getGnpgs() < 0) {
					// for germplasms created via a derivative or maintenance
					// method
					// skip and then expand on the gpid1 parent
					if (germplasmToExpand.getGpid1() != null && germplasmToExpand.getGpid1() != 0 && !forComplexCross) {
						SingleGermplasmCrossElementNode nextElement = new SingleGermplasmCrossElementNode();

						Germplasm gpid1Germplasm =
								this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
										germplasmToExpand.getGpid1());

						if (gpid1Germplasm != null) {
							setMetaData(nextElement, gpid1Germplasm, false);

							//nextElement.setGermplasm(gpid1Germplasm);
							return this.expandGermplasmCross(nextElement, rootLevel, currentLevel, forComplexCross);
						} else {
							return element;
						}
					} else {
						return element;
					}
				// If this is a generative crossing
				} else {
					final GermplasmCrossNode cross = new GermplasmCrossNode();

					final Method method =
							this.pedigreeDataManagerFactory.getGermplasmDataManager().getMethodByID(germplasmToExpand.getMethodId());
					// Note not a parent element and thus the level is the same
					setMetaData(cross, germplasmToExpand, currentLevel == rootLevel);

					if (method != null) {
						String methodName = method.getMname();
						if (methodName != null) {
							methodName = methodName.toLowerCase();
						} else {
							methodName = "";
						}

						if (methodName.contains("single cross")) {
							processSingleCross(element, rootLevel, currentLevel, forComplexCross, germplasmToExpand, cross);
						} else if (methodName.contains("double cross")) {
							processDoubleCross(element, rootLevel, currentLevel, forComplexCross, germplasmToExpand, cross);
						} else if (methodName.contains("three-way cross")) {
							processThreeWayCross(element, rootLevel, currentLevel, forComplexCross, germplasmToExpand, cross);
						} else if (methodName.contains("backcross")) {
							BackcrossElementNode generateBackcross = generateBackcross(element, rootLevel, currentLevel, forComplexCross, germplasmToExpand, cross);
							if(generateBackcross != null) {
								return generateBackcross;
							}
						} else if (methodName.contains("cross") && methodName.contains("complex")) {
							processComplexCross(element, rootLevel, currentLevel, germplasmToExpand, cross);
						} else if (methodName.contains("cross")) {
							processNormalCross(element, currentLevel, germplasmToExpand, cross);
						} else {
							SingleGermplasmCrossElementNode crossElement = new SingleGermplasmCrossElementNode();
							// crossElement.setGermplasm(germplasmToExpand);
							setMetaData(crossElement, germplasmToExpand, currentLevel == rootLevel);

							return crossElement;
						}
						return cross;
					} else {
						this.logAndThrowException("Error with expanding cross, can not find method with id: " + germplasmToExpand.getMethodId());
					}
				}
			} else {
				this.logAndThrowException("expandGermplasmCross was incorrectly called");
			}
		}
		return element;
	}

	private void processNormalCross(GermplasmCrossElementNode element, int level, Germplasm germplasmToExpand, final GermplasmCrossNode cross) {
		Germplasm firstParent =
				this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
						germplasmToExpand.getGpid1());
		Germplasm secondParent =
				this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
						germplasmToExpand.getGpid2());

		SingleGermplasmCrossElementNode firstParentElem = new SingleGermplasmCrossElementNode();
		//firstParentElem.setGermplasm(firstParent);
		setMetaData(firstParentElem, firstParent, false);

		SingleGermplasmCrossElementNode secondParentElem = new SingleGermplasmCrossElementNode();
		//secondParentElem.setGermplasm(secondParent);
		setMetaData(secondParentElem, secondParent, false);

		cross.setFirstParent(firstParentElem);
		cross.setSecondParent(secondParentElem);
		cross.setNumberOfCrossesBefore(0);
	}

	private void processComplexCross(GermplasmCrossElementNode element, int rootLevel, int level, Germplasm germplasmToExpand, final GermplasmCrossNode cross) {
		// get the immediate parents
		Germplasm firstParent =
				this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
						germplasmToExpand.getGpid1());
		SingleGermplasmCrossElementNode firstParentElem = new SingleGermplasmCrossElementNode();
		//firstParentElem.setGermplasm(firstParent);
		setMetaData(firstParentElem, firstParent, false);

		Germplasm secondParent =
				this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
						germplasmToExpand.getGpid2());
		SingleGermplasmCrossElementNode secondParentElem = new SingleGermplasmCrossElementNode();
		//secondParentElem.setGermplasm(secondParent);
		setMetaData(secondParentElem, secondParent, false);

		// expand the parents as needed, depends on the
		// level
		GermplasmCrossElementNode expandedFirstParent = this.expandGermplasmCross(firstParentElem, rootLevel, level, true);
		GermplasmCrossElementNode expandedSecondParent = this.expandGermplasmCross(secondParentElem, rootLevel, level, true);

		// get the number of crosses in the first parent
		int numOfCrosses = 0;
		if (expandedFirstParent instanceof GermplasmCrossNode) {
			numOfCrosses = ((GermplasmCrossNode) expandedFirstParent).getNumberOfCrossesBefore() + 1;
		}

		cross.setFirstParent(expandedFirstParent);
		cross.setSecondParent(expandedSecondParent);
		cross.setNumberOfCrossesBefore(numOfCrosses);
	}

	private BackcrossElementNode generateBackcross(GermplasmCrossElementNode element, int rootLevel, int currentLevel, boolean forComplexCross, Germplasm germplasmToExpand,
			final GermplasmCrossNode cross) {
		BackcrossElementNode backcross = new BackcrossElementNode();
		setMetaData(backcross, germplasmToExpand, currentLevel == rootLevel);

		Germplasm firstParent =
				this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
						germplasmToExpand.getGpid1());
		Germplasm secondParent =
				this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
						germplasmToExpand.getGpid2());

		boolean itsABackCross = false;

		// determine which is the recurrent parent
		if (firstParent != null && secondParent != null) {
			SingleGermplasmCrossElementNode recurringParentElem = new SingleGermplasmCrossElementNode();
			SingleGermplasmCrossElementNode parentElem = new SingleGermplasmCrossElementNode();
			if (secondParent.getGnpgs() >= 2) {
				if (firstParent.getGid().equals(secondParent.getGpid1())
						|| firstParent.getGid().equals(secondParent.getGpid2())) {
					itsABackCross = true;
					backcross.setRecurringParentOnTheRight(false);

					//recurringParentElem.setGermplasm(firstParent);
					setMetaData(recurringParentElem, firstParent, false);

					Germplasm toCheck = null;
					if (firstParent.getGid().equals(secondParent.getGpid1()) && secondParent.getGpid2() != null) {
						toCheck =
								this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
										secondParent.getGpid2());
					} else if (firstParent.getGid().equals(secondParent.getGpid2()) && secondParent.getGpid1() != null) {
						toCheck =
								this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
										secondParent.getGpid1());
					}
					Object[] numOfDosesAndOtherParent =
							this.determineNumberOfRecurringParent(firstParent.getGid(), toCheck);
					// parentElem.setGermplasm((Germplasm) numOfDosesAndOtherParent[1]);
					setMetaData(parentElem, ((Germplasm) numOfDosesAndOtherParent[1]), false);

					backcross.setNumberOfDosesOfRecurringParent(((Integer) numOfDosesAndOtherParent[0]).intValue() + 2);
				}
			} else if (firstParent.getGnpgs() >= 2) {
				if (secondParent.getGid().equals(firstParent.getGpid1())
						|| secondParent.getGid().equals(firstParent.getGpid2())) {
					itsABackCross = true;
					backcross.setRecurringParentOnTheRight(true);

					// recurringParentElem.setGermplasm(secondParent);
					setMetaData(recurringParentElem, secondParent, false);

					Germplasm toCheck = null;
					if (secondParent.getGid().equals(firstParent.getGpid1()) && firstParent.getGpid2() != null) {
						toCheck =
								this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
										firstParent.getGpid2());
					} else if (secondParent.getGid().equals(firstParent.getGpid2()) && firstParent.getGpid1() != null) {
						toCheck =
								this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
										firstParent.getGpid1());
					}
					Object[] numOfDosesAndOtherParent =
							this.determineNumberOfRecurringParent(secondParent.getGid(), toCheck);
					// parentElem.setGermplasm((Germplasm) numOfDosesAndOtherParent[1]);
					setMetaData(parentElem, ((Germplasm) numOfDosesAndOtherParent[1]), false);

					backcross.setNumberOfDosesOfRecurringParent(((Integer) numOfDosesAndOtherParent[0]).intValue() + 2);
				}
			} else {
				itsABackCross = false;
			}

			if (itsABackCross) {
				GermplasmCrossElementNode expandedRecurringParent =
						this.expandGermplasmCross(recurringParentElem, rootLevel, currentLevel - 1, forComplexCross);
				backcross.setRecurringParent(expandedRecurringParent);

				GermplasmCrossElementNode expandedParent =
						this.expandGermplasmCross(parentElem, rootLevel, currentLevel - 1, forComplexCross);
				backcross.setParent(expandedParent);

				return backcross;
			}
		}

		if (!itsABackCross) {
			SingleGermplasmCrossElementNode firstParentElem = new SingleGermplasmCrossElementNode();
			// firstParentElem.setGermplasm(firstParent);
			setMetaData(firstParentElem, firstParent, false);

			SingleGermplasmCrossElementNode secondParentElem = new SingleGermplasmCrossElementNode();
			// secondParentElem.setGermplasm(secondParent);
			setMetaData(secondParentElem, secondParent, false);

			cross.setFirstParent(firstParentElem);
			cross.setSecondParent(secondParentElem);
			cross.setNumberOfCrossesBefore(0);
		}

		return null;
	}

	private void processThreeWayCross(GermplasmCrossElementNode element, int rootLevel, int level, boolean forComplexCross, Germplasm germplasmToExpand,
			final GermplasmCrossNode cross) {
		// get the two parents first
		Germplasm firstParent =
				this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmByGID(
						germplasmToExpand.getGpid1());
		Germplasm secondParent =
				this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmByGID(
						germplasmToExpand.getGpid2());

		// check for the parent generated by a cross, the
		// other one should be a derived germplasm
		if (firstParent != null && firstParent.getGnpgs() > 0) {
			// the first parent is the one created by a
			// cross
			Germplasm firstGrandParent =
					this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
							firstParent.getGpid1());
			SingleGermplasmCrossElementNode firstGrandParentElem = new SingleGermplasmCrossElementNode();
			//firstGrandParentElem.setGermplasm(firstGrandParent);

			setMetaData(firstGrandParentElem, firstGrandParent, false);


			Germplasm secondGrandParent =
					this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
							firstParent.getGpid2());
			SingleGermplasmCrossElementNode secondGrandParentElem = new SingleGermplasmCrossElementNode();
			// secondGrandParentElem.setGermplasm(secondGrandParent);
			setMetaData(secondGrandParentElem, secondGrandParent, false);


			// expand the grand parents as needed, depends
			// on the level
			GermplasmCrossElementNode expandedFirstGrandParent =
					this.expandGermplasmCross(firstGrandParentElem, rootLevel, level - 1, forComplexCross);
			GermplasmCrossElementNode expandedSecondGrandParent =
					this.expandGermplasmCross(secondGrandParentElem, rootLevel, level - 1, forComplexCross);

			// make the cross object for the grand parents
			GermplasmCrossNode crossForGrandParents = new GermplasmCrossNode();
			crossForGrandParents.setFirstParent(expandedFirstGrandParent);
			crossForGrandParents.setSecondParent(expandedSecondGrandParent);
			// compute the number of crosses before this one
			int numOfCrossesForGrandParents = 0;
			if (expandedFirstGrandParent instanceof GermplasmCrossNode) {
				numOfCrossesForGrandParents =
						((GermplasmCrossNode) expandedFirstGrandParent).getNumberOfCrossesBefore() + 1;
			}
			crossForGrandParents.setNumberOfCrossesBefore(numOfCrossesForGrandParents);
			setMetaData(crossForGrandParents, firstParent, false);

			// make the element for the second parent
			secondParent =
					this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
							germplasmToExpand.getGpid2());
			SingleGermplasmCrossElementNode secondParentElem = new SingleGermplasmCrossElementNode();
			// secondParentElem.setGermplasm(secondParent);
			setMetaData(secondParentElem, secondParent, false);


			// create the cross to return
			cross.setFirstParent(crossForGrandParents);
			cross.setSecondParent(secondParentElem);
			// compute the number of crosses before this
			// cross
			cross.setNumberOfCrossesBefore(numOfCrossesForGrandParents + 1);
		} else if (secondParent != null) {
			// the second parent is the one created by a
			// cross
			Germplasm firstGrandParent =
					this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
							secondParent.getGpid1());
			SingleGermplasmCrossElementNode firstGrandParentElem = new SingleGermplasmCrossElementNode();
			// firstGrandParentElem.setGermplasm(firstGrandParent);

			setMetaData(firstGrandParentElem, firstGrandParent, false);


			Germplasm secondGrandParent =
					this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
							secondParent.getGpid2());
			SingleGermplasmCrossElementNode secondGrandParentElem = new SingleGermplasmCrossElementNode();
			// secondGrandParentElem.setGermplasm(secondGrandParent);

			setMetaData(secondGrandParentElem, secondGrandParent, false);


			// expand the grand parents as needed, depends
			// on the level
			GermplasmCrossElementNode expandedFirstGrandParent =
					this.expandGermplasmCross(firstGrandParentElem, rootLevel, level - 1, forComplexCross);
			GermplasmCrossElementNode expandedSecondGrandParent =
					this.expandGermplasmCross(secondGrandParentElem, rootLevel, level - 1, forComplexCross);

			// make the cross object for the grand parents
			GermplasmCrossNode crossForGrandParents = new GermplasmCrossNode();
			crossForGrandParents.setFirstParent(expandedFirstGrandParent);
			crossForGrandParents.setSecondParent(expandedSecondGrandParent);
			setMetaData(crossForGrandParents, secondParent, false);

			// compute the number of crosses before this one
			int numOfCrossesForGrandParents = 0;
			if (expandedFirstGrandParent instanceof GermplasmCrossNode) {
				numOfCrossesForGrandParents =
						((GermplasmCrossNode) expandedFirstGrandParent).getNumberOfCrossesBefore() + 1;
			}
			crossForGrandParents.setNumberOfCrossesBefore(numOfCrossesForGrandParents);

			// make the element for the first parent
			firstParent =
					this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
							germplasmToExpand.getGpid1());
			SingleGermplasmCrossElementNode firstParentElem = new SingleGermplasmCrossElementNode();
			// firstParentElem.setGermplasm(firstParent);

			setMetaData(firstParentElem, firstParent, false);


			// create the cross to return
			cross.setFirstParent(crossForGrandParents);
			cross.setSecondParent(firstParentElem);
			cross.setNumberOfCrossesBefore(numOfCrossesForGrandParents + 1);
		} else {
			SingleGermplasmCrossElementNode firstParentElem = new SingleGermplasmCrossElementNode();
			// firstParentElem.setGermplasm(firstParent);
			setMetaData(firstParentElem, firstParent, false);

			SingleGermplasmCrossElementNode secondParentElem = new SingleGermplasmCrossElementNode();
			// secondParentElem.setGermplasm(secondParent);
			setMetaData(secondParentElem, firstParent, false);

			cross.setFirstParent(firstParentElem);
			cross.setSecondParent(secondParentElem);
			cross.setNumberOfCrossesBefore(0);
		}
	}

	private void processDoubleCross(GermplasmCrossElementNode element, int rootLevel, int level, boolean forComplexCross, Germplasm germplasmToExpand,
			final GermplasmCrossNode cross) {
		// get the grandparents on both sides
		Germplasm firstParent =
				this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmByGID(
						germplasmToExpand.getGpid1());
		Germplasm secondParent =
				this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmByGID(
						germplasmToExpand.getGpid2());

		SingleGermplasmCrossElementNode firstGrandParentElem = new SingleGermplasmCrossElementNode();
		SingleGermplasmCrossElementNode secondGrandParentElem = new SingleGermplasmCrossElementNode();
		processParent(element, level, firstParent, firstGrandParentElem, secondGrandParentElem);

		SingleGermplasmCrossElementNode thirdGrandParentElem = new SingleGermplasmCrossElementNode();
		SingleGermplasmCrossElementNode fourthGrandParentElem = new SingleGermplasmCrossElementNode();
		processParent(element, level, secondParent, thirdGrandParentElem, fourthGrandParentElem);

		// expand the grand parents as needed, depends on
		// the level
		GermplasmCrossElementNode expandedFirstGrandParent = null;
		GermplasmCrossElementNode expandedSecondGrandParent = null;
		GermplasmCrossElementNode expandedThirdGrandParent = null;
		GermplasmCrossElementNode expandedFourthGrandParent = null;

		if (firstParent != null) {
			expandedFirstGrandParent = this.expandGermplasmCross(firstGrandParentElem, rootLevel, level - 1, forComplexCross);
			expandedSecondGrandParent = this.expandGermplasmCross(secondGrandParentElem, rootLevel, level - 1, forComplexCross);
		}

		if (secondParent != null) {
			expandedThirdGrandParent = this.expandGermplasmCross(thirdGrandParentElem, rootLevel, level - 1, forComplexCross);
			expandedFourthGrandParent = this.expandGermplasmCross(fourthGrandParentElem, rootLevel, level - 1, forComplexCross);
		}

		// create the cross object for the first pair of
		// grand parents
		GermplasmCrossNode firstCross = new GermplasmCrossNode();
		int numOfCrossesForFirst = 0;
		if (firstParent != null) {
			firstCross.setFirstParent(expandedFirstGrandParent);
			firstCross.setSecondParent(expandedSecondGrandParent);
			// compute the number of crosses before this
			// cross
			if (expandedFirstGrandParent instanceof GermplasmCrossNode) {
				numOfCrossesForFirst = ((GermplasmCrossNode) expandedFirstGrandParent).getNumberOfCrossesBefore() + 1;
			}
			firstCross.setNumberOfCrossesBefore(numOfCrossesForFirst);
			setMetaData(firstCross, firstParent, false);

		}

		// create the cross object for the second pair of
		// grand parents
		GermplasmCrossNode secondCross = new GermplasmCrossNode();
		if (secondParent != null) {
			secondCross.setFirstParent(expandedThirdGrandParent);
			secondCross.setSecondParent(expandedFourthGrandParent);
			// compute the number of crosses before this
			// cross
			int numOfCrossesForSecond = 0;
			if (expandedThirdGrandParent instanceof GermplasmCrossNode) {
				numOfCrossesForSecond = ((GermplasmCrossNode) expandedThirdGrandParent).getNumberOfCrossesBefore() + 1;
			}
			secondCross.setNumberOfCrossesBefore(numOfCrossesForSecond);
			setMetaData(secondCross, secondParent, false);

		}

		// create the cross of the two sets of grandparents,
		// this will be returned
		if (firstParent != null) {
			cross.setFirstParent(firstCross);
		} else {
			SingleGermplasmCrossElementNode firstParentElem = new SingleGermplasmCrossElementNode();
			// firstParentElem.setGermplasm(firstParent);
			setMetaData(firstParentElem, firstParent, false);
			cross.setFirstParent(firstParentElem);
		}

		if (secondParent != null) {
			cross.setSecondParent(secondCross);
		} else {
			SingleGermplasmCrossElementNode secondParentElem = new SingleGermplasmCrossElementNode();
			// secondParentElem.setGermplasm(secondParent);
			setMetaData(secondParentElem, secondParent, false);
			cross.setSecondParent(secondParentElem);
		}

		// compute the number of crosses before the cross to
		// be returned
		int numOfCrosses = 0;
		if (firstParent != null) {
			numOfCrosses = numOfCrossesForFirst + 1;
			if (expandedSecondGrandParent instanceof GermplasmCrossNode) {
				numOfCrosses =
						numOfCrosses + ((GermplasmCrossNode) expandedSecondGrandParent).getNumberOfCrossesBefore() + 1;
			}
		}
		cross.setNumberOfCrossesBefore(numOfCrosses);
	}

	private void processParent(GermplasmCrossElementNode element, int level, Germplasm parent,
			SingleGermplasmCrossElementNode firstGrandParentElem, SingleGermplasmCrossElementNode secondGrandParentElem) {
		Germplasm firstGrandParent;
		Germplasm secondGrandParent;
		if (parent != null) {
			firstGrandParent =
					this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
							parent.getGpid1());
			//firstGrandParentElem.setGermplasm(firstGrandParent);
			setMetaData(firstGrandParentElem, firstGrandParent, false);


			secondGrandParent =
					this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
							parent.getGpid2());
			//secondGrandParentElem.setGermplasm(secondGrandParent);
			setMetaData(secondGrandParentElem, secondGrandParent, false);

		}
	}

	private void processSingleCross(GermplasmCrossElementNode element, int rootLevel, int level, boolean forComplexCross, Germplasm germplasmToExpand,
			final GermplasmCrossNode cross) {
		// get the immediate parents
		Germplasm firstParent =
				this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
						germplasmToExpand.getGpid1());
		SingleGermplasmCrossElementNode firstParentElem = new SingleGermplasmCrossElementNode();
		//firstParentElem.setGermplasm(firstParent);
		setMetaData(firstParentElem, firstParent, false);


		Germplasm secondParent =
				this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
						germplasmToExpand.getGpid2());
		SingleGermplasmCrossElementNode secondParentElem = new SingleGermplasmCrossElementNode();
//		secondParentElem.setGermplasm(secondParent);
		setMetaData(secondParentElem, secondParent, false);

		// expand the parents as needed, depends on the
		// level
		GermplasmCrossElementNode expandedFirstParent =
				this.expandGermplasmCross(firstParentElem, rootLevel, level - 1, forComplexCross);
		GermplasmCrossElementNode expandedSecondParent =
				this.expandGermplasmCross(secondParentElem, rootLevel, level - 1, forComplexCross);

		// get the number of crosses in the first parent
		int numOfCrosses = 0;
		if (expandedFirstParent instanceof GermplasmCrossNode) {
			numOfCrosses = ((GermplasmCrossNode) expandedFirstParent).getNumberOfCrossesBefore() + 1;
		}

		cross.setFirstParent(expandedFirstParent);
		cross.setSecondParent(expandedSecondParent);
		cross.setNumberOfCrossesBefore(numOfCrosses);
	}

	private void setMetaData(GermplasmCrossElementNode childElement, Germplasm germplasm, boolean rootNode) {
		childElement.setGermplasm(germplasm);
		childElement.setRootNode(rootNode);
	}

	/**
	 *
	 * @param recurringParentGid
	 * @param toCheck
	 * @return an array of 2 Objects, first is an Integer which is the number of doses of the recurring parent, and the other is a Germplasm
	 *         object representing the parent crossed with the recurring parent.
	 */
	private Object[] determineNumberOfRecurringParent(Integer recurringParentGid, Germplasm toCheck) {
		Object[] toreturn = new Object[2];
		if (toCheck == null) {
			toreturn[0] = Integer.valueOf(0);
			toreturn[1] = null;
		} else if (toCheck.getGpid1() != null && !toCheck.getGpid1().equals(recurringParentGid) && toCheck.getGpid2() != null
				&& !toCheck.getGpid2().equals(recurringParentGid)) {
			toreturn[0] = Integer.valueOf(0);
			toreturn[1] = toCheck;
		} else if (toCheck.getGpid1() != null && toCheck.getGpid1().equals(recurringParentGid)) {
			Germplasm nextToCheck = null;
			if (toCheck.getGpid2() != null) {
				nextToCheck = this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(toCheck.getGpid2());
			}
			Object[] returned = this.determineNumberOfRecurringParent(recurringParentGid, nextToCheck);
			toreturn[0] = (Integer) returned[0] + 1;
			toreturn[1] = returned[1];
		} else if (toCheck.getGpid2() != null && toCheck.getGpid2().equals(recurringParentGid)) {
			Germplasm nextToCheck = null;
			if (toCheck.getGpid1() != null) {
				nextToCheck = this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(toCheck.getGpid1());
			}
			Object[] returned = this.determineNumberOfRecurringParent(recurringParentGid, nextToCheck);
			toreturn[0] = (Integer) returned[0] + 1;
			toreturn[1] = returned[1];
		} else {
			toreturn[0] = Integer.valueOf(0);
			toreturn[1] = toCheck;
		}

		return toreturn;
	}

	private void logAndThrowException(String message) {
		final MiddlewareQueryException exception = new MiddlewareQueryException(message);
		LOG.error(message, exception);
		throw exception;
	}


}

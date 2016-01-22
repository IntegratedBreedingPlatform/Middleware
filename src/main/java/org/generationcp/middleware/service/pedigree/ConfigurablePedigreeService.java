
package org.generationcp.middleware.service.pedigree;

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

	public ConfigurablePedigreeService() {

	}

	public ConfigurablePedigreeService(HibernateSessionProvider sessionProvider) {
		this.pedigreeDataManagerFactory = new PedigreeDataManagerFactory(sessionProvider);

	}

	@Override
	public String getCrossExpansion(Integer gid, CrossExpansionProperties crossExpansionProperties) {
		return this.getCrossExpansion(gid, null, crossExpansionProperties);
	}

	@Override
	public String getCrossExpansion(Integer gid, Integer level, CrossExpansionProperties crossExpansionProperties) {
		Germplasm germplasm = this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(gid);
		if (germplasm != null) {
			int rootLevel = level == null ? crossExpansionProperties.getDefaultLevel() : level;
			final SingleGermplasmCrossElement startElement = new SingleGermplasmCrossElement();
			startElement.setGermplasm(germplasm);
			startElement.setLevel(rootLevel);
			startElement.setRootLevel(rootLevel);
			startElement.setNames(this.pedigreeDataManagerFactory.getGermplasmDataManager().getNamesByGID(gid, null, null));
			GermplasmCrossElement cross =
					this.expandGermplasmCross(startElement, rootLevel, false);
			return cross.toString();
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

	private GermplasmCrossElement expandGermplasmCross(GermplasmCrossElement element, int level, boolean forComplexCross) {
		if (level == 0) {
			// if the level is zero then there is no need to expand and the
			// element
			// should be returned as is
			return element;
		} else {
			if (element instanceof SingleGermplasmCrossElement) {
				// No meta data attached to this single class as it is passed in
				SingleGermplasmCrossElement singleGermplasm = (SingleGermplasmCrossElement) element;
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
						SingleGermplasmCrossElement nextElement = new SingleGermplasmCrossElement();

						Germplasm gpid1Germplasm =
								this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
										germplasmToExpand.getGpid1());

						if (gpid1Germplasm != null) {
							nextElement.setGermplasm(gpid1Germplasm);
							// -1 on the element because its a child element.
							setMetaData(nextElement, element, gpid1Germplasm.getGid(), level-1);
							return this.expandGermplasmCross(nextElement, level, forComplexCross);
						} else {
							return element;
						}
					} else {
						return element;
					}
				// If this is a generative crossing
				} else {
					final GermplasmCross cross = new GermplasmCross();

					final Method method =
							this.pedigreeDataManagerFactory.getGermplasmDataManager().getMethodByID(germplasmToExpand.getMethodId());
					// Note not a parent element and thus the level is the samel
					setMetaData(cross, element, germplasmToExpand.getGid(), level);

					if (method != null) {
						String methodName = method.getMname();
						if (methodName != null) {
							methodName = methodName.toLowerCase();
						} else {
							methodName = "";
						}

						if (methodName.contains("single cross")) {
							// get the immediate parents
							Germplasm firstParent =
									this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
											germplasmToExpand.getGpid1());
							SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
							firstParentElem.setGermplasm(firstParent);
							setMetaData(firstParentElem, element, firstParent.getGid(), level-1);


							Germplasm secondParent =
									this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
											germplasmToExpand.getGpid2());
							SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
							secondParentElem.setGermplasm(secondParent);

							setMetaData(secondParentElem, element, secondParent.getGid(), level-1);

							// expand the parents as needed, depends on the
							// level
							GermplasmCrossElement expandedFirstParent =
									this.expandGermplasmCross(firstParentElem, level - 1, forComplexCross);
							GermplasmCrossElement expandedSecondParent =
									this.expandGermplasmCross(secondParentElem, level - 1, forComplexCross);

							// get the number of crosses in the first parent
							int numOfCrosses = 0;
							if (expandedFirstParent instanceof GermplasmCross) {
								numOfCrosses = ((GermplasmCross) expandedFirstParent).getNumberOfCrossesBefore() + 1;
							}

							cross.setFirstParent(expandedFirstParent);
							cross.setSecondParent(expandedSecondParent);
							cross.setNumberOfCrossesBefore(numOfCrosses);

						} else if (methodName.contains("double cross")) {
							// get the grandparents on both sides
							Germplasm firstParent =
									this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmByGID(
											germplasmToExpand.getGpid1());
							Germplasm secondParent =
									this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmByGID(
											germplasmToExpand.getGpid2());

							Germplasm firstGrandParent = null;
							SingleGermplasmCrossElement firstGrandParentElem = new SingleGermplasmCrossElement();
							Germplasm secondGrandParent = null;
							SingleGermplasmCrossElement secondGrandParentElem = new SingleGermplasmCrossElement();
							if (firstParent != null) {
								firstGrandParent =
										this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
												firstParent.getGpid1());
								firstGrandParentElem.setGermplasm(firstGrandParent);
								// Note sure if this is -1 or -2 since they are grand parents
								setMetaData(firstGrandParentElem, element, firstGrandParent.getGid(), level-1);


								secondGrandParent =
										this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
												firstParent.getGpid2());
								// Note sure if this is -1 or -2 since they are grand parents
								setMetaData(secondGrandParentElem, element, secondGrandParent.getGid(), level-1);

								secondGrandParentElem.setGermplasm(secondGrandParent);
							}

							Germplasm thirdGrandParent = null;
							SingleGermplasmCrossElement thirdGrandParentElem = new SingleGermplasmCrossElement();
							Germplasm fourthGrandParent = null;
							SingleGermplasmCrossElement fourthGrandParentElem = new SingleGermplasmCrossElement();
							if (secondParent != null) {
								thirdGrandParent =
										this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
												secondParent.getGpid1());
								thirdGrandParentElem.setGermplasm(thirdGrandParent);
								// Note sure if this is -1 or -2 since they are grand parents
								setMetaData(thirdGrandParentElem, element, thirdGrandParent.getGid(), level-1);

								fourthGrandParent =
										this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
												secondParent.getGpid2());

								fourthGrandParentElem.setGermplasm(fourthGrandParent);
								// Note sure if this is -1 or -2 since they are grand parents
								setMetaData(fourthGrandParentElem, element, fourthGrandParent.getGid(), level-1);

							}

							// expand the grand parents as needed, depends on
							// the level
							GermplasmCrossElement expandedFirstGrandParent = null;
							GermplasmCrossElement expandedSecondGrandParent = null;
							GermplasmCrossElement expandedThirdGrandParent = null;
							GermplasmCrossElement expandedFourthGrandParent = null;

							if (firstParent != null) {
								expandedFirstGrandParent = this.expandGermplasmCross(firstGrandParentElem, level - 1, forComplexCross);
								expandedSecondGrandParent = this.expandGermplasmCross(secondGrandParentElem, level - 1, forComplexCross);
							}

							if (secondParent != null) {
								expandedThirdGrandParent = this.expandGermplasmCross(thirdGrandParentElem, level - 1, forComplexCross);
								expandedFourthGrandParent = this.expandGermplasmCross(fourthGrandParentElem, level - 1, forComplexCross);
							}

							// create the cross object for the first pair of
							// grand parents
							GermplasmCross firstCross = new GermplasmCross();
							int numOfCrossesForFirst = 0;
							if (firstParent != null) {
								firstCross.setFirstParent(expandedFirstGrandParent);
								firstCross.setSecondParent(expandedSecondGrandParent);
								// compute the number of crosses before this
								// cross
								if (expandedFirstGrandParent instanceof GermplasmCross) {
									numOfCrossesForFirst = ((GermplasmCross) expandedFirstGrandParent).getNumberOfCrossesBefore() + 1;
								}
								firstCross.setNumberOfCrossesBefore(numOfCrossesForFirst);
								setMetaData(firstCross, element, firstParent.getGid(), level-1);

							}

							// create the cross object for the second pair of
							// grand parents
							GermplasmCross secondCross = new GermplasmCross();
							if (secondParent != null) {
								secondCross.setFirstParent(expandedThirdGrandParent);
								secondCross.setSecondParent(expandedFourthGrandParent);
								// compute the number of crosses before this
								// cross
								int numOfCrossesForSecond = 0;
								if (expandedThirdGrandParent instanceof GermplasmCross) {
									numOfCrossesForSecond = ((GermplasmCross) expandedThirdGrandParent).getNumberOfCrossesBefore() + 1;
								}
								secondCross.setNumberOfCrossesBefore(numOfCrossesForSecond);
								setMetaData(secondCross, element, secondParent.getGid(), level-1);

							}

							// create the cross of the two sets of grandparents,
							// this will be returned
							if (firstParent != null) {
								cross.setFirstParent(firstCross);
							} else {
								SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
								firstParentElem.setGermplasm(firstParent);
								setMetaData(firstParentElem, element, null, level-1);
								cross.setFirstParent(firstParentElem);
							}

							if (secondParent != null) {
								cross.setSecondParent(secondCross);
							} else {
								SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
								secondParentElem.setGermplasm(secondParent);
								setMetaData(secondParentElem, element, null, level-1);
								cross.setSecondParent(secondParentElem);
							}

							// compute the number of crosses before the cross to
							// be returned
							int numOfCrosses = 0;
							if (firstParent != null) {
								numOfCrosses = numOfCrossesForFirst + 1;
								if (expandedSecondGrandParent instanceof GermplasmCross) {
									numOfCrosses =
											numOfCrosses + ((GermplasmCross) expandedSecondGrandParent).getNumberOfCrossesBefore() + 1;
								}
							}
							cross.setNumberOfCrossesBefore(numOfCrosses);

						} else if (methodName.contains("three-way cross")) {
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
								SingleGermplasmCrossElement firstGrandParentElem = new SingleGermplasmCrossElement();
								setMetaData(firstGrandParentElem, element, firstGrandParent.getGid(), level-1);

								firstGrandParentElem.setGermplasm(firstGrandParent);

								Germplasm secondGrandParent =
										this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
												firstParent.getGpid2());
								SingleGermplasmCrossElement secondGrandParentElem = new SingleGermplasmCrossElement();
								secondGrandParentElem.setGermplasm(secondGrandParent);
								setMetaData(secondGrandParentElem, element, secondGrandParent.getGid(), level-1);


								// expand the grand parents as needed, depends
								// on the level
								GermplasmCrossElement expandedFirstGrandParent =
										this.expandGermplasmCross(firstGrandParentElem, level - 1, forComplexCross);
								GermplasmCrossElement expandedSecondGrandParent =
										this.expandGermplasmCross(secondGrandParentElem, level - 1, forComplexCross);

								// make the cross object for the grand parents
								GermplasmCross crossForGrandParents = new GermplasmCross();
								crossForGrandParents.setFirstParent(expandedFirstGrandParent);
								crossForGrandParents.setSecondParent(expandedSecondGrandParent);
								// compute the number of crosses before this one
								int numOfCrossesForGrandParents = 0;
								if (expandedFirstGrandParent instanceof GermplasmCross) {
									numOfCrossesForGrandParents =
											((GermplasmCross) expandedFirstGrandParent).getNumberOfCrossesBefore() + 1;
								}
								crossForGrandParents.setNumberOfCrossesBefore(numOfCrossesForGrandParents);

								// make the element for the second parent
								secondParent =
										this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
												germplasmToExpand.getGpid2());
								SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
								secondParentElem.setGermplasm(secondParent);
								setMetaData(secondParentElem, element, secondParent.getGid(), level-1);


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
								SingleGermplasmCrossElement firstGrandParentElem = new SingleGermplasmCrossElement();
								firstGrandParentElem.setGermplasm(firstGrandParent);

								setMetaData(firstGrandParentElem, element, firstGrandParent.getGid(), level-1);


								Germplasm secondGrandParent =
										this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
												secondParent.getGpid2());
								SingleGermplasmCrossElement secondGrandParentElem = new SingleGermplasmCrossElement();
								secondGrandParentElem.setGermplasm(secondGrandParent);

								setMetaData(secondGrandParentElem, element, secondGrandParent.getGid(), level-1);


								// expand the grand parents as needed, depends
								// on the level
								GermplasmCrossElement expandedFirstGrandParent =
										this.expandGermplasmCross(firstGrandParentElem, level - 1, forComplexCross);
								GermplasmCrossElement expandedSecondGrandParent =
										this.expandGermplasmCross(secondGrandParentElem, level - 1, forComplexCross);

								// make the cross object for the grand parents
								GermplasmCross crossForGrandParents = new GermplasmCross();
								crossForGrandParents.setFirstParent(expandedFirstGrandParent);
								crossForGrandParents.setSecondParent(expandedSecondGrandParent);
								// compute the number of crosses before this one
								int numOfCrossesForGrandParents = 0;
								if (expandedFirstGrandParent instanceof GermplasmCross) {
									numOfCrossesForGrandParents =
											((GermplasmCross) expandedFirstGrandParent).getNumberOfCrossesBefore() + 1;
								}
								crossForGrandParents.setNumberOfCrossesBefore(numOfCrossesForGrandParents);

								// make the element for the first parent
								firstParent =
										this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
												germplasmToExpand.getGpid1());
								SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
								firstParentElem.setGermplasm(firstParent);

								setMetaData(firstParentElem, element, firstParent.getGid(), level-1);


								// create the cross to return
								cross.setFirstParent(crossForGrandParents);
								cross.setSecondParent(firstParentElem);
								cross.setNumberOfCrossesBefore(numOfCrossesForGrandParents + 1);
							} else {
								SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
								firstParentElem.setGermplasm(firstParent);
								setMetaData(firstParentElem, element, firstParent.getGid(), level-1);

								SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
								secondParentElem.setGermplasm(secondParent);
								setMetaData(secondParentElem, element, firstParent.getGid(), level-1);

								cross.setFirstParent(firstParentElem);
								cross.setSecondParent(secondParentElem);
								cross.setNumberOfCrossesBefore(0);
							}

						} else if (methodName.contains("backcross")) {
							BackcrossElement backcross = new BackcrossElement();
							setMetaData(backcross, element, germplasmToExpand.getGid(), level);

							Germplasm firstParent =
									this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
											germplasmToExpand.getGpid1());
							Germplasm secondParent =
									this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
											germplasmToExpand.getGpid2());

							boolean itsABackCross = false;

							// determine which is the recurrent parent
							if (firstParent != null && secondParent != null) {
								SingleGermplasmCrossElement recurringParentElem = new SingleGermplasmCrossElement();
								SingleGermplasmCrossElement parentElem = new SingleGermplasmCrossElement();
								if (secondParent.getGnpgs() >= 2) {
									if (firstParent.getGid().equals(secondParent.getGpid1())
											|| firstParent.getGid().equals(secondParent.getGpid2())) {
										itsABackCross = true;
										backcross.setRecurringParentOnTheRight(false);

										recurringParentElem.setGermplasm(firstParent);
										setMetaData(recurringParentElem, element, firstParent.getGid(), level-1);

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
										parentElem.setGermplasm((Germplasm) numOfDosesAndOtherParent[1]);
										setMetaData(parentElem, element, ((Germplasm) numOfDosesAndOtherParent[1]).getGid(), level-1);

										backcross.setNumberOfDosesOfRecurringParent(((Integer) numOfDosesAndOtherParent[0]).intValue() + 2);
									}
								} else if (firstParent.getGnpgs() >= 2) {
									if (secondParent.getGid().equals(firstParent.getGpid1())
											|| secondParent.getGid().equals(firstParent.getGpid2())) {
										itsABackCross = true;
										backcross.setRecurringParentOnTheRight(true);

										recurringParentElem.setGermplasm(secondParent);
										setMetaData(recurringParentElem, element, secondParent.getGid(), level-1);

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
										parentElem.setGermplasm((Germplasm) numOfDosesAndOtherParent[1]);
										setMetaData(parentElem, element, ((Germplasm) numOfDosesAndOtherParent[1]).getGid(), level-1);

										backcross.setNumberOfDosesOfRecurringParent(((Integer) numOfDosesAndOtherParent[0]).intValue() + 2);
									}
								} else {
									itsABackCross = false;
								}

								if (itsABackCross) {
									GermplasmCrossElement expandedRecurringParent =
											this.expandGermplasmCross(recurringParentElem, level - 1, forComplexCross);
									backcross.setRecurringParent(expandedRecurringParent);

									GermplasmCrossElement expandedParent =
											this.expandGermplasmCross(parentElem, level - 1, forComplexCross);
									backcross.setParent(expandedParent);

									return backcross;
								}
							}

							if (!itsABackCross) {
								SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
								firstParentElem.setGermplasm(firstParent);
								setMetaData(firstParentElem, element, firstParent.getGid(), level-1);

								SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
								secondParentElem.setGermplasm(secondParent);
								setMetaData(secondParentElem, element, secondParent.getGid(), level-1);

								cross.setFirstParent(firstParentElem);
								cross.setSecondParent(secondParentElem);
								cross.setNumberOfCrossesBefore(0);
							}
						} else if (methodName.contains("cross") && methodName.contains("complex")) {
							// get the immediate parents
							Germplasm firstParent =
									this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
											germplasmToExpand.getGpid1());
							SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
							firstParentElem.setGermplasm(firstParent);
							setMetaData(firstParentElem, element, firstParent.getGid(), level-1);

							Germplasm secondParent =
									this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
											germplasmToExpand.getGpid2());
							SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
							secondParentElem.setGermplasm(secondParent);
							setMetaData(secondParentElem, element, secondParent.getGid(), level-1);

							// expand the parents as needed, depends on the
							// level
							GermplasmCrossElement expandedFirstParent = this.expandGermplasmCross(firstParentElem, level, true);
							GermplasmCrossElement expandedSecondParent = this.expandGermplasmCross(secondParentElem, level, true);

							// get the number of crosses in the first parent
							int numOfCrosses = 0;
							if (expandedFirstParent instanceof GermplasmCross) {
								numOfCrosses = ((GermplasmCross) expandedFirstParent).getNumberOfCrossesBefore() + 1;
							}

							cross.setFirstParent(expandedFirstParent);
							cross.setSecondParent(expandedSecondParent);
							cross.setNumberOfCrossesBefore(numOfCrosses);
						} else if (methodName.contains("cross")) {
							Germplasm firstParent =
									this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
											germplasmToExpand.getGpid1());
							Germplasm secondParent =
									this.pedigreeDataManagerFactory.getGermplasmDataManager().getGermplasmWithPrefName(
											germplasmToExpand.getGpid2());

							SingleGermplasmCrossElement firstParentElem = new SingleGermplasmCrossElement();
							firstParentElem.setGermplasm(firstParent);
							setMetaData(firstParentElem, element, firstParent.getGid(), level-1);

							SingleGermplasmCrossElement secondParentElem = new SingleGermplasmCrossElement();
							secondParentElem.setGermplasm(secondParent);
							setMetaData(secondParentElem, element, secondParent.getGid(), level-1);

							cross.setFirstParent(firstParentElem);
							cross.setSecondParent(secondParentElem);
							cross.setNumberOfCrossesBefore(0);
						} else {
							SingleGermplasmCrossElement crossElement = new SingleGermplasmCrossElement();
							crossElement.setGermplasm(germplasmToExpand);
							setMetaData(crossElement, element, germplasmToExpand.getGid(), level-1);

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

	private void setMetaData(GermplasmCrossElement childElement, GermplasmCrossElement parentElement,  Integer childElementGid, int level) {
		if(childElementGid != null && childElementGid != 0) {
			final List<Name> namesByGID = this.pedigreeDataManagerFactory.getGermplasmDataManager().getNamesByGID(childElementGid, null, null);
			childElement.setNames(namesByGID);
		}
		childElement.setLevel(level);
		childElement.setRootLevel(parentElement.getRootLevel());
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

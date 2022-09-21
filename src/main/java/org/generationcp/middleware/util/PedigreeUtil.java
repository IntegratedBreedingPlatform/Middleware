package org.generationcp.middleware.util;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Progenitor;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PedigreeUtil {

	public static void assignProgenitors(final Germplasm germplasm, final Map<String, Germplasm> progenitorsMapByGid,
		final Multimap<String, Object[]> conflictErrors, final Integer femaleParentGid,
		final Integer maleParentGid, final Method breedingMethod, final List<Integer> otherProgenitors) {
		if (breedingMethod.getMprgn() == 1) {
			conflictErrors.put("germplasm.update.mutation.method.is.not.supported", new String[] {
				String.valueOf(germplasm.getGid())});
		} else if (femaleParentGid != null && maleParentGid != null) {
			// Only update the progenitors if both male and female are available.
			final String femaleParentGidString = String.valueOf(femaleParentGid);
			final String maleParentGidString = String.valueOf(maleParentGid);
			germplasm.setGnpgs(
				calculateGnpgs(breedingMethod, femaleParentGidString, maleParentGidString, Lists.transform(otherProgenitors, Functions
					.toStringFunction())));
			setProgenitors(germplasm, breedingMethod, femaleParentGidString, maleParentGidString, progenitorsMapByGid, conflictErrors);
			setOtherProgenitors(germplasm, breedingMethod, otherProgenitors, conflictErrors);
		}
	}

	public static void setProgenitors(final Germplasm germplasm, final Method method, final String progenitor1, final String progenitor2,
		final Map<String, Germplasm> progenitorsMap, final Multimap<String, Object[]> progenitorErrors) {

		if (isInvalidMethodType(method)) {
			progenitorErrors
				.put("import.germplasm.invalid.method.type", new String[] {String.valueOf(germplasm.getGid()), method.getMcode()});
			return;
		}

		if (isInvalidMutation(method, progenitor2)) {
			progenitorErrors.put("germplasm.gpid2.must.be.zero.for.mutations", new String[] {String.valueOf(germplasm.getGid())});
			return;
		}

		if (method.isGenerative() || isNewGermplasmATerminalAncestor(progenitor1, progenitor2)) {
			germplasm.setGpid1(resolveGpid(progenitor1, progenitorsMap));
			germplasm.setGpid2(resolveGpid(progenitor2, progenitorsMap));
			return;
		}

		//DERIVATIVE OR MAINTENANCE CASES
		final Germplasm progenitor1Germplasm = progenitorsMap.get(progenitor1);
		final Germplasm progenitor2Germplasm = progenitorsMap.get(progenitor2);

		//Known Immediate Source, Unknown Group Source
		if ("0".equals(progenitor1)) {
			// If Immediate Source is Terminal Ancestor or Generative, then the Group Source is Progenitor 2 GID
			// Otherwise, Group Source will be set to Progenitor 2 Group Source
			germplasm.setGpid1(getProgenyGroupSource(progenitor2Germplasm));
			germplasm.setGpid2(progenitor2Germplasm.getGid());
			return;
		}

		//Defined BOTH Immediate Source and Group Source, They are equals and it is either GEN or a terminal node
		if (progenitor1.equals(progenitor2) && (progenitor2Germplasm.getMethod().isGenerative() || progenitor2Germplasm
			.isTerminalAncestor())) {
			germplasm.setGpid1(progenitor1Germplasm.getGid());
			germplasm.setGpid2(progenitor2Germplasm.getGid());
			return;
		}

		//Defined BOTH Immediate Source and Group Source
		if (!"0".equals(progenitor2)) {
			final boolean firstDerivationWithWrongGroupSource = progenitor2Germplasm.getMethod().isGenerative() && !progenitor1.equals(progenitor2);
			if (firstDerivationWithWrongGroupSource || !progenitor2Germplasm.getGpid1().equals(progenitor1Germplasm.getGid())) {
				progenitorErrors.put("import.germplasm.invalid.immediate.source.group", new String[] {
					String.valueOf(germplasm.getGid()),
					String.valueOf(progenitor2Germplasm.getGid()),
					String.valueOf(progenitor1Germplasm.getGid())});
				return;
			}
			germplasm.setGpid1(progenitor1Germplasm.getGid());
			germplasm.setGpid2(progenitor2Germplasm.getGid());
			return;
		}

		//Defined ONLY GroupSource
		if ("0".equals(progenitor2)) {
			if (progenitor1Germplasm.getMethod().isGenerative() || progenitor1Germplasm.isTerminalAncestor()) {
				germplasm.setGpid1(progenitor1Germplasm.getGid());
				germplasm.setGpid2(resolveGpid(progenitor2, progenitorsMap));
				return;
			}
			progenitorErrors
				.put("import.germplasm.invalid.derivative.group.source",
					new String[] {String.valueOf(germplasm.getGid()), String.valueOf(progenitor1Germplasm.getGid())});
		}
	}

	public static void setOtherProgenitors(final Germplasm germplasm, final Method method, final List<Integer> otherProgenitors,
		final Multimap<String, Object[]> progenitorErrors) {
		if (method.isDerivativeOrMaintenance() && !CollectionUtils.isEmpty(otherProgenitors)) {
			progenitorErrors.put("germplasm.update.other.progenitors.can.not.be.set.for.der.man", new String[] {});
			return;
		} else {
			//Generative validations
			if (!CollectionUtils.isEmpty(otherProgenitors) && !Integer.valueOf(0).equals(method.getMprgn())) {
				progenitorErrors
					.put("germplasm.update.other.progenitors.can.not.be.set.for.gen.with.mprgn.non.equal.zero", new String[] {});
				return;
			}
		}
		if (!germplasm.otherProgenitorsGidsEquals(otherProgenitors)) {
			if (!CollectionUtils.isEmpty(otherProgenitors)) {
				//It is required to identify if germplasm and progenitor number already exists in the list
				//So we replace the progenitorId instead of adding a new element to the bag
				//This was required because Unique key progntrs_unique fails due to orphans are removed at the end of the transaction
				int progenitorNumber = 2;
				for (final Integer otherProgenitorGid : otherProgenitors) {
					progenitorNumber++;
					final Optional<Progenitor> progenitorOptional = germplasm.findByProgNo(progenitorNumber);
					if (progenitorOptional.isPresent()) {
						progenitorOptional.get().setProgenitorGid(otherProgenitorGid);
					} else {
						germplasm.getOtherProgenitors().add(new Progenitor(germplasm, progenitorNumber, otherProgenitorGid));
					}
				}
				final List<Progenitor> toRemove =
					germplasm.getOtherProgenitors().stream().filter(p -> p.getProgenitorNumber() > 2 + otherProgenitors.size()).collect(
						Collectors.toList());
				germplasm.getOtherProgenitors().removeAll(toRemove);
			} else {
				germplasm.getOtherProgenitors().clear();
			}
		}
	}

	public static Integer calculateGnpgs(final Method method, final String progenitor1, final String progenitor2,
		final List<String> otherProgenitors) {
		if (method.isGenerative()) {
			if ((StringUtils.isEmpty(progenitor1) && StringUtils.isEmpty(progenitor2)) || ("0".equals(progenitor1) && "0"
				.equals(progenitor2))) {
				return 0;
			} else {
				if (method.getMprgn().equals(1)) {
					return 1;
				} else {
					final int otherProgenitorsSize = (otherProgenitors == null) ? 0 : otherProgenitors.size();
					return 2 + otherProgenitorsSize;
				}
			}
		} else {
			return -1;
		}
	}

	public static Integer getProgenyGroupSource(final Germplasm germplasm) {
		//For a terminal node or a generative germplasm, the group source for any derivative progeny is itself.
		//Otherwise the group source is gpid1
		if (germplasm.isTerminalAncestor() || germplasm.getMethod().isGenerative()) {
			return germplasm.getGid();
		}
		return germplasm.getGpid1();
	}

	private static boolean isInvalidMethodType(final Method method) {
		return !method.isGenerative() && !method.isDerivativeOrMaintenance();
	}

	private static boolean isNewGermplasmATerminalAncestor(final String progenitor1, final String progenitor2) {
		return (StringUtils.isEmpty(progenitor1) && StringUtils.isEmpty(progenitor2)) || ("0".equals(progenitor1) && "0"
			.equals(progenitor2));
	}

	private static Integer resolveGpid(final String progenitor, final Map<String, Germplasm> progenitorsMap) {
		return ("0".equals(progenitor) || StringUtils.isEmpty(progenitor)) ? 0 : progenitorsMap.get(progenitor).getGid();
	}

	private static boolean isInvalidMutation(final Method method, final String progenitor2) {
		return method.isGenerative() && Integer.valueOf(1).equals(method.getMprgn()) && StringUtils.isNotEmpty(progenitor2) && !"0"
			.equals(progenitor2);
	}

}

package org.generationcp.middleware.util.uid;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

public final class UIDGenerator {

	public static enum UID_ROOT {
		LOT("L"),
		OBSERVATION_UNIT("P"), // plot
		GERMPLASM("G"),
		FILE("F");

		private final String root;

		UID_ROOT(final String root) {
			this.root = root;
		}

		public String getRoot() {
			return this.root;
		}
	}


	public interface UIDAdapter<T> {

		String getUID(T entry);

		void setUID(T entry, String uid);
	}

	/**
	 * @param crop for crop prefix and UUID-or-UID flag
	 * @param list generate uuid for list
	 * @param root uid root name
	 * @param suffixLength for random suffix
	 * @param adapter get/set uid on lis
	 * @param <T> type to operate on
	 */
	public static <T> void generate(
		final CropType crop,
		final List<T> list,
		final UID_ROOT root,
		final int suffixLength,
		final UIDAdapter<T> adapter
	) {
		Preconditions.checkNotNull(crop);
		Preconditions.checkState(!CollectionUtils.isEmpty(list));

		final boolean doUseUUID = crop.isUseUUID();
		for (final T entry : list) {
			if (adapter.getUID(entry) == null) {
				if (doUseUUID) {
					adapter.setUID(entry, UUID.randomUUID().toString());
				} else {
					final String cropPrefix = crop.getPlotCodePrefix();
					final String suffix = RandomStringUtils.randomAlphanumeric(suffixLength);
					adapter.setUID(entry, cropPrefix + root.getRoot() + suffix);
				}
			}
		}
	}
}

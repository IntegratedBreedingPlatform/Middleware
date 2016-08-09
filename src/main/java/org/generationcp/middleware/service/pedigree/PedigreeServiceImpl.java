
package org.generationcp.middleware.service.pedigree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.service.pedigree.cache.keys.CropMethodKey;
import org.generationcp.middleware.service.pedigree.cache.keys.CropNameTypeKey;
import org.generationcp.middleware.service.pedigree.string.processors.PedigreeStringBuilder;
import org.generationcp.middleware.service.pedigree.string.util.FixedLineNameResolver;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.cache.FunctionBasedGuavaCacheLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * Default algorithm for generating pedigree strings.
 *
 */
@Transactional
public class PedigreeServiceImpl implements PedigreeService {

	private static final Logger LOG = LoggerFactory.getLogger(PedigreeServiceImpl.class);

	private PedigreeDataManagerFactory pedigreeDataManagerFactory;

	private static Cache<CropMethodKey, Method> breedingMethodCache;

	private static Cache<CropNameTypeKey, List<Integer>> nameTypeCache;

	private String cropName;

    private FunctionBasedGuavaCacheLoader<CropMethodKey, Method> methodCropBasedCache;

	private FunctionBasedGuavaCacheLoader<CropNameTypeKey, List<Integer>> nameTypeBasedCache;

	private GermplasmDataManager germplasmDataManager;
	
	static {

		breedingMethodCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(100, TimeUnit.MINUTES).build();
		nameTypeCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(100, TimeUnit.MINUTES).build();
	}

	public PedigreeServiceImpl() {

	}

	public PedigreeServiceImpl(final HibernateSessionProvider sessionProvider, final String cropName) {
		this.cropName = cropName;
		this.pedigreeDataManagerFactory = new PedigreeDataManagerFactory(sessionProvider);
		this.germplasmDataManager = this.pedigreeDataManagerFactory.getGermplasmDataManager();

		methodCropBasedCache = new FunctionBasedGuavaCacheLoader<CropMethodKey, Method>(breedingMethodCache, new Function<CropMethodKey, Method>() {

			@Override
			public Method apply(CropMethodKey key) {
				return PedigreeServiceImpl.this.germplasmDataManager.getMethodByID(key.getMethodId());
			}
		});

		final Function<CropNameTypeKey, List<Integer>> nameTypeLoader = new Function<CropNameTypeKey, List<Integer>>() {

			@Override
			public List<Integer> apply(final CropNameTypeKey input) {

				final List<String> nameTypeOrder = input.getNameTypeOrder();
				final List<Integer> nameTypeOrderIds = new ArrayList<>();

				for (final String nameType : nameTypeOrder) {
					final UserDefinedField userDefinedFieldByTableTypeAndCode =
							germplasmDataManager.getUserDefinedFieldByTableTypeAndCode("NAMES", "NAME", nameType);
					if (userDefinedFieldByTableTypeAndCode != null) {
						nameTypeOrderIds.add(userDefinedFieldByTableTypeAndCode.getFldno());
					} else {
						throw new MiddlewareException(String.format(
								"Name type code of '%s' specified in crossing.properties is not present in the"
										+ " UDFLDS table. Please make sure your properties file is configured correctly."
										+ " Please contact your administrator for further assistance.", nameType));
					}
				}
				return nameTypeOrderIds;
			}
		};

		nameTypeBasedCache =
				new FunctionBasedGuavaCacheLoader<CropNameTypeKey, List<Integer>>(nameTypeCache,
						nameTypeLoader);

	}

	@Override
	public String getCropName() {
		return this.cropName;
	}


	@Override
	public Map<Integer, String> getCrossExpansions(final Set<Integer> gids, final Integer level,
			final CrossExpansionProperties crossExpansionProperties) {

		if (gids.size() > 5000) {
			throw new IllegalArgumentException(
					"Max set size has to be less than 5000." + " Any thing about this might casue caching and performace issues.");
		}

		final Monitor monitor = MonitorFactory.start(
				"org.generationcp.middleware.service.pedigree.PedigreeServiceImpl.getCrossExpansion(List<Integer>, Integer, CrossExpansionProperties)");
		final Map<Integer, String> pedigreeStrings = new HashMap<>();
		try {
			// Get the cross string
			final int numberOfLevelsToTraverse =
					level == null ? crossExpansionProperties.getCropGenerationLevel(this.getCropName()) : level;

			final GermplasmCache germplasmAncestryCache =
					new GermplasmCache(germplasmDataManager, getNumberOfLevelsToTraverseInDb(numberOfLevelsToTraverse));
			// Prime cache
			germplasmAncestryCache.initialiseCache(this.getCropName(), gids, getNumberOfLevelsToTraverseInDb(numberOfLevelsToTraverse));
			
			// Please note the cache about has been primed with all germplasm and their ancestry tree and thus will not need to go back to
			// the database for each germplasm required. It will occasionally go back to the DB in case it cannot find the required gid.
			// This might happen in the case of backcross because we predetermine the number of crosses for a backcross. 
			for (final Integer gid : gids) {
				pedigreeStrings.put(gid,
						buildPedigreeString(gid, level, crossExpansionProperties, germplasmAncestryCache, numberOfLevelsToTraverse));
			}
			return pedigreeStrings;
		} finally {
			monitor.stop();
		}
	}
	
	/**
	 * (non-Javadoc)
	 *
	 * @see org.generationcp.middleware.service.api.PedigreeService#getCrossExpansion(java.lang.Integer, java.lang.Integer,
	 *      org.generationcp.middleware.util.CrossExpansionProperties)
	 */
	@Override
	public String getCrossExpansion(final Integer gid, final Integer level, final CrossExpansionProperties crossExpansionProperties) {
		final Monitor monitor = MonitorFactory.start("org.generationcp.middleware.service.pedigree.PedigreeServiceImpl.getCrossExpansion(Integer, Integer, CrossExpansionProperties)");

		try {
			// Get the cross string
			final int numberOfLevelsToTraverse = level == null ? crossExpansionProperties.getCropGenerationLevel(this.getCropName()) : level;
			
			final GermplasmCache germplasmAncestryCache = new GermplasmCache(germplasmDataManager, getNumberOfLevelsToTraverseInDb(numberOfLevelsToTraverse));

			return buildPedigreeString(gid, level, crossExpansionProperties, germplasmAncestryCache, numberOfLevelsToTraverse);
		} finally {
			monitor.stop();
		}
	}

	/**
	 * We essentially want to traverse double the number of required levels. This is because every derivative germplasm will point to its
	 * generative ancestor. Thus we essentially need to traverse twice our required level. The plus three is in case we have a double cross
	 * at the leaf node. Note this will not catering for a backcross. Encountering backcross will require additional database trips.
	 * 
	 * @param numberOfLevelsToTraverse the number of levels up the ancestry tree we need to traverse to generate the tree.
	 * @return the number of levels to traverse in the DB when retrieving ancestry 
	 */
	private int getNumberOfLevelsToTraverseInDb(final int numberOfLevelsToTraverse) {
		return ((numberOfLevelsToTraverse + 1 )  * 2) + 3;
	}

	private String buildPedigreeString(final Integer gid, final Integer level, final CrossExpansionProperties crossExpansionProperties,
			final GermplasmCache germplasmAncestryCache, final int numberOfLevelsToTraverse) {
		final Monitor monitor = MonitorFactory.start("org.generationcp.middleware.service.pedigree.PedigreeServiceImpl.buildPeidgreeString(Integer, Integer, CrossExpansionProperties, GermplasmCache, int)");

		try {

			Preconditions.checkNotNull(gid);
			Preconditions.checkArgument(gid > 0);
			LOG.debug(String.format("Building ancestory tree for gid - '%d'", gid));
	
			// Build the pedigree tree
			final AncestryTreeService ancestryTreeService = new AncestryTreeService(germplasmAncestryCache, this.methodCropBasedCache, this.getCropName());
			final GermplasmNode gidAncestryTree = ancestryTreeService.buildAncestryTree(gid, numberOfLevelsToTraverse + 3);
	
			LOG.debug(String.format("Traversing '%d' number of levels.", numberOfLevelsToTraverse));
	
			final PedigreeStringBuilder pedigreeString = new PedigreeStringBuilder();
	
			LOG.debug(String.format("Building pedigree string for gid '%d'.", gid));
	
			return pedigreeString.buildPedigreeString(gidAncestryTree, numberOfLevelsToTraverse,
					new FixedLineNameResolver(crossExpansionProperties, pedigreeDataManagerFactory, nameTypeBasedCache, cropName), false)
					.getPedigree();
		} finally {
			monitor.stop();
		}
	}
	
	/**
	 * (non-Javadoc)
	 *
	 * @see org.generationcp.middleware.service.api.PedigreeService#getCrossExpansion(java.lang.Integer,
	 *      org.generationcp.middleware.util.CrossExpansionProperties)
	 */
	@Override
	public String getCrossExpansion(final Integer gid, final CrossExpansionProperties crossExpansionProperties) {
		return this.getCrossExpansion(gid, null, crossExpansionProperties);
	}

	@Override
	public String getCrossExpansion(final Germplasm germplasm, final Integer level, final CrossExpansionProperties crossExpansionProperties) {

		// We need to clean up our pedigree service
		throw new UnsupportedOperationException("This method is curently not supported and"
				+ " really should not be called from anywhere in the code.");
	}



}

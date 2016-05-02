
package org.generationcp.middleware.service.pedigree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.service.pedigree.cache.keys.CropGermplasmKey;
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

@Transactional
public class PedigreeServiceImpl implements PedigreeService {

	private static final Logger LOG = LoggerFactory.getLogger(PedigreeServiceImpl.class);

	private PedigreeDataManagerFactory pedigreeDataManagerFactory;

	private static Cache<CropGermplasmKey, Germplasm> germplasmCache;

	private static Cache<CropMethodKey, Method> methodCache;

	private static Cache<CropNameTypeKey, List<Integer>> nameTypeCache;

	private String cropName;

	private FunctionBasedGuavaCacheLoader<CropGermplasmKey, Germplasm> germplasmCropBasedCache;

	private FunctionBasedGuavaCacheLoader<CropMethodKey, Method> methodCropBasedCache;

	private FunctionBasedGuavaCacheLoader<CropNameTypeKey, List<Integer>> nameTypeBasedCache;

	private GermplasmDataManager germplasmDataManager;
	
	static {

		// FIXME: Invalidation logic may need to applied.
		germplasmCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(1000, TimeUnit.MINUTES).build();
		methodCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(1000, TimeUnit.MINUTES).build();
		nameTypeCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(1000, TimeUnit.MINUTES).build();
	}

	public PedigreeServiceImpl() {

	}

	public PedigreeServiceImpl(final HibernateSessionProvider sessionProvider, final String cropName) {
		this.cropName = cropName;
		this.pedigreeDataManagerFactory = new PedigreeDataManagerFactory(sessionProvider);
		this.germplasmDataManager =this.pedigreeDataManagerFactory.getGermplasmDataManager();

		germplasmCropBasedCache =
				new FunctionBasedGuavaCacheLoader<CropGermplasmKey, Germplasm>(germplasmCache, new Function<CropGermplasmKey, Germplasm>() {

					@Override
					public Germplasm apply(CropGermplasmKey key) {
						return germplasmDataManager.getGermplasmWithPrefName(
								key.getGid());
					}
				});

		methodCropBasedCache = new FunctionBasedGuavaCacheLoader<CropMethodKey, Method>(methodCache, new Function<CropMethodKey, Method>() {

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

	/**
	 * (non-Javadoc)
	 *
	 * @see org.generationcp.middleware.service.api.PedigreeService#getCrossExpansion(java.lang.Integer, java.lang.Integer,
	 *      org.generationcp.middleware.util.CrossExpansionProperties)
	 */
	@Override
	public String getCrossExpansion(final Integer gid, final Integer level, final CrossExpansionProperties crossExpansionProperties) {

	
		final Monitor monitor = MonitorFactory.start("org.generationcp.middleware.service.pedigree.PedigreeServiceImpl.getCrossExpansion()");
		try {
			Preconditions.checkNotNull(gid);
			Preconditions.checkArgument(gid > 0);
			LOG.debug(String.format("Building ancestory tree for gid - '%d'", gid));
			// Build the pedigree tree
			final AncestryTreeService ancestryTreeService = new AncestryTreeService(this.germplasmCropBasedCache, this.methodCropBasedCache, this.getCropName());
			final GermplasmNode gidAncestryTree = ancestryTreeService.buildAncestryTree(gid);
	
			// Get the cross string
			final int numberOfLevelsToTraverse = level == null ? crossExpansionProperties.getCropGenerationLevel(this.getCropName()) : level;
	
			LOG.debug(String.format("Traversing '%d' number of levels.", numberOfLevelsToTraverse));
	
			final PedigreeStringBuilder pedigreeString = new PedigreeStringBuilder();
	
			LOG.debug(String.format("Building pedigree string for gid '%d'.", gid));
			return pedigreeString.buildPedigreeString(gidAncestryTree, numberOfLevelsToTraverse,
					new FixedLineNameResolver(crossExpansionProperties, pedigreeDataManagerFactory, nameTypeBasedCache, cropName))
					.getPedigree();
		} finally {
			PedigreeServiceImpl.LOG.info("" + monitor.stop());
		}
	}

	@Override
	public String getCrossExpansion(final Germplasm germplasm, final Integer level, final CrossExpansionProperties crossExpansionProperties) {

		// We need to clean up our pedigree service
		throw new UnsupportedOperationException("This method is curently not supported and"
				+ " really should not be called from anywhere in the code.");
	}

}


package org.generationcp.middleware.service.pedigree;

import java.util.concurrent.TimeUnit;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.service.FieldbookServiceImpl;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Transactional
public class PedigreeServiceImpl implements PedigreeService {

	private static final Logger LOG = LoggerFactory.getLogger(FieldbookServiceImpl.class);

	private PedigreeDataManagerFactory pedigreeDataManagerFactory;

	private static LoadingCache<GermplasmKey, Optional<Germplasm>> germplasmCache;

	private static LoadingCache<MethodKey, Optional<Method>> methodCache;

	private String cropName;

	public PedigreeServiceImpl() {

	}

	public PedigreeServiceImpl(final HibernateSessionProvider sessionProvider, final String cropName) {
		this.cropName = cropName;
		this.pedigreeDataManagerFactory = new PedigreeDataManagerFactory(sessionProvider);

		// FIXME This is a broken cache
		PedigreeServiceImpl.germplasmCache =
				CacheBuilder.newBuilder().maximumSize(100000).expireAfterWrite(1000, TimeUnit.MINUTES)
						.build(new CacheLoader<GermplasmKey, Optional<Germplasm>>() {

							@Override
							public Optional<Germplasm> load(final GermplasmKey key) {
								Optional<Germplasm> germplasmWithPrefName =
										Optional.fromNullable(PedigreeServiceImpl.this.pedigreeDataManagerFactory.getGermplasmDataManager()
												.getGermplasmWithPrefName(key.getGid()));
								return germplasmWithPrefName;
							}
						});

		PedigreeServiceImpl.methodCache =
				CacheBuilder.newBuilder().maximumSize(100000).expireAfterWrite(1000, TimeUnit.MINUTES)
						.build(new CacheLoader<MethodKey, Optional<Method>>() {

							@Override
							public Optional<Method> load(final MethodKey key) {
								Optional<Method> methodByID =
										Optional.fromNullable(PedigreeServiceImpl.this.pedigreeDataManagerFactory.getGermplasmDataManager()
												.getMethodByID(key.getMethodId()));
								return methodByID;
							}
						});

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
		Preconditions.checkNotNull(gid);
		Preconditions.checkArgument(gid > 0);
		// Build the pedigree tree
		final PedigreeTree pedigreeTree =
				new PedigreeTree(PedigreeServiceImpl.germplasmCache, PedigreeServiceImpl.methodCache, this.getCropName());
		final GermplasmNode gidPedigreeTree = pedigreeTree.buildPedigreeTree(gid);

		// System.out.println(gidPedigreeTree);
		gidPedigreeTree.printTree();
		// Get the cross string
		final int numberOfLevelsToTraverse = level == null ? crossExpansionProperties.getCropGenerationLevel(this.getCropName()) : level;

		PedigreeStringBuilder pedigreeString = new PedigreeStringBuilder();

		return pedigreeString.buildPedigreeString(gidPedigreeTree, numberOfLevelsToTraverse,
				new FixedLineNameResolver(crossExpansionProperties, pedigreeDataManagerFactory, cropName)).getPedigree();
	}

	@Override
	public String getCrossExpansion(final Germplasm germplasm, final Integer level, final CrossExpansionProperties crossExpansionProperties) {

		// We need to clean up our pedigree service
		throw new UnsupportedOperationException("This method is curently not supported and"
				+ " really should not be called from anywhere in the code.");
	}

}

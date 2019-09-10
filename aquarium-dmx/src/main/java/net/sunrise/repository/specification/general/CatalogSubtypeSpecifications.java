/**
 * 
 */
package net.sunrise.repository.specification.general;

import java.text.ParseException;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lombok.Builder;
import net.sunrise.common.CommonUtility;
import net.sunrise.common.DateTimeUtility;
import net.sunrise.common.ListUtility;
import net.sunrise.domain.entity.general.CatalogueSubtype;
import net.sunrise.framework.model.SearchParameter;
import net.sunrise.framework.model.specifications.SearchRequest;
import net.sunrise.framework.specifications.BrillianceSpecifications;

/**
 * @author bqduc
 *
 */

@Builder
public class CatalogSubtypeSpecifications extends BrillianceSpecifications<CatalogueSubtype, SearchRequest>{
	//public static String PROP_VISIBLE = "visible"; 
	private final static String fieldFirstName = "firstName";
	private final static String fieldLastName = "lastName";
	private final static String fieldPhones = "phones";
	private final static String fieldDateOfBirthFrom = "dateOfBirthFrom";
	private final static String fieldDateOfBirthTo = "dateOfBirthTo";
	private final static String fieldCode = "code";
	private final static String fieldDateOfIssueTo = "dateOfIssueTo";
	private final static String fieldDateOfIssueFrom = "dateOfIssueFrom";

	/**
	 * Specification used to construct dynamic query based on JPA Criteria API.
	 * 
	 * @param searchParameter
	 *          Demanded search parameters (must be bigger than 0 to be taken for the query)
	 * @param name
	 *          Contact's name (must be not null to be taken for the query)
	 * @return Specification to use with JpaSpecificationExecutor
	 */
  @Override
	protected Specification<CatalogueSubtype> buildSpecifications(final SearchParameter searchParameter) {
		return new Specification<CatalogueSubtype>() {
			@Override
			public Predicate toPredicate(Root<CatalogueSubtype> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				/*if (CommonUtility.isNotEmpty(searchParameter.getParameterMap())) {
					throw new IllegalStateException("At least one parameter should be provided to construct complex query");
				}*/

				List<Predicate> predicates = ListUtility.createArrayList();
				if (CommonUtility.isNotEmpty(searchParameter.getParameterMap())){
					predicates.add(builder.and(builder.like(root.get(fieldFirstName), containsWildcard((String)searchParameter.getParameterMap().get(fieldFirstName)))));
					if (CommonUtility.isNotEmpty(searchParameter.getParameterMap().get(fieldLastName))){
						predicates.add(builder.and(builder.like(root.get(fieldLastName), containsWildcard((String)searchParameter.getParameterMap().get(fieldLastName)))));
					}

					if (CommonUtility.isNotEmpty(searchParameter.getParameterMap().get(fieldCode))){
						predicates.add(builder.and(builder.like(root.get(fieldCode), containsWildcard((String)searchParameter.getParameterMap().get(fieldCode)))));
					}

					if (CommonUtility.isNotEmpty(searchParameter.getParameterMap().get(fieldPhones))){
						predicates.add(builder.and(builder.like(root.get(fieldPhones), containsWildcard((String)searchParameter.getParameterMap().get(fieldPhones)))));
					}

					try {
						if (CommonUtility.isNotEmpty(searchParameter.getParameterMap().get(fieldDateOfBirthFrom)) && CommonUtility.isNotEmpty(searchParameter.getParameterMap().get(fieldDateOfBirthTo))){
							predicates.add(builder.and(builder.between(root.get("dateOfBirth"), 
									DateTimeUtility.getDateInstance((String)searchParameter.getParameterMap().get(fieldDateOfBirthFrom)), 
									DateTimeUtility.getDateInstance((String)searchParameter.getParameterMap().get(fieldDateOfBirthTo)))
								)
							);
						}

						//Issue date
						if (CommonUtility.isNotEmpty(searchParameter.getParameterMap().get(fieldDateOfIssueFrom)) 
								&& CommonUtility.isNotEmpty(searchParameter.getParameterMap().get(fieldDateOfIssueTo))){
							predicates.add(builder.and(builder.between(root.get("issuedDate"), 
									DateTimeUtility.getDateInstance((String)searchParameter.getParameterMap().get(fieldDateOfIssueFrom)), 
									DateTimeUtility.getDateInstance((String)searchParameter.getParameterMap().get(fieldDateOfIssueTo)))
								)
							);
						}
					} catch (ParseException e) {
						log.error("ContactSpecifications::toPredicate", e);
					}
				}
				Predicate[] predicatesArray = new Predicate[predicates.size()];
				return builder.and(predicates.toArray(predicatesArray));
			}
		};
	}

	public static Specification<CatalogueSubtype> buildSpecification(final SearchParameter searchParameter) {
		return CatalogSubtypeSpecifications
				.builder()
				.build()
				.buildSpecifications(searchParameter);
	}
}

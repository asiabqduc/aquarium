/**
 * 
 */
package net.brilliance.repository.specification.dmx;

import org.springframework.data.jpa.domain.Specification;

import lombok.Builder;
import net.brilliance.domain.entity.config.Configuration;
import net.brilliance.framework.model.SearchParameter;
import net.brilliance.framework.model.specifications.SearchRequest;
import net.brilliance.framework.specifications.BrillianceSpecifications;

/**
 * @author bqduc
 *
 */
@Builder
public class BpmActivityRepoSpecification extends BrillianceSpecifications<Configuration, SearchRequest>{
	public static Specification<Configuration> buildSpecification(final SearchParameter searchParameter) {
		return BpmActivityRepoSpecification
				.builder()
				.build()
				.buildSpecifications(searchParameter);
	}
}

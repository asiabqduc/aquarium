/**
 * 
 */
package net.sunrise.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.brilliance.framework.repository.BaseRepository;
import net.sunrise.domain.entity.BusinessPackage;

/**
 * @author bqduc
 *
 */
@Repository
public interface BusinessPackageRepository extends BaseRepository<BusinessPackage, Long> {
	Optional<BusinessPackage> findByCode(String code);
	Optional<BusinessPackage> findByName(String name);

	@Query("SELECT entity FROM #{#entityName} entity WHERE ("
			+ " LOWER(entity.code) like LOWER(CONCAT('%',:keyword,'%'))"
			+ " or LOWER(entity.name) like LOWER(CONCAT('%',:keyword,'%'))"
			+ ")"
	)
	Page<BusinessPackage> search(@Param("keyword") String keyword, Pageable pageable);
}

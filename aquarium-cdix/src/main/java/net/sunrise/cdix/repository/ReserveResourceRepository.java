/**
 * 
 */
package net.sunrise.cdix.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.sunrise.cdix.entity.ReserveResource;
import net.sunrise.framework.repository.BaseRepository;

/**
 * @author bqduc
 *
 */
@Repository
public interface ReserveResourceRepository extends BaseRepository<ReserveResource, Long> {
	Page<ReserveResource> findAll(Pageable pageable);
	Page<ReserveResource> findAllByOrderByIdAsc(Pageable pageable);
	Optional<ReserveResource> findByName(String name);
	Long countByName(String name);

	@Query("SELECT entity FROM #{#entityName} entity WHERE ("
			+ " LOWER(entity.name) like LOWER(CONCAT('%',:keyword,'%'))"
			+ "or LOWER(entity.type) like LOWER(CONCAT('%',:keyword,'%')) "
			+ "or LOWER(entity.description) like LOWER(CONCAT('%',:keyword,'%')) "
			+ ")"
	)
	Page<ReserveResource> search(@Param("keyword") String keyword, Pageable pageable);
}

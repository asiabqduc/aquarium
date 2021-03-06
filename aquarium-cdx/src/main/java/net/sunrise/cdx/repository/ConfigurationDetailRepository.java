/**
 * 
 */
package net.sunrise.cdx.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.sunrise.cdx.domain.entity.Configuration;
import net.sunrise.cdx.domain.entity.ConfigurationDetail;
import net.sunrise.framework.repository.BaseRepository;

/**
 * @author bqduc
 *
 */
@Repository
public interface ConfigurationDetailRepository extends BaseRepository<ConfigurationDetail, Long> {
	ConfigurationDetail findByName(String name);

	ConfigurationDetail findByConfigurationAndName(Configuration configuration, String name);

	@Query("SELECT count(entity.id)>0 FROM #{#entityName} entity "
			+ "WHERE ("
			+ " LOWER(entity.name) = LOWER(:name)"
			+ ")"
	)
	boolean isExists(String name);

	@Query("SELECT entity FROM #{#entityName} entity "
			+ "WHERE ("
			+ " LOWER(entity.name) like LOWER(CONCAT('%',:keyword,'%'))"
			+ ")"
	)
	Page<ConfigurationDetail> search(@Param("keyword") String keyword, Pageable pageable);
}

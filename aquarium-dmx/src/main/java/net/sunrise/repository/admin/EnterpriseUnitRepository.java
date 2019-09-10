package net.sunrise.repository.admin;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.sunrise.domain.entity.dmx.EnterpriseUnit;
import net.sunrise.framework.repository.BaseRepository;

@Repository
public interface EnterpriseUnitRepository extends BaseRepository<EnterpriseUnit, Long>{
	EnterpriseUnit findByPhones(String phones);
	Optional<EnterpriseUnit> findByCode(String code);
	Long countByCode(String code);

	@Query("SELECT entity FROM #{#entityName} entity WHERE ("
			+ " LOWER(entity.code) like LOWER(CONCAT('%',:keyword,'%')) or "
			+ " LOWER(entity.name) like LOWER(CONCAT('%',:keyword,'%')) or "
			+ " LOWER(entity.manager.firstName) like LOWER(CONCAT('%',:keyword,'%')) or "
			+ " LOWER(entity.manager.lastName) like LOWER(CONCAT('%',:keyword,'%')) or "
			+ " LOWER(entity.spoc.firstName) like LOWER(CONCAT('%',:keyword,'%')) or "
			+ " LOWER(entity.spoc.lastName) like LOWER(CONCAT('%',:keyword,'%')) or "
			+ " LOWER(entity.phones) like LOWER(CONCAT('%',:keyword,'%')) "
			+ ")"
	)
	Page<EnterpriseUnit> search(@Param("keyword") String keyword, Pageable pageable);
}

/**
 * 
 */
package net.sunrise.crsx.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.sunrise.domain.entity.crx.BizOrder;
import net.sunrise.domain.entity.crx.BizOrderDetail;
import net.sunrise.framework.repository.BaseRepository;

/**
 * @author bqduc
 *
 */
@Repository
public interface BizOrderDetailRepository extends BaseRepository<BizOrderDetail, Long> {
	List<BizOrderDetail> findByBizOrder(BizOrder order);

	@Query("SELECT entity FROM #{#entityName} entity WHERE ("
			+ " LOWER(entity.entry.code) like LOWER(CONCAT('%',:keyword,'%'))"
			+ "or LOWER(entity.entry.name) like LOWER(CONCAT('%',:keyword,'%')) "

			+ "or LOWER(entity.bizOrder.code) like LOWER(CONCAT('%',:keyword,'%')) "
			+ "or LOWER(entity.bizOrder.name) like LOWER(CONCAT('%',:keyword,'%')) "
			+ ")"
	)
	Page<BizOrderDetail> search(@Param("keyword") String keyword, Pageable pageable);
}

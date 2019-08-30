/**
 * 
 */
package net.sunrise.repository.vbb;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.brilliance.framework.repository.BaseRepository;
import net.sunrise.domain.entity.vbb.VbbThread;

/**
 * @author bqduc
 *
 */
@Repository
public interface VbbThreadRepository extends BaseRepository<VbbThread, Long> {
	VbbThread findByName(String name);

	@Query("SELECT entity FROM #{#entityName} entity "
			+ "WHERE ("
			+ " LOWER(entity.name) like LOWER(CONCAT('%',:keyword,'%'))"
			+ ")"
	)
	Page<VbbThread> search(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT entity FROM #{#entityName} entity "
			+ "WHERE ("
			+ " entity.topic.id = :topicId"
			+ ")"
	)
	Page<VbbThread> findByTopicId(@Param("topicId") Long topicId, Pageable pageable);
}

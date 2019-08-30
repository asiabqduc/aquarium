package net.brilliance.framework.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import net.brilliance.common.CommonConstants;
import net.brilliance.exceptions.ExecutionContextException;
import net.brilliance.framework.entity.ObjectBase;
import net.brilliance.framework.manager.BaseManager;
import net.brilliance.framework.model.ExecutionContext;
import net.brilliance.framework.model.SearchParameter;
import net.brilliance.framework.model.specifications.SearchRequest;
import net.brilliance.framework.repository.BaseRepository;
import net.brilliance.framework.specifications.DefaultSpecification;
import net.brilliance.framework.specifications.predicator.BrilliancePredicator;


@Service
public abstract class GenericServiceImpl<EntityType extends ObjectBase, Key extends Serializable> extends BaseManager /*RootService*/ <EntityType, Key> implements GenericService<EntityType, Key>{
	private static final long serialVersionUID = 7066816485194481124L;

	protected abstract BaseRepository<EntityType, Key> getRepository();
	//protected abstract BrillianceRepository<EntityType, Key> getRepository();

	protected BrilliancePredicator<EntityType> getRepositoryPredicator(){
		return null;
	}

  @PersistenceContext
  private EntityManager em;

  protected EntityManager getEntityManager(){
  	return this.em;
  }

	protected Specification<EntityType> getRepoSpecification(SearchParameter searchParameter){
  	//return (Specification<EntityType>) DefaultBrilliancePredicator.builder().build().buildSpecification(searchParameter);
  	return null;
  }

	protected Page<EntityType> getPaginatedObjects(Integer page, Integer size){
		int requestedPageIdx = page-1;
		if (requestedPageIdx < 0)
			requestedPageIdx = 0;

		PageRequest pageRequest = PageRequest.of(requestedPageIdx, size, Sort.Direction.ASC, "id");
    BaseRepository<EntityType, Key> repo = getRepository();
    if (null != repo)
    	return repo.findAll(pageRequest);

    return null;
	}

  /**
   * Get entity with the provided key.
   * 
   * @param id The entity key
   * @return The entity
   */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public EntityType getObject(Key id) {
		EntityType entity = getRepository().getOne(id);
		return entity;
	}

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
	public void remove(Key id) {
		try {
			//getRepository().delete(id);
		} catch (EmptyResultDataAccessException e) {
			log.error("Delete object by key", e);
		}
	}

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
	public void remove(EntityType entity) {
		try {
			getRepository().delete(entity);
		} catch (EmptyResultDataAccessException e) {
			log.error("Delete object. ", e);
		}
	}

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void removeAll() {
		try {
			getRepository().deleteAll();
		} catch (EmptyResultDataAccessException e) {
			log.error("Delete all objects. ", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public long count() {
		return getRepository().count();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<EntityType> getObjects() {
		List<EntityType> results = new ArrayList<>();
		getRepository().findAll().forEach(results::add);
		return results;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Page<EntityType> getObjects(Integer pageNumber) {
		return getPaginatedObjects(pageNumber, CommonConstants.DEFAULT_PAGE_SIZE);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Page<EntityType> getObjects(Integer pageNumber, Integer size) {
		return getPaginatedObjects(pageNumber, size);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Page<EntityType> searchObjects(String keyword, Pageable pageable) {
		return performSearch(keyword, pageable);
	}

	@Transactional(readOnly = true)
	public Page<EntityType> search(Map<String, Object> parameters){
		return searchObjects((String)parameters.get(CommonConstants.PARAM_KEYWORD), (Pageable)parameters.get(CommonConstants.PARAM_PAGEABLE));
	}

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public EntityType saveOrUpdate(EntityType entity) {
  	EntityType mergedEntity = null;
  	BaseRepository<EntityType, Key> respository = getRepository();
  	if (null != respository){
  		respository.saveAndFlush(entity);
  	} else {
  		log.info("There is no implemented repository for " + this.getClass().getSimpleName());
    	if (null == entity.getId()){
    		this.em.persist(entity);
    	}else{
    		mergedEntity = this.em.merge(entity);
    		this.em.refresh(mergedEntity);
    	}
    	this.em.flush();
  		log.info("Use the persistence context entity manager object instead of repository. ");
  	}

  	return entity;
	}

	public List<EntityType> imports(Map<Object, Object> parameters){
		return null;
	}

	protected Page<EntityType> doGetObjects(SearchParameter searchParameter) {
		BaseRepository<EntityType, Key> repository = getRepository();
		BrilliancePredicator<EntityType> predicator = this.getRepositoryPredicator();
		Page<EntityType> pagedObjects = null;
		if (null != predicator){
			pagedObjects = repository.findAll(predicator.buildSpecification(searchParameter), searchParameter.getPageable());
		}else{
			//pagedObjects = ListUtility.createPageable(repository.findAll(), searchParameter.getPageable());
		}
		return pagedObjects;
	}

	@Override
	public Page<EntityType> getObjects(SearchParameter searchParameter) {
		//Page<EntityType> pagedEntities = doGetObjects(searchParameter);
		Page<EntityType> pagedEntities = performGetObjects(searchParameter);
		//Perform additional operations here
		return pagedEntities;
	}

	public ExecutionContext load(ExecutionContext executionContext) throws ExecutionContextException {
		return executionContext;
	}

	protected Page<EntityType> performGetObjects(SearchParameter searchParameter) {
		return getRepository().findAll(
				DefaultSpecification.<EntityType, SearchRequest>builder().build().buildRepoSpecification(searchParameter),
				searchParameter.getPageable());
	}
}
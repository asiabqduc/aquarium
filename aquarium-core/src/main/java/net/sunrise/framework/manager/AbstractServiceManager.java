package net.sunrise.framework.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import net.sunrise.common.CommonConstants;
import net.sunrise.common.CommonUtility;
import net.sunrise.framework.entity.BizObjectBase;
import net.sunrise.framework.model.SearchCondition;
import net.sunrise.framework.repository.AdvancedSearchRepository;
import net.sunrise.framework.repository.JBaseRepository;
import net.sunrise.framework.repository.SearchRepository;
import net.sunrise.framework.repository.SearchableRepository;
import net.sunrise.helper.WebServicingHelper;

public abstract class AbstractServiceManager<T extends BizObjectBase, PK extends Serializable> extends RootService<T, PK>{
	private static final long serialVersionUID = -4604863496974502182L;

	protected abstract JBaseRepository<T, PK> getRepository();

	protected Page<T> performSearch(String keyword, Pageable pageable){
		SearchRepository<T, PK> searchRepository = (SearchRepository<T, PK>)getRepository();
		Page<T> result = new PageImpl<>(searchRepository.search(keyword));
		return result;
	}

	protected List<T> performSearch(String keyword, short page, short pageSize){
		SearchRepository<T, PK> searchRepository = (SearchRepository<T, PK>)getRepository();
		return searchRepository.search(keyword);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Page<T> performSearchObjects(String keyword, Pageable pageable){
		JBaseRepository<T, PK> repository = getRepository();
		if (CommonUtility.isEmpty(keyword)){
			return repository.findAll(pageable);
		}

		String searchableKeyword = new StringBuilder("%").append(keyword).append("%").toString();
		if (repository instanceof AdvancedSearchRepository || repository instanceof SearchRepository){
			return ((AdvancedSearchRepository)repository).search(searchableKeyword, pageable);
		}

		log.info("Not advanced search repository. ");
		return null;
	}

	protected Page<T> getPaginatedObjects(Integer page, Integer size){
    PageRequest pageRequest = PageRequest.of(page-1, size, Sort.Direction.ASC, "id");
    return getRepository().findAll(pageRequest);
	}

	public Page<T> getList(Integer pageNumber) {
		return getPaginatedObjects(pageNumber, CommonConstants.DEFAULT_PAGE_SIZE);
	}

	public Page<T> getList(Integer pageNumber, Integer size) {
		return getPaginatedObjects(pageNumber, size);
	}

	public T save(T entity) {
		return getRepository().save(entity);
	}

	public T create(T entity) {
		return getRepository().save(entity);
	}

	public T get(PK id) {
		T entity = getRepository().getOne(id);
		return entity;
	}

	public T getById(PK id) {
		return get(id);
	}

	public void delete(PK id) {
		try {
			//getRepository().delete(id);
		} catch (EmptyResultDataAccessException e) {
			log.error("Delete object by key", e);
		}
	}

	public void delete(T entity) {
		try {
			getRepository().delete(entity);
		} catch (EmptyResultDataAccessException e) {
			log.error("Delete object. ", e);
		}
	}

	
	public T update(T entity) {
		T getEntity = getRepository().getOne((PK) entity.getId());
		getRepository().save(entity);
		log.info("Merged entity: " + getEntity.getId());
		return entity;
	}

	public long count() {
		return getRepository().count();
	}

	@Transactional(readOnly = true)
	public List<T> getAll() {
		List<T> results = new ArrayList<>();
		results.addAll(getRepository().findAll());
		return results;
	}

	@Transactional(readOnly = true)
	public Page<T> search(Map<String, Object> parameters){
		return performSearchObjects((String)parameters.get(CommonConstants.PARAM_KEYWORD), (Pageable)parameters.get(CommonConstants.PARAM_PAGEABLE));
	}

	@Transactional(readOnly = true)
	public List<T> search(String keyword){
		Page<T> pageObjects = search(WebServicingHelper.createSearchParameters(keyword, null, null));
		return pageObjects.getContent();
	}

	@Transactional(readOnly = true)
	public Page<T> search(SearchCondition searchCondition){
		String keyword = (String)searchCondition.getParameters().get(CommonConstants.PARAM_SEARCH_PATTERN);
		JBaseRepository<T, PK> repository = this.getRepository();
		if (repository instanceof SearchableRepository){
			return ((SearchableRepository<T, PK>)repository).searchObjects(keyword, searchCondition.getPageable());
		}
		return new PageImpl<T>(null, searchCondition.getPageable(), 0);
	}
	
}
package net.sunrise.service.impl.contact;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import net.sunrise.domain.entity.general.Activity;
import net.sunrise.exceptions.ObjectNotFoundException;
import net.sunrise.framework.model.SearchParameter;
import net.sunrise.framework.repository.BaseRepository;
import net.sunrise.framework.service.GenericServiceImpl;
import net.sunrise.repository.contact.ActivityRepository;
import net.sunrise.service.api.contact.ActivityService;

@Service
public class ActivityServiceImpl extends GenericServiceImpl<Activity, Long> implements ActivityService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4580295852310880409L;

	@Inject 
	private ActivityRepository repository;
	
	protected BaseRepository<Activity, Long> getRepository() {
		return this.repository;
	}

	@Override
	public Page<Activity> getObjects(SearchParameter searchParameter) {
		return null;
	}

	@Override
	public Activity getName(String name) throws ObjectNotFoundException {
		return repository.findByName(name);
	}

	@Override
	protected Page<Activity> performSearch(String keyword, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}
}

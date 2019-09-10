package net.sunrise.service.dashboard;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import net.sunrise.domain.entity.system.DigitalDashlet;
import net.sunrise.exceptions.ObjectNotFoundException;
import net.sunrise.framework.model.SearchParameter;
import net.sunrise.framework.repository.BaseRepository;
import net.sunrise.framework.service.GenericServiceImpl;
import net.sunrise.repository.dashboard.DashletRepository;
import net.sunrise.repository.specification.system.DashletSpecifications;
import net.sunrise.service.api.dashboard.DashletService;

@Service
public class DashletServiceImpl extends GenericServiceImpl<DigitalDashlet, Long> implements DashletService{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3023542224992538560L;

	@Inject 
	private DashletRepository repository;
	
	protected BaseRepository<DigitalDashlet, Long> getRepository() {
		return this.repository;
	}

	@Override
	public DigitalDashlet getOne(String code) throws ObjectNotFoundException {
		return (DigitalDashlet)super.getOptionalObject(repository.findByName(code));
	}

	@Override
	protected Page<DigitalDashlet> performSearch(String keyword, Pageable pageable) {
		return repository.search(keyword, pageable);
	}

	@Override
	public Page<DigitalDashlet> getObjects(SearchParameter searchParameter) {
		Page<DigitalDashlet> pagedProducts = this.repository.findAll(DashletSpecifications.buildSpecification(searchParameter), searchParameter.getPageable());
		//Perform additional operations here
		return pagedProducts;
	}
}

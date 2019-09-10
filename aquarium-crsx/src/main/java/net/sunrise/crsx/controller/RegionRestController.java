package net.sunrise.crsx.controller;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.sunrise.common.CommonUtility;
import net.sunrise.constants.ControllerConstants;
import net.sunrise.controller.base.BaseRestController;
import net.sunrise.crsx.domain.entity.Region;
import net.sunrise.crsx.service.RegionService;
import net.sunrise.framework.model.SearchParameter;


@RequestMapping(ControllerConstants.REST_API + ControllerConstants.URI_REGION)
@RestController
public class RegionRestController extends BaseRestController<Region>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1238016119370043061L;
	private final static String CACHE_OBJECTS_KEY = "cache.regions";
	@Inject 
	private RegionService businessService;

	@RequestMapping(path = "/list", method = RequestMethod.GET)
	public List<Region> onList(HttpServletRequest request, HttpServletResponse response, Model model) {
		log.info("RestController::Come to enterprise data listing ...>>>>>>");
		List<Region> results = null;
		Object cachedValue = super.cacheGet(CACHE_OBJECTS_KEY);
		PageRequest pageRequest = null;
		SearchParameter searchParameter = null;
		Page<Region> objects = null;
		if (CommonUtility.isNotEmpty(cachedValue)){
			results = (List<Region>)cachedValue;
		} else {
			pageRequest = PageRequest.of(0, 500, Sort.Direction.ASC, "id");
			searchParameter = SearchParameter.builder()
					.pageable(pageRequest)
					.build();
			objects = businessService.getObjects(searchParameter);
			results = objects.getContent();
			super.cachePut(CACHE_OBJECTS_KEY, results);
		}
		log.info("Region data is loaded. >>>>>>");

		return results;
	}

	@Override
	protected void doUpdateBusinessObject(Region updatedClientObject) {
		super.doUpdateBusinessObject(updatedClientObject);
	}

	@Override
	protected Page<Region> doFetchBusinessObjects(Integer page, Integer size) {
		Page<Region> fetchedBusinessObjects = businessService.getObjects(page, size);
		log.info("Region Rest::FetchBusinessObjects: " + fetchedBusinessObjects.getTotalElements());
		return fetchedBusinessObjects;
	}

	@Override
	protected Region doFetchBusinessObject(Long id) {
		Region fetchedBusinessObject = businessService.getObject(id);
		log.info("Region Rest::FetchBusinessObject: " + fetchedBusinessObject.getName());
		return fetchedBusinessObject;
	}

	@Override
	protected void doDeleteBusinessObject(Long id) {
		log.info("Region Rest::DeleteBusinessObject: " + id);
		businessService.remove(id);
		log.info("Region Rest::DeleteBusinessObject is done");
	}

	@Override
	protected void doCreateBusinessObject(Region businessObject) {
		log.info("Region Rest::CreateBusinessObject: " + businessObject.getName());
		businessService.saveOrUpdate((Region)businessObject);
		log.info("Region Rest::CreateBusinessObject is done");
	}
}

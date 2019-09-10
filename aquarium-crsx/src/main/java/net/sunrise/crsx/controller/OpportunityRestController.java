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

import lombok.extern.slf4j.Slf4j;
import net.sunrise.common.CommonUtility;
import net.sunrise.constants.ControllerConstants;
import net.sunrise.controller.base.BaseRestController;
import net.sunrise.domain.entity.crx.Opportunity;
import net.sunrise.framework.model.SearchParameter;
import net.sunrise.service.api.crx.OpportunityService;

@Slf4j
@RequestMapping(ControllerConstants.REST_API + ControllerConstants.URI_OPPORTUNITY)
@RestController
public class OpportunityRestController extends BaseRestController<Opportunity>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8680801965632177225L;

	private final static String CACHE_OBJECTS_KEY = "cache.opportunities";

	@Inject 
	private OpportunityService businessService;

	@RequestMapping(path = "/list", method = RequestMethod.GET)
	public List<Opportunity> onListing(HttpServletRequest request, HttpServletResponse response, Model model) {
		log.info("RestController::Come to data listing ...>>>>>>");
		List<Opportunity> results = null;
		Object cachedValue = super.cacheGet(CACHE_OBJECTS_KEY);
		PageRequest pageRequest = null;
		SearchParameter searchParameter = null;
		Page<Opportunity> objects = null;
		if (CommonUtility.isNotEmpty(cachedValue)){
			results = (List<Opportunity>)cachedValue;
		} else {
			pageRequest = PageRequest.of(0, 500, Sort.Direction.ASC, "id");
			searchParameter = SearchParameter.builder()
					.pageable(pageRequest)
					.build();
			objects = businessService.getObjects(searchParameter);
			results = objects.getContent();
			super.cachePut(CACHE_OBJECTS_KEY, results);
		}
		log.info("Data listing is done. >>>>>>");

		return results;
	}

	@Override
	protected void doUpdateBusinessObject(Opportunity updatedClientObject) {
		super.doUpdateBusinessObject(updatedClientObject);
	}

	@Override
	protected Page<Opportunity> doFetchBusinessObjects(Integer page, Integer size) {
		Page<Opportunity> fetchedBusinessObjects = businessService.getObjects(page, size);
		log.info("Opportunity Rest::FetchBusinessObjects: " + fetchedBusinessObjects.getTotalElements());
		return fetchedBusinessObjects;
	}

	@Override
	protected Opportunity doFetchBusinessObject(Long id) {
		Opportunity fetchedBusinessObject = businessService.getObject(id);
		log.info("Opportunity Rest::FetchBusinessObject: " + fetchedBusinessObject.getName());
		return fetchedBusinessObject;
	}

	@Override
	protected void doDeleteBusinessObject(Long id) {
		log.info("Opportunity Rest::DeleteBusinessObject: " + id);
		businessService.remove(id);
		log.info("Opportunity Rest::DeleteBusinessObject is done");
	}

	@Override
	protected void doCreateBusinessObject(Opportunity businessObject) {
		log.info("Opportunity Rest::CreateBusinessObject: " + businessObject.getName());
		businessService.saveOrUpdate((Opportunity)businessObject);
		log.info("Opportunity Rest::CreateBusinessObject is done");
	}
}

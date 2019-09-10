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
import net.sunrise.domain.entity.crx.Campaign;
import net.sunrise.framework.model.SearchParameter;
import net.sunrise.service.api.crx.CampaignService;

@Slf4j
@RequestMapping(ControllerConstants.REST_API + ControllerConstants.URI_CAMPAIGN)
@RestController
public class CampaignRestController extends BaseRestController<Campaign>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3570656624451230166L;
	private final static String CACHE_OBJECTS_KEY = "cache.campaigns";
	@Inject 
	private CampaignService businessService;

	@RequestMapping(path = "/list", method = RequestMethod.GET)
	public List<Campaign> onListCampaigns(HttpServletRequest request, HttpServletResponse response, Model model) {
		log.info("RestController::Come to enterprise data listing ...>>>>>>");
		List<Campaign> results = null;
		Object cachedValue = super.cacheGet(CACHE_OBJECTS_KEY);
		PageRequest pageRequest = null;
		SearchParameter searchParameter = null;
		Page<Campaign> objects = null;
		if (CommonUtility.isNotEmpty(cachedValue)){
			results = (List<Campaign>)cachedValue;
		} else {
			pageRequest = PageRequest.of(0, 500, Sort.Direction.ASC, "id");
			searchParameter = SearchParameter.builder()
					.pageable(pageRequest)
					.build();
			objects = businessService.getObjects(searchParameter);
			results = objects.getContent();
			super.cachePut(CACHE_OBJECTS_KEY, results);
		}
		log.info("Campaign data is loaded. >>>>>>");

		return results;
	}

	@Override
	protected void doUpdateBusinessObject(Campaign updatedClientObject) {
		super.doUpdateBusinessObject(updatedClientObject);
	}

	@Override
	protected Page<Campaign> doFetchBusinessObjects(Integer page, Integer size) {
		Page<Campaign> fetchedBusinessObjects = businessService.getObjects(page, size);
		log.info("Campaign Rest::FetchBusinessObjects: " + fetchedBusinessObjects.getTotalElements());
		return fetchedBusinessObjects;
	}

	@Override
	protected Campaign doFetchBusinessObject(Long id) {
		Campaign fetchedBusinessObject = businessService.getObject(id);
		log.info("Campaign Rest::FetchBusinessObject: " + fetchedBusinessObject.getName());
		return fetchedBusinessObject;
	}

	@Override
	protected void doDeleteBusinessObject(Long id) {
		log.info("Campaign Rest::DeleteBusinessObject: " + id);
		businessService.remove(id);
		log.info("Campaign Rest::DeleteBusinessObject is done");
	}

	@Override
	protected void doCreateBusinessObject(Campaign businessObject) {
		log.info("Campaign Rest::CreateBusinessObject: " + businessObject.getName());
		businessService.saveOrUpdate((Campaign)businessObject);
		log.info("Campaign Rest::CreateBusinessObject is done");
	}
}

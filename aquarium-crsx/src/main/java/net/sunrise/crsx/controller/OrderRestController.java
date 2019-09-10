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
import net.sunrise.domain.entity.crx.BizOrder;
import net.sunrise.framework.model.SearchParameter;
import net.sunrise.service.api.crx.orders.BizOrderService;

@Slf4j
@RequestMapping(ControllerConstants.REST_API + ControllerConstants.URI_ORDER)
@RestController
public class OrderRestController extends BaseRestController<BizOrder>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8553947088866809347L;
	private final static String CACHE_OBJECTS_KEY = "cache.orders";
	@Inject 
	private BizOrderService businessService;

	@RequestMapping(path = "/list", method = RequestMethod.GET)
	public List<BizOrder> onListing(HttpServletRequest request, HttpServletResponse response, Model model) {
		log.info("RestController::Come to order data listing ...>>>>>>");
		List<BizOrder> results = null;
		Object cachedValue = super.cacheGet(CACHE_OBJECTS_KEY);
		PageRequest pageRequest = null;
		SearchParameter searchParameter = null;
		Page<BizOrder> objects = null;
		if (CommonUtility.isNotEmpty(cachedValue)){
			results = (List<BizOrder>)cachedValue;
		} else {
			pageRequest = PageRequest.of(0, 500, Sort.Direction.ASC, "id");
			searchParameter = SearchParameter.builder()
					.pageable(pageRequest)
					.build();
			objects = businessService.getObjects(searchParameter);
			results = objects.getContent();
			super.cachePut(CACHE_OBJECTS_KEY, results);
		}
		log.info("Order data is loaded. >>>>>>");

		return results;
	}

	@Override
	protected void doUpdateBusinessObject(BizOrder updatedClientObject) {
		super.doUpdateBusinessObject(updatedClientObject);
	}

	@Override
	protected Page<BizOrder> doFetchBusinessObjects(Integer page, Integer size) {
		Page<BizOrder> fetchedBusinessObjects = businessService.getObjects(page, size);
		log.info("Order Rest::FetchBusinessObjects: " + fetchedBusinessObjects.getTotalElements());
		return fetchedBusinessObjects;
	}

	@Override
	protected BizOrder doFetchBusinessObject(Long id) {
		BizOrder fetchedBusinessObject = businessService.getObject(id);
		log.info("Order Rest::FetchBusinessObject: " + fetchedBusinessObject.getCode());
		return fetchedBusinessObject;
	}

	@Override
	protected void doDeleteBusinessObject(Long id) {
		log.info("Order Rest::DeleteBusinessObject: " + id);
		businessService.remove(id);
		log.info("Order Rest::DeleteBusinessObject is done");
	}

	@Override
	protected void doCreateBusinessObject(BizOrder businessObject) {
		log.info("Order Rest::CreateBusinessObject: " + businessObject.getCode());
		businessService.saveOrUpdate((BizOrder)businessObject);
		log.info("Order Rest::CreateBusinessObject is done");
	}
}

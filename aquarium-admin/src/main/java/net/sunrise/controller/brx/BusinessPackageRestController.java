package net.sunrise.controller.brx;

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
import net.sunrise.controller.ControllerConstants;
import net.sunrise.controller.base.BaseRestController;
import net.sunrise.domain.entity.BusinessPackage;
import net.sunrise.framework.model.SearchParameter;
import net.sunrise.service.BusinessPackageService;

@Slf4j
@RequestMapping(ControllerConstants.REST_API + ControllerConstants.URI_BUSINESS_PACKAGE)
@RestController
public class BusinessPackageRestController extends BaseRestController<BusinessPackage>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1016607970173104453L;
	private final static String CACHE_OBJECTS_KEY = "cache.bizPackages";
	@Inject 
	private BusinessPackageService businessService;

	
	@RequestMapping(path = "/list", method = RequestMethod.GET)
	public List<BusinessPackage> onListBusinessPackages(HttpServletRequest request, HttpServletResponse response, Model model) {
		log.info("RestController::Come to enterprise data listing ...>>>>>>");
		List<BusinessPackage> results = null;
		Object cachedValue = super.cacheGet(CACHE_OBJECTS_KEY);
		PageRequest pageRequest = null;
		SearchParameter searchParameter = null;
		Page<BusinessPackage> objects = null;
		if (CommonUtility.isNotEmpty(cachedValue)){
			results = (List<BusinessPackage>)cachedValue;
		} else {
			pageRequest = PageRequest.of(0, 500, Sort.Direction.ASC, "id");
			searchParameter = SearchParameter.builder()
					.pageable(pageRequest)
					.build();
			objects = businessService.getObjects(searchParameter);
			results = objects.getContent();
			super.cachePut(CACHE_OBJECTS_KEY, results);
		}
		log.info("BusinessPackage data is loaded. >>>>>>");

		return results;
	}

	@Override
	protected void doUpdateBusinessObject(BusinessPackage updatedClientObject) {
		super.doUpdateBusinessObject(updatedClientObject);
	}

	@Override
	protected Page<BusinessPackage> doFetchBusinessObjects(Integer page, Integer size) {
		Page<BusinessPackage> fetchedBusinessObjects = businessService.getObjects(page, size);
		log.info("BusinessPackage Rest::FetchBusinessObjects: " + fetchedBusinessObjects.getTotalElements());
		return fetchedBusinessObjects;
	}

	@Override
	protected BusinessPackage doFetchBusinessObject(Long id) {
		BusinessPackage fetchedBusinessObject = businessService.getObject(id);
		log.info("BusinessPackage Rest::FetchBusinessObject: " + fetchedBusinessObject.getName());
		return fetchedBusinessObject;
	}

	@Override
	protected void doDeleteBusinessObject(Long id) {
		log.info("BusinessPackage Rest::DeleteBusinessObject: " + id);
		businessService.remove(id);
		log.info("BusinessPackage Rest::DeleteBusinessObject is done");
	}

	@Override
	protected void doCreateBusinessObject(BusinessPackage businessObject) {
		log.info("BusinessPackage Rest::CreateBusinessObject: " + businessObject.getName());
		businessService.saveOrUpdate((BusinessPackage)businessObject);
		log.info("BusinessPackage Rest::CreateBusinessObject is done");
	}
}

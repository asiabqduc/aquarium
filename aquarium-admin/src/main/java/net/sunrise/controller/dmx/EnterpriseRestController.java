package net.sunrise.controller.dmx;

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
import net.sunrise.domain.entity.dmx.Enterprise;
import net.sunrise.framework.model.SearchParameter;
import net.sunrise.service.api.dmx.EnterpriseService;

@Slf4j
@RequestMapping(ControllerConstants.REST_API + ControllerConstants.REQ_MAPPING_ENTERPRISE)
@RestController
public class EnterpriseRestController extends BaseRestController<Enterprise>{
	private final static String CACHE_OBJECTS_KEY = "cache.enterprises";
	@Inject 
	private EnterpriseService businessService;

	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/list", method = RequestMethod.GET)
	public List<Enterprise> onListEnterprises(HttpServletRequest request, HttpServletResponse response, Model model) {
		log.info("RestController::Come to enterprise data listing ...>>>>>>");
		List<Enterprise> results = null;
		Object cachedValue = super.cacheGet(CACHE_OBJECTS_KEY);
		PageRequest pageRequest = null;
		SearchParameter searchParameter = null;
		Page<Enterprise> objects = null;
		if (CommonUtility.isNotEmpty(cachedValue)){
			results = (List<Enterprise>)cachedValue;
		} else {
			pageRequest = PageRequest.of(0, 500, Sort.Direction.ASC, "id");
			searchParameter = SearchParameter.builder()
					.pageable(pageRequest)
					.build();
			objects = businessService.getObjects(searchParameter);
			results = objects.getContent();
			super.cachePut(CACHE_OBJECTS_KEY, results);
		}
		log.info("Enterprise data is loaded. >>>>>>");

		return results;
	}

	@Override
	protected void doUpdateBusinessObject(Enterprise updatedClientObject) {
		super.doUpdateBusinessObject(updatedClientObject);
	}

	@Override
	protected Page<Enterprise> doFetchBusinessObjects(Integer page, Integer size) {
		Page<Enterprise> fetchedBusinessObjects = businessService.getObjects(page, size);
		log.info("Enterprise Rest::FetchBusinessObjects: " + fetchedBusinessObjects.getTotalElements());
		return fetchedBusinessObjects;
	}

	@Override
	protected Enterprise doFetchBusinessObject(Long id) {
		Enterprise fetchedBusinessObject = businessService.getObject(id);
		log.info("Enterprise Rest::FetchBusinessObject: " + fetchedBusinessObject.getCode());
		return fetchedBusinessObject;
	}

	@Override
	protected void doDeleteBusinessObject(Long id) {
		log.info("Enterprise Rest::DeleteBusinessObject: " + id);
		businessService.remove(id);
		log.info("Enterprise Rest::DeleteBusinessObject is done");
	}

	@Override
	protected void doCreateBusinessObject(Enterprise businessObject) {
		log.info("Enterprise Rest::CreateBusinessObject: " + businessObject.getCode());
		businessService.saveOrUpdate((Enterprise)businessObject);
		log.info("Enterprise Rest::CreateBusinessObject is done");
	}
}

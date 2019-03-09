package com.sunrise.controller.brx;

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

import com.sunrise.controller.ControllerConstants;

import lombok.extern.slf4j.Slf4j;
import net.brilliance.common.CommonUtility;
import net.brilliance.framework.model.SearchParameter;
import net.sunrise.controller.base.BaseRestController;
import net.sunrise.domain.entity.Schedule;
import net.sunrise.service.ScheduleService;

@Slf4j
@RequestMapping(ControllerConstants.REST_API + ControllerConstants.URI_BRX_SCHEDULE)
@RestController
public class ScheduleRestController extends BaseRestController<Schedule>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4744658945401331912L;
	private final static String CACHE_OBJECTS_KEY = "cache.bizPackages";
	@Inject 
	private ScheduleService businessService;

	@RequestMapping(path = "/list", method = RequestMethod.GET)
	public List<Schedule> onListBusinessPackages(HttpServletRequest request, HttpServletResponse response, Model model) {
		log.info("RestController::Come to enterprise data listing ...>>>>>>");
		List<Schedule> results = null;
		Object cachedValue = super.cacheGet(CACHE_OBJECTS_KEY);
		PageRequest pageRequest = null;
		SearchParameter searchParameter = null;
		Page<Schedule> objects = null;
		if (CommonUtility.isNotEmpty(cachedValue)){
			results = (List<Schedule>)cachedValue;
		} else {
			pageRequest = PageRequest.of(0, 500, Sort.Direction.ASC, "id");
			searchParameter = SearchParameter.builder()
					.pageable(pageRequest)
					.build();
			objects = businessService.getObjects(searchParameter);
			results = objects.getContent();
			super.cachePut(CACHE_OBJECTS_KEY, results);
		}
		log.info("Schedule data is loaded. >>>>>>");

		return results;
	}

	@Override
	protected void doUpdateBusinessObject(Schedule updatedClientObject) {
		super.doUpdateBusinessObject(updatedClientObject);
	}

	@Override
	protected Page<Schedule> doFetchBusinessObjects(Integer page, Integer size) {
		Page<Schedule> fetchedBusinessObjects = businessService.getObjects(page, size);
		log.info("Schedule Rest::FetchBusinessObjects: " + fetchedBusinessObjects.getTotalElements());
		return fetchedBusinessObjects;
	}

	@Override
	protected Schedule doFetchBusinessObject(Long id) {
		Schedule fetchedBusinessObject = businessService.getObject(id);
		log.info("Schedule Rest::FetchBusinessObject: " + fetchedBusinessObject.getName());
		return fetchedBusinessObject;
	}

	@Override
	protected void doDeleteBusinessObject(Long id) {
		log.info("Schedule Rest::DeleteBusinessObject: " + id);
		businessService.remove(id);
		log.info("Schedule Rest::DeleteBusinessObject is done");
	}

	@Override
	protected void doCreateBusinessObject(Schedule businessObject) {
		log.info("Schedule Rest::CreateBusinessObject: " + businessObject.getName());
		businessService.saveOrUpdate((Schedule)businessObject);
		log.info("Schedule Rest::CreateBusinessObject is done");
	}
}

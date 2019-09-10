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
import net.sunrise.domain.entity.general.Attachment;
import net.sunrise.framework.model.SearchParameter;
import net.sunrise.service.api.general.AttachmentService;

@Slf4j
@RequestMapping(ControllerConstants.REST_API + ControllerConstants.URI_ATTACHMENT)
@RestController
public class AttachmentRestController extends BaseRestController<Attachment>{
	private final static String CACHE_OBJECTS_KEY = "cache.attachmens";
	@Inject 
	private AttachmentService businessService;

	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/list", method = RequestMethod.GET)
	public List<Attachment> listBusinessObjects(HttpServletRequest request, HttpServletResponse response, Model model) {
		log.info("RestController::Come to list business objects ...>>>>>>");
		List<Attachment> results = null;
		Object cachedValue = super.cacheGet(CACHE_OBJECTS_KEY);
		PageRequest pageRequest = null;
		SearchParameter searchParameter = null;
		Page<Attachment> objects = null;
		if (CommonUtility.isNotEmpty(cachedValue)){
			results = (List<Attachment>)cachedValue;
		} else {
			pageRequest = PageRequest.of(0, 500, Sort.Direction.ASC, "id");
			searchParameter = SearchParameter.builder().pageable(pageRequest).build();
			objects = businessService.getObjects(searchParameter);
			results = objects.getContent();
			super.cachePut(CACHE_OBJECTS_KEY, results);
		}
		log.info("Attachment data is loaded. >>>>>>");

		return results;
	}

	@Override
	protected void doUpdateBusinessObject(Attachment updatedClientObject) {
		super.doUpdateBusinessObject(updatedClientObject);
	}

	@Override
	protected Page<Attachment> doFetchBusinessObjects(Integer page, Integer size) {
		Page<Attachment> fetchedBusinessObjects = businessService.getObjects(page, size);
		log.info("Attachment Rest::FetchBusinessObjects: " + fetchedBusinessObjects.getTotalElements());
		return fetchedBusinessObjects;
	}

	@Override
	protected Attachment doFetchBusinessObject(Long id) {
		Attachment fetchedBusinessObject = businessService.getObject(id);
		log.info("Attachment Rest::FetchBusinessObject: " + fetchedBusinessObject.getName());
		return fetchedBusinessObject;
	}

	@Override
	protected void doDeleteBusinessObject(Long id) {
		log.info("Attachment Rest::DeleteBusinessObject: " + id);
		businessService.remove(id);
		log.info("Attachment Rest::DeleteBusinessObject is done");
	}

	@Override
	protected void doCreateBusinessObject(Attachment businessObject) {
		log.info("Attachment Rest::CreateBusinessObject: " + businessObject.getName());
		businessService.saveOrUpdate((Attachment)businessObject);
		log.info("Attachment Rest::CreateBusinessObject is done");
	}
}

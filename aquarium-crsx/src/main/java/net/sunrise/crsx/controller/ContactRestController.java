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
import net.sunrise.constants.ControllerConstants;
import net.sunrise.controller.base.BaseRestController;
import net.sunrise.domain.entity.crx.contact.Contact;
import net.sunrise.framework.model.SearchParameter;
import net.sunrise.service.api.contact.ContactService;

@Slf4j
@RestController
@RequestMapping(ControllerConstants.REQUEST_URI_CONTACT)
public class ContactRestController extends BaseRestController<Contact>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5325220935619954744L;
	@Inject
	private ContactService businessService;

	@RequestMapping(path = "/list", method = RequestMethod.GET)
	public List<Contact> list(HttpServletRequest request, HttpServletResponse response, Model model) {
		PageRequest pageRequest = PageRequest.of(0, 500, Sort.Direction.ASC, "id");
		SearchParameter searchParameter = SearchParameter.getInstance()
				.setPageable(pageRequest);
		Page<Contact> objects = businessService.getObjects(searchParameter);
		return objects.getContent();
	}

	@Override
	protected void doUpdateBusinessObject(Contact updatedClientObject) {
		super.doUpdateBusinessObject(updatedClientObject);
	}

	@Override
	protected Page<Contact> doFetchBusinessObjects(Integer page, Integer size) {
		Page<Contact> fetchedBusinessObjects = businessService.getObjects(page, size);
		log.info("Contact Rest::FetchBusinessObjects: " + fetchedBusinessObjects.getTotalElements());
		return fetchedBusinessObjects;
	}

	@Override
	protected Contact doFetchBusinessObject(Long id) {
		Contact fetchedBusinessObject = businessService.getObject(id);
		log.info("Contact Rest::FetchBusinessObject: " + fetchedBusinessObject.getCode());
		return fetchedBusinessObject;
	}

	@Override
	protected void doDeleteBusinessObject(Long id) {
		log.info("Contact Rest::DeleteBusinessObject: " + id);
		businessService.remove(id);
		log.info("Contact Rest::DeleteBusinessObject is done");
	}

	@Override
	protected void doCreateBusinessObject(Contact businessObject) {
		log.info("Contact Rest::CreateBusinessObject: " + businessObject.getCode());
		businessService.saveOrUpdate((Contact)businessObject);
		log.info("Contact Rest::CreateBusinessObject is done");
	}
}

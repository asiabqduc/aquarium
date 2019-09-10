package net.sunrise.controller.auth;

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
import net.sunrise.controller.ControllerConstants;
import net.sunrise.controller.base.BaseRestController;
import net.sunrise.domain.entity.admin.ClientUserAccount;
import net.sunrise.framework.model.SearchParameter;
import net.sunrise.service.api.admin.ClientUserAccountService;

@RequestMapping(ControllerConstants.REST_API + ControllerConstants.REQ_URI_CLIENT_USER_ACCOUNT)
@RestController
public class ClientUserAccountRestController extends BaseRestController<ClientUserAccount>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8693944244959440889L;
	private final static String CACHE_OBJECTS_KEY = "cache.clientAccounts";
	@Inject 
	private ClientUserAccountService businessService;

	
	@RequestMapping(path = "/list", method = RequestMethod.GET)
	public List<ClientUserAccount> onListBusinessObjects(HttpServletRequest request, HttpServletResponse response, Model model) {
		log.info("RestController::Come to enterprise data listing ...>>>>>>");
		List<ClientUserAccount> results = null;
		Object cachedValue = super.cacheGet(CACHE_OBJECTS_KEY);
		PageRequest pageRequest = null;
		SearchParameter searchParameter = null;
		Page<ClientUserAccount> objects = null;
		if (CommonUtility.isNotEmpty(cachedValue)){
			results = (List<ClientUserAccount>)cachedValue;
		} else {
			pageRequest = PageRequest.of(0, 500, Sort.Direction.ASC, "id");
			searchParameter = SearchParameter.builder()
					.pageable(pageRequest)
					.build();
			objects = businessService.getObjects(searchParameter);
			results = objects.getContent();
			super.cachePut(CACHE_OBJECTS_KEY, results);
		}
		log.info("ClientUserAccount data is loaded. >>>>>>");

		return results;
	}

	@Override
	protected void doUpdateBusinessObject(ClientUserAccount updatedClientObject) {
		super.doUpdateBusinessObject(updatedClientObject);
	}

	@Override
	protected Page<ClientUserAccount> doFetchBusinessObjects(Integer page, Integer size) {
		Page<ClientUserAccount> fetchedBusinessObjects = businessService.getObjects(page, size);
		log.info("ClientUserAccount Rest::FetchBusinessObjects: " + fetchedBusinessObjects.getTotalElements());
		return fetchedBusinessObjects;
	}

	@Override
	protected ClientUserAccount doFetchBusinessObject(Long id) {
		ClientUserAccount fetchedBusinessObject = businessService.getObject(id);
		log.info("ClientUserAccount Rest::FetchBusinessObject: " + fetchedBusinessObject.getUserAccount().getSsoId());
		return fetchedBusinessObject;
	}

	@Override
	protected void doDeleteBusinessObject(Long id) {
		log.info("ClientUserAccount Rest::DeleteBusinessObject: " + id);
		businessService.remove(id);
		log.info("ClientUserAccount Rest::DeleteBusinessObject is done");
	}

	@Override
	protected void doCreateBusinessObject(ClientUserAccount businessObject) {
		log.info("ClientUserAccount Rest::CreateBusinessObject: " + businessObject.getUserAccount().getSsoId());
		businessService.saveOrUpdate((ClientUserAccount)businessObject);
		log.info("ClientUserAccount Rest::CreateBusinessObject is done");
	}
}

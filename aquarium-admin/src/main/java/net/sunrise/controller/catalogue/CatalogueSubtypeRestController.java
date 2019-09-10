package net.sunrise.controller.catalogue;

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

import net.sunrise.controller.ControllerConstants;
import net.sunrise.controller.base.BaseRestController;
import net.sunrise.domain.entity.general.CatalogueSubtype;
import net.sunrise.framework.model.SearchParameter;
import net.sunrise.service.api.inventory.CatalogueSubtypeService;

@RestController
@RequestMapping("/" + ControllerConstants.REQUEST_MAPPING_CATALOG_SUBTYPE)
public class CatalogueSubtypeRestController extends BaseRestController<CatalogueSubtype> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4361513429746873014L;
	
	@Inject 
	private CatalogueSubtypeService businessManager;

	@RequestMapping(path = "/list", method = RequestMethod.GET)
	public List<CatalogueSubtype> onListCatalogueSubtypes(HttpServletRequest request, HttpServletResponse response, Model model) {
		log.info("Rest::Come to catalogue subtype data listing ...>>>>>>");
		PageRequest pageRequest = PageRequest.of(0, 500, Sort.Direction.ASC, "id");
		SearchParameter searchParameter = SearchParameter.getInstance()
				.setPageable(pageRequest);
		Page<CatalogueSubtype> objects = businessManager.getObjects(searchParameter);
		log.info("Catalogue subtype data is loaded. >>>>>>");
		return objects.getContent();
	}
}

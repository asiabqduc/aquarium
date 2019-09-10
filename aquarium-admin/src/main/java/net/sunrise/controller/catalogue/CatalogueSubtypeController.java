package net.sunrise.controller.catalogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;

import net.sunrise.common.CommonUtility;
import net.sunrise.common.GUUISequenceGenerator;
import net.sunrise.controller.ControllerConstants;
import net.sunrise.controller.base.BaseController;
import net.sunrise.domain.SelectItem;
import net.sunrise.domain.entity.general.CatalogueSubtype;
import net.sunrise.framework.model.SearchParameter;
import net.sunrise.framework.model.SequenceType;
import net.sunrise.runnable.UpdateSystemSequenceThread;
import net.sunrise.service.api.inventory.CatalogueSubtypeService;

@Controller
@RequestMapping("/" + ControllerConstants.REQUEST_MAPPING_CATALOG_SUBTYPE)
public class CatalogueSubtypeController extends BaseController { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -7519805436977333705L;

	private static final String PAGE_CONTEXT_PREFIX = ControllerConstants.CONTEXT_WEB_PAGES + "general/catalog/catalogueSubtype";

	@Inject
  private TaskExecutor taskExecutor;
	
	@Inject
	private ApplicationContext applicationContext;

	/*@Inject
	private GlobalResourcesDispatcher globalResourcesDispatcher;
	@Inject 
	private InventoryCatalogDataDispatchHelper inventoryCatalogDataDispatchHelper;*/

	@Inject
	private CatalogueSubtypeService businessManager;


	/**
	 * Import catalogs.
   */
	@Override
	protected String performImport(Model model, HttpServletRequest request) {
		log.info("Importing catalogue subtypes .....");
		constructData();
		log.info("Leave importing catalog-subtypes!");
		return PAGE_CONTEXT_PREFIX + "Browse";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String list(Model model, HttpServletRequest request) {
		return PAGE_CONTEXT_PREFIX + "Browse";
	}

	@Override
	protected String performListing(Model model, HttpServletRequest request) {
		System.out.println("catalogue subtypes performListing");
		return PAGE_CONTEXT_PREFIX + "Browse";
	}

	@Override
	protected void constructData() {
		log.info("Constructing catalogue subtypes data.....");

		//List<CatalogueSubtype> catalogues;
		try {
			/*catalogues = inventoryCatalogDataDispatchHelper.dispatchCatalogues();
			for (CatalogueSubtype catalog :catalogues){
				if (!businessManager.getByCode(catalog.getCode()).isPresent()){
					businessManager.save(catalog);
				}
			}*/
		} catch (Exception e) {
			log.error("Import catalogues", e);
		}
		log.info("Leave constructing catalogue subtypes data!");
	}

	@Override
	protected void onPostConstruct() {
		super.postConstructData("Setup catalogue subtypes");
	}

	/**
	 * Export catalogs.
   */
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public String exports(Model model, HttpServletRequest request) {
		log.info("Exporting catalogue subtypes .....");
		return PAGE_CONTEXT_PREFIX + "Browse";
	}

	/**
	 * Create a new department and place in Model attribute.
	 */
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String createForm(Model model) {
		String guuId = GUUISequenceGenerator.getInstance().nextGUUIdString(SequenceType.CATALOGUE_SUBTYPE.getType());

		CatalogueSubtype newCatalogueSubtype = CatalogueSubtype
		.builder()
		.code(guuId)
		.build();
		model.addAttribute(net.sunrise.common.CommonConstants.FETCHED_OBJECT, newCatalogueSubtype);
		return PAGE_CONTEXT_PREFIX + "Edit";
	}

	/**
	 * Create/update a contact.
	*/
	@RequestMapping(value="/create", method = RequestMethod.POST)
	public String create(@Valid CatalogueSubtype uiBizObject, BindingResult bindingResult,
			Model model, HttpServletRequest httpServletRequest,
			RedirectAttributes redirectAttributes, Locale locale) {
		
		if (bindingResult.hasErrors()) {
			model.addAttribute(net.sunrise.common.CommonConstants.FETCHED_OBJECT, uiBizObject);
			return PAGE_CONTEXT_PREFIX + "Edit";
		}

		if (!CommonUtility.isNull(uiBizObject.getParent()) && CommonUtility.isNull(uiBizObject.getParent().getId())){
			uiBizObject.setParent(null);
		}

		log.info("Creating/updating catalogue subtype");
		
		model.asMap().clear();
		//redirectAttributes.addFlashAttribute("message", new Message("success", messageSource.getMessage("general_save_success", new Object[] {}, locale)));

		businessManager.saveOrUpdate(uiBizObject);
		//systemSequenceManager.registerSequence(uiBizObject.getCode());

		UpdateSystemSequenceThread updateSystemSequenceThread = applicationContext.getBean(UpdateSystemSequenceThread.class, uiBizObject.getCode());
		taskExecutor.execute(updateSystemSequenceThread);
		//TODO: Pay attention please
		String ret = "redirect:/catalogSubtype/"+uiBizObject.getId().toString();
		return ret;//"redirect:/department/" + UrlUtil.encodeUrlPathSegment(department.getId().toString(), httpServletRequest);
	}

	/**
	 * Retrieve the book with the specified id for the update form.
	 */
	@RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model model) {
			model.addAttribute(net.sunrise.common.CommonConstants.FETCHED_OBJECT, businessManager.getObject(id));
			return PAGE_CONTEXT_PREFIX + "Edit";
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String show(@PathVariable("id") Long id, Model model) {
		log.info("Fetch business object of catalogue subtype with id: " + id);

		model.addAttribute(ControllerConstants.FETCHED_OBJECT, businessManager.getObject(id));
		
		return PAGE_CONTEXT_PREFIX + "Show";
	}

	@Override
	protected List<SelectItem> suggestItems(String keyword) {
		List<SelectItem> suggestedItems = new ArrayList<>();
		Page<CatalogueSubtype> fetchedObjects = this.businessManager.searchObjects(keyword, null);
		for (CatalogueSubtype dept : fetchedObjects.getContent()) {
			suggestedItems.add(SelectItem.builder().id(dept.getId()).code(dept.getCode()).name(dept.getName()).build());
		}
		return suggestedItems;
	}

	@Override
	protected String performSearch(SearchParameter params) {
		Map<String, Object> parameters = new HashMap<>();
		Page<CatalogueSubtype> pageContentData = businessManager.search(parameters);
		params.getModel().addAttribute(ControllerConstants.FETCHED_OBJECT, pageContentData);
		/*HttpSession session = super.getSession();
		session.setAttribute(CommonConstants.CACHED_PAGE_MODEL, params.getModel());*/
		Gson gson = new Gson();
		//return gson.toJson(pageContentData.getContent());
		return PAGE_CONTEXT_PREFIX + "Browse :: result-teable " + gson.toJson(pageContentData.getContent());
	}

	/*protected List performSearchObjects(SearchParameter params){
		Map<String, Object> parameters = new HashMap<>();
		Page<Catalog> pageContentData = businessManager.search(parameters);
		params.getModel().addAttribute("catalogues", pageContentData);
		return pageContentData.getContent();
	}*/
}

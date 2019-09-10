package net.sunrise.controller.admin;

import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sunrise.common.CommonUtility;
import net.sunrise.controller.ControllerConstants;
import net.sunrise.controller.base.BaseController;
import net.sunrise.dispatch.GlobalDataInitializer;
import net.sunrise.domain.entity.admin.Office;
import net.sunrise.framework.model.SearchParameter;
import net.sunrise.service.api.admin.OfficeService;

@Controller
@RequestMapping(ControllerConstants.URI_ENTERPRISE_UNIT)
public class EnterpriseUnitController extends BaseController {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8691283989352764049L;

	private static final String PAGE_CONTEXT_PREFIX = ControllerConstants.CONTEXT_WEB_PAGES + "admin/org/enterprise";

	@Inject
	private OfficeService businessServiceManager;

	@Inject 
	private GlobalDataInitializer globalDataInitializer;

	@RequestMapping(path={"/", ""}, method=RequestMethod.GET)
	public String goHome(){
		return getDefaultPage();
	}

	@RequestMapping(path="/search", method=RequestMethod.POST)
	@ResponseBody
	public String onSearch(@RequestBody(required = false) SearchParameter searchParams, Model model, Pageable pageable){
		if (CommonUtility.isEmpty(searchParams.getPageable())) {
			searchParams.setPageable(pageable);
		}
		Page<Office> results = businessServiceManager.getObjects(searchParams);
		List<Office> expectedResults = results.getContent().subList(0, 1550);
		Gson gson = new GsonBuilder().serializeNulls().create();
		String jsonBizObjects = gson.toJson(expectedResults);
		return jsonBizObjects;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String onShow(@PathVariable("id") Long id, Model model) {
		log.info("Fetch office object with id: " + id);

		Office fetchedObject = businessServiceManager.getObject(id);
		model.addAttribute(ControllerConstants.FETCHED_OBJECT, fetchedObject);
		
		return PAGE_CONTEXT_PREFIX + "Vieww";
	}

	/**
	 * Create a new office object and place in Model attribute.
	 */
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String showCreateForm(Model model) {
		model.addAttribute(net.sunrise.common.CommonConstants.FETCHED_OBJECT, new Office());
		return PAGE_CONTEXT_PREFIX + "Edit";
	}

	/**
	 * Import employee objects.
	 */
	@Override
	protected String performImport(Model model, HttpServletRequest request) {
		InputStream inputStream = null;
		try {
			/*inputStream = CommonUtility.getClassPathResourceInputStream("/config/data/data-vpex-repaired.xlsx");
			businessServiceManager.importOffices(inputStream, "chinh thuc", 1);*/
		} catch (Exception e) {
			log.error(CommonUtility.getStackTrace(e));
		} finally{
			try {
				CommonUtility.closeInputStream(inputStream);
			} catch (Exception e2) {
				log.error(CommonUtility.getStackTrace(e2));
			}
		}
		return PAGE_CONTEXT_PREFIX + "Browse";
	}

	/**
	 * Retrieve the book with the specified id for the update form.
	 */
	@RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
	public String updateForm(@PathVariable("id") Long id, Model model) {
		model.addAttribute(net.sunrise.common.CommonConstants.FETCHED_OBJECT, businessServiceManager.getObject(id));
		return PAGE_CONTEXT_PREFIX + "Edit";
	}

	@Override
	protected String performSearch(SearchParameter params) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getDefaultPage(){
		if (1 > this.businessServiceManager.count()){
			setupFakeObjects();
		}
		return PAGE_CONTEXT_PREFIX + "Browse";
	}

	private void setupFakeObjects(){
		this.globalDataInitializer.constructOffices();
	}
}

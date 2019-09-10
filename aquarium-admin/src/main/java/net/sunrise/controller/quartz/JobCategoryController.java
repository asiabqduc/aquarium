package net.sunrise.controller.quartz;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sunrise.common.CommonConstants;
import net.sunrise.common.CommonUtility;
import net.sunrise.controller.ControllerConstants;
import net.sunrise.controller.base.BaseController;
import net.sunrise.dispatch.GlobalDataInitializer;
import net.sunrise.domain.SelectItem;
import net.sunrise.domain.entity.schedule.JobCategory;
import net.sunrise.framework.model.SearchParameter;
import net.sunrise.service.api.admin.quartz.JobCategoryService;
import net.sunrise.utility.Message;

@Controller
@RequestMapping(ControllerConstants.REQUEST_URI_JOB_CATEGORY)
public class JobCategoryController extends BaseController {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7907223122267378812L;
	private static final String REDIRECT_PAGE = ControllerConstants.REDIRECT_PREFIX + "jobCategory/";
	private static final String PAGE_CONTEXT_PREFIX = ControllerConstants.CONTEXT_WEB_PAGES + "admin/quartz/jobCategory";

	/*@Inject
	@Lazy
	private SchedulerFactoryBean schedulerFactoryBean;*/

	@Inject
	private JobCategoryService businessService;

	@Inject 
	private GlobalDataInitializer globalDataInitializer;

	@RequestMapping(path={"/", ""}, method=RequestMethod.GET)
	public String goHome(){
		return getDefaultPage();
	}

	@RequestMapping(path=ControllerConstants.REQUEST_SEARCH, method=RequestMethod.POST)
	@ResponseBody
	public String onSearch(@RequestBody(required = false) SearchParameter searchParams, Model model, Pageable pageable){
		if (CommonUtility.isEmpty(searchParams.getPageable())) {
			searchParams.setPageable(pageable);
		}
		Page<JobCategory> results = businessService.getObjects(searchParams);
		List<JobCategory> expectedResults = results.getContent().subList(0, 1550);
		Gson gson = new GsonBuilder().serializeNulls().create();
		String jsonBizObjects = gson.toJson(expectedResults);
		return jsonBizObjects;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String onShow(@PathVariable("id") Long id, Model model) {
		log.info("Fetch office object with id: " + id);

		JobCategory fetchedObject = businessService.getObject(id);
		model.addAttribute(ControllerConstants.FETCHED_OBJECT, fetchedObject);
		
		return PAGE_CONTEXT_PREFIX + "Show";
	}

	/**
	 * Create a new office object and place in Model attribute.
	 */
	@RequestMapping(value = ControllerConstants.REQUEST_CREATE, method = RequestMethod.GET)
	public String showCreateForm(Model model) {
		model.addAttribute(net.sunrise.common.CommonConstants.FETCHED_OBJECT, JobCategory.builder().build());
		return PAGE_CONTEXT_PREFIX + "Edit";
	}

	/**
	 * Create/update a business entity.
	*/
	@RequestMapping(value=ControllerConstants.REQUEST_CREATE, method = RequestMethod.POST)
	public String create(@ModelAttribute JobCategory bizObject, BindingResult bindingResult, Model model, HttpServletRequest httpServletRequest,
			RedirectAttributes redirectAttributes, Locale locale) {

		if (bindingResult.hasErrors()) {
			model.addAttribute(net.sunrise.common.CommonConstants.FETCHED_OBJECT, bizObject);
			return PAGE_CONTEXT_PREFIX + "Edit";
		}

		log.info("Creating/updating project");
		String object = messageSource.getMessage("label.object.project", new Object[] {}, locale);
		model.asMap().clear();
		redirectAttributes.addFlashAttribute("message", new Message("success", messageSource.getMessage("label.general.saveSuccess", new Object[] { object }, locale)));

		businessService.saveOrUpdate(bizObject);
		return REDIRECT_PAGE;
	}

	/**
	 * Retrieve the book with the specified id for the update form.
	 */
	@RequestMapping(value = ControllerConstants.REQUEST_UPDATE + "/{id}", method = RequestMethod.GET)
	public String updateForm(@PathVariable("id") Long id, Model model) {
		model.addAttribute(net.sunrise.common.CommonConstants.FETCHED_OBJECT, businessService.getObject(id));
		return PAGE_CONTEXT_PREFIX + "Edit";
	}

	@Override
	protected String performSearch(SearchParameter params) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getDefaultPage(){
		if (1 >= this.businessService.count()){
			setupFakeObjects();
		}
		return PAGE_CONTEXT_PREFIX + "Browse";
	}

	private void setupFakeObjects(){
		this.globalDataInitializer.constructJobCategories();
	}

	@Override
	protected List<SelectItem> suggestItems(String keyword) {
		Page<JobCategory> searchProjects = this.businessService.searchObjects(keyword, null);
		List<SelectItem> suggestedSelectItems = super.buildSelectItems(searchProjects.getContent(), CommonConstants.BEAN_PROPERTY_OBJECT_ID, new String[]{CommonConstants.BEAN_PROPERTY_NAME});
		return suggestedSelectItems;
	}
}

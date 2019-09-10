package net.sunrise.controller.dmx;

import java.net.URI;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;
import net.sunrise.common.CommonConstants;
import net.sunrise.controller.ControllerConstants;
import net.sunrise.domain.entity.general.Department;
import net.sunrise.manager.catalog.impl.DepartmentManager;

@Slf4j
@RequestMapping(CommonConstants.REST_API + ControllerConstants.REQUEST_MAPPING_DEPARTMENT)
@RestController
public class DepartmentAPIController {
	@Autowired
	private DepartmentManager serviceManager;

	@RequestMapping(value = "/get/{name}", method = RequestMethod.GET)
	public @ResponseBody Department get(HttpServletRequest request, @PathVariable("name") String name) {
		Department fetchedObject = null;
		try {
			Optional<Department> queryObject = this.serviceManager.getByName(name);
			if (!queryObject.isPresent()) {
				fetchedObject = new Department();
				fetchedObject.setName("Nhóm khởi động");
				fetchedObject.setCode("DEPT-20170501");
			}else {
				fetchedObject = queryObject.get();
			}
			System.out.println("Found department: [" + fetchedObject + "]");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return fetchedObject;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseEntity<?> add(HttpServletRequest request, @RequestBody Department department) {
		ResponseEntity<?> responseEntity = null;
		try {
			this.serviceManager.save(department);
			URI projectUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/" + ControllerConstants.REQUEST_MAPPING_DEPARTMENT + "/{id}")
					.buildAndExpand(department.getId()).toUri();

			responseEntity = ResponseEntity.created(projectUri).build();
		} catch (Exception e) {
			log.error(e.getMessage());
			responseEntity = ResponseEntity.noContent().build();
		}
		return responseEntity;
	}
}

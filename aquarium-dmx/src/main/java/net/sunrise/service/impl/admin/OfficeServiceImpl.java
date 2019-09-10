package net.sunrise.service.impl.admin;

import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.sunrise.domain.entity.admin.Office;
import net.sunrise.exceptions.ObjectNotFoundException;
import net.sunrise.framework.model.SearchParameter;
import net.sunrise.framework.repository.BaseRepository;
import net.sunrise.framework.service.GenericServiceImpl;
import net.sunrise.repository.admin.OfficeRepository;
import net.sunrise.repository.specification.admin.OfficeRepositorySpec;
import net.sunrise.service.api.admin.OfficeService;


@Service
public class OfficeServiceImpl extends GenericServiceImpl<Office, Long> implements OfficeService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5706290180460138956L;

	@Inject 
	private OfficeRepository repository;
	
	protected BaseRepository<Office, Long> getRepository() {
		return this.repository;
	}

	@Override
	public Office getOne(String code) throws ObjectNotFoundException {
		return super.getOptionalObject(repository.findByCode(code));
	}

	@Override
	protected Page<Office> performSearch(String keyword, Pageable pageable) {
		return repository.search(keyword, pageable);
	}

	@Override
	public String deployObjects(List<List<String>> dataStrings) {
		log.info("Enter deploy contacts.");
		StringBuilder deployedObjects = new StringBuilder();
		Office deployedObject = null;
		Office currentObject = null;
		for (List<String> dataObjectParts :dataStrings){
			try {
				currentObject = this.parseEntity(dataObjectParts);
				if (this.repository.countByCode(currentObject.getCode())<1){
					deployedObject = repository.saveAndFlush(currentObject);
					deployedObjects.append(deployedObject.getCode()).append(";");
				}
			} catch (Exception e) {
				log.error("Error at contact code: " + currentObject.getCode(), e);
			}
		}
		log.info("Leave deploy contacts.");
		return deployedObjects.toString();
	}

	private Office parseEntity(List<String> data){
		return new Office();
		/*return Office.getInstance(
				(String)ListUtility.getEntry(data, 0), //Code
				(String)ListUtility.getEntry(data, 2), //First name
				(String)ListUtility.getEntry(data, 1)) //Last name
				.setDateOfBirth(DateTimeUtility.createFreeDate((String)ListUtility.getEntry(data, 4)))
				.setPlaceOfBirth((String)ListUtility.getEntry(data, 5))
				.setNationalId((String)ListUtility.getEntry(data, 6))
				.setNationalIdIssuedDate(DateTimeUtility.createFreeDate((String)ListUtility.getEntry(data, 7)))
				.setNationalIdIssuedPlace((String)ListUtility.getEntry(data, 8))
				.setGender(GenderTypeUtility.getGenderType((String)ListUtility.getEntry(data, 21)))
				.setAddress((String)ListUtility.getEntry(data, 14))
				.setPresentAddress((String)ListUtility.getEntry(data, 14), (String)ListUtility.getEntry(data, 22))
				.setBillingAddress((String)ListUtility.getEntry(data, 15), (String)ListUtility.getEntry(data, 22))
				.setPhones(CommonUtility.safeSubString((String)ListUtility.getEntry(data, 18), 0, 50))
				.setCellPhones(CommonUtility.safeSubString((String)ListUtility.getEntry(data, 19), 0, 50))
				.setOverallExpectation((String)ListUtility.getEntry(data, 28))
				.setOverallExperience((String)ListUtility.getEntry(data, 27))
				.setEmail((String)ListUtility.getEntry(data, 20))
				.setNotes((String)ListUtility.getEntry(data, 29))
			;*/
	}

	@Override
	public Page<Office> getObjects(SearchParameter searchParameter) {
		return this.repository.findAll(OfficeRepositorySpec.buildSpecification(searchParameter), searchParameter.getPageable());
	}
}

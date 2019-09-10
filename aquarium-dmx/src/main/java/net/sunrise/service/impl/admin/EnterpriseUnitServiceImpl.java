package net.sunrise.service.impl.admin;

import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import net.sunrise.domain.entity.dmx.EnterpriseUnit;
import net.sunrise.exceptions.ObjectNotFoundException;
import net.sunrise.framework.model.SearchParameter;
import net.sunrise.framework.repository.BaseRepository;
import net.sunrise.framework.service.GenericServiceImpl;
import net.sunrise.repository.admin.EnterpriseUnitRepository;
import net.sunrise.repository.specification.admin.EnterpriseUnitRepositorySpec;
import net.sunrise.service.api.admin.EnterpriseUnitService;


@Service
public class EnterpriseUnitServiceImpl extends GenericServiceImpl<EnterpriseUnit, Long> implements EnterpriseUnitService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1025316894391258610L;

	@Inject 
	private EnterpriseUnitRepository repository;
	
	protected BaseRepository<EnterpriseUnit, Long> getRepository() {
		return this.repository;
	}

	@Override
	public EnterpriseUnit getOne(String code) throws ObjectNotFoundException {
		return super.getOptionalObject(repository.findByCode(code));
	}

	@Override
	protected Page<EnterpriseUnit> performSearch(String keyword, Pageable pageable) {
		return repository.search(keyword, pageable);
	}

	@Override
	public String deployObjects(List<List<String>> dataStrings) {
		log.info("Enter deploy contacts.");
		StringBuilder deployedObjects = new StringBuilder();
		EnterpriseUnit deployedObject = null;
		EnterpriseUnit currentObject = null;
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

	private EnterpriseUnit parseEntity(List<String> data){
		return new EnterpriseUnit();
		/*return EnterpriseUnit.getInstance(
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
	public Page<EnterpriseUnit> getObjects(SearchParameter searchParameter) {
		return this.repository.findAll(EnterpriseUnitRepositorySpec.buildSpecification(searchParameter), searchParameter.getPageable());
	}
}

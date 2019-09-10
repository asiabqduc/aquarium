package net.sunrise.service.impl.contact;

import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.sunrise.common.DateTimeUtility;
import net.sunrise.common.GenderTypeUtility;
import net.sunrise.common.ListUtility;
import net.sunrise.domain.entity.contact.ContactTeam;
import net.sunrise.domain.entity.crx.contact.Contact;
import net.sunrise.domain.entity.crx.contact.ContactAddress;
import net.sunrise.exceptions.ObjectNotFoundException;
import net.sunrise.framework.repository.BaseRepository;
import net.sunrise.framework.service.GenericServiceImpl;
import net.sunrise.repository.contact.ContactAddressRepository;
import net.sunrise.repository.contact.ContactRepository;
import net.sunrise.repository.contact.ContactTeamRepository;
import net.sunrise.service.api.contact.ContactService;


@Service
public class ContactServiceImpl extends GenericServiceImpl<Contact, Long> implements ContactService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4580295852310880409L;

	@Inject 
	private ContactRepository repository;
	
	@Inject 
	private ContactAddressRepository contactAddressRepository;

	@Inject 
	private ContactTeamRepository contactTeamRepository;

	protected BaseRepository<Contact, Long> getRepository() {
		return this.repository;
	}

	@Override
	public Contact getOne(String code) throws ObjectNotFoundException {
		return repository.findByCode(code);
	}

  /**
   * Get one contact object with the provided id.
   * 
   * @param id The contact id
   * @return The contact
   * @throws ObjectNotFoundException If no such account exists.
   */
	@Override
	public Contact getObject(Long id) throws ObjectNotFoundException {
		Contact foundObject = repository.getOne(id);
		if (null==foundObject)
			throw new ObjectNotFoundException(String.valueOf(id));

		List<ContactAddress> contactAddresses = contactAddressRepository.findByContact(foundObject);
		foundObject.setContactAddresses(contactAddresses);

		List<ContactTeam> contactTeams = contactTeamRepository.findByContact(foundObject);
		foundObject.setContactTeams(contactTeams);

		return foundObject;
	}

	@Override
	protected Page<Contact> performSearch(String keyword, Pageable pageable) {
		return repository.search(keyword, pageable);
	}

	@Override
	public String deployContacts(List<List<String>> dataStrings) {
		log.info("Enter deploy contacts.");
		StringBuilder errorObjects = new StringBuilder();
		StringBuilder deployedObjects = new StringBuilder();
		Contact deployedObject = null;
		Contact currentObject = null;
		List<String> contactCodes = getContactCodes();
		for (List<String> dataObjectParts :dataStrings){
			try {
				currentObject = this.parseEntity(dataObjectParts);
				if (!contactCodes.contains(currentObject.getCode())){
					deployedObject = repository.saveAndFlush(currentObject);
					contactCodes.add(deployedObject.getCode());

					deployedObjects.append(deployedObject.getCode()).append(";");
				}
			} catch (Exception e) {
				log.error("Error at contact code: " + currentObject.getCode(), e);
				errorObjects.append(currentObject.getCode()).append(";");
			}
		}
		log.info("Leave deploy contacts.");
		if (errorObjects.length() > 0){
			log.info("Error objects: " + errorObjects.toString());
		}
		return deployedObjects.toString();
	}

	private List<String> getContactCodes(){
		return this.repository.findCode();
	}

	private Contact parseEntity(List<String> data){
		return Contact.builder()
				.code(ListUtility.getEntry(data, 0)) 
				.firstName(ListUtility.getEntry(data, 2))
				.lastName(ListUtility.getEntry(data, 1))
				.birthdate(DateTimeUtility.createFreeDate((String)ListUtility.getEntry(data, 4)))
				.birthplace((String)ListUtility.getEntry(data, 5))
				.gender(GenderTypeUtility.getGenderType((String)ListUtility.getEntry(data, 21)))
				.email((String)ListUtility.getEntry(data, 20))
				.description((String)ListUtility.getEntry(data, 29))
				.build();
				
		/*return Contact.builder()
				.code(ListUtility.getEntry(data, 0)) 
				.personalInfo(PersonalInfo.builder()
						.firstName(ListUtility.getEntry(data, 2))
						.lastName(ListUtility.getEntry(data, 1))
						.build())
				.birthdate(DateTimeUtility.createFreeDate((String)ListUtility.getEntry(data, 4)))
				.birthplace((String)ListUtility.getEntry(data, 5))
				.documents(documents)
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
}

/**
 * 
 */
package net.sunrise.dispatch;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import net.brilliance.domain.entity.general.CatalogueSubtype;
import net.brilliance.domain.entity.schedule.JobCategory;
import net.brilliance.domain.entity.schedule.JobDefinition;
import net.brilliance.framework.component.RootComponent;
import net.brilliance.framework.model.ExecutionContext;
import net.brilliance.service.api.admin.BusinessUnitService;
import net.brilliance.service.api.admin.OfficeService;
import net.brilliance.service.api.admin.quartz.JobCategoryService;
import net.brilliance.service.api.dmx.EnterpriseService;
import net.brilliance.service.api.inventory.CatalogueSubtypeService;

/**
 * @author bqduc
 *
 */
@Service
public class GlobalDataInitializer extends RootComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5814017485820198746L;

	@Inject
	private OfficeService bizServiceManager;

	@Inject
	private BusinessUnitService bizUnitServiceManager;

	@Inject
	private EnterpriseService enterpriseService;

	@Inject
	private CatalogueSubtypeService catalogueSubtypeService;

	@Inject
	private JobCategoryService jobCategoryService;

	public void constructOffices() {
		log.info("Generating {} random offices", DataInitializerRepo.NUMBER_TO_GENERATE);
		/*Office currentObject = null;
		Faker faker = new Faker();
		Address[] addresses = DataInitializerRepo.buildAddresses();
		for (int i = 0; i < DataInitializerRepo.NUMBER_TO_GENERATE; i++) {
			try {
				currentObject = Office.builder()
						.code(faker.code().ean13())
						.name(CommonUtility.stringTruncate(faker.company().name(), 200))
						.phones(faker.phoneNumber().phoneNumber())
						.description(faker.company().industry() + "\n" + faker.commerce().department() + "\n" + faker.company().profession())
						.address(addresses[i])
						.build();
				bizServiceManager.saveOrUpdate(currentObject);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}*/
	}

	public void constructBusinessUnits() {
		int numberOfBusinessUnits = DataInitializerRepo.NUMBER_TO_GENERATE/3;
		log.info("Generating {} random business units", numberOfBusinessUnits);
		/*BusinessUnit currentObject = null;
		Faker faker = new Faker();
		for (int i = 0; i < DataInitializerRepo.NUMBER_TO_GENERATE; i++) {
			try {
				currentObject = BusinessUnit.builder()
						.code(faker.code().ean13())
						.name(CommonUtility.stringTruncate(faker.company().name(), 200))
						.nameLocal(CommonUtility.stringTruncate(faker.company().name(), 200))
						.description(faker.company().industry() + "\n" + faker.commerce().department() + "\n" + faker.company().profession())
						.build();
				bizUnitServiceManager.saveOrUpdate(currentObject);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}*/
	}

	/**
	 * Office data faker initializing
	 */

	public void constructCatalogueSubtypes() {
		log.info("Generating {} random catalog subtypes", DataInitializerRepo.NUMBER_OF_CATALOGUE_SUBTYPES_GENERATE);
		CatalogueSubtype currentObject = null;
		/*Faker faker = new Faker();
		for (int i = 0; i < DataInitializerRepo.NUMBER_TO_GENERATE; i++) {
			try {
				currentObject = CatalogueSubtype.builder()
				.code(faker.code().ean8())
				.name(faker.commerce().department())
				.description(faker.commerce().material() + "::" + faker.commerce().productName()+"::"+faker.commerce().color())
				.build();
				catalogueSubtypeService.saveOrUpdate(currentObject);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}*/
	}

	public void constructSubtypeLevels() {
		
	}

	/**
	 * Job category data faker initializing
	 */

	public void constructJobCategories() {
		log.info("Generating {} random job categories", DataInitializerRepo.NUMBER_OF_CATALOGUE_SUBTYPES_GENERATE/5);
		JobCategory currentObject = null;
		/*Faker faker = new Faker();
		for (int i = 0; i < DataInitializerRepo.NUMBER_TO_GENERATE/5; i++) {
			try {
				currentObject = JobCategory.builder()
				.name(faker.commerce().productName())
				.info(faker.commerce().material() + "::" + faker.commerce().productName())
				.build();

				jobCategoryService.saveOrUpdate(currentObject);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}*/
	}

	public List<JobDefinition> constructJobDefinitions(){
		/*final String[] scheduledExpressions = new String[]{
				"0/5 * * * * ?", //Day end job schedule
				"0/6 * * * * ?", //Month end job schedule
				"0/7 * * * * ?", //Year end job schedule
				"0/8 * * * * ?", //Feed alert job schedule
				"0/9 * * * * ?", //Client expiration job schedule
				"0/11 * * * * ?", //Food synchronization job schedule
				"0/12 * * * * ?" //Day end job schedule
		};
*/
		return null;
	}

	public void buildFakeEnterprises(ExecutionContext excecutionContext){
		/*Enterprise currentObject = null;
		if (excecutionContext.isEmpty()){
			//Build the fake enterprise objects
			Faker faker = new Faker();
			for (int i = 0; i < DataInitializerRepo.NUMBER_TO_GENERATE; i++) {
				try {
					currentObject = Enterprise.builder()
							.code(faker.code().ean13())
							.name(CommonUtility.stringTruncate(faker.company().name(), 200))
							.nameLocal(CommonUtility.stringTruncate(faker.company().name(), 200))
							.description(faker.company().industry() + "\n" + faker.commerce().department() + "\n" + faker.company().profession())
							.build();
					enterpriseService.saveOrUpdate(currentObject);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		} else {
			//Inquiry information from execution context parameter, parse and persist. 
		}*/
	}
}

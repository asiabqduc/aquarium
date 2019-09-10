/**
 * 
 */
package net.sunrise.asyn;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import net.sunrise.common.CommonConstants;
import net.sunrise.common.ListUtility;
import net.sunrise.domain.entity.config.Item;
import net.sunrise.domain.entity.config.Language;
import net.sunrise.domain.entity.config.LocalizedItem;
import net.sunrise.exceptions.EcosysException;
import net.sunrise.framework.component.ComponentBase;
import net.sunrise.helper.GlobalDataServiceHelper;
import net.sunrise.model.Bucket;
import net.sunrise.service.api.invt.ItemService;
import net.sunrise.service.api.invt.LanguageService;

/**
 * @author bqduc
 *
 */

@Component
public class InventoryDataDeployer extends ComponentBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5439039826537936572L;

	@Inject
	private ItemService itemService;

	@Inject
	private LanguageService languageService;

	@Inject
	private GlobalDataServiceHelper globalDataServiceHelper;

	private Map<String, Language> languagesMap = ListUtility.createMap();
	private Map<String, Item> itemsMap = ListUtility.createMap();

	private final static String[] sheetIds = new String[]{"languages", "items", "localized-items"}; 

	@Async
	public void asyncDeployConstructionData(Map<?, ?> contextParams) throws EcosysException {
		System.out.println("Enter InventoryDataDeployer::asyncDeployConstructionData .....");
		for (int idx = 0; idx < CommonConstants.DUMMY_LARGE_COUNT; idx++){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Enter InventoryDataDeployer::asyncDeployConstructionData .....");
		/*Bucket bucket = loadConfigurationData("/config/data/data-catalog.xlsx", sheetIds, new int[]{1, 1, 1});
		deployDataItems(bucket);*/
	}

	private void deployLanguages(List<List<String>> languageStrings){
		log.info("Enter deploy languages.");
		Language deployedObject = null;
		Language currentObject = null;
		try {
			for (List<String> languageParts :languageStrings){
				currentObject = this.parseLanguage(languageParts);
				if (null==(deployedObject = this.languageService.getByCode(currentObject.getCode()))){
					deployedObject = languageService.saveOrUpdate(currentObject);
				}
				languagesMap.put(deployedObject.getCode(), deployedObject);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info("Leave deploy languages.");
	}

	private void deployItems(List<List<String>> itemStrings){
		log.info("Enter deploy items.");
		Item deployedObject = null;
		Item currentObject = null;
		try {
			for (List<String> objectParts :itemStrings){
				currentObject = this.parseItem(objectParts);
				if (null==(deployedObject = this.itemService.getOne(currentObject.getCode()))){
					deployedObject = this.itemService.saveOrUpdate(currentObject);
				}
				itemsMap.put(deployedObject.getCode(), deployedObject);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info("Leave deploy items.");
	}

	private void deployLocalizedItems(List<List<String>> itemStrings){
		log.info("Enter deploy localized items.");
		LocalizedItem deployedObject = null;
		try {
			for (List<String> objectParts :itemStrings){
				deployedObject = this.parseLocalizedItem(objectParts);
				if (null==deployedObject.getItem()||null==deployedObject.getLanguage())
					continue;

				if (null==this.itemService.getLocalizedItem(deployedObject.getItem(), deployedObject.getLanguage())){
					deployedObject = this.itemService.saveLocalizedItem(deployedObject);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info("Leave deploy localized items.");
	}
	
	@SuppressWarnings("unchecked")
	protected void deployDataItems(Bucket dataBucket){
		log.info("Enter deploy default inventory items.");
		try {
			//Deploy languages
			this.deployLanguages((List<List<String>>)dataBucket.get(sheetIds[0]));

			//Deploy items
			this.deployItems((List<List<String>>)dataBucket.get(sheetIds[1]));

			//Deploy localized items
			this.deployLocalizedItems((List<List<String>>)dataBucket.get(sheetIds[2]));
			System.out.println("DONE!");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info("Leave deploy default inventory items.");
	}

	private Item parseItem(List<String> strings){
		return Item
				.builder()
				.code(strings.get(0))
				.subtype(strings.get(1))
				.build();
	}

	private Language parseLanguage(List<String> strings){
		return Language.instance(strings.get(0), strings.get(1));
	}

	private LocalizedItem parseLocalizedItem(List<String> strings){
		Item item = itemsMap.get(strings.get(0));
		Language language = languagesMap.get(strings.get(1));
		return LocalizedItem.instance(item, language, strings.get(2), strings.get(3));
	}

	protected Bucket loadConfigurationData(String resourceFileName, String[]sheets, int[] indexes){
		Bucket bucket = null;
		Map<Object, Object>configParams = ListUtility.createMap();
		//Configure the started index for each sheet
		for (int idx = 0; idx < sheets.length; idx++){
			configParams.put(sheets[idx] + Bucket.PARAM_STARTED_ROW_INDEX, Integer.valueOf(indexes[idx]));
		}
		configParams.put(Bucket.PARAM_DATA_SHEETS, ListUtility.arraysAsList(sheets));
		configParams.put(Bucket.PARAM_WORK_DATA_SHEET, sheets);

		try {
			bucket = globalDataServiceHelper.readSpreadsheetData(new ClassPathResource(resourceFileName).getInputStream(), configParams);
			//return (List<List<String>>)bucket.get(configParams.get(Bucket.PARAM_WORK_DATA_SHEET));
		} catch (EcosysException | IOException e) {
			e.printStackTrace();
		}
		return bucket;
	}
}

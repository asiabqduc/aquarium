package net.sunrise.manager.contact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sunrise.cdx.domain.entity.Configuration;
import net.sunrise.cdx.manager.ConfigurationManager;
import net.sunrise.common.CommonConstants;
import net.sunrise.common.CommonUtility;
import net.sunrise.common.DateTimeUtility;
import net.sunrise.domain.entity.admin.UserAccount;
import net.sunrise.domain.entity.contact.ClientProfile;
import net.sunrise.enums.DefaultConfigurations;
import net.sunrise.framework.manager.AbstractServiceManager;
import net.sunrise.framework.repository.JBaseRepository;
import net.sunrise.helper.GlobalDataServiceHelper;
import net.sunrise.model.Bucket;
import net.sunrise.repository.contact.ClientProfileRepository;


@Service
@Transactional
public class ClientProfileManager extends AbstractServiceManager<ClientProfile, Long>{
	private static final long serialVersionUID = 8844355473614598235L;

	@Inject
	private ClientProfileRepository clientRepository;

	@Inject 
	private GlobalDataServiceHelper globalDataServiceHelper;

	@Inject
	private ConfigurationManager configurationManager;

	@Transactional(readOnly = true)
	public Page<ClientProfile> search(Map<String, Object> parameters){
		String keyword = (String)parameters.get(CommonConstants.PARAM_KEYWORD);
		Page<ClientProfile> searchResults = clientRepository.searchObjects(new StringBuilder("%").append(keyword).append("%").toString(), null);
		return searchResults;
	}

	@Transactional(readOnly = true)
	public List<ClientProfile> getAll() {
		List<ClientProfile> results = new ArrayList<>();
		results.addAll((Collection<? extends ClientProfile>) clientRepository.findAll());
		return results;
	}

	@Transactional(readOnly = true)
	public ClientProfile getById(java.lang.Long id) {
		return clientRepository.getOne(id);
	}

	@Transactional(readOnly = true)
	public ClientProfile getByCode(String code) {
		return clientRepository.findByCode(code);
	}

	@Transactional(readOnly = true)
	public ClientProfile getClientProfile(UserAccount user) {
		return clientRepository.findByUser(user);
	}

	public ClientProfile save(ClientProfile clientProfile) {
		clientRepository.save(clientProfile);
		return clientProfile;
	}

	public void delete(ClientProfile clientProfile) {
		clientRepository.delete(clientProfile);
	}

	/**
	 * Removes all ClientProfile entities from database.
	 */
	public void deleteAll() {
		clientRepository.deleteAll();
	}

	/**
	 * Restore the original set of books to the database.
	 */
	public void restoreDefaultClients() {
		String[] words = null;
		ClientProfile client = null;
		String code, name, address, email, phone;
		try {
			List<String> lines = this.globalDataServiceHelper.getCsvStrings("/db/data/clients.csv", 1);
			for (String line :lines){
				words = line.split("~");
				code = words[0];
				name = words[1];
				address = words[2];
				email = words[3];
				phone = words[4];
				if (email.length() < 1) {
					email = code+"@gmail.com";
				}
				client = ClientProfile.getInstance(code, name, address, phone, email);

				try {
					clientRepository.save(client);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setupDefaultMasterData(){
		Optional<Configuration> setupConfiguration = configurationManager.getByName(DefaultConfigurations.setupClientProfile.getConfigurationName());
		if (setupConfiguration.isPresent() && 
				!CommonUtility.BOOLEAN_STRING_FALSE.equalsIgnoreCase(setupConfiguration.get().getValue())){
			log.info("Default client profile is setup already. ");
			return;
		}

		parseAndImportClientProfiles();
		Configuration configuration = Configuration.builder().name(DefaultConfigurations.setupClientProfile.getConfigurationName()).value(CommonUtility.BOOLEAN_STRING_TRUE).build();
		configurationManager.save(configuration);
	}

	protected void parseAndImportClientProfiles(){
		List<List<String>> dataStrings = null;
		ClientProfile clientProfile = null;
		try {
			Bucket dataBucket = this.globalDataServiceHelper.readSpreadsheetData(new ClassPathResource("/db/data/Danh sach chot luu ky.xlsx").getInputStream());
			int count = 0;
			for (Object key :dataBucket.getBucketData().keySet()) {
				dataStrings = (List<List<String>>)dataBucket.getBucketData().get(key);
				int size = dataStrings.size();
				for (int idx = 1; idx < size; idx++) {
					List<String> dataString = dataStrings.get(idx);
					if (null==this.clientRepository.findByCode(dataString.get(6))){
						clientProfile = parseClientProfile(dataString);
						try {
							this.clientRepository.save(clientProfile);
							count++;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			System.out.println("Total profiles imported: " + count);

			/*dataBucket = this.externalDataHelper.readXlsData(new ClassPathResource("/db/data/DanhMucSachMoi.xls").getInputStream(), null);
			for (Object key :dataBucket.getBucketData().keySet()) {
				dataStrings = (List<List<String>>)dataBucket.getBucketData().get(key);
				for (List<String> dataString :dataStrings) {
					System.out.println(dataString);
				}
			}*/

			/*dataBucket = this.externalDataHelper.readXlsData(new ClassPathResource("/db/data/Danh sach CBCNV cac don vi chot den 30-4-2016.xlsx").getInputStream(), null);
			for (Object key :dataBucket.getBucketData().keySet()) {
				dataStrings = (List<String>)dataBucket.getBucketData().get(key);
				for (String dataString :dataStrings) {
					System.out.println(dataString);
				}
			}

			dataBucket = this.externalDataHelper.readXlsData(new ClassPathResource("/db/data/danh-sach-co-dong-nhan-co-tuc-2013-signed.xlsx").getInputStream(), null);
			for (Object key :dataBucket.getBucketData().keySet()) {
				dataStrings = (List<String>)dataBucket.getBucketData().get(key);
				for (String dataString :dataStrings) {
					System.out.println(dataString);
				}
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ClientProfile parseClientProfile(List<String> dataStringParts){
		ClientProfile clientProfile = ClientProfile.getInstance();
		try {
			clientProfile.setCode(dataStringParts.get(6));
			clientProfile.setFullName(dataStringParts.get(5));
			clientProfile.setPhones(dataStringParts.get(10));
			clientProfile.setAddress(dataStringParts.get(8));
			clientProfile.setEmail(dataStringParts.get(18));
			clientProfile.setIssueDate(DateTimeUtility.parseDate(dataStringParts.get(7), "dd/MM/yyyy"));
			clientProfile.setType(dataStringParts.get(14));
			clientProfile.setProfileType(dataStringParts.get(13));
			clientProfile.setNationality(dataStringParts.get(1));
			clientProfile.setNotes(dataStringParts.get(17));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clientProfile;
	}

	@Override
	protected JBaseRepository<ClientProfile, Long> getRepository() {
		return clientRepository;
	}
}

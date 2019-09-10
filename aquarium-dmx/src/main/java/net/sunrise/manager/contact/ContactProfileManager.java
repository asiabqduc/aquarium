package net.sunrise.manager.contact;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sunrise.domain.entity.contact.ContactProc;
import net.sunrise.framework.manager.BaseManager;
import net.sunrise.framework.repository.BaseRepository;
import net.sunrise.repository.contact.ContactProfileRepository;

@Service
@Transactional
public class ContactProfileManager extends BaseManager<ContactProc, Long>{
	private static final long serialVersionUID = -3874412554298276460L;

	@Inject
	private ContactProfileRepository contactRepository;

	/**
	 * Restore the original set of contacts to the database.
	 */
	/*public void restoreDefaultContacts() {
		ClassPathResource resource = new ClassPathResource("/config/liquibase/contacts.csv");

		BufferedReader br = null;
		try {

			br = new BufferedReader(new InputStreamReader(resource.getInputStream()));

			// Skip first line that only holds headers.
			br.readLine();

			String line;
			String[] words = null;
			Contact contact = null;
			while ((line = br.readLine()) != null) {
				words = line.split("~");

				Integer version = Integer.valueOf(words[0]);
				String name = words[1];
				String code = "";
				String producer = words[2];

				String description = words[4];

				contact = new Contact(code, name, producer, description);

				contactRepository.save(contact);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// Release resources.
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}*/

	public ContactProc getByName(String name) {
		return contactRepository.findByFullName(name);
	}

	public ContactProc getByCode(String code) {
		return contactRepository.findByCode(code);
	}

	@Override
	protected BaseRepository<ContactProc, Long> getRepository() {
		return this.contactRepository;
	}

	@Override
	protected Page<ContactProc> performSearch(String keyword, Pageable pageable) {
		return this.contactRepository.search(keyword, pageable);
	}
}

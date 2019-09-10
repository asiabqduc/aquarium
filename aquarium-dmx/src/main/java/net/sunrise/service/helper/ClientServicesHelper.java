/**
 * 
 */
package net.sunrise.service.helper;

import java.time.ZonedDateTime;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import net.sunrise.common.Base64Utils;
import net.sunrise.common.CommonUtility;
import net.sunrise.common.DateTimeUtility;
import net.sunrise.domain.entity.admin.UserAccount;
import net.sunrise.domain.entity.contact.ClientProfile;
import net.sunrise.exceptions.AuthenticationException;
import net.sunrise.manager.auth.AuthenticationServiceManager;
import net.sunrise.manager.auth.AuthorityManager;
import net.sunrise.manager.contact.ClientProfileManager;
import net.sunrise.manager.mail.freemarker.EmailServiceHelper;
import net.sunrise.manager.security.BrillianceEncoder;

/**
 * @author bqduc
 *
 */
@Service
public class ClientServicesHelper {
	@Inject 
	private ClientProfileManager businessServiceManager;

	@Inject 
	private AuthenticationServiceManager authenticationServiceManager;

	@Inject
  private EmailServiceHelper emailServiceHelper;

	@Inject
	private AuthorityManager authorityServiceManager;

	@Inject
	private BrillianceEncoder passwordEncoder;
	/*@Inject
	private SimpleEncryptionManager simpleEncryptor;*/

	public ClientProfile getClient(String userSsoId) throws AuthenticationException {
		UserAccount user = authenticationServiceManager.getUser(userSsoId);
		return businessServiceManager.getClientProfile(user);
	}
	
	public ClientProfile confirmClient(String token) throws AuthenticationException {
		UserAccount confirmUser = null;
		ClientProfile confirmClientProfile = null;
		String decodedToken = null;
		try {
			decodedToken = Base64Utils.decode(token);//Base64Utils.decodeFromString(token);
			//decoded token is user's email
			confirmUser = authenticationServiceManager.getUser(decodedToken);
			if (CommonUtility.isNull(confirmUser))
				throw new AuthenticationException("Could not get the associated user base on token: [" + decodedToken +"]");
	
			confirmClientProfile = businessServiceManager.getClientProfile(confirmUser);
			if (CommonUtility.isNull(confirmClientProfile))
				throw new AuthenticationException("Could not get the connected client profile of user: [" + confirmUser.getEmail() +"]");

			confirmUser.setActivated(true);
			confirmUser.setActivationDate(DateTimeUtility.getSystemDateTime());
			authenticationServiceManager.save(confirmUser);

			confirmClientProfile.setActivationKey(token);
			confirmClientProfile.setActivationDate(ZonedDateTime.now());
			confirmClientProfile.setActivated(Boolean.TRUE);
			businessServiceManager.save(confirmClientProfile);
		} catch (Exception e) {
			throw new AuthenticationException(e);
		}
		return confirmClientProfile;
	}

	/*public ClientProfile registerBasicClient(UserProfile user, Locale locale) throws AuthenticationException {
		ClientProfile basicClientProfile = null;
		Authority clientRole = null;
		Authority userRole = null;
		UserAccount clientUser = null;
		try {
			if (CommonUtility.isNull(user))
				throw new AuthenticationException("Could not register the basic client with the empty user dto!!");

			clientRole = authorityServiceManager.getByName(AuthorityGroup.RoleClient.getCode());
			userRole = authorityServiceManager.getByName(AuthorityGroup.RoleUser.getCode());

			Set<Authority> clientAuthorities = new HashSet<>();
			clientAuthorities.add(clientRole);
			clientAuthorities.add(userRole);

			String originalPassowrd = new String(user.getPassword());
			String encodedPassowrd = passwordEncoder.encode(user.getPassword(), CryptoAlgorithm.BASIC);
			clientUser = UserAccount.createInstance(
					user.getEmail(), 
					user.getFirstName(), 
					user.getLastName(), 
					user.getEmail(), 
					encodedPassowrd, 
					clientAuthorities);
			authenticationServiceManager.save(clientUser);

			basicClientProfile = ClientProfile.getInstance(clientUser);
			businessServiceManager.save(basicClientProfile);

			clientUser.setPassword(originalPassowrd);
			emailServiceHelper.sendClientRegisterMail(locale, clientUser, "emailClientRegister.ftl");
		} catch (Exception e) {
			throw new AuthenticationException(e);
		}
		return basicClientProfile;
	}*/

	public ClientProfile registerBasicClient(UserAccount user) throws AuthenticationException {
		if (CommonUtility.isNull(user))
			throw new AuthenticationException("Could not register the basic client with the empty user dto!!");

		ClientProfile basicClientProfile = ClientProfile.getInstance(user);
		businessServiceManager.save(basicClientProfile);
		return basicClientProfile;
	}

	public ClientProfile getClient(UserAccount user) throws AuthenticationException {
		if (CommonUtility.isNull(user))
			throw new AuthenticationException("Could not get client profile with the empty user!");

		return businessServiceManager.getClientProfile(user);
	}
}

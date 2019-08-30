/**
 * 
 */
package net.brilliance.service.helper;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import net.brilliance.exceptions.EcosysException;
import net.brilliance.framework.component.BaseComponent;
import net.brilliance.manager.auth.AuthenticationServiceManager;
import net.sunrise.manager.ConfigurationManager;

/**
 * @author bqduc
 *
 */
@Component
public class GlobalServicesHelper extends BaseComponent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5021261610500311738L;

	@Inject 
	private AuthenticationServiceManager authenticationServiceManager;

	@Inject 
	private ConfigurationManager configurationManager;

	/*@Inject 
	private EmployeeManager employeeManager;*/

	public void initDefaultComponents() throws EcosysException {
		log.info("Enter GlobalServicesHelper::initDefaultComponents(): Initializing the default components....");
		try {
			configurationManager.initializeMasterData();
			log.info("Master default master data for authentication is initialized!");

			authenticationServiceManager.initializeMasterData();
			log.info("Master default master data for authentication is initialized!");

			//employeeManager.initDefaultData();
			log.info("Default master data for authentication is initialized!");
		} catch (Exception e) {
			throw new EcosysException(e);
		}
		log.info("Leave GlobalServicesHelper::initDefaultComponents()");
	}

}

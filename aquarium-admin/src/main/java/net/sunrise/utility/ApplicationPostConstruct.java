/**
 * 
 */
package net.sunrise.utility;

import java.util.Calendar;

import javax.inject.Inject;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import net.sunrise.asyn.DataConfigurationDispatcher;
import net.sunrise.asyn.InventoryDataDeployer;
import net.sunrise.asyn.ResourcesDispatcher;
import net.sunrise.framework.logging.LogService;

/**
 * @author bqduc
 *
 */
@Component
public class ApplicationPostConstruct implements ApplicationListener<ApplicationReadyEvent> {
	@Inject 
	protected LogService cLogger;

	@Inject 
	private DataConfigurationDispatcher dataConfigurationDispatcher;
	
	@Inject 
	private ResourcesDispatcher resourcesDispatcher;

	@Inject 
	private InventoryDataDeployer inventoryDataDeployer;
	
	/*@Inject
	private BpmProcessEngine bpmProcessEngine;*/

	/*@Inject 
	private GlobalDataRepositoryManager globalDataRepositoryManager;*/

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		System.out.println("Fired on application ready event at: " + Calendar.getInstance().getTime());
		try {
			dataConfigurationDispatcher.asyncDeployConstructionData(null);
			resourcesDispatcher.asyncDeployConstructionData(null);
			inventoryDataDeployer.asyncDeployConstructionData(null);
			//globalDataRepositoryManager.initializeGlobalData();
		} catch (Exception e) {
			cLogger.error(e);
		}
	}

}

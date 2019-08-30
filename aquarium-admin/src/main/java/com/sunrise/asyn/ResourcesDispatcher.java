/**
 * 
 */
package com.sunrise.asyn;

import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import net.brilliance.common.CommonConstants;
import net.brilliance.exceptions.EcosysException;
import net.brilliance.framework.component.ComponentBase;

/**
 * @author bqduc
 *
 */
@Component
public class ResourcesDispatcher extends ComponentBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1683993901380583793L;

	@Async
	public void asyncDeployConstructionData(Map<?, ?> contextParams) throws EcosysException {
		log.info("Enter ResourcesDispatcher::asyncDeployConstructionData .....");
		for (int idx = 0; idx < CommonConstants.DUMMY_LARGE_COUNT; idx++){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		log.info("Enter ResourcesDispatcher::asyncDeployConstructionData .....");
	}
}

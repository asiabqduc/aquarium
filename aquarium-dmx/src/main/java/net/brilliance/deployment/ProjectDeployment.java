/**
 * 
 */
package net.brilliance.deployment;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import net.brilliance.constants.CommonManagerConstants;
import net.brilliance.exceptions.ExecutionContextException;
import net.brilliance.framework.deployment.Deployment;
import net.brilliance.framework.deployment.DeploymentBase;
import net.brilliance.framework.model.ExecutionContext;
import net.brilliance.service.api.dmx.ProjectService;

/**
 * @author bqduc
 *
 */
@Component
public class ProjectDeployment extends DeploymentBase implements Deployment {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6388000854548763374L;

	@Inject
	private ProjectService projectService;

	@Override
	public void deploy(ExecutionContext executionContext) throws ExecutionContextException {
		super.log.info("Enter ProjectDeployment::deploy ");
		if (executionContext.containKey(CommonManagerConstants.CONTEXT_DATA)){
			projectService.count();
		}
		super.log.info("Leave ProjectDeployment::deploy ");
	}

}

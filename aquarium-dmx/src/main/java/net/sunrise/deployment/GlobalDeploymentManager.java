/**
 * 
 */
package net.sunrise.deployment;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import net.sunrise.common.CommonUtility;
import net.sunrise.constants.CommonManagerConstants;
import net.sunrise.deplyment.DeploymentSpecification;
import net.sunrise.exceptions.ExecutionContextException;
import net.sunrise.framework.component.BaseComponent;
import net.sunrise.framework.model.ExecutionContext;
import net.sunrise.service.api.dmx.EnterpriseService;
import net.sunrise.service.api.dmx.ProjectService;

/**
 * @author bqduc
 * Query the configured data and deploy all activated deployment entries
 *
 */
@Component
public class GlobalDeploymentManager extends BaseComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 288413656949421360L;

	@Inject 
	private ProjectService projectService;

	@Inject 
	private EnterpriseService enterpriseService;

	public ExecutionContext deploy(ExecutionContext executionContext) throws ExecutionContextException {
		if (!executionContext.containKey(CommonManagerConstants.CONFIG_GROUP_DEPLOYMENT)){
			log.info("There is no configuration for deployment group in execution context. ");
			return executionContext;
		}

		String deploymentServices = (String)executionContext.getContextData(CommonManagerConstants.CONFIG_GROUP_DEPLOYMENT);
		if (CommonUtility.isEmpty(deploymentServices)){
			log.info("There is no deployment services in execution context. ");
			return executionContext;
		}

		deployProjects(executionContext);
		return executionContext;
	}

	protected void deployProjects(ExecutionContext executionContext) throws ExecutionContextException {
		ExecutionContext projectExecutionContext = null;
		Object projectContextData = null;
		try {
			if (!executionContext.containKey(CommonManagerConstants.CONTEXT_DEPLOYMENT_PREFIX + DeploymentSpecification.CONTEXT_PROJECT)){
				log.info("There is no configuration for deployment project in execution context. ");
				return;
			}

			projectExecutionContext = ExecutionContext.builder().build();
			projectContextData = executionContext.getContextData(
					new StringBuilder(CommonManagerConstants.CONTEXT_DEPLOYMENT_PREFIX)
					.append(DeploymentSpecification.CONTEXT_PROJECT)
					.append(DeploymentSpecification.CONTEXT_DATA)
					.toString());

			projectExecutionContext.putContextData(DeploymentSpecification.DEPLOYMENT_DATA_KEY, projectContextData);
			projectService.load(projectExecutionContext);
		} catch (Exception e) {
			throw new ExecutionContextException(e);
		}
	}

	protected void deployBooks(ExecutionContext executionContext) throws ExecutionContextException {
		final String DEPLOY_ENTRY = "book";
		ExecutionContext projectExecutionContext = null;
		String contextKey = null;
		//Object projectContextData = null;
		try {
			contextKey = new StringBuilder(CommonManagerConstants.CONTEXT_DEPLOYMENT_PREFIX).append(DEPLOY_ENTRY).toString();
			if (!executionContext.containKey(contextKey)){
				log.info("There is no configuration for deployment project in execution context. ");
				return;
			}

			projectExecutionContext = ExecutionContext.builder().build();
			/*projectContextData = executionContext.getContextData(
					new StringBuilder(CommonManagerConstants.CONTEXT_DEPLOYMENT_PREFIX)
					.append(DEPLOY_ENTRY)
					.append(CommonManagerConstants.CONTEXT_DEPLOYMENT_DATA)
					.toString());*/

			//projectExecutionContext.putContextData(DeploymentSpecification.DEPLOYMENT_DATA_KEY, projectContextData);
			projectService.load(projectExecutionContext);
		} catch (Exception e) {
			throw new ExecutionContextException(e);
		}
	}

	public void deployEnterprises(ExecutionContext executionContext) throws ExecutionContextException {
		ExecutionContext projectExecutionContext = null;
		Object projectContextData = null;
		try {
			if (null==executionContext || executionContext.isEmpty())
				throw new ExecutionContextException("Execution context is empty. Please check, setup the necessary data and perform again. ");

			if (!executionContext.containKey(CommonManagerConstants.CONTEXT_DEPLOYMENT_PREFIX + DeploymentSpecification.CONTEXT_ENTERPRISE)){
				log.info("There is no configuration for deployment project in execution context. ");
				return;
			}

			projectExecutionContext = ExecutionContext.builder().build();
			projectContextData = executionContext.getContextData(
					new StringBuilder(CommonManagerConstants.CONTEXT_DEPLOYMENT_PREFIX)
					.append(DeploymentSpecification.CONTEXT_ENTERPRISE)
					.append(DeploymentSpecification.CONTEXT_DATA)
					.toString());

			projectExecutionContext.putContextData(DeploymentSpecification.DEPLOYMENT_DATA_KEY, projectContextData);
			enterpriseService.load(projectExecutionContext);
		} catch (Exception e) {
			throw new ExecutionContextException(e);
		}
	}
	
}

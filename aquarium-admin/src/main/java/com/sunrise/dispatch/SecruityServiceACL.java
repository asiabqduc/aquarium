/**
 * 
 */
package com.sunrise.dispatch;

import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.sunrise.controller.ControllerConstants;

import net.brilliance.common.CommonUtility;
import net.brilliance.domain.entity.admin.Authority;
import net.brilliance.framework.component.ComponentBase;
import net.brilliance.manager.auth.AuthenticationServiceManager;

/**
 * @author bqduc
 *
 */

@Component(value="securityServiceAcl")
@SessionScope
public class SecruityServiceACL extends ComponentBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8134016986414706917L;

	private Map<String, Authority> authorityMap = null;
	
	@Inject
	private HttpServletRequest request;
	
	@Inject 
	private AuthenticationServiceManager authenticationServiceManager;

	public boolean hasPermission(final Authentication auth, Object targetDomainObject, Object permission){
		/*HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = request.getSession(true);*///true will create if
		//request.getSession().getAttribute("authenticatedUser")
		if (CommonUtility.isEmpty(this.authorityMap)){
			this.authorityMap = (Map)this.request.getSession().getAttribute(ControllerConstants.AUTHORITY_MAP);
		}

		log.info("Enter check permission gate. Target: [" + targetDomainObject + "]. Permission: [" + permission + "]. Authentication: " + auth + ". Associate manager: " + authenticationServiceManager);
		if (CommonUtility.isEmpty(authorityMap)){
			log.info("The authority map is empty!");
			return false;
		}

		boolean hasPermission = false;
		for (GrantedAuthority grantedAuthority :auth.getAuthorities()){
			if (this.authorityMap.containsKey(grantedAuthority.getAuthority())){
				hasPermission = true;
				break;
			}
		}
		log.info("Name: [" + auth.getName() + "]. Pricipal: " + auth.getPrincipal() + "]. Credentials: [" + auth.getCredentials() + "]. Authorities: [" + auth.getAuthorities() + "]");
		return hasPermission;
	}

	public Map<String, Authority> populateAuthorities(){
		this.authorityMap = this.authenticationServiceManager.getAuthorityMap();
		return this.authorityMap;
	}
}

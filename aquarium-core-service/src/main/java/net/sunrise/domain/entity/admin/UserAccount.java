/*
* Copyright 2017, Bui Quy Duc
* by the @authors tag. See the LICENCE in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package net.sunrise.domain.entity.admin;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.sunrise.common.CommonUtility;
import net.sunrise.common.DateTimeUtility;
import net.sunrise.domain.entity.general.Attachment;
import net.sunrise.framework.entity.BizObjectBase;
import net.sunrise.framework.entity.auth.AuthAccount;
import net.sunrise.framework.model.security.CryptoAlgorithm;
import net.sunrise.model.DateTimePatterns;

/**
 * A user.
 * 
 * @author bqduc
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sa_user_account")
@ToString(exclude = { "authorities" })
@EqualsAndHashCode(callSuper = true)
public class UserAccount extends BizObjectBase implements AuthAccount {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3124167777154600539L;

	@NotNull
	//@Pattern(regexp = "^[a-z0-9]*$|(anonymousUser)")
	@Size(min = 1, max = 100)
	@Column(name="sso_id", unique = true)
	private String ssoId;

	@NotNull
	@Size(min = 3, max = 60)
	@Column(name = "password")
	@JsonIgnore
	private String password;

	@Size(max = 30)
	@Column(name = "encrypt_alg")
	private String encryptAlgorithm;

	@NotNull
	@Size(min = 5, max = 100)
	@Column(name = "email", unique = true)
	private String email;

	@Builder.Default
	@Column(name = "locked")
	private Boolean locked = false;

	@Builder.Default
	@Column(name = "system_admin")
	private Boolean systemAdmin = false;

	@Size(max = 50)
	@Column(name = "first_name")
	private String firstName;

	@Size(max = 50)
	@Column(name = "last_name")
	private String lastName;
	
	@Size(max = 120)
	@Column(name = "activation_key")
	@JsonIgnore
	private String activationKey;

	@Column(name = "activation_date")
	private Date activationDate;

	@Column(name = "issue_date")
	private Date issueDate;

	@Size(max = 20)
	@Column(name = "reset_key")
	@JsonIgnore
	private String resetKey;

	@Column(name = "reset_date")
	private ZonedDateTime resetDate;

	@ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	//@formatter:off
	@JoinTable(
			name = "sa_granted_user_authority", 
			inverseJoinColumns = {@JoinColumn(name = "authority_id")},
			joinColumns = {@JoinColumn(name = "account_id")}
	)
	//@formatter:on
	private Set<Authority> authorities;

	@ManyToOne(targetEntity=Attachment.class, fetch=FetchType.EAGER)
	@JoinColumn(name = "attachment_id")
	private Attachment attachment;

	@Transient
	private UserDetails userDetails;
	
	@Transient
	@Builder.Default
	private Map<String, Authority> authorityMap = new HashMap<>();

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public String getResetKey() {
		return resetKey;
	}

	public void setResetKey(String resetKey) {
		this.resetKey = resetKey;
	}

	public ZonedDateTime getResetDate() {
		return resetDate;
	}

	public void setResetDate(ZonedDateTime resetDate) {
		this.resetDate = resetDate;
	}

	/*public Set<Authority> getAuthorities() {
		return authorities;
	}*/

	public void setAuthorities(Set<Authority> authorities) {
		this.authorities = authorities;
	}

	public UserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(UserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	@Transient
	public String getDisplayName() {
		return this.firstName + " " + this.lastName;
	}

	public Map<String, Authority> getAuthorityMap() {
		return authorityMap;
	}

	public void setAuthorityMap(Map<String, Authority> authorityMap) {
		this.authorityMap = authorityMap;
	}

	@Transient
	public String getDisplayIssueDate() {
		return DateTimeUtility.dateToString(this.issueDate, DateTimePatterns.ddMMyyyy_SLASH.getDateTimePattern());
	}

	public static UserAccount createInstance(String email, String firstName, String lastName, String login, String password, Set<Authority> authorities) {
		UserAccount instance = UserAccount.builder()
		.issueDate(DateTimeUtility.getSystemDateTime())
		.encryptAlgorithm(CryptoAlgorithm.BASIC.getAlgorithm())
		.firstName(firstName)
		.lastName(lastName)
		.authorities(authorities)
		.build();

		instance.setEmail(email);
		instance.setSsoId(login);
		instance.setPassword(password);
		return instance;
	}

	public String getSsoId() {
		return ssoId;
	}

	public void setSsoId(String ssoId) {
		this.ssoId = ssoId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEncryptAlgorithm() {
		return encryptAlgorithm;
	}

	public void setEncryptAlgorithm(String encryptAlgorithm) {
		this.encryptAlgorithm = encryptAlgorithm;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public Boolean getSystemAdmin() {
		return systemAdmin;
	}

	public void setSystemAdmin(Boolean systemAdmin) {
		this.systemAdmin = systemAdmin;
	}

	public Attachment getAttachment() {
		return attachment;
	}

	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Iterator<Authority> itr = null;
		Authority authority = null;
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		if (CommonUtility.isNotEmpty(this.authorities)) {
			itr = this.authorities.iterator();
			while (itr.hasNext()) {
				authority = itr.next();
				grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName()));
			}
		}
		return grantedAuthorities;
	}
}
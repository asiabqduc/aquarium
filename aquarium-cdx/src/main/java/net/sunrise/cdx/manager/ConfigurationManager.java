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
package net.sunrise.cdx.manager;

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sunrise.cdx.domain.entity.Configuration;
import net.sunrise.cdx.repository.ConfigurationRepository;
import net.sunrise.common.CommonConstants;
import net.sunrise.exceptions.EcosysException;
import net.sunrise.framework.manager.BaseManager;
import net.sunrise.framework.repository.BaseRepository;
import net.sunrise.global.WebAdminConstants;
import net.sunrise.model.DateTimePatterns;

/**
 * Configuration service implementation. Provides implementation of the configuration
 * 
 * @author bqduc
 *
 */

@Service
@Transactional
public class ConfigurationManager extends BaseManager<Configuration, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4728373147908397873L;

	@Inject
	private ConfigurationRepository repository;

	@Override
	protected BaseRepository<Configuration, Long> getRepository() {
		return this.repository;
	}

	@Override
	protected Page<Configuration> performSearch(String keyword, Pageable pageable) {
		return repository.search(keyword, pageable);
	}

	public Optional<Configuration> getByName(String name) {
		return repository.findByName(name);
	}

	public boolean isExists(String name){
		return repository.isExists(name);
	}

	public void initializeMasterData() throws EcosysException{
		log.info("Setup the default configurations for date/time ");
		this.setupDateTimeConfigurations();

		log.info("Leave the setup master data of configuration");
	}

	protected void setupDateTimeConfigurations() {
		//Setup default date formats
		Optional<Configuration> optConfig = this.repository.findByName(WebAdminConstants.CONFIG_DATE_FORMAT);
		if (!optConfig.isPresent()) {
			StringBuilder dateFormats = new StringBuilder();
			for (DateTimePatterns dateTimePattern :DateTimePatterns.values()) {
				dateFormats.append(dateTimePattern.getDateTimePattern()).append(CommonConstants.SEMICOLON);				
			}

			Configuration config = Configuration.builder()
					.group(WebAdminConstants.CONFIG_GROUP_DATE_TIME)
					.name(WebAdminConstants.CONFIG_DATE_FORMAT)
					.value(dateFormats.toString())
					.build();
			this.repository.save(config);
		}
	}
}

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
package net.sunrise.manager.agricultural;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sunrise.domain.entity.aquacultural.Aquafeed;
import net.sunrise.framework.manager.AbstractServiceManager;
import net.sunrise.framework.repository.JBaseRepository;
import net.sunrise.repository.agricultural.AquafeedRepository;

/**
 * Provides implementation of the Aquafeed
 * 
 * @author bqduc
 *
 */
@Service
@Transactional
public class AquafeedManager extends AbstractServiceManager<Aquafeed, Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7978498406689919003L;

	@Inject
	private AquafeedRepository repository;

	@Override
	protected JBaseRepository<Aquafeed, Long> getRepository() {
		return this.repository;
	}

	public List<Aquafeed> createDummyObjects() {
		List<Aquafeed> fetchedObjects = null;
		return fetchedObjects;
	}

	public Optional<Aquafeed> getByName(String name) {
		return repository.findByName(name);
	}

	public Aquafeed createAquafeed(Aquafeed aquafeed) {
		return super.create(aquafeed);
	}

	public Aquafeed getAquafeed(Long id) {
		return this.repository.getOne(id);
	}

	public void deleteAquafeed(Long id) {
		//this.repository.delete(id);
	}

	public void updateAquafeed(Aquafeed aquafeed) {
		this.repository.saveAndFlush(aquafeed);
	}
}
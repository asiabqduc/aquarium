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

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sunrise.domain.entity.aquacultural.Pond;
import net.sunrise.framework.manager.AbstractServiceManager;
import net.sunrise.framework.repository.JBaseRepository;
import net.sunrise.repository.agricultural.PondRepository;

/**
 * Provides implementation of the Aquafeed
 * 
 * @author bqduc
 *
 */
@Service
@Transactional
public class PondManager extends AbstractServiceManager<Pond, Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7713269837364755606L;

	@Inject
	private PondRepository repository;

	@Override
	protected JBaseRepository<Pond, Long> getRepository() {
		return this.repository;
	}

	public List<Pond> createDummyObjects() {
		List<Pond> fetchedObjects = null;
		return fetchedObjects;
	}

	public Pond createPond(Pond aquafeed) {
		return super.create(aquafeed);
	}

	public Pond getPond(Long id) {
		return this.repository.getOne(id);
	}

	public void deletePond(Long id) {
		//this.repository.delete(id);
	}

	public void updatePond(Pond aquafeed) {
		this.repository.saveAndFlush(aquafeed);
	}
}
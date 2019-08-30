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
package net.sunrise.service.impl;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.brilliance.framework.repository.BaseRepository;
import net.brilliance.framework.service.GenericServiceImpl;
import net.sunrise.domain.entity.Route;
import net.sunrise.repository.RouteRepository;
import net.sunrise.service.RouteService;

/**
 * Category service implementation. Provides implementation of the department
 * 
 * @author bqduc
 *
 */
@Service
@Transactional
public class RouteServiceImpl extends GenericServiceImpl<Route, Long> implements RouteService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9006110510581945642L;

	@Inject
	private RouteRepository repository;

	protected BaseRepository<Route, Long> getRepository() {
		return this.repository;
	}
}

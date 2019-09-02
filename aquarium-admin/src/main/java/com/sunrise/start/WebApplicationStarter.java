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
package com.sunrise.start;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.sunrise.config.AspectConfig;
import com.sunrise.config.BaseConfiguration;
import com.sunrise.config.H2Config;
import com.sunrise.config.JpaAuditingConfig;
import com.sunrise.config.PostgresConfig;
import com.sunrise.config.WebSecurityConfiguration;
import com.sunrise.dispatch.GlobalDataRepositoryManager;

import lombok.extern.slf4j.Slf4j;
import net.brilliance.common.CommonConstants;
import net.brilliance.manager.mail.ThymeleafMailConfig;
import net.brilliance.manager.mail.freemarker.FreeMarkerEmailConfiguration;

/**
 * Application root configuration. The
 * {@link SpringBootApplication @SpringBootApplication} <br />
 * is a convenience annotation for {@link ComponentScan @ComponentScan},
 * {@link Configuration @Configuration}, and <br />
 * {@link EnableAutoConfiguration @EnableAutoConfiguration}. The
 * {@code scanBasePackageClasses} in this context is type safe.
 * <p>
 * The application can run on multiple profiles to support different types of
 * databases, relational and non-relational.<br />
 * In the current state, the application runs on:
 * <ol>
 * <li>{@link H2Config H2 Database}</li>
 * <li>{@link PostgresConfig PostgreSQL Database}</li>
 * <li>{@link MySQLConfig MySQL Database}</li>
 * <li>{@link MongoConfig MongoDB}</li>
 * </ol>
 * </p>
 * 
 * @see H2Config
 * @see PostgresConfig
 * @see MongoConfig
 * 
 * @author bqduc
 *
 */
@Slf4j
@SpringBootApplication(/* scanBasePackageClasses = { Controllers.class, Services.class } */)
@EnableConfigurationProperties({ MailProperties.class })
@EnableAspectJAutoProxy
@Import(value = { BaseConfiguration.class, JpaAuditingConfig.class, PostgresConfig.class, AspectConfig.class,
		WebSecurityConfiguration.class,
		// SecurityConfig.class,
		ThymeleafMailConfig.class, FreeMarkerEmailConfiguration.class })
@EnableAsync
public class WebApplicationStarter
		implements WebMvcConfigurer /* extends SpringBootServletInitializer */ /* WebMvcConfigurerAdapter */ {
	/*
	 * @Inject private CategoryManager categoryService;
	 */

	/**
	 * Entry point of the application
	 * 
	 * @param args The arguments passed in from the command line
	 */
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(WebApplicationStarter.class);
		// app.setBannerMode(Mode.OFF);
		ConfigurableApplicationContext configAppContext = app.run(args);
		initializeGlobalData(configAppContext);
		/*
		 * try { log.info("Start initialize the global data");
		 * globalDataRepositoryManager =
		 * configAppContext.getBean(GlobalDataRepositoryManager.class);
		 * globalDataRepositoryManager.initializeGlobalData();
		 * log.info("The global data is initialization is done. "); } catch (Exception
		 * e) { log.error(e.getMessage(), e); }
		 */
	}

	// Tomcat large file upload connection reset
	@Bean
	public TomcatServletWebServerFactory containerFactory() {
		return new TomcatServletWebServerFactory() {
			protected void customizeConnector(Connector connector) {
				int maxSize = CommonConstants.MAX_POST_SIZE;
				super.customizeConnector(connector);
				connector.setMaxPostSize(maxSize);
				connector.setMaxSavePostSize(maxSize);
				if (connector.getProtocolHandler() instanceof AbstractHttp11Protocol) {
					//((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1); //Unlimited
					((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(maxSize);
					logger.info("Set MaxSwallowSize " + maxSize);
				}
			}
		};

	}

	protected static void initializeGlobalData(ConfigurableApplicationContext configAppContext) {
		GlobalDataRepositoryManager globalDataRepositoryManager = null;
		log.info("Enter initialize global data");
		try {
			globalDataRepositoryManager = configAppContext.getBean(GlobalDataRepositoryManager.class);
			globalDataRepositoryManager.initializeGlobalData();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info("Leave initialize global data. ");
	}

	/*
	 * @Bean public SpringTemplateEngine templateEngine(ITemplateResolver
	 * templateResolver, SpringSecurityDialect sec) { final SpringTemplateEngine
	 * templateEngine = new SpringTemplateEngine();
	 * templateEngine.setTemplateResolver(templateResolver);
	 * templateEngine.addDialect(sec); // Enable use of "sec" return templateEngine;
	 * }
	 */

	/**
	 * i18n support bean. The locale resolver being used is Cookie.<br />
	 * When locale is changed and intercepted by the
	 * {@link WebApplicationStarter#localeChangeInterceptor
	 * localeChangeInterceptor}. <br />
	 * The new locale is stored in a Cookie and remains active even after session
	 * timeout<br />
	 * or session being invalidated
	 * <p>
	 * Set a fixed Locale to <em>US</em> that this resolver will return if no cookie
	 * found.
	 * </p>
	 * 
	 * @return {@code LocaleResolver}
	 * @see WebApplicationStarter#localeChangeInterceptor
	 */
	/*
	 * @Bean public LocaleResolver localeResolver() { CookieLocaleResolver clr = new
	 * CookieLocaleResolver(); //clr.setDefaultLocale(Locale.US);
	 * clr.setDefaultLocale(getDefaultLocale()); return clr; }
	 */

	/**
	 * i18n bean support for switching locale through a request param. <br />
	 * Users who are authenticated can change their default locale to another when
	 * they pass in a<br />
	 * url (http://example.com/&lt;contextpath&gt;/<em>lang=&lt;locale&gt;</em>)
	 * 
	 * @return
	 */
	/*
	 * @Bean public LocaleChangeInterceptor localeChangeInterceptor() {
	 * LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
	 * lci.setParamName("lang"); return lci; }
	 */

	/*
	 * @Bean public MessageSource messageSource() { String[] resourceBundles = new
	 * String[]{ "classpath:/i18n/messages-menu", "classpath:/i18n/messages-stock",
	 * "classpath:/i18n/messages-catalog", "classpath:/i18n/messages-general",
	 * "classpath:/i18n/messages-emp", "classpath:/i18n/messages-master",
	 * "classpath:/i18n/messages-contact", "classpath:/i18n/messages",
	 * "classpath:/i18n/messages-crx", "classpath:/i18n/messages-admin" };
	 * 
	 * log.info("Initialize the message source......");
	 * 
	 * ReloadableResourceBundleMessageSource messageSource = new
	 * ReloadableResourceBundleMessageSource();
	 * messageSource.setBasenames(resourceBundles);
	 * messageSource.setDefaultEncoding("UTF-8"); return messageSource; }
	 */

	/**
	 * {@inheritDoc}
	 */
	/*
	 * @Override public void addViewControllers(ViewControllerRegistry registry) {
	 * //registry.addViewController("/login").setViewName("signin");
	 * registry.addViewController("/login").setViewName("clientLogin"); }
	 * 
	 *//**
		  * {@inheritDoc}
		  *//*
				 * @Override public void addInterceptors(InterceptorRegistry registry) {
				 * registry.addInterceptor(localeChangeInterceptor()); }
				 */

	/*
	 * @Override public void addFormatters(FormatterRegistry registry) {
	 * registry.addConverter(new CategoryConverter(categoryService));
	 * 
	 * registry.addFormatter(new CategoryFormatter(categoryService)); }
	 *//*
		  * 
		  * private Locale getDefaultLocale(){ return new Locale("vi", "VN"); }
		  */
}

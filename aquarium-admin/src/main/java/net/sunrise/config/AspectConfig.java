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
package net.sunrise.config;

import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;

import net.sunrise.asyn.AsyncMailSender;
import net.sunrise.asyn.ExceptionAspect;
import net.sunrise.asyn.ExceptionInterceptor;
import net.sunrise.utility.Profiles;

/**
 * Configures asynchronous mailing When an exception is encountered in the
 * application, it will send an email to the developer.<br />
 * It may also send the <i>Stacktrace</i> of the error depending on the
 * configured property of the application.<br />
 * <p>
 * 
 * The application's email feature can be enabled when the {@code aspect}
 * profile is active.
 * </p>
 * 
 * @author Julius Krah
 *
 */
@EnableAsync
@Profile(Profiles.ASPECT)
@Configuration
@ComponentScan(basePackageClasses = ExceptionAspect.class)
public class AspectConfig {
	@Inject
	private MailProperties mailProperties;

	private void applyProperties(JavaMailSenderImpl sender) {
		sender.setHost(this.mailProperties.getHost());
		if (this.mailProperties.getPort() != null) {
			sender.setPort(this.mailProperties.getPort());
		}
		sender.setUsername(this.mailProperties.getUsername());
		sender.setPassword(this.mailProperties.getPassword());
		sender.setProtocol(this.mailProperties.getProtocol());
		if (this.mailProperties.getDefaultEncoding() != null) {
			sender.setDefaultEncoding(this.mailProperties.getDefaultEncoding().name());
		}
		if (!this.mailProperties.getProperties().isEmpty()) {
			sender.setJavaMailProperties(asProperties(this.mailProperties.getProperties()));
		}
	}

	private Properties asProperties(Map<String, String> source) {
		Properties properties = new Properties();
		properties.putAll(source);
		return properties;
	}

	@Bean
	public JavaMailSender mailSender() {
		JavaMailSenderImpl sender = new AsyncMailSender();

		applyProperties(sender);

		return sender;
	}

	/**
	 * Bean to intercept all application exceptions and queue them for asynchronous mail sending
	 * 
	 * @return ExceptionInterceptor
	 */
	// @Bean uncomment to enable
	public ExceptionInterceptor exceptionInterceptor() {
		return new ExceptionInterceptor();
	}

	/**
	 * Bean to create proxies for the interceptor. It scans the classpath for Controller classes
	 * 
	 * @return BeanNameAutoProxyCreator
	 */
	// @Bean uncomment to enable
	public BeanNameAutoProxyCreator autoProxyCreater() {
		BeanNameAutoProxyCreator autoProxyCreator = new BeanNameAutoProxyCreator();
		autoProxyCreator.setBeanNames("*Controller");
		autoProxyCreator.setInterceptorNames("exceptionInterceptor");

		return autoProxyCreator;
	}

}

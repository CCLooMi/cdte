package com.ccloomi.cdte.spring.springboot;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.ccloomi.cdte.CDTEngine;
import com.ccloomi.cdte.spring.springmvc.CDTEViewResolver;

/**© 2015-2018 Chenxj Copyright
 * 类    名：CDTEAutoConfiguration
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年3月18日-下午3:17:35
 */
@Configurable
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class CDTEAutoConfiguration {
	
	@Bean
	@ConditionalOnMissingBean(CDTEngine.class)
	public CDTEngine cdteEngine() {
		CDTEngine engine=new CDTEngine();
		engine.compileTemplates();
		return engine;
	}
	
	@Bean
	public CDTEViewResolver cdteViewResolver() {
		CDTEViewResolver resolver=new CDTEViewResolver();
		return resolver;
	}
}

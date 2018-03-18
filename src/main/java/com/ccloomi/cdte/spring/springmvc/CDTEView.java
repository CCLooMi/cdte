package com.ccloomi.cdte.spring.springmvc;

import static com.ccloomi.cdte.CDTEConfigure.charset;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContextException;
import org.springframework.web.servlet.view.AbstractTemplateView;

import com.ccloomi.cdte.CDTEngine;

/**© 2015-2018 Chenxj Copyright
 * 类    名：CDTEView
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年3月18日-下午3:33:21
 */
public class CDTEView extends AbstractTemplateView{
	private CDTEngine engine;
	public CDTEView() {
	}
	@Override
	protected void initServletContext(ServletContext servletContext) {
		if(engine==null) {
			engine=autodetectConfiguration();
		}
	}
	
	protected CDTEngine autodetectConfiguration() throws BeansException {
		try {
			return BeanFactoryUtils.beanOfTypeIncludingAncestors(getApplicationContext(), CDTEngine.class, true, false);
		} catch (NoSuchBeanDefinitionException ex) {
			throw new ApplicationContextException("Must define a single CDTEngine bean in this web application context "
					+ "(may be inherited): CDTEngine is the usual implementation. "
					+ "This bean may be given any name.", ex);
		}
	}
	@Override
	public boolean checkResource(Locale locale) throws Exception {
		return engine.checkTemplate(getUrl());
	}
	@Override
	protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setCharacterEncoding(charset.name());
		engine.findTemplate(getUrl()).render(model, response.getOutputStream());
	}

}
